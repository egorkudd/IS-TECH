import is.technologies.exceptions.CentralBankException;
import is.technologies.exceptions.TransactionException;
import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
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
    private Config config;
    private final UUID userId;
    private final String bankName;
    private final Money trustLimit = new Money(11_000);

    public BankTests() {
        cb = CentralBankImpl.getInstance();

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
                trustLimit);

        bankName = "SBER";
        cb.addBank(bankName, config);

        UserData data = cb.createUserData()
                .withPassport(new Passport("1234", "567890"))
                .withAddress(new Address("SPB", "Good Street", 5, 220))
                .withPhoneNumber(new PhoneNumber("+7(921)123-45-67"))
                .create("Alesha", "Popovich");
        userId = cb.addUser(data);
    }

    @ParameterizedTest
    @EnumSource(AccountMode.class)
    public void putMoneyTest(AccountMode mode) {
        int moneyValue = 50_000;
        UUID accountId = cb.openAccount(userId, bankName, mode, new Money(moneyValue));
        assertEquals(new Money(moneyValue), cb.getAccountData(accountId).getMoney());

        int secondMoneyValue = 10_000;
        cb.transactMoney(accountId, new Money(secondMoneyValue), MoneyActionMode.PUT_MONEY);
        assertEquals(
                new Money(secondMoneyValue + moneyValue),
                cb.getAccountData(accountId).getMoney()
        );
    }

    @ParameterizedTest
    @EnumSource(AccountMode.class)
    public void takeMoneyTest(AccountMode mode) {
        int moneyValue = 50_000;
        UUID accountId = cb.openAccount(userId, bankName, mode, new Money(moneyValue));
        assertEquals(new Money(moneyValue), cb.getAccountData(accountId).getMoney());

        int firstTimeToRewind = 10;
        BankTimer.rewindTime(firstTimeToRewind);
        BankTimer.rewindTime(config.getDepositDays() - firstTimeToRewind);
        Money moneyValueAfterRewinding = cb.getAccountData(accountId).getMoney();

        int secondMoneyValue = 10_000;
        cb.transactMoney(accountId, new Money(secondMoneyValue), MoneyActionMode.TAKE_MONEY);
        assertEquals(
                moneyValueAfterRewinding.minus(new Money(secondMoneyValue)),
                cb.getAccountData(accountId).getMoney()
        );
    }

    @Test
    public void SpecialCreditTransactionsTest() {
        UUID accountId = cb.openAccount(userId, bankName, AccountMode.CREDIT, Money.ZERO);

        int moneyValue = 10_000;
        cb.transactMoney(accountId, new Money(moneyValue), MoneyActionMode.TAKE_MONEY);
        assertEquals(new Money(-moneyValue), cb.getAccountData(accountId).getMoney());

        UUID secondAccountId = cb.openAccount(userId, bankName, AccountMode.CREDIT, Money.ZERO);
        cb.transactMoney(accountId, secondAccountId, new Money(moneyValue));
        assertEquals(new Money(-2 * moneyValue), cb.getAccountData(accountId).getMoney());
        assertEquals(new Money(moneyValue), cb.getAccountData(secondAccountId).getMoney());
    }

    @ParameterizedTest
    @EnumSource(value = AccountMode.class, names = {"DEBIT", "CREDIT"})
    public void DebitAndCreditTransactTest(AccountMode mode) {
        int moneyValue = 50_000;
        UUID accountId = cb.openAccount(userId, bankName, mode, new Money(moneyValue));
        assertEquals(new Money(moneyValue), cb.getAccountData(accountId).getMoney());

        int transactMoneyValue = 15_000;
        cb.transactMoney(accountId, new Money(transactMoneyValue), MoneyActionMode.TAKE_MONEY);
        assertEquals(
                new Money(moneyValue - transactMoneyValue),
                cb.getAccountData(accountId).getMoney()
        );

        int secondMoneyValue = 10_000;
        int secondTransactMoneyValue = 5_000;
        UUID secondDebitAccount =
                cb.openAccount(userId, bankName, AccountMode.DEBIT, new Money(secondMoneyValue));
        cb.transactMoney(accountId, secondDebitAccount, new Money(secondTransactMoneyValue));
        assertEquals(
                new Money(moneyValue - transactMoneyValue - secondTransactMoneyValue),
                cb.getAccountData(accountId).getMoney()
        );
        assertEquals(
                new Money(secondMoneyValue + secondTransactMoneyValue),
                cb.getAccountData(secondDebitAccount).getMoney()
        );
    }

    @Test
    public void DepositTransactTest() {
        int moneyValue = 50_000;
        UUID accountId =
                cb.openAccount(userId, bankName, AccountMode.DEPOSIT, new Money(moneyValue));
        assertEquals(new Money(moneyValue), cb.getAccountData(accountId).getMoney());

        int transactMoneyValue = 5_000;
        assertThrows(
                TransactionException.class,
                () -> cb.transactMoney(accountId, new Money(transactMoneyValue), MoneyActionMode.TAKE_MONEY)
        );
        assertEquals(new Money(moneyValue), cb.getAccountData(accountId).getMoney());

        int secondMoneyValue = 10_000;
        UUID secondDebitAccount =
                cb.openAccount(userId, bankName, AccountMode.DEBIT, new Money(secondMoneyValue));
        assertThrows(
                TransactionException.class,
                () -> cb.transactMoney(accountId, secondDebitAccount, new Money(transactMoneyValue))
        );

        assertEquals(new Money(moneyValue), cb.getAccountData(accountId).getMoney());
        assertEquals(new Money(secondMoneyValue), cb.getAccountData(secondDebitAccount).getMoney());
    }

    @Test
    public void RevertTransactionTest() {
        UUID account1Id = cb.openAccount(userId, bankName, AccountMode.DEBIT, new Money(10_000));
        UUID account2Id = cb.openAccount(userId, bankName, AccountMode.DEBIT, new Money(20_000));
        UUID transactionId = cb.transactMoney(account1Id, account2Id, new Money(5_000));

        assertEquals(new Money(5_000), cb.getAccountData(account1Id).getMoney());
        assertEquals(new Money(25_000), cb.getAccountData(account2Id).getMoney());

        boolean transactionReverted = cb.revertTransaction(transactionId);
        assertTrue(transactionReverted);

        assertEquals(new Money(10_000), cb.getAccountData(account1Id).getMoney());
        assertEquals(new Money(20_000), cb.getAccountData(account2Id).getMoney());
    }

    @Test
    public void TransactionTrustExceptionTest() {
        UserData data = cb.createUserData().create("Ilusha", "Muromets");
        UUID userId = cb.addUser(data);

        int moneyValue = 50_000;
        UUID account1Id = cb.openAccount(userId, bankName, AccountMode.DEBIT, new Money(moneyValue));
        UUID account2Id = cb.openAccount(userId, bankName, AccountMode.CREDIT, new Money(moneyValue));

        int transactMoneyValue = config.getTrustLimit().getMoneyValue() + 10_000;
        assertThrows(
                CentralBankException.class,
                () -> cb.transactMoney(account1Id, account2Id, new Money(transactMoneyValue))
        );

        assertThrows(
                CentralBankException.class,
                () -> cb.transactMoney(account2Id, account2Id, new Money(transactMoneyValue))
        );

        cb.addUserAddress(userId, new Address("SPB", "Good Street", 5, 220));
        cb.addUserPassport(userId, new Passport("1234", "567890"));
        cb.addUserPhoneNumber(userId, new PhoneNumber("+7(921)123-45-67"));

        cb.transactMoney(account1Id, account2Id, new Money(transactMoneyValue));
        assertEquals(new Money(moneyValue - transactMoneyValue), cb.getAccountData(account1Id).getMoney());
        assertEquals(new Money(moneyValue + transactMoneyValue), cb.getAccountData(account2Id).getMoney());

        int secondTransactMoneyValue = config.getTrustLimit().getMoneyValue() + 5_000;
        cb.transactMoney(account2Id, account1Id, new Money(secondTransactMoneyValue));
        assertEquals(
                new Money(moneyValue - transactMoneyValue + secondTransactMoneyValue),
                cb.getAccountData(account1Id).getMoney()
        );
        assertEquals(
                new Money(moneyValue + transactMoneyValue - secondTransactMoneyValue),
                cb.getAccountData(account2Id).getMoney()
        );
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
        double newTrustLimit = 30_000;

        cb.changeCreditCommission(bankName, new Money(creditCommission));
        cb.changeDebitPercent(bankName, debitPercent);
        cb.changeDepositTime(bankName, days);
        cb.changeCreditHighLimit(bankName, new Money(creditHighLimit));
        cb.changeCreditLowLimit(bankName, new Money(creditLowLimit));
        cb.changeDebitHighLimit(bankName, new Money(debitHighLimit));
        cb.changeDepositHighLimit(bankName, new Money(depositHighLimit));
        cb.changeDepositPercents(
                bankName,
                new Money(newDepositIntervalMoney),
                newDepositIntervalPercent,
                ChangeDepositPercentMode.ADD_INTERVAL);

        cb.changeTrustLimit(bankName, new Money(newTrustLimit));

        config = cb.getConfig(bankName);
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

        cb.changeTrustLimit(bankName, trustLimit);
    }
}
