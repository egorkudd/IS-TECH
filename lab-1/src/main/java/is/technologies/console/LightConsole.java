package is.technologies.console;

import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.exceptions.ConsoleProgramException;
import is.technologies.models.*;
import is.technologies.services.CentralBank;
import is.technologies.services.CentralBankImpl;

import java.util.*;

public class LightConsole {
    private static CentralBank cb;
    private static final Scanner scanner = new Scanner(System.in);


    public static void run() {
        cb = new CentralBankImpl();
        System.out.printf("'%s' - possible commands%n", Commands.Help);

        boolean work = true;
        while (work) {
            String command = scanner.nextLine();
            switch (command) {
                case "":
                    continue;
                case Commands.Help:
                    printHelp();
                    break;
                case Commands.CreateBank:
                    createBank();
                    break;
                case Commands.CreateUser:
                    createUser();
                    break;
                case Commands.OpenAccount:
                    openAccount();
                    break;
                case Commands.PutMoney:
                    putOrTakeMoney(MoneyActionMode.PUT_MONEY);
                    break;
                case Commands.TakeMoney:
                    putOrTakeMoney(MoneyActionMode.TAKE_MONEY);
                    break;
                case Commands.TransactMoney:
                    transactMoney();
                    break;
                case Commands.GetAccountData:
                    getAccountData();
                    break;
                case Commands.GetUserAccountsData:
                    getUserAccountsData();
                    break;
                case Commands.GetConfig:
                    getConfig();
                    break;
                case Commands.ChangeDebitPercent:
                    changeDebitPercent();
                    break;
                case Commands.ChangeDepositPercents:
                    changeDepositPercents();
                    break;
                case Commands.ChangeDebitHighLimit:
                    changeDebitHighLimit();
                    break;
                case Commands.ChangeDepositHighLimit:
                    changeDepositHighLimit();
                    break;
                case Commands.ChangeCreditLowLimit:
                    changeCreditLowLimit();
                    break;
                case Commands.ChangeCreditHighLimit:
                    changeCreditHighLimit();
                    break;
                case Commands.ChangeCreditCommission:
                    changeCreditCommission();
                    break;
                case Commands.ChangeDepositTime:
                    changeDepositTime();
                    break;
                case Commands.ChangeTrustLimit:
                    changeTrustLimit();
                    break;
                case Commands.AddUserAddress:
                    addUserAddress();
                    break;
                case Commands.AddUserPassport:
                    addUserPassport();
                    break;
                case Commands.AddUserPhoneNumber:
                    addUserPhoneNumber();
                    break;
                case Commands.GetUserData:
                    getUserData();
                    break;
                case Commands.Quit:
                    work = false;
                    break;
                case Commands.QuickStart:
                    quickStart();
                    break;
                default:
                    System.out.println("Incorrect command. Try again");
                    break;
            }
        }
    }

    private static void printHelp() {
        System.out.printf("'%s' - create bank%n", Commands.CreateBank);
        System.out.printf("'%s' - create user%n", Commands.CreateUser);
        System.out.printf("'%s' - open account%n", Commands.OpenAccount);
        System.out.printf("'%s' - put money%n", Commands.PutMoney);
        System.out.printf("'%s' - take money%n", Commands.TakeMoney);
        System.out.printf("'%s' - transact money from one account to another%n", Commands.TransactMoney);
        System.out.printf("'%s' - get account data%n", Commands.GetAccountData);
        System.out.printf("'%s' - get data of all user's accounts%n", Commands.GetUserAccountsData);
        System.out.printf("'%s' - get config data%n", Commands.GetConfig);
        System.out.printf("'%s' - change debit percent%n", Commands.ChangeDebitPercent);
        System.out.printf("'%s' - change deposit percents%n", Commands.ChangeDepositPercents);
        System.out.printf("'%s' - change debit high limit%n", Commands.ChangeDebitHighLimit);
        System.out.printf("'%s' - change deposit high limit%n", Commands.ChangeDepositHighLimit);
        System.out.printf("'%s' - change credit low limit%n", Commands.ChangeCreditLowLimit);
        System.out.printf("'%s' - change credit high limit%n", Commands.ChangeCreditHighLimit);
        System.out.printf("'%s' - change credit commission%n", Commands.ChangeCreditCommission);
        System.out.printf("'%s' - change deposit time%n", Commands.ChangeDepositTime);
        System.out.printf("'%s' - change maximum limit for untrust users%n", Commands.ChangeTrustLimit);
        System.out.printf("'%s' - add address for user%n", Commands.AddUserAddress);
        System.out.printf("'%s' - add passport for user%n", Commands.AddUserPassport);
        System.out.printf("'%s' - add phone number for user%n", Commands.AddUserPhoneNumber);
        System.out.printf("'%s' - get user's name, surname, address, phone, passport%n", Commands.GetUserData);
        System.out.printf("'%s' - help to test program%n", Commands.QuickStart);
        System.out.printf("'%s' - exit from program%n", Commands.Quit);
    }

