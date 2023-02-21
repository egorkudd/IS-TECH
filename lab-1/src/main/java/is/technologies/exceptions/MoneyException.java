package is.technologies.exceptions;

import is.technologies.models.Money;

public class MoneyException extends RuntimeException {
    private MoneyException(String message) {
        super(message);
    }

    public static MoneyException incorrectMoneyCount(Money money) {
        return new MoneyException(money.toString());
    }
}