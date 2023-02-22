package is.technologies;

import is.technologies.enums.AccountMode;
import is.technologies.models.*;
import is.technologies.services.CentralBank;
import is.technologies.services.CentralBankImpl;

import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        CentralBank cb = CentralBankImpl.getInstance();

        Config config = new Config(
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
                new Money(11_000));

        String bankName = "SBER";
        cb.addBank(bankName, config);

        UserData data = cb.createUserData()
                .withPassport(new Passport("1234", "567890"))
                .withAddress(new Address("SPB", "Good Street", 5, 220))
                .withPhoneNumber(new PhoneNumber("+7(921)123-45-67"))
                .create("Alesha", "Popovich");
        UUID userId = cb.addUser(data);

        AccountMode mode = AccountMode.DEPOSIT;
        int firstMoneyValue = 50_000;
        UUID accountId = cb.openAccount(userId, bankName, mode, new Money(firstMoneyValue));

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

//        cb.ChangeCreditCommission(bankName, new Money(creditCommission));
//        cb.ChangeDebitPercent(bankName, debitPercent);
//        cb.ChangeDepositTime(bankName, days);
//        cb.ChangeCreditHighLimit(bankName, new Money(creditHighLimit));
//        cb.ChangeCreditLowLimit(bankName, new Money(creditLowLimit));
//        cb.ChangeDebitHighLimit(bankName, new Money(debitHighLimit));
//        cb.ChangeDepositHighLimit(bankName, new Money(depositHighLimit));
//        cb.ChangeDepositPercents(
//                bankName,
//                new Money(newDepositIntervalMoney),
//                newDepositIntervalPercent,
//                ChangeDepositPercentMode.ADD_INTERVAL);
//        cb.ChangeTrustLimit(bankName, new Money(newTrustLimit));

//        TreeMap<Money, Double> map = new TreeMap<>();
//        map.put(new Money(0), 0.01);
//        DepositPercents percents = new DepositPercents(map);
//
//        DepositPercents percents1 = percents.clone();
//        System.out.println(percents);
//        System.out.println(percents1);
//
//        map.put(new Money(0), map.get(new Money(0)) + 1.2);
//        percents.getData().put(new Money(123), 0.05);
//        System.out.println(percents);
//        System.out.println(percents1);

        LocalDateTime time = LocalDateTime.now();
        System.out.println(time);
    }
}