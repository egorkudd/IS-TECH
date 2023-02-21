package is.technologies.exceptions;

import is.technologies.models.Money;

public class DepositPercentException extends RuntimeException {
    private DepositPercentException(String message) {
        super(message);
    }

    public static DepositPercentException incorrectFirstLowLimit(Money limit) {
        return new DepositPercentException("First low limit must be Money.ZERO, but it's %s".formatted(limit));
    }

    public static DepositPercentException incorrectPercent(double percent) {
        return new DepositPercentException("Incorrect percent %s".formatted(percent));
    }
}