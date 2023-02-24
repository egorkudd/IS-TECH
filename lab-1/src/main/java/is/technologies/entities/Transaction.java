package is.technologies.entities;

import is.technologies.enums.MoneyActionMode;
import is.technologies.enums.TransactionMode;
import is.technologies.models.BankTime;
import is.technologies.models.BankTimer;
import is.technologies.models.Money;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * Transaction class has opportunity to execute and revert transaction
 */
@Getter
@ToString
public class Transaction {
    private final UUID id;
    private final Account accountFrom;
    private final Account accountTo;
    private final Money money;
    private final BankTime time;
    private TransactionMode mode;

    public Transaction(
            Account accountFrom,
            Account accountTo,
            Money money
    ) {
        this.id = UUID.randomUUID();
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.money = money;
        this.time = BankTimer.getTime();
    }

    /**
     * Execute transaction and return true if it has happened, else - false
     * @return boolean
     */
    public boolean execute() {
        return makeTransaction(
                accountFrom, accountTo, TransactionMode.EXECUTED, TransactionMode.DENIED
        );
    }

    /**
     * Revert transaction and return true if it has happened, else - false
     * @return boolean
     */
    public boolean revertTransaction() {
        return makeTransaction(
                accountTo, accountFrom, TransactionMode.REVERTED, TransactionMode.EXECUTED
        );
    }

    /**
     *
     * @param accountFrom is account-sender
     * @param accountTo is account-getter
     * @param acceptMode is mode which means positive result
     * @param wrongMode is mode which means negative result
     * @return boolean
     */
    private boolean makeTransaction(
            Account accountFrom,
            Account accountTo,
            TransactionMode acceptMode,
            TransactionMode wrongMode
    ) {
        boolean moneyTook = accountFrom.makeTransaction(money, MoneyActionMode.TAKE_MONEY);
        if (!moneyTook) {
            mode = wrongMode;
            return false;
        }

        boolean moneyPut = accountTo.makeTransaction(money, MoneyActionMode.PUT_MONEY);
        if (moneyPut) {
            mode = acceptMode;
            return true;
        }

        boolean moneyBack = accountFrom.makeTransaction(money, MoneyActionMode.PUT_MONEY);
        mode = wrongMode;
        return false;
    }
}