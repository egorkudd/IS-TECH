package is.technologies.services;

import is.technologies.exceptions.*;
import is.technologies.entities.*;
import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.util.*;

public class CentralBankImpl implements CentralBank {
    private static CentralBankImpl centralBank;
    private final HashMap<String, Bank> banks;
    private final HashMap<UUID, User> users;
    private final HashMap<UUID, Transaction> transactions;
    private final HashMap<UUID, AbstractAccount> accounts;

    private CentralBankImpl() {
        banks = new HashMap<>();
        users = new HashMap<>();
        transactions = new HashMap<>();
        accounts = new HashMap<>();
    }

    public static CentralBankImpl getInstance() {
        if (centralBank == null) {
            centralBank = new CentralBankImpl();
        }
        return centralBank;
    }

    public boolean addBank(String name, Config config) {
        if (name.isBlank()) {
            throw new NullPointerException("name"); // TODO : NPE
        }

        if (config == null) {
            throw new NullPointerException("config"); // TODO : NPE
        }

        if (banks.containsKey(name)) {
            return false;
        }

        var bank = new Bank(name, config);
        banks.put(name, bank);
        return true;
    }

    public UserCreator createUserData() {
        return new UserCreator();
    }

    public UUID addUser(UserData data) {
        if (data == null) {
            throw new NullPointerException("data"); // TODO : NPE
        }

        var id = UUID.randomUUID();
        var user = new User(id, data);
        users.put(id, user);
        return id;
    }

    public UUID openAccount(UUID userId, String bankName, AccountMode mode, Money money) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (bankName.isBlank()) {
            throw new NullPointerException("bankName"); // TODO : NPE
        }

        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        Bank bank = banks.get(bankName);
        Config config = bank.getConfig();
        var id = UUID.randomUUID();
        AbstractAccount account = switch (mode) {
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
                    new BankTime(BankTimer.getTime().getTime().plusDays(config.getDepositDays())),
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

        AbstractAccount accountFrom = accounts.get(accountFromId);
        AbstractAccount accountTo = accounts.get(accountToId);

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

    public boolean revertTransaction(UUID transactionId) {
        if (!transactions.containsKey(transactionId)) {
            return false;
        }

        return transactions.get(transactionId).revertTransaction();
    }

    public AccountData getAccountData(UUID accountId) {
        if (!accounts.containsKey(accountId)) {
            throw CentralBankException.incorrectAccountId(accountId);
        }

        return accounts.get(accountId).getAccountData();
    }

    public List<AccountData> getUserAccountsData(UUID userId) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        return users.get(userId).GetAccountsData();
    }

    public Config getConfig(String bankName) {
        Bank bank = getBank(bankName);
        return bank.getConfig();
    }

    public void changeDebitPercent(String bankName, double percent) {
        if (percent <= 0) {
            throw ConfigException.incorrectPercent(percent);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDebitPercent(percent);
        bank.changeConfig(config);
    }

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

    public void changeDebitHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDebitHighLimit(limit);
        bank.changeConfig(config);
    }

    public void changeDepositHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDepositHighLimit(limit);
        bank.changeConfig(config);
    }

    public void changeCreditLowLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) > 0) {
            throw ConfigException.incorrectLowLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setCreditLowLimit(limit);
        bank.changeConfig(config);
    }

    public void changeCreditHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setCreditHighLimit(limit);
        bank.changeConfig(config);
    }

    public void changeCreditCommission(String bankName, Money commission) {
        if (commission.compareTo(Money.ZERO) < 0) {
            throw ConfigException.incorrectCommission(commission);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setCreditCommission(commission);
        bank.changeConfig(config);
    }

    public void changeDepositTime(String bankName, int days) {
        if (days < 365) {
            throw ConfigException.tooShortDepositLimit(days);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setDepositDays(days);
        bank.changeConfig(config);
    }

    public void changeTrustLimit(String bankName, Money newTrustLimit) {
        if (newTrustLimit.compareTo(Money.ZERO) < 0) {
            throw ConfigException.incorrectCommission(newTrustLimit);
        }

        Bank bank = getBank(bankName);
        Config config = bank.getConfig();
        config.setTrustLimit(newTrustLimit);
        bank.changeConfig(config);
    }

    public void addUserAddress(UUID userId, Address address) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (address == null) {
            throw new NullPointerException("address"); // TODO : NPE
        }
        users.get(userId).AddAddress(address);
    }

    public void addUserPassport(UUID userId, Passport passport) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (passport == null) {
            throw new NullPointerException("passport"); // TODO : NPE
        }

        users.get(userId).AddPassport(passport);
    }

    public void addUserPhoneNumber(UUID userId, PhoneNumber phoneNumber) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber"); // TODO : NPE
        }

        users.get(userId).addPhoneNumber(phoneNumber);
    }

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

    public String getTransactionString(UUID id) {
        return transactions.get(id).toString();
    }

    private Bank getBank(String bankName) {
        if (!banks.containsKey(bankName)) {
            throw CentralBankException.incorrectBankName(bankName);
        }

        return banks.get(bankName);
    }
}
