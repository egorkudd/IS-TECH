package is.technologies.exceptions;

public class BankTimerException extends RuntimeException {
    private BankTimerException(String message) {
        super(message);
    }

    public static BankTimerException incorrectShiftTime(int days) {
        return new BankTimerException("%d is less than 0".formatted(days));
    }
}
