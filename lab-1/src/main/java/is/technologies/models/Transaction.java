package is.technologies.models;

import is.technologies.enums.TransactionMode;
import lombok.Getter;

import java.util.UUID;

public class Transaction {
    public UUID id;
    @Getter
    public UUID accountFromId;
    @Getter
    public UUID accountToId;
    @Getter
    public Money money;
    public BankTime time;
    @Getter
    public TransactionMode mode;

    public Transaction(UUID id, UUID accountFromId, UUID accountToId, Money money, TransactionMode mode) {
        this.id = id;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.money = money;
        this.mode = mode;
        this.time = BankTimer.getTime();
    }

    @Override
    public String toString() {
        return "%s : %s : %s : %s : %s : %s"
                .formatted(id, mode, accountFromId, accountToId, money, time);
    }

    public void revertTransaction() {
        mode = TransactionMode.REVERTED;
    }
}