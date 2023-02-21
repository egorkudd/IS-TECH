package is.technologies.exceptions;

public class PassportException extends RuntimeException {
    private PassportException(String message) {
        super(message);
    }

    public static PassportException incorrectSeries(String series) {
        return new PassportException(series);
    }

    public static PassportException incorrectNumber(String number) {
        return new PassportException(number);
    }
}