    private static void createBank() {
        System.out.println("Creating bank:");

        System.out.println("Creating config:");
        var config = new Config(
                getDebitPercent(),
                getDepositPercents(),
                getDebitHighLimit(),
                getDepositHighLimit(),
                getCreditLowLimit(),
                getCreditHighLimit(),
                getCreditCommission(),
                getDepositTime(),
                getTrustLimit());
        System.out.println("Config was created");

        String bankName = getBankName();
        boolean bankCreated = cb.AddBank(bankName, config);
        if (!bankCreated) throw ConsoleProgramException.IncorrectBankName(bankName);
        System.out.printf("Bank (%s) was created%n", bankName);
    }

    private static double getDebitPercent() {
        System.out.println("Enter debit percent:");
        String input = scanner.nextLine();
        return Double.parseDouble(input);
    }

    private static DepositPercents getDepositPercents() {
        System.out.println("Creating deposit percents:");
        System.out.println("Enter intervals\nFor example: [0 0,01], [10000 0,03], [20000 0,45]");
        String strIntervals = scanner.nextLine();
        if (strIntervals.isBlank()) {
            throw new NullPointerException("strIntervals"); // TODO : NPE
        }
        String[] intervals = strIntervals.split(", ");
        TreeMap<Money, Double> dictPercents = new TreeMap<>();
        for(String interval : intervals)
        {
            String normalInterval = interval.substring(1, interval.length() - 2);
            String[] intervalData = normalInterval.split(" ");
            var money = new Money(Double.parseDouble(intervalData[0]));
            double percent = Double.parseDouble(intervalData[1]);
            dictPercents.put(money, percent);
        }

        var depositPercents = new DepositPercents(dictPercents);
        System.out.println("Deposit percents were created.");
        return depositPercents;
    }

    private static Money getDebitHighLimit() {
        System.out.println("Enter debit high limit:");
        String input = scanner.nextLine();
        return new Money(Double.parseDouble(input));
    }

    private static Money getDepositHighLimit() {
        System.out.println("Enter deposit high limit:");
        String input = scanner.nextLine();
        return new Money(Double.parseDouble(input));
    }

    private static Money getCreditLowLimit() {
        System.out.println("Enter credit low limit:");
        String input = scanner.nextLine();
        return new Money(Double.parseDouble(input));
    }

    private static Money getCreditHighLimit() {
        System.out.println("Enter credit high limit:");
        String input = scanner.nextLine();
        return new Money(Double.parseDouble(input));
    }

    private static Money getCreditCommission() {
        System.out.println("Enter credit commission:");
        String input = scanner.nextLine();
        return new Money(Double.parseDouble(input));
    }

    private static int getDepositTime() {
        System.out.println("Enter count days for deposit time (days):");
        String input = scanner.nextLine();
        return Integer.parseInt(input);
    }

    private static Money getTrustLimit() {
        System.out.println("Enter max trust limit:");
        String input = scanner.nextLine();
        return new Money(Double.parseDouble(input));
    }

    private static void createUser() {
        System.out.println("Creating user:");
        String name = getUserName();
        String surname = getUserSurname();
        UserData userData = cb.CreateUserData()
                .withPassport(getPassport())
                .withAddress(getAddress())
                .withPhoneNumber(getPhoneNumber())
                .create(name, surname);
        UUID userId = cb.AddUser(userData);
        System.out.printf("User with id (%s) was created%n", userId);
    }

    private static String getUserName() {
        System.out.println("Enter name:");
        String name = scanner.nextLine();
        if (name.isBlank()) {
            throw new NullPointerException("name"); // TODO : NPE
        }
        return name;
    }

