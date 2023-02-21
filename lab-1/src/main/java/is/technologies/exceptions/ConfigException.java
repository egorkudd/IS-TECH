package is.technologies.exceptions;

import is.technologies.models.Money;

public class ConfigException extends RuntimeException {
    private ConfigException(String message) {
        super(message);
    }

    public static ConfigException incorrectPercent(double percent) {
        return new ConfigException(String.valueOf(percent));
    }

    public static ConfigException incorrectLowLimit(Money limit) {
        return new ConfigException(String.valueOf(limit));
    }

    public static ConfigException incorrectHighLimit(Money limit) {
        return new ConfigException(String.valueOf(limit));
    }

    public static ConfigException incorrectCommission(Money commission) {
        return new ConfigException(String.valueOf(commission));
    }

    public static ConfigException tooShortDepositLimit(int days) {
        return new ConfigException(String.valueOf(days));
    }
}