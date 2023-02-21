package is.technologies.services;

import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.util.List;
import java.util.UUID;

public interface CentralBank {
    boolean AddBank(String name, Config config);

    UserCreator CreateUserData();

    UUID AddUser(UserData data);

    UUID OpenAccount(UUID userId, String bankName, AccountMode mode, Money money);

    UUID TransactMoney(UUID accountFromId, UUID accountToId, Money money);

    void TransactMoney(UUID accountId, Money money, MoneyActionMode mode);

    boolean RevertTransaction(UUID transactionId);

    AccountData GetAccountData(UUID accountId);

    List<AccountData> GetUserAccountsData(UUID userId);

    Config GetConfig(String bankName);

    void ChangeDebitPercent(String bankName, double percent);

    void ChangeDepositPercents(String bankName, Money money, double percent, ChangeDepositPercentMode mode);

    void ChangeDebitHighLimit(String bankName, Money limit);

    void ChangeDepositHighLimit(String bankName, Money limit);

    void ChangeCreditLowLimit(String bankName, Money limit);

    void ChangeCreditHighLimit(String bankName, Money limit);

    void ChangeCreditCommission(String bankName, Money commission);

    void ChangeDepositTime(String bankName, int days);

    void ChangeTrustLimit(String bankName, Money newTrustLimit);

    void AddUserAddress(UUID userId, Address address);

    void AddUserPassport(UUID userId, Passport passport);

    void AddUserPhoneNumber(UUID userId, PhoneNumber phoneNumber);

    UserData GetUserData(UUID userId);

    String getTransactionInfo(UUID id);
}