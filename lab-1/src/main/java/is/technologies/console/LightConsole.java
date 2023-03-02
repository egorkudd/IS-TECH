package is.technologies.console;

import is.technologies.enums.AccountMode;
import is.technologies.enums.ChangeDepositPercentMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.exceptions.ConsoleProgramException;
import is.technologies.models.*;
import is.technologies.services.CentralBank;
import is.technologies.services.CentralBankImpl;

import java.util.*;

import static is.technologies.console.Commands.*;

public class LightConsole {
    private static CentralBank cb;
    private static final Scanner scanner = new Scanner(System.in);


    public static void run() {
        cb = CentralBankImpl.getInstance();
        System.out.printf("'%s' - possible commands%n", HELP.getName());

        boolean work = true;
        while (work) {
            try {
                String commandString = scanner.nextLine();
                if (commandString.equals("")) {
                    continue;
                }

                Commands command = valueOf(
                        commandString
                                .substring(1)
                                .replace("-", "_")
                                .toUpperCase()
                );

                switch (command) {
                    case HELP -> printHelp();
                    case CREATE_BANK -> createBank();
                    case CREATE_USER -> createUser();
                    case OPEN_ACCOUNT -> openAccount();
                    case PUT_MONEY -> putOrTakeMoney(MoneyActionMode.PUT_MONEY);
                    case TAKE_MONEY -> putOrTakeMoney(MoneyActionMode.TAKE_MONEY);
                    case TRANSACT_MONEY -> transactMoney();
                    case ACCOUNT_DATA -> getAccountData();
                    case USER_ACCOUNTS_DATA -> getUserAccountsData();
                    case GET_CONFIG -> getConfig();
                    case CHANGE_DEBIT_PERCENT -> changeDebitPercent();
                    case CHANGE_DEPOSIT_PERCENTS -> changeDepositPercents();
                    case CHANGE_DEBIT_HIGH_LIMIT -> changeDebitHighLimit();
                    case CHANGE_DEPOSIT_HIGH_LIMIT -> changeDepositHighLimit();
                    case CHANGE_CREDIT_LOW_LIMIT -> changeCreditLowLimit();
                    case CHANGE_CREDIT_HIGH_LIMIT -> changeCreditHighLimit();
                    case CHANGE_CREDIT_COMMISSION -> changeCreditCommission();
                    case CHANGE_DEPOSIT_TIME -> changeDepositTime();
                    case CHANGE_TRUST_LIMIT -> changeTrustLimit();
                    case ADD_ADDRESS -> addUserAddress();
                    case ADD_PASSPORT -> addUserPassport();
                    case ADD_PHONE_NUMBER -> addUserPhoneNumber();
                    case GET_USER_DATA -> getUserData();
                    case QUIT -> work = false;
                    case QUICK_START -> quickStart();
                    default -> System.out.println("Incorrect command. Try again");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Incorrect command. Try again");
            } catch (RuntimeException e) {
                System.out.println("You have error: " + e.getMessage());
                System.out.println("Enter command again");
            }
        }
    }

    private static void printHelp() {
        System.out.printf("'%s' - create bank%n", CREATE_BANK.getName());
        System.out.printf("'%s' - create user%n", CREATE_USER.getName());
        System.out.printf("'%s' - open account%n", OPEN_ACCOUNT.getName());
        System.out.printf("'%s' - put money%n", PUT_MONEY.getName());
        System.out.printf("'%s' - take money%n", TAKE_MONEY.getName());
        System.out.printf("'%s' - transact money from one account to another%n", TRANSACT_MONEY.getName());
        System.out.printf("'%s' - get account data%n", ACCOUNT_DATA.getName());
        System.out.printf("'%s' - get data of all user's accounts%n", USER_ACCOUNTS_DATA.getName());
        System.out.printf("'%s' - get config data%n", GET_CONFIG.getName());
        System.out.printf("'%s' - change debit percent%n", CHANGE_DEBIT_PERCENT.getName());
        System.out.printf("'%s' - change deposit percents%n", CHANGE_DEPOSIT_PERCENTS.getName());
        System.out.printf("'%s' - change debit high limit%n", CHANGE_DEBIT_HIGH_LIMIT.getName());
        System.out.printf("'%s' - change deposit high limit%n", CHANGE_DEPOSIT_HIGH_LIMIT.getName());
        System.out.printf("'%s' - change credit low limit%n", CHANGE_CREDIT_LOW_LIMIT.getName());
        System.out.printf("'%s' - change credit high limit%n", CHANGE_CREDIT_HIGH_LIMIT.getName());
        System.out.printf("'%s' - change credit commission%n", CHANGE_CREDIT_COMMISSION.getName());
        System.out.printf("'%s' - change deposit time%n", CHANGE_DEPOSIT_TIME.getName());
        System.out.printf("'%s' - change maximum limit for untrust users%n", CHANGE_TRUST_LIMIT.getName());
        System.out.printf("'%s' - add address for user%n", ADD_ADDRESS.getName());
        System.out.printf("'%s' - add passport for user%n", ADD_PASSPORT.getName());
        System.out.printf("'%s' - add phone number for user%n", ADD_PHONE_NUMBER.getName());
        System.out.printf("'%s' - get user's name, surname, address, phone, passport%n", GET_USER_DATA.getName());
        System.out.printf("'%s' - help to test program%n", QUICK_START.getName());
        System.out.printf("'%s' - exit from program%n", QUIT.getName());
    }

    private static void createBank() {
        System.out.println("Creating bank:");

        System.out.println("Creating config:");
        Config config = new Config(
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
        boolean bankCreated = cb.addBank(bankName, config);
        if (!bankCreated) {
            throw ConsoleProgramException.incorrectBankName(bankName);
        }

        System.out.printf("Bank (%s) was created%n", bankName);
    }

    private static double getDebitPercent() {
        System.out.println("Enter debit percent:");
        String input = scanner.nextLine();
        return Double.parseDouble(input);
    }

    private static DepositPercents getDepositPercents() {
        System.out.println("Creating deposit percents:");
        System.out.println("Enter intervals\nFor example: [0 0.01], [10000 0.03], [20000 0.45]");
        String strIntervals = scanner.nextLine();
        if (strIntervals.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        String[] intervals = strIntervals.split(", ");
        TreeMap<Money, Double> dictPercents = new TreeMap<>();
        for (String interval : intervals) {
            String normalInterval = interval.substring(1, interval.length() - 1);
            String[] intervalData = normalInterval.split(" ");
            Money money = new Money(Double.parseDouble(intervalData[0]));
            double percent = Double.parseDouble(intervalData[1]);
            dictPercents.put(money, percent);
        }

        DepositPercents depositPercents = new DepositPercents(dictPercents);
        System.out.println("Deposit percents were created.");
        return depositPercents;
    }

    private static Money getDebitHighLimit() {
        System.out.println("Enter debit high limit:");
        return new Money(Double.parseDouble(scanner.nextLine()));
    }

    private static Money getDepositHighLimit() {
        System.out.println("Enter deposit high limit:");
        return new Money(Double.parseDouble(scanner.nextLine()));
    }

    private static Money getCreditLowLimit() {
        System.out.println("Enter credit low limit:");
        return new Money(Double.parseDouble(scanner.nextLine()));
    }

    private static Money getCreditHighLimit() {
        System.out.println("Enter credit high limit:");
        return new Money(Double.parseDouble(scanner.nextLine()));
    }

    private static Money getCreditCommission() {
        System.out.println("Enter credit commission:");
        return new Money(Double.parseDouble(scanner.nextLine()));
    }

    private static int getDepositTime() {
        System.out.println("Enter count days for deposit time (days):");
        return Integer.parseInt(scanner.nextLine());
    }

    private static Money getTrustLimit() {
        System.out.println("Enter max trust limit:");
        return new Money(Double.parseDouble(scanner.nextLine()));
    }

    private static void createUser() {
        System.out.println("Creating user:");
        String name = getUserName();
        String surname = getUserSurname();
        UserData userData = cb.createUserData()
                .withPassport(getPassport())
                .withAddress(getAddress())
                .withPhoneNumber(getPhoneNumber())
                .create(name, surname);
        UUID userId = cb.addUser(userData);
        System.out.printf("User with id (%s) was created%n", userId);
    }

    private static String getUserName() {
        System.out.println("Enter name:");
        String name = scanner.nextLine();
        if (name.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        return name;
    }

    private static String getUserSurname() {
        System.out.println("Enter surname:");
        String surname = scanner.nextLine();
        if (surname.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        return surname;
    }

    private static Passport getPassport() {
        System.out.println("Enter passport series and number (xxxx-xxxxxx), if you don't want - enter 'no':");
        String passport = scanner.nextLine();
        if (passport.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        if (passport.equals("no")) {
            return null;
        }

        String[] passportData = passport.split("-");
        if (passportData.length != 2) {
            throw ConsoleProgramException.incorrectPassport(passport);
        }

        return new Passport(passportData[0], passportData[1]);
    }

    private static Address getAddress() {
        System.out.println("Enter Address (town; street; house; flat), if you don't want - enter 'no':");
        String address = scanner.nextLine();
        if (address.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        if (address.equals("no")) {
            return null;
        }

        String[] addressData = address.split("; ");
        if (addressData.length != 4) {
            throw ConsoleProgramException.incorrectAddress(address);
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
            throw new IllegalStateException("Value is blank");
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
        UUID accountId = cb.openAccount(userId, bankName, accountMode, money);
        System.out.printf("Account (%s) was created%n", accountId);
    }

    private static AccountMode getAccountMode() {
        System.out.println("Enter account type (Credit / Debit / Deposit):");
        String strAccountMode = scanner.nextLine();
        if (strAccountMode.isBlank()) {
            throw new IllegalStateException("Value is blank");
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
            throw new IllegalStateException("Value is blank");
        }

        return UUID.fromString(strUserId);
    }

    private static String getBankName() {
        System.out.println("Enter bank's name:");
        String bankName = scanner.nextLine();
        if (bankName.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        return bankName;
    }

    private static Money getMoney() {
        System.out.println("Enter money:");
        String strMoney = scanner.nextLine();
        if (strMoney.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        return new Money(Double.parseDouble(strMoney));
    }

    private static void putOrTakeMoney(MoneyActionMode mode) {
        System.out.println("Enter account's id and money");
        UUID accountId = getAccountId();
        Money money = getMoney();
        cb.transactMoney(accountId, money, mode);
        System.out.println("Done");
    }

    private static UUID getAccountId() {
        System.out.println("Enter account's id:");
        String strAccountId = scanner.nextLine();
        if (strAccountId.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

        return UUID.fromString(strAccountId);
    }

    private static void transactMoney() {
        System.out.println("Enter sender account's id, getter account's id and money");
        UUID accountFromId = getAccountId();
        UUID accountToId = getAccountId();
        Money money = getMoney();
        cb.transactMoney(accountFromId, accountToId, money);
        System.out.println("Done");
    }

    private static void getAccountData() {
        UUID accountId = getAccountId();
        AccountData data = cb.getAccountData(accountId);
        System.out.println(data);
    }

    private static void getUserAccountsData() {
        UUID userId = getUserId();
        List<AccountData> data = cb.getUserAccountsData(userId);
        for (AccountData accountData : data) {
            System.out.println(accountData);
        }
    }

    private static void getConfig() {
        String bankName = getBankName();
        System.out.println(cb.getConfig(bankName));
    }

    private static void changeDebitPercent() {
        cb.changeDebitPercent(getBankName(), getDebitPercent());
        System.out.println("Done");
    }

    private static void changeDepositPercents() {
        ChangeDepositPercentMode mode = getChangeDepositPercentMode();
        cb.changeDepositPercents(getBankName(), getMoney(), getDepositPercent(), mode);
        System.out.println("Done");
    }

    private static ChangeDepositPercentMode getChangeDepositPercentMode() {
        System.out.println("Enter 'Add interval' or 'Delete interval'");
        String mode = scanner.nextLine();
        if (mode.isBlank()) {
            throw new IllegalStateException("Value is blank");
        }

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
        cb.changeDebitHighLimit(getBankName(), getDebitHighLimit());
        System.out.println("Done");
    }

    private static void changeDepositHighLimit() {
        cb.changeDepositHighLimit(getBankName(), getDepositHighLimit());
        System.out.println("Done");
    }

    private static void changeCreditLowLimit() {
        cb.changeCreditLowLimit(getBankName(), getCreditLowLimit());
        System.out.println("Done");
    }

    private static void changeCreditHighLimit() {
        cb.changeCreditHighLimit(getBankName(), getCreditHighLimit());
        System.out.println("Done");
    }

    private static void changeCreditCommission() {
        cb.changeCreditCommission(getBankName(), getCreditCommission());
        System.out.println("Done");
    }

    private static void changeDepositTime() {
        cb.changeDepositTime(getBankName(), getDepositTime());
        System.out.println("Done");
    }

    private static void changeTrustLimit() {
        cb.changeTrustLimit(getBankName(), getTrustLimit());
        System.out.println("Done");
    }

    private static void addUserAddress() {
        cb.addUserAddress(getUserId(), getAddress());
        System.out.println("Done");
    }

    private static void addUserPassport() {
        cb.addUserPassport(getUserId(), getPassport());
        System.out.println("Done");
    }

    private static void addUserPhoneNumber() {
        cb.addUserPhoneNumber(getUserId(), getPhoneNumber());
        System.out.println("Done");
    }

    private static void getUserData() {
        System.out.println(cb.getUserData(getUserId()));
    }

    private static void quickStart() {
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
                new Money(20_000));

        String bankName = "SBER";
        cb.addBank(bankName, config);

        String userName = "Alesha";
        String userSurname = "Popovich";
        UserData data = cb.createUserData()
                .withPassport(new Passport("1234", "567890"))
                .withAddress(new Address("SPB", "Good Street", 5, 220))
                .withPhoneNumber(new PhoneNumber("+7(921)123-45-67"))
                .create(userName, userSurname);
        UUID userId = cb.addUser(data);

        UUID debitId = cb.openAccount(userId, bankName, AccountMode.DEBIT, new Money(10_000));
        UUID creditId = cb.openAccount(userId, bankName, AccountMode.CREDIT, new Money(20_000));
        UUID depositId = cb.openAccount(userId, bankName, AccountMode.DEPOSIT, new Money(30_000));

        System.out.printf("UserId: %s%n", userId);
        System.out.printf("BankName: %s%n", bankName);
        System.out.printf("DebitId: %s%n", debitId);
        System.out.printf("CreditId: %s%n", creditId);
        System.out.printf("DepositId: %s%n", depositId);
    }
}