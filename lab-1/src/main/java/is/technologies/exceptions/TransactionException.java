package is.technologies.exceptions;

import is.technologies.models.Money;

public class TransactionException extends RuntimeException {
    private TransactionException(String message) {
        super(message);
    }

    public static TransactionException aboveHighLimit(Money money, Money highLimit) {
        return new TransactionException("Money: %s, High Limit: %s".formatted(money, highLimit));
    }

    public static TransactionException belowLowLimit(Money money, Money lowLimit) {
        return new TransactionException("Money: %s, Low Limit: %s".formatted(money, lowLimit));
    }

    public static TransactionException unrealDepositTransaction() {
        return new TransactionException("Unreal transaction, because deposit time has not passed");
    }
}