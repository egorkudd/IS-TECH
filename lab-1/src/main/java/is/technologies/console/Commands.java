package is.technologies.console;

/**
 * Command enum for console
 */
public enum Commands {
    HELP("-help"),
    CREATE_BANK("-create-bank"),
    CREATE_USER("-create-user"),
    OPEN_ACCOUNT("-open-account"),
    PUT_MONEY("-put-money"),
    TAKE_MONEY("-take-money"),
    TRANSACT_MONEY("-transact-money"),
    ACCOUNT_DATA("-account-data"),
    USER_ACCOUNTS_DATA("-user-accounts-data"),
    GET_CONFIG("-get-config"),
    CHANGE_DEBIT_PERCENT("-change-debit-percent"),
    CHANGE_DEPOSIT_PERCENTS("-change-deposit-percents"),
    CHANGE_DEBIT_HIGH_LIMIT("-change-debit-high-limit"),
    CHANGE_DEPOSIT_HIGH_LIMIT("-change-deposit-high-limit"),
    CHANGE_CREDIT_LOW_LIMIT("-change-credit-low-limit"),
    CHANGE_CREDIT_HIGH_LIMIT("-change-credit-high-limit"),
    CHANGE_CREDIT_COMMISSION("-change-credit-commission"),
    CHANGE_DEPOSIT_TIME("-change-deposit-time"),
    CHANGE_TRUST_LIMIT("-change-trust-limit"),
    ADD_ADDRESS("-add-address"),
    ADD_PASSPORT("-add-passport"),
    ADD_PHONE_NUMBER("-add-phone-number"),
    GET_USER_DATA("-get-user-data"),
    QUIT("-quit"),
    QUICK_START("-quick-start");

    private final String name;

    Commands(String commandName) {
        this.name = commandName;
    }

    public String getName() {
        return name;
    }
}
