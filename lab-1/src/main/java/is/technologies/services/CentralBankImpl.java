package is.technologies.services;

import is.technologies.exceptions.*;
import is.technologies.entities.*;
import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.enums.TransactionMode;
import is.technologies.models.*;

import java.util.*;

public class CentralBankImpl implements CentralBank {
    private final HashMap<String, Bank> banks;
    private final HashMap<UUID, User> users;
    private final HashMap<UUID, Transaction> transactions;
    private final HashMap<UUID, AbstractAccount> accounts;

    public CentralBankImpl() {
        banks = new HashMap<>();
        users = new HashMap<>();
        transactions = new HashMap<>();
        accounts = new HashMap<>();
    }

    public boolean AddBank(String name, Config config) {
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

    public UserCreator CreateUserData() {
        return new UserCreator();
    }

    public UUID AddUser(UserData data) {
        if (data == null) {
            throw new NullPointerException("data"); // TODO : NPE
        }

        var id = UUID.randomUUID();
        var user = new User(id, data);
        users.put(id, user);
        return id;
    }

    public UUID OpenAccount(UUID userId, String bankName, AccountMode mode, Money money) {
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
                    new BankTime(BankTimer.getTime().getTime().plusDays(365)),
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


//            bank.Notify += account.ChangeConfig;
        // TODO : add event
        accounts.put(id, account);
        users.get(userId).AddAccount(account);
        return id;
    }

    public void TransactMoney(UUID accountId, Money money, MoneyActionMode mode) {
        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        if (!accounts.containsKey(accountId)) {
            throw CentralBankException.incorrectAccountId(accountId);
        }

        boolean isNotTrustedUser = !users.get(accounts.get(accountId).getUserId()).IsTrusted();
        boolean isMoreThanLimit = money.compareTo(accounts.get(accountId).getTrustLimit()) > 0;
        if (mode == MoneyActionMode.TAKE_MONEY && isNotTrustedUser && isMoreThanLimit) {
            throw CentralBankException.noTrust(accounts.get(accountId).getUserId());
        }

        if (accounts.containsKey(accountId)) {
            if (!accounts.get(accountId).makeTransaction(money, mode)) {
                throw TransactionException.unrealDepositTransaction();
            }
        }
    }

    public UUID TransactMoney(UUID accountFromId, UUID accountToId, Money money) {
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

        boolean isNotTrustedUser = !users.get(accounts.get(accountFromId).getUserId()).IsTrusted();
        boolean isMoreThanLimit = money.compareTo(accounts.get(accountFromId).getTrustLimit()) > 0;
        if (isNotTrustedUser && isMoreThanLimit) {
            throw CentralBankException.noTrust(accounts.get(accountFromId).getUserId());
        }

        boolean transactionDone = makeTransaction(accountFromId, accountToId, money);
        var id = UUID.randomUUID();
        Transaction transaction = transactionDone
                ? new Transaction(id, accountFromId, accountToId, money, TransactionMode.EXECUTED)
                : new Transaction(id, accountFromId, accountToId, money, TransactionMode.DENIED);

        transactions.put(id, transaction);
        return id;
    }

    public boolean RevertTransaction(UUID transactionId) {
        if (!transactions.containsKey(transactionId)) {
            return false;
        }

        boolean transactionReverted = false;
        Transaction transaction = transactions.get(transactionId);
        if (transaction.getMode() == TransactionMode.EXECUTED) {
            transactionReverted = makeTransaction(
                    transaction.getAccountToId(),
                    transaction.getAccountFromId(),
                    transaction.getMoney()
            );
        }

        if (transactionReverted) transaction.revertTransaction();
        return transactionReverted;
    }

    public AccountData GetAccountData(UUID accountId) {
        if (!accounts.containsKey(accountId)) {
            throw CentralBankException.incorrectAccountId(accountId);
        }

        return accounts.get(accountId).getAccountData();
    }

    public List<AccountData> GetUserAccountsData(UUID userId) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        return users.get(userId).GetAccountsData();
    }

    public Config GetConfig(String bankName) {
        Bank bank = GetBank(bankName);
        return bank.getConfig();
    }

