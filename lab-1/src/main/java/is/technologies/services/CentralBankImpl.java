package is.technologies.services;

import is.technologies.exceptions.*;
import is.technologies.entities.*;
import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.util.*;

/**
 * Implementation of central bank
 */
public class CentralBankImpl implements CentralBank {
    private static CentralBankImpl centralBank;
    private final HashMap<String, Bank> banks;
    private final HashMap<UUID, User> users;
    private final HashMap<UUID, Transaction> transactions;
    private final HashMap<UUID, Account> accounts;

    private CentralBankImpl() {
        banks = new HashMap<>();
        users = new HashMap<>();
        transactions = new HashMap<>();
        accounts = new HashMap<>();
    }

    /**
     * Gets single instance of central bank
     * @return CentralBankImpl
     */
    public static CentralBankImpl getInstance() {
        if (centralBank == null) {
            centralBank = new CentralBankImpl();
        }
        return centralBank;
    }

    /**
     * Add bank to system pool
     * @param name to create bank
     * @param config to add to bank
     * @return boolean
     */
    public boolean addBank(String name, Config config) {
        if (name.isBlank()) {
            throw new IllegalStateException("Name is blank");
        }

        if (config == null) {
            throw new NullPointerException("config"); // TODO : NPE
        }

        if (banks.containsKey(name)) {
            return false;
        }

        Bank bank = new Bank(name, config);
        banks.put(name, bank);
        return true;
    }

    /**
     * Create new user's creator
     * @return User's creator
     */
    public UserCreator createUserData() {
        return new UserCreator();
    }

    /**
     * Add user to system pool
     * @param data to create user
     * @return UUID of user
     */
    public UUID addUser(UserData data) {
        if (data == null) {
            throw new NullPointerException("data"); // TODO : NPE
        }

        UUID id = UUID.randomUUID();
        User user = new User(id, data);
        users.put(id, user);
        return id;
    }

    /**
     * Open account
     * @param userId of account owner
     * @param bankName of account's bank
     * @param mode means account's type
     * @param money is initial value
     * @return UUID of new account
     */
    public UUID openAccount(UUID userId, String bankName, AccountMode mode, Money money) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (bankName.isBlank()) {
            throw new IllegalStateException("Bank's name is blank");
        }

        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        Bank bank = banks.get(bankName);
        Config config = bank.getConfig();
        UUID id = UUID.randomUUID();
        Account account = switch (mode) {
            case DEBIT -> new DebitAccount(
                    id,
                    bankName,
                    money,
                    config.getDebitPercent(),
                    config.getCreditHighLimit(),
                    BankTimer.getTime(),
                    config.getTrustLimit(),
                    userId
            );
            case DEPOSIT -> new DepositAccount(
                    id,
                    bankName,
                    money,
                    config.getDepositPercents(),
                    config.getCreditHighLimit(),
                    BankTimer.getTime(),
                    new BankTime(BankTimer.getTime().time().plusDays(config.getDepositDays())),
                    config.getTrustLimit(),
                    userId
            );
            case CREDIT -> new CreditAccount(
                    id,
                    bankName,
                    money,
                    config.getCreditLowLimit(),
                    config.getCreditHighLimit(),
                    config.getCreditCommission(),
                    config.getTrustLimit(),
                    userId
            );
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };

