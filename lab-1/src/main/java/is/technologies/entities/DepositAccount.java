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
     *
     * @return Money
     */
    public Money getMoney() {
        if (finishTime.time().isAfter(BankTimer.getTime().time())) {
            BankTime currTime = BankTimer.getTime();
            int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.time(), currTime.time());
            lastUpdateTime = currTime;
            percentShift = percentShift
                    .plus(money.multiply((int) Math.pow(1 + (percent / 365), pow)))
                    .minus(money);
        } else if (!paid) {
            BankTime currTime = finishTime;
            int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.time(), currTime.time()) + 1;
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
     * @param mode  which means type of transaction
     * @return boolean
     * @throws TransactionException  when transaction is unreal
     * @throws IllegalStateException when mode is unreal
     */
    public boolean makeTransaction(Money money, MoneyActionMode mode) {
        switch (mode) {
            case PUT_MONEY -> {
                if (this.money.plus(money).compareTo(highLimit) >= 1) {
                    throw TransactionException.aboveHighLimit(this.money.plus(money), highLimit);
                }

                this.money = this.money.plus(money);
                return true;
            }
            case TAKE_MONEY -> {
                boolean isFinishTimePast = BankTimer.getTime().time().isAfter(finishTime.time());
                if (!isFinishTimePast || this.money.compareTo(money) <= -1) {
                    throw TransactionException.unrealDepositTransaction();
                }

                this.money = this.money.minus(money);
                return true;
            }

            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

    }

    /**
     * Change necessary parameters to change
     *
     * @param config which contains new parameters
     */
    public void changeConfig(Config config) {
        highLimit = config.getDepositHighLimit();
        trustLimit = config.getTrustLimit();
        percent = config.getDepositPercents().getPercent(money);
    }
}