    public void ChangeDebitPercent(String bankName, double percent) {
        if (percent <= 0) {
            throw ConfigException.incorrectPercent(percent);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setDebitPercent(percent);
        bank.changeConfig(config);
    }

    public void ChangeDepositPercents(
            String bankName, Money money, double percent, ChangeDepositPercentMode mode) {
        if (money.compareTo(Money.ZERO) < 1) {
            throw MoneyException.incorrectMoneyCount(money);
        }

        if (percent <= 0) {
            throw DepositPercentException.incorrectPercent(percent);
        }

        Bank bank = GetBank(bankName);
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

    public void ChangeDebitHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setDebitHighLimit(limit);
        bank.changeConfig(config);
    }

    public void ChangeDepositHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setDepositHighLimit(limit);
        bank.changeConfig(config);
    }

    public void ChangeCreditLowLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) > 0) {
            throw ConfigException.incorrectLowLimit(limit);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setCreditLowLimit(limit);
        bank.changeConfig(config);
    }

    public void ChangeCreditHighLimit(String bankName, Money limit) {
        if (limit.compareTo(Money.ZERO) < 1) {
            throw ConfigException.incorrectHighLimit(limit);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setCreditHighLimit(limit);
        bank.changeConfig(config);
    }

    public void ChangeCreditCommission(String bankName, Money commission) {
        if (commission.compareTo(Money.ZERO) < 0) {
            throw ConfigException.incorrectCommission(commission);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setCreditCommission(commission);
        bank.changeConfig(config);
    }

    public void ChangeDepositTime(String bankName, int days) {
        if (days < 365) {
            throw ConfigException.tooShortDepositLimit(days);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setDepositDays(days);
        bank.changeConfig(config);
    }

    public void ChangeTrustLimit(String bankName, Money newTrustLimit) {
        if (newTrustLimit.compareTo(Money.ZERO) < 0) {
            throw ConfigException.incorrectCommission(newTrustLimit);
        }

        Bank bank = GetBank(bankName);
        Config config = bank.getConfig();
        config.setTrustLimit(newTrustLimit);
        bank.changeConfig(config);
    }

    public void AddUserAddress(UUID userId, Address address) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (address == null) {
            throw new NullPointerException("address"); // TODO : NPE
        }
        users.get(userId).AddAddress(address);
    }

    public void AddUserPassport(UUID userId, Passport passport) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (passport == null) {
            throw new NullPointerException("passport"); // TODO : NPE
        }

        users.get(userId).AddPassport(passport);
    }

    public void AddUserPhoneNumber(UUID userId, PhoneNumber phoneNumber) {
        if (!users.containsKey(userId)) {
            throw CentralBankException.incorrectUserId(userId);
        }

        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber"); // TODO : NPE
        }

        users.get(userId).AddPhoneNumber(phoneNumber);
    }

    public UserData GetUserData(UUID userId) {
        User user = users.get(userId);
        return new UserData(
                user.getName(),
                user.getSurname(),
                user.getPassport(),
                user.getAddress(),
                user.getPhoneNumber()
        );
    }

    public String getTransactionInfo(UUID id) {
        return transactions.get(id).toString();
    }

    private boolean makeTransaction(UUID accountFromId, UUID accountToId, Money money) {
        boolean moneyTook = accounts.get(accountFromId)
                .makeTransaction(money, MoneyActionMode.TAKE_MONEY);
        if (!moneyTook) {
            return false;
        }

        boolean moneyPut = accounts.get(accountToId)
                .makeTransaction(money, MoneyActionMode.PUT_MONEY);
        if (moneyPut) {
            return true;
        }

        accounts.get(accountFromId).makeTransaction(money, MoneyActionMode.PUT_MONEY);

        return false;
    }

    private Bank GetBank(String bankName) {
        if (bankName.isBlank()) {
            throw new NullPointerException("bankName"); // TODO : NPE
        }

        if (!banks.containsKey(bankName)) {
            throw CentralBankException.incorrectBankName(bankName);
        }

        return banks.get(bankName);
    }
}
