package is.technologies.entities;

import is.technologies.exceptions.ConfigException;
import is.technologies.exceptions.TransactionException;
import is.technologies.enums.AccountMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.*;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class DepositAccount extends AbstractAccount {
    private final BankTime finishTime;
    private DepositPercents percents;
    private Money money;
    private Money percentShift;
    private boolean paid;
    private BankTime lastUpdateTime;


    // TODO : what type for id ???
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
        this.percents = percents;
        this.lastUpdateTime = time;
        this.finishTime = finishTime;
        this.trustLimit = trustLimit;
        this.percentShift = Money.ZERO;
        this.paid = false;
        this.userId = userId;
    }

    public Money getMoney() {
        if (finishTime.getTime().isAfter(BankTimer.getTime().getTime())) {
            BankTime currTime = BankTimer.getTime();
            int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.getTime(), currTime.getTime());
            lastUpdateTime = currTime;
            percentShift = percentShift.plus(money.multiply((int) Math.pow(
                    1 + (percent / 365),
                    pow
            ))).minus(money);
        } else if (!paid) {
            BankTime currTime = finishTime;
            int pow = (int) ChronoUnit.DAYS.between(lastUpdateTime.getTime(), currTime.getTime());
            lastUpdateTime = currTime;
            percentShift = percentShift.plus(money.multiply((int) Math.pow(
                    1 + (percent / 365),
                    pow
            ))).minus(money);
            money = money.plus(percentShift);
            paid = true;
        }

        return money;
    }

    public double getPercent() {
        return percents.GetPercent(money);
    }

    public AccountData getAccountData() {
        return new AccountData(id, bankName, AccountMode.DEPOSIT, money);
    }

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
            return false;
        }

        throw new IllegalStateException("Unexpected value: " + mode);
    }

    public void changeConfig(Config config) {
        highLimit = config.getDepositHighLimit();
        percents = config.getDepositPercents();
    }
}