    private static String getUserSurname() {
        System.out.println("Enter surname:");
        String surname = scanner.nextLine();
        if (surname.isBlank()) {
            throw new NullPointerException("surname"); // TODO : NPE
        }

        return surname;
    }

    private static Passport getPassport() {
        System.out.println("Enter passport series and number (xxxx-xxxxxx), if you don't want - enter 'no':");
        String passport = scanner.nextLine();
        if (passport.isBlank()) {
            throw new NullPointerException("passport"); // TODO : NPE
        }
        if (passport.equals("no")) {
            return null;
        }

        String[] passportData = passport.split("-");
        if (passportData.length != 2) {
            throw ConsoleProgramException.IncorrectAddress(passport);
        }

        return new Passport(passportData[0], passportData[1]);
    }

    private static Address getAddress() {
        System.out.println("Enter Address (town; street; house; flat), if you don't want - enter 'no':");
        String address = scanner.nextLine();
        if (address.isBlank()) {
            throw new NullPointerException("address"); // TODO : NPE
        }

        if (address.equals("no")) {
            return null;
        }

        String[] addressData = address.split("; ");
        if (addressData.length != 4) {
            throw ConsoleProgramException.IncorrectAddress(address);
        }

        return new Address(
                addressData[0],
                addressData[1],
                Integer.parseInt(addressData[2]),
                Integer.parseInt(addressData[3])
        );
    }

    private static PhoneNumber getPhoneNumber() {
        System.out.println("Enter PhoneNumber, if you don't want - enter 'no':");
        String phoneNumber = scanner.nextLine();
        if (phoneNumber.isBlank()) {
            throw new NullPointerException("phoneNumber"); // TODO : NPE
        }

        if (phoneNumber.equals("no")) {
            return null;
        }

        return new PhoneNumber(phoneNumber);
    }

    private static void openAccount() {
        System.out.println("Creating account:");
        AccountMode accountMode = getAccountMode();
        UUID userId = getUserId();
        String bankName = getBankName();
        Money money = getMoney();
        UUID accountId = cb.OpenAccount(userId, bankName, accountMode, money);
        System.out.printf("Account (%s) was created%n", accountId);
    }

    private static AccountMode getAccountMode() {
        System.out.println("Enter account type (Credit / Debit / Deposit):");
        String strAccountMode = scanner.nextLine();
        if (strAccountMode.isBlank()) {
            throw new NullPointerException(strAccountMode); // TODO : NPE
        }

        return switch (strAccountMode) {
            case "Credit" -> AccountMode.CREDIT;
            case "Debit" -> AccountMode.DEBIT;
            case "Deposit" -> AccountMode.DEPOSIT;
            default -> throw ConsoleProgramException.incorrectInput(strAccountMode);
        };
    }

    private static UUID getUserId() {
        System.out.println("Enter user's id:");
        String strUserId = scanner.nextLine();
        if (strUserId.isBlank()) {
            throw new NullPointerException("strUserId"); // TODO : NPE
        }
        return UUID.fromString(strUserId);
    }

    private static String getBankName() {
        System.out.println("Enter bank's name:");
        String bankName = scanner.nextLine();
        if (bankName.isBlank()) {
            throw new NullPointerException(bankName); // TODO : NPE
        }
        return bankName;
    }

    private static Money getMoney() {
        System.out.println("Enter money:");
        String strMoney = scanner.nextLine();
        if (strMoney.isBlank()) {
            throw new NullPointerException("strMoney"); // TODO : NPE
        }

        return new Money(Double.parseDouble(strMoney));
    }

    private static void putOrTakeMoney(MoneyActionMode mode) {
        System.out.println("Enter account's id and money");
        UUID accountId = getAccountId();
        Money money = getMoney();
        cb.TransactMoney(accountId, money, mode);
        System.out.println("Done");
    }

    private static UUID getAccountId() {
        System.out.println("Enter account's id:");
        String strAccountId = scanner.nextLine();
        if (strAccountId.isBlank()) {
            throw new NullPointerException("strAccountId"); // TODO : NPE
        }

        return UUID.fromString(strAccountId);
    }

    private static void transactMoney() {
        System.out.println("Enter sender account's id, getter account's id and money");
        UUID accountFromId = getAccountId();
        UUID accountToId = getAccountId();
        Money money = getMoney();
        cb.TransactMoney(accountFromId, accountToId, money);
        System.out.println("Done");
    }

