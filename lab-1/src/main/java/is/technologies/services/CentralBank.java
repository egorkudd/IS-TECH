package is.technologies.services;

import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.util.List;
import java.util.UUID;

public interface CentralBank {
    boolean addBank(String name, Config config);

    UserCreator createUserData();

    UUID addUser(UserData data);

    UUID openAccount(UUID userId, String bankName, AccountMode mode, Money money);

    UUID transactMoney(UUID accountFromId, UUID accountToId, Money money);

    void transactMoney(UUID accountId, Money money, MoneyActionMode mode);

    boolean revertTransaction(UUID transactionId);

    AccountData getAccountData(UUID accountId);

    List<AccountData> getUserAccountsData(UUID userId);

    Config getConfig(String bankName);

    void changeDebitPercent(String bankName, double percent);

    void changeDepositPercents(String bankName, Money money, double percent, ChangeDepositPercentMode mode);

    void changeDebitHighLimit(String bankName, Money limit);

    void changeDepositHighLimit(String bankName, Money limit);

    void changeCreditLowLimit(String bankName, Money limit);

    void changeCreditHighLimit(String bankName, Money limit);

    void changeCreditCommission(String bankName, Money commission);

    void changeDepositTime(String bankName, int days);

    void changeTrustLimit(String bankName, Money newTrustLimit);

    void addUserAddress(UUID userId, Address address);

    void addUserPassport(UUID userId, Passport passport);

    void addUserPhoneNumber(UUID userId, PhoneNumber phoneNumber);

    UserData getUserData(UUID userId);

    String getTransactionString(UUID id);
}