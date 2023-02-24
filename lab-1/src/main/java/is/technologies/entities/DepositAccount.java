package is.technologies.entities;

import is.technologies.exceptions.ConfigException;
import is.technologies.exceptions.TransactionException;
import is.technologies.enums.AccountMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Deposit account class, has percent and special updating money.
 * Has no opportunity to transact money before finish time
 */
public class DepositAccount extends Account {
    private final BankTime finishTime;
    private Money money;
    private Money percentShift;
    private boolean paid;
    private BankTime lastUpdateTime;

    public DepositAccount(
            UUID id,
            String bankName,
            Money money,
            DepositPercents percents,
            Money highLimit,
            BankTime time,
            BankTime finishTime,
            Money trustLimit,
            UUID userId) {
        if (money == null) {
            throw new NullPointerException("money"); // TODO : NPE
        }

        if (money.compareTo(Money.ZERO) > -1) {
            this.money = money;
        } else {
            throw ConfigException.incorrectHighLimit(money);
        }

        this.id = id;
        this.mode = AccountMode.DEPOSIT;
        this.bankName = bankName;
        this.highLimit = highLimit;
        this.percent = percents.getPercent(this.money);
        this.lastUpdateTime = time;
        this.finishTime = finishTime;
        this.trustLimit = trustLimit;
        this.percentShift = Money.ZERO;
        this.paid = false;
        this.userId = userId;
    }

    /**
     * Update money only after time to finish this account
     * @return Money
     */
    public Money getMoney() {
        if (finishTime.getTime().isAfter(BankTimer.getTime().getTime())) {
            BankTime currTime = BankTimer.getTime();
            int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.getTime(), currTime.getTime());
            lastUpdateTime = currTime;
            percentShift = percentShift
                    .plus(money.multiply((int) Math.pow(1 + (percent / 365), pow)))
                    .minus(money);
        } else if (!paid) {
            BankTime currTime = finishTime;
            int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.getTime(), currTime.getTime()) + 1;
            lastUpdateTime = currTime;
            percentShift = percentShift
                    .plus(money.multiply(Math.pow(1 + (percent / 365), pow)))
                    .minus(money);
            money = money.plus(percentShift);
            paid = true;
        }

        return money;
    }

    /**
     * @param money to transact
     * @param mode which means type of transaction
     * @return boolean
     * @exception TransactionException when transaction is unreal
     * @exception IllegalStateException when mode is unreal
     */
    public boolean makeTransaction(Money money, MoneyActionMode mode) {
        if (mode == MoneyActionMode.PUT_MONEY && this.money.plus(money).compareTo(highLimit) < 1) {
            this.money = this.money.plus(money);
            return true;
        } else if (mode == MoneyActionMode.PUT_MONEY) {
            throw TransactionException.aboveHighLimit(this.money.plus(money), highLimit);
        } else if (mode == MoneyActionMode.TAKE_MONEY &&
                BankTimer.getTime().getTime().isAfter(finishTime.getTime()) &&
                this.money.compareTo(money) > -1) {
            this.money = this.money.minus(money);
            return true;
        } else if (mode == MoneyActionMode.TAKE_MONEY) {
            throw TransactionException.unrealDepositTransaction();
        }

        throw new IllegalStateException("Unexpected value: " + mode);
    }

    /**
     * Change necessary parameters to change
     * @param config which contains new parameters
     */
    public void changeConfig(Config config) {
        highLimit = config.getDepositHighLimit();
        trustLimit = config.getTrustLimit();
        percent = config.getDepositPercents().getPercent(money);
    }
}