        bank.getSubscribers().add(account);
        accounts.put(id, account);
        users.get(userId).addAccount(account);
        return id;
    }

    /**
     * Put money from account or take money to account
     * @param accountId of account
     * @param money of transaction
     * @param mode means type of transaction
     * @exception CentralBankException if accountId is incorrect
     */
    public void transactMoney(UUID accountId, Money money, MoneyActionMode mode) {
        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        if (!accounts.containsKey(accountId)) {
            throw CentralBankException.incorrectAccountId(accountId);
        }

        boolean isNotTrustedUser = !users.get(accounts.get(accountId).getUserId()).isTrusted();
        boolean isMoreThanLimit = money.compareTo(accounts.get(accountId).getTrustLimit()) > 0;
        if (mode == MoneyActionMode.TAKE_MONEY && isNotTrustedUser && isMoreThanLimit) {
            throw CentralBankException.noTrust(accounts.get(accountId).getUserId());
        }

        if (accounts.containsKey(accountId)) {
            accounts.get(accountId).makeTransaction(money, mode);
        }
    }

    /**
     * Transact money from one account to another
     * @param accountFromId is account-sender id
     * @param accountToId is account-getter id
     * @param money is money to transact
     * @return UUID of transaction
     * @exception CentralBankException is accounts' data is incorrect
     */
    public UUID transactMoney(UUID accountFromId, UUID accountToId, Money money) {
        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        if (!accounts.containsKey(accountFromId)) {
            throw CentralBankException.incorrectAccountId(accountFromId);
        }

        if (!accounts.containsKey(accountToId)) {
            throw CentralBankException.incorrectAccountId(accountToId);
        }

        if (accountFromId == accountToId) {
            throw CentralBankException.accountFromIsAccountTo(accountFromId, accountToId);
        }

        Account accountFrom = accounts.get(accountFromId);
        Account accountTo = accounts.get(accountToId);

        boolean isNotTrustedUser = !users.get(accountFrom.getUserId()).isTrusted();
        boolean isMoreThanLimit = money.compareTo(accountTo.getTrustLimit()) > 0;

        if (isNotTrustedUser && isMoreThanLimit) {
            throw CentralBankException.noTrust(accountFrom.getUserId());
        }

        Transaction transaction = new Transaction(accountFrom, accountTo, money);
        transaction.execute();

        UUID transactionId = transaction.getId();
        transactions.put(transactionId, transaction);
        return transactionId;
    }

    /**
     * Revert transaction
     * @param transactionId is id of transaction
     * @return boolean
     */
    public boolean revertTransaction(UUID transactionId) {
        if (!transactions.containsKey(transactionId)) {
            return false;
        }

        return transactions.get(transactionId).revertTransaction();
    }

    /**
     * Gets account's data by account's id
     * @param accountId account's id
     * @return data of account
     */
    public AccountData getAccountData(UUID accountId) {
        if (!accounts.containsKey(accountId)) {
            throw CentralBankException.incorrectAccountId(accountId);
        }

        return accounts.get(accountId).getAccountData();
    }

    /**
     * Gets data oof all user's accounts
     * @param userId is user's id
     * @return data about all user's accounts
     */
    public List<AccountData> getUserAccountsData(UUID userId) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        return users.get(userId).GetAccountsData();
    }

    /**
     * Gets bank's config
     * @param bankName is name of bank
     * @return Config
     */
    public Config getConfig(String bankName) {
        Bank bank = getBank(bankName);
        return bank.getConfig();
    }

    /**
     * Change debit percent
     * @param bankName is name of bank
     * @param percent is new debit percent
     */
    public void changeDebitPercent(String bankName, double percent) {
        if (percent <= 0) {
            throw ConfigException.incorrectPercent(percent);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDebitPercent(percent);
        bank.changeConfig(config);
    }

    /**
     * Create new interval with new money value and new percent or delete previous interval.
     * This interval is inserted into previous interval pool or is deleted from it
     * @param bankName is name of bank
     * @param money is money value of interval
     * @param percent is percent of interval
     * @param mode means to add or to delete interval
     * @exception DepositPercentException if data is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeDepositPercents(
            String bankName, Money money, double percent, ChangeDepositPercentMode mode) {
        if (money.compareTo(Money.ZERO) < 1) {
            throw MoneyException.incorrectMoneyCount(money);
        }

        if (percent <= 0) {
            throw DepositPercentException.incorrectPercent(percent);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        TreeMap<Money, Double> percents = config.getDepositPercents().getData();
        switch (mode) {
            case ADD_INTERVAL -> percents.put(money, percent);
            case REMOVE_INTERVAL -> percents.remove(money);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        config.setDepositPercents(new DepositPercents(percents));
        bank.changeConfig(config);
    }

    /**
     * Change debit high limit
     * @param bankName is name of bank
     * @param limit if new limit
     * @exception ConfigException if limit is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeDebitHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDebitHighLimit(limit);
        bank.changeConfig(config);
    }

    /**
     * Change deposit high limit
     * @param bankName is name of bank
     * @param limit is new limit
     * @exception ConfigException if limit is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeDepositHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDepositHighLimit(limit);
        bank.changeConfig(config);
    }

    /**
     * Change credit low limit
     * @param bankName is name of bank
     * @param limit is new limit
     * @exception ConfigException if limit is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeCreditLowLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) > 0) {
            throw ConfigException.incorrectLowLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setCreditLowLimit(limit);
        bank.changeConfig(config);
    }

    /**
     * Change credit high limit
     * @param bankName is name of bank
     * @param limit is new limit
     * @exception ConfigException if limit is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeCreditHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setCreditHighLimit(limit);
        bank.changeConfig(config);
    }

    /**
     * Change credit commission
     * @param bankName is name of bank
     * @param commission is new commission
     * @exception ConfigException if commission is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeCreditCommission(String bankName, Money commission) {
        if (commission.compareTo(Money.ZERO) < 0) {
            throw ConfigException.incorrectCommission(commission);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setCreditCommission(commission);
        bank.changeConfig(config);
    }

    /**
     * Change deposit time
     * @param bankName is name of bank
     * @param days are new deposit days
     * @exception ConfigException if limit is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeDepositTime(String bankName, int days) {
        if (days < 365) {
            throw ConfigException.tooShortDepositLimit(days);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDepositDays(days);
        bank.changeConfig(config);
    }

    /**
     * Change trust limit
     * @param bankName is name of bank
     * @param newTrustLimit is new limit
     * @exception ConfigException if limit is incorrect
     * @exception CentralBankException if name of bank is incorrect
     */
    public void changeTrustLimit(String bankName, Money newTrustLimit) {
        if (newTrustLimit.compareTo(Money.ZERO) < 0) {
            throw ConfigException.incorrectCommission(newTrustLimit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setTrustLimit(newTrustLimit);
        bank.changeConfig(config);
    }

    /**
     * Add address to user
     * @param userId is user's id
     * @param address to add
     * @exception CentralBankException if user's id is incorrect
     * @exception UserException if user already has address
     */
    public void addUserAddress(UUID userId, Address address) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (address == null) {
            throw new NullPointerException("address"); // TODO : NPE
        }

        users.get(userId).addAddress(address);
    }

    /**
     * Add passport to user
     * @param userId is user's id
     * @param passport to add
     * @exception CentralBankException if user's id is incorrect
     * @exception UserException if user already has passport
     */
    public void addUserPassport(UUID userId, Passport passport) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (passport == null) {
            throw new NullPointerException("passport"); // TODO : NPE
        }

        users.get(userId).AddPassport(passport);
    }

    /**
     * Add address to user
     * @param userId is user's id
     * @param phoneNumber to add
     * @exception CentralBankException if user's id is incorrect
     * @exception UserException if user already has phone number
     */
    public void addUserPhoneNumber(UUID userId, PhoneNumber phoneNumber) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber"); // TODO : NPE
        }

        users.get(userId).addPhoneNumber(phoneNumber);
    }

    /**
     * Create copy of user's data
     * @param userId is user's id
     * @return User's data
     */
    public UserData getUserData(UUID userId) {
        User user = users.get(userId);
        return new UserData(
                user.getName(),
                user.getSurname(),
                user.getPassport(),
                user.getAddress(),
                user.getPhoneNumber()
        );
    }

    /**
     * Create new string of transaction info for print
     * @param id is transaction id
     * @return transaction's info ike a string
     */
    public String getTransactionString(UUID id) {
        return transactions.get(id).toString();
    }

    /**
     * Find bank by its name
     * @param bankName is name of bank
     * @return Bank
     * @exception CentralBankException if there is no bank with this name
     */
    private Bank getBank(String bankName) {
        if (!banks.containsKey(bankName)) {
            throw CentralBankException.incorrectBankName(bankName);
        }

        return banks.get(bankName);
    }
}