    private static void getAccountData() {
        UUID accountId = getAccountId();
        AccountData data = cb.GetAccountData(accountId);
        System.out.println(data);
    }

    private static void getUserAccountsData() {
        UUID userId = getUserId();
        List<AccountData> data = cb.GetUserAccountsData(userId);
        for (AccountData accountData : data) {
            System.out.println(accountData);
        }
    }

    private static void getConfig() {
        String bankName = getBankName();
        System.out.println(cb.GetConfig(bankName));
    }

    private static void changeDebitPercent() {
        cb.ChangeDebitPercent(getBankName(), getDebitPercent());
        System.out.println("Done");
    }

    private static void changeDepositPercents() {
        ChangeDepositPercentMode mode = getChangeDepositPercentMode();
        cb.ChangeDepositPercents(getBankName(), getMoney(), getDepositPercent(), mode);
        System.out.println("Done");
    }

    private static ChangeDepositPercentMode getChangeDepositPercentMode() {
        System.out.println("Enter 'Add interval' or 'Delete interval'");
        String mode = scanner.nextLine();
        if (mode.isBlank())
            throw new NullPointerException("mode"); // TODO : NPE

        return switch (mode) {
            case "Add interval" -> ChangeDepositPercentMode.ADD_INTERVAL;
            case "Delete interval" -> ChangeDepositPercentMode.REMOVE_INTERVAL;
            default -> throw ConsoleProgramException.incorrectInput(mode);
        };
    }

    private static double getDepositPercent() {
        System.out.println("Enter deposit percent:");
        return scanner.nextDouble();
    }

    private static void changeDebitHighLimit() {
        cb.ChangeDebitHighLimit(getBankName(), getDebitHighLimit());
        System.out.println("Done");
    }

    private static void changeDepositHighLimit() {
        cb.ChangeDepositHighLimit(getBankName(), getDepositHighLimit());
        System.out.println("Done");
    }

    private static void changeCreditLowLimit() {
        cb.ChangeCreditLowLimit(getBankName(), getCreditLowLimit());
        System.out.println("Done");
    }

    private static void changeCreditHighLimit() {
        cb.ChangeCreditHighLimit(getBankName(), getCreditHighLimit());
        System.out.println("Done");
    }

    private static void changeCreditCommission() {
        cb.ChangeCreditCommission(getBankName(), getCreditCommission());
        System.out.println("Done");
    }

    private static void changeDepositTime() {
        cb.ChangeDepositTime(getBankName(), getDepositTime());
        System.out.println("Done");
    }

    private static void changeTrustLimit() {
        cb.ChangeTrustLimit(getBankName(), getTrustLimit());
        System.out.println("Done");
    }

    private static void addUserAddress() {
        cb.AddUserAddress(getUserId(), getAddress());
        System.out.println("Done");
    }

    private static void addUserPassport() {
        cb.AddUserPassport(getUserId(), getPassport());
        System.out.println("Done");
    }

    private static void addUserPhoneNumber() {
        cb.AddUserPhoneNumber(getUserId(), getPhoneNumber());
        System.out.println("Done");
    }

    private static void getUserData() {
        System.out.println(cb.GetUserData(getUserId()));
    }

    private static void quickStart() {
        var config = new Config(
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

        String bankName = "SBER";
        cb.AddBank(bankName, config);

        String userName = "Alesha";
        String userSurname = "Popovich";
        UserData data = cb.CreateUserData()
                .withPassport(new Passport("1234", "567890"))
                .withAddress(new Address("SPB", "Good Street", 5, 220))
                .withPhoneNumber(new PhoneNumber("+7(921)123-45-67"))
                .create(userName, userSurname);
        UUID userId = cb.AddUser(data);

        UUID debitId = cb.OpenAccount(userId, bankName, AccountMode.DEBIT, new Money(10_000));
        UUID creditId = cb.OpenAccount(userId, bankName, AccountMode.CREDIT, new Money(20_000));
        UUID depositId = cb.OpenAccount(userId, bankName, AccountMode.DEPOSIT, new Money(30_000));

        System.out.printf("UserId: %s%n", userId);
        System.out.printf("BankName: %s%n", bankName);
        System.out.printf("DebitId: %s%n", debitId);
        System.out.printf("CreditId: %s%n", creditId);
        System.out.printf("DepositId: %s%n", depositId);
    }
}