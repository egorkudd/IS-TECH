package is.technologies.models;

import is.technologies.exceptions.ConfigException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Config class, contains fields, checking data and clone method
 */
@Getter
@Setter
@ToString
public class Config implements Cloneable {
    private double debitPercent;
    private DepositPercents depositPercents;
    private Money debitHighLimit;
    private Money depositHighLimit;
    private Money creditLowLimit;
    private Money creditHighLimit;
    private Money creditCommission;
    private int depositDays;
    private Money trustLimit;

    public Config(
            double debitPercent,
            DepositPercents depositPercents,
            Money debitHighLimit,
            Money depositHighLimit,
            Money creditLowLimit,
            Money creditHighLimit,
            Money creditCommission,
            int depositDays,
            Money trustLimit) {
        checkDataToNull(
                depositPercents,
                debitHighLimit,
                depositHighLimit,
                creditLowLimit,
                creditHighLimit,
                creditCommission,
                trustLimit
        );

        if (debitPercent > 0) {
            this.debitPercent = debitPercent;
        } else {
            throw ConfigException.incorrectPercent(debitPercent);
        }

        this.depositPercents = depositPercents;

        if (debitHighLimit.compareTo(Money.ZERO) > 0) {
            this.debitHighLimit = debitHighLimit;
        } else {
            throw ConfigException.incorrectHighLimit(debitHighLimit);
        }

        if (depositHighLimit.compareTo(Money.ZERO) > 0) {
            this.depositHighLimit = depositHighLimit;
        } else {
            throw ConfigException.incorrectHighLimit(depositHighLimit);
        }

        if (creditLowLimit.compareTo(Money.ZERO) < 1) {
            this.creditLowLimit = creditLowLimit;
        } else {
            throw ConfigException.incorrectLowLimit(creditLowLimit);
        }

        if (creditHighLimit.compareTo(Money.ZERO) > 0) {
            this.creditHighLimit = creditHighLimit;
        } else {
            throw ConfigException.incorrectHighLimit(creditHighLimit);
        }

        if (creditCommission.compareTo(Money.ZERO) > 0) {
            this.creditCommission = creditCommission;
        } else {
            throw ConfigException.incorrectCommission(creditCommission);
        }

        if (depositDays >= 365) {
            this.depositDays = depositDays;
        } else {
            throw ConfigException.tooShortDepositLimit(depositDays);
        }

        this.trustLimit = trustLimit;
    }

    /**
     * Check parameters to null
     * @param depositPercents to check
     * @param debitHighLimit to check
     * @param depositHighLimit to check
     * @param creditLowLimit to check
     * @param creditHighLimit to check
     * @param creditCommission to check
     * @param trustLimit to check
     */
    private void checkDataToNull(
            DepositPercents depositPercents,
            Money debitHighLimit,
            Money depositHighLimit,
            Money creditLowLimit,
            Money creditHighLimit,
            Money creditCommission,
            Money trustLimit) {
        if (depositPercents == null) throw new NullPointerException("depositPercents");
        if (debitHighLimit == null) throw new NullPointerException("debitHighLimit");
        if (depositHighLimit == null) throw new NullPointerException("depositHighLimit");
        if (creditLowLimit == null) throw new NullPointerException("creditLowLimit");
        if (creditHighLimit == null) throw new NullPointerException("creditHighLimit");
        if (creditCommission == null) throw new NullPointerException("creditCommission");
        if (trustLimit == null) throw new NullPointerException("trustLimit");
    }


    @Override
    public Config clone() {
        return new Config(
                debitPercent,
                depositPercents,
                debitHighLimit,
                depositHighLimit,
                creditLowLimit,
                creditHighLimit,
                creditCommission,
                depositDays,
                trustLimit
        );
    }
}
