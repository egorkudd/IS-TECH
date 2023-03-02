package is.technologies.entities;

import is.technologies.exceptions.ConfigException;
import is.technologies.exceptions.TransactionException;
import is.technologies.enums.AccountMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Debit account class, has percent to increase money and special updating money
 */
public class DebitAccount extends Account {
    private Money money;
    private Money percentShift;
    private int daysOfCurrentMonth;
    private BankTime lastUpdateTime;

    public DebitAccount(
            UUID id,
            String bankName,
            Money money,
            double percent,
            Money highLimit,
            BankTime time,
            Money trustLimit,
            UUID userId
    ) {
        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        if (money.compareTo(Money.ZERO) <= -1) {
            throw ConfigException.incorrectHighLimit(money);
        }

        this.money = money;
        this.id = id;
        this.mode = AccountMode.DEBIT;
        this.bankName = bankName;
        this.percent = percent;
        this.lowLimit = Money.ZERO;
        this.highLimit = highLimit;
        this.lastUpdateTime = time;
        this.trustLimit = trustLimit;
        this.percentShift = Money.ZERO;
        this.daysOfCurrentMonth = 0;
        this.userId = userId;
    }

    /**
     * Increase percentShift every day and increase money every month
     *
     * @return Money
     */
    public Money getMoney() {
        BankTime currTime = BankTimer.getTime();
        int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.time(), currTime.time());
        lastUpdateTime = currTime;

        while (pow >= 30) {
            if (daysOfCurrentMonth > 0) {
                percentShift = percentShift.plus(money.multiply((int) Math.pow(
                        1 + (percent / 365),
                        30 - daysOfCurrentMonth
                ))).minus(money);
                money = money.plus(percentShift);
                percentShift = Money.ZERO;
                daysOfCurrentMonth = 0;
                pow -= 30 - daysOfCurrentMonth;
            } else {
                money = money.multiply((int) Math.pow(1 + (percent / 365), 30));
                pow -= 30;
            }
        }

        percentShift = percentShift.plus(money.multiply((int) Math.pow(
                1 + (percent / 365),
                pow)
        )).minus(money);
        daysOfCurrentMonth += pow;

        return money;
    }

    /**
     * @param money to transact
     * @param mode  which means type of transaction
     * @return boolean
     * @throws TransactionException  when transaction is unreal
     * @throws IllegalStateException when mode is unreal
     */
    public boolean makeTransaction(Money money, MoneyActionMode mode) {
        if (mode == MoneyActionMode.PUT_MONEY && this.money.plus(money).compareTo(highLimit) < 1) {
            this.money = this.money.plus(money);
            return true;
        } else if (mode == MoneyActionMode.PUT_MONEY) {
            throw TransactionException.aboveHighLimit(this.money.plus(money), highLimit);
        } else if (mode == MoneyActionMode.TAKE_MONEY &&
                this.money.minus(money).compareTo(lowLimit) > -1) {
            this.money = this.money.minus(money);
            return true;
        } else if (mode == MoneyActionMode.TAKE_MONEY) {
            throw TransactionException.belowLowLimit(this.money.plus(money), lowLimit);
        }

        throw new IllegalStateException("Unexpected value: " + mode);
    }

    /**
     * Change necessary parameters to change
     *
     * @param config which contains new parameters
     */
    public void changeConfig(Config config) {
        percent = config.getDebitPercent();
        highLimit = config.getDebitHighLimit();
        trustLimit = config.getTrustLimit();
    }
}