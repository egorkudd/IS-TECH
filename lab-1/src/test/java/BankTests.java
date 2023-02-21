import is.technologies.exceptions.CentralBankException;
import is.technologies.exceptions.TransactionException;
import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.enums.TransactionMode;
import is.technologies.models.*;
import is.technologies.services.CentralBank;
import is.technologies.services.CentralBankImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BankTests {
    private final CentralBank cb;
    private final Config config;
    private final UUID userId;
    private final String bankName;

    public BankTests() {
        cb = new CentralBankImpl();

        config = new Config(
                0.03,
                new DepositPercents(
                        new TreeMap<>() {{
                            put(Money.ZERO, 0.04);
                            put(new Money(50_000), 0.05);
                            put(new Money(150_000), 0.06);
                            put(new Money(300_000), 0.075);
                        }}),
                new Money(500_000),
                new Money(10_000_000),
                new Money(-50_000),
                new Money(150_000),
                new Money(200),
                365,
                new Money(20_000));

        bankName = "SBER";
        cb.AddBank(bankName, config);

        UserData data = cb.CreateUserData()
                .withPassport(new Passport("1234", "567890"))
                .withAddress(new Address("SPB", "Good Street", 5, 220))
                .withPhoneNumber(new PhoneNumber("+7(921)123-45-67"))
                .create("Alesha", "Popovich");
        userId = cb.AddUser(data);
    }

    @ParameterizedTest
    @EnumSource(AccountMode.class)
    public void putMoneyTest(AccountMode mode) {
        int firstMoneyValue = 50_000;
        UUID accountId = cb.OpenAccount(userId, bankName, mode, new Money(firstMoneyValue));
        assertEquals(new Money(firstMoneyValue), cb.GetAccountData(accountId).getMoney());

        int secondMoneyValue = 10_000;
        cb.TransactMoney(accountId, new Money(secondMoneyValue), MoneyActionMode.PUT_MONEY);
        assertEquals(new Money(firstMoneyValue + secondMoneyValue), cb.GetAccountData(accountId).getMoney());
    }

    @Test
    public void SpecialCreditTransactionsTest() {
        UUID accountId = cb.OpenAccount(userId, bankName, AccountMode.CREDIT, Money.ZERO);

        int moneyValue = 10_000;
        cb.TransactMoney(accountId, new Money(moneyValue), MoneyActionMode.TAKE_MONEY);
        assertEquals(new Money(-moneyValue), cb.GetAccountData(accountId).getMoney());

        UUID secondAccountId = cb.OpenAccount(userId, bankName, AccountMode.CREDIT, Money.ZERO);
        cb.TransactMoney(accountId, secondAccountId, new Money(moneyValue));
        assertEquals(new Money(-2 * moneyValue), cb.GetAccountData(accountId).getMoney());
        assertEquals(new Money(moneyValue), cb.GetAccountData(secondAccountId).getMoney());
    }

    @ParameterizedTest
    @EnumSource(value = AccountMode.class, names = {"DEBIT", "CREDIT"})
    public void DebitAndCreditTransactTest(AccountMode mode) {
        int moneyValue = 50_000;
        UUID accountId = cb.OpenAccount(userId, bankName, mode, new Money(moneyValue));
        assertEquals(new Money(moneyValue), cb.GetAccountData(accountId).getMoney());

        int transactMoneyValue = 15_000;
        cb.TransactMoney(accountId, new Money(transactMoneyValue), MoneyActionMode.TAKE_MONEY);
        assertEquals(
                new Money(moneyValue - transactMoneyValue),
                cb.GetAccountData(accountId).getMoney()
        );

        int secondMoneyValue = 10_000;
        int secondTransactMoneyValue = 5_000;
        UUID secondDebitAccount =
                cb.OpenAccount(userId, bankName, AccountMode.DEBIT, new Money(secondMoneyValue));
        cb.TransactMoney(accountId, secondDebitAccount, new Money(secondTransactMoneyValue));
        assertEquals(
                new Money(moneyValue - transactMoneyValue - secondTransactMoneyValue),
                cb.GetAccountData(accountId).getMoney()
        );
        assertEquals(
                new Money(secondMoneyValue + secondTransactMoneyValue),
                cb.GetAccountData(secondDebitAccount).getMoney()
        );
    }

    @Test
    public void DepositTransactTest() {
        int moneyValue = 50_000;
        UUID accountId =
                cb.OpenAccount(userId, bankName, AccountMode.DEPOSIT, new Money(moneyValue));
        assertEquals(new Money(moneyValue), cb.GetAccountData(accountId).getMoney());

        int transactMoneyValue = 5_000;
        assertThrows(
                TransactionException.class,
                () -> cb.TransactMoney(accountId, new Money(transactMoneyValue), MoneyActionMode.TAKE_MONEY)
        );
        assertEquals(new Money(moneyValue), cb.GetAccountData(accountId).getMoney());

        int secondMoneyValue = 10_000;
        UUID secondDebitAccount =
                cb.OpenAccount(userId, bankName, AccountMode.DEBIT, new Money(secondMoneyValue));
        UUID transactionId =
                cb.TransactMoney(accountId, secondDebitAccount, new Money(transactMoneyValue));
        assertEquals(
                cb.getTransactionInfo(transactionId).split(" : ")[1],
                TransactionMode.DENIED.toString()
        );
        assertEquals(new Money(moneyValue), cb.GetAccountData(accountId).getMoney());
        assertEquals(new Money(secondMoneyValue), cb.GetAccountData(secondDebitAccount).getMoney());
    }

    @Test
    public void ChangeConfigTest() {
        double debitPercent = 1.1;
        double debitHighLimit = 2222222;
        double depositHighLimit = 3333333;
        double creditLowLimit = -44444;
        double creditHighLimit = 555555;
        double creditCommission = 150;
        int days = 777;
        double newDepositIntervalMoney = 400_000;
        double newDepositIntervalPercent = 0.55;
        double newTrustLimit = 30_010;

        cb.ChangeCreditCommission(bankName, new Money(creditCommission));
        cb.ChangeDebitPercent(bankName, debitPercent);
        cb.ChangeDepositTime(bankName, days);
        cb.ChangeCreditHighLimit(bankName, new Money(creditHighLimit));
        cb.ChangeCreditLowLimit(bankName, new Money(creditLowLimit));
        cb.ChangeDebitHighLimit(bankName, new Money(debitHighLimit));
        cb.ChangeDepositHighLimit(bankName, new Money(depositHighLimit));
        cb.ChangeDepositPercents(
                bankName,
                new Money(newDepositIntervalMoney),
                newDepositIntervalPercent,
                ChangeDepositPercentMode.ADD_INTERVAL);
        cb.ChangeTrustLimit(bankName, new Money(newTrustLimit));

        Config config = cb.GetConfig(bankName);
        assertEquals(new Money(creditCommission), config.getCreditCommission());
        assertEquals(debitPercent, config.getDebitPercent());
        assertEquals(days, config.getDepositDays());
        assertEquals(new Money(creditHighLimit), config.getCreditHighLimit());
        assertEquals(new Money(creditLowLimit), config.getCreditLowLimit());
        assertEquals(new Money(debitHighLimit), config.getDebitHighLimit());
        assertEquals(new Money(depositHighLimit), config.getDepositHighLimit());
        assertEquals(
                new HashMap<Money, Double>() {{
                    put(Money.ZERO, 0.04);
                    put(new Money(50_000), 0.05);
                    put(new Money(150_000), 0.06);
                    put(new Money(300_000), 0.075);
                    put(new Money(newDepositIntervalMoney), newDepositIntervalPercent);
                }},
                config.getDepositPercents().getData()
        );
        assertEquals(new Money(newTrustLimit), config.getTrustLimit());
    }

    @Test
    public void RevertTransactionTest() {
        UUID account1Id = cb.OpenAccount(userId, bankName, AccountMode.DEBIT, new Money(10_000));
        UUID account2Id = cb.OpenAccount(userId, bankName, AccountMode.DEBIT, new Money(20_000));
        UUID transactionId = cb.TransactMoney(account1Id, account2Id, new Money(5_000));

        assertEquals(new Money(5_000), cb.GetAccountData(account1Id).getMoney());
        assertEquals(new Money(25_000), cb.GetAccountData(account2Id).getMoney());

        boolean transactionReverted = cb.RevertTransaction(transactionId);
        assertTrue(transactionReverted);

        assertEquals(new Money(10_000), cb.GetAccountData(account1Id).getMoney());
        assertEquals(new Money(20_000), cb.GetAccountData(account2Id).getMoney());
    }

    @Test
    public void TransactionTrustExceptionTest() {
        UserData data = cb.CreateUserData().create("Ilusha", "Muromets");
        UUID userId = cb.AddUser(data);

        int moneyValue = 50_000;
        UUID account1Id = cb.OpenAccount(userId, bankName, AccountMode.DEBIT, new Money(moneyValue));
        UUID account2Id = cb.OpenAccount(userId, bankName, AccountMode.CREDIT, new Money(moneyValue));

        int transactMoneyValue = config.getTrustLimit().getMoneyValue() + 10_000;
        assertThrows(
                CentralBankException.class,
                () -> cb.TransactMoney(account1Id, account2Id, new Money(transactMoneyValue))
        );

        assertThrows(
                CentralBankException.class,
                () -> cb.TransactMoney(account2Id, account2Id, new Money(transactMoneyValue))
        );

        cb.AddUserAddress(userId, new Address("SPB", "Good Street", 5, 220));
        cb.AddUserPassport(userId, new Passport("1234", "567890"));
        cb.AddUserPhoneNumber(userId, new PhoneNumber("+7(921)123-45-67"));

        cb.TransactMoney(account1Id, account2Id, new Money(transactMoneyValue));
        assertEquals(new Money(moneyValue - transactMoneyValue), cb.GetAccountData(account1Id).getMoney());
        assertEquals(new Money(moneyValue + transactMoneyValue), cb.GetAccountData(account2Id).getMoney());

        int secondTransactMoneyValue = config.getTrustLimit().getMoneyValue() + 5_000;
        cb.TransactMoney(account2Id, account1Id, new Money(secondTransactMoneyValue));
        assertEquals(
                new Money(moneyValue - transactMoneyValue + secondTransactMoneyValue),
                cb.GetAccountData(account1Id).getMoney()
        );
        assertEquals(
                new Money(moneyValue + transactMoneyValue - secondTransactMoneyValue),
                cb.GetAccountData(account2Id).getMoney()
        );
    }
}
