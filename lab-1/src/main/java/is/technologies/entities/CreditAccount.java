package is.technologies.entities;

import is.technologies.exceptions.ConfigException;
import is.technologies.exceptions.TransactionException;
import is.technologies.enums.AccountMode;
import is.technologies.enums.MoneyActionMode;
import is.technologies.models.Config;
import is.technologies.models.Money;
import is.technologies.models.BankTime;
import is.technologies.models.BankTimer;

import java.time.temporal.ChronoUnit;
import java.util.UUID;


public class CreditAccount extends AbstractAccount {
    private BankTime lastUpdateTime;
    private Money commission;

    public CreditAccount(
            UUID id,
            String bankName,
            Money money,
            Money lowLimit,
            Money highLimit,
            Money commission,
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
        this.mode = AccountMode.CREDIT;
        this.bankName = bankName;
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
        this.commission = commission;
        this.trustLimit = trustLimit;
        this.lastUpdateTime = null;
        this.userId = userId;
    }

    public Money getMoney() {
        if (money.compareTo(Money.ZERO) < 0 && lastUpdateTime != null) {
            BankTime currTime = BankTimer.getTime();
            int days = (int) ChronoUnit.DAYS.between(lastUpdateTime.getTime(), currTime.getTime());
            int months = days / 30;
            money = money.minus(commission.multiply(months));
        }

        return money;
    }

    public void changeConfig(Config config) {
        lowLimit = config.getCreditLowLimit();
        highLimit = config.getCreditHighLimit();
        commission = config.getCreditCommission();
    }

    public boolean makeTransaction(Money money, MoneyActionMode mode) {
        if (mode == MoneyActionMode.PUT_MONEY && this.money.plus(money).compareTo(highLimit) < 1) {
            this.money = this.money.plus(money);
            if (this.money.compareTo(Money.ZERO) > -1) lastUpdateTime = null;
            return true;
        } else if (mode == MoneyActionMode.PUT_MONEY) {
            throw TransactionException.aboveHighLimit(this.money.plus(money), highLimit);
        } else if (mode == MoneyActionMode.TAKE_MONEY && this.money.minus(money).compareTo(lowLimit) > 0) {
            this.money = this.money.minus(money);
            if (this.money.compareTo(Money.ZERO) < 0) lastUpdateTime = BankTimer.getTime();
            return true;
        } else if (mode == MoneyActionMode.TAKE_MONEY) {
            throw TransactionException.belowLowLimit(this.money.plus(money), lowLimit);
        }

        throw new IllegalStateException("Unexpected value: " + mode);
    }
}
