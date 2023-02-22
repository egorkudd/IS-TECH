package is.technologies.entities;

import is.technologies.enums.MoneyActionMode;
import is.technologies.enums.TransactionMode;
import is.technologies.models.BankTime;
import is.technologies.models.BankTimer;
import is.technologies.models.Money;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class Transaction {
    private final UUID id;
    private final AbstractAccount accountFrom;
    private final AbstractAccount accountTo;
    private final Money money;
    private final BankTime time;
    private TransactionMode mode;

    public Transaction(
            AbstractAccount accountFrom,
            AbstractAccount accountTo,
            Money money
    ) {
        this.id = UUID.randomUUID();
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.money = money;
        this.time = BankTimer.getTime();
    }

    public boolean execute() {
        return makeTransaction(accountFrom, accountTo, TransactionMode.EXECUTED);
    }

    public boolean revertTransaction() {
        return makeTransaction(accountTo, accountFrom, TransactionMode.REVERTED);
    }

    private boolean makeTransaction(
            AbstractAccount accountFrom, AbstractAccount accountTo, TransactionMode acceptMode
    ) {
        boolean moneyTook = accountFrom.makeTransaction(money, MoneyActionMode.TAKE_MONEY);
        if (!moneyTook) {
            mode = TransactionMode.DENIED;
            return false;
        }

        boolean moneyPut = accountTo.makeTransaction(money, MoneyActionMode.PUT_MONEY);
        if (moneyPut) {
            mode = acceptMode;
            return true;
        }

        boolean moneyBack = accountFrom.makeTransaction(money, MoneyActionMode.PUT_MONEY);
        mode = TransactionMode.DENIED;
        return false;
    }
}