package is.technologies.exceptions;

public class PhoneException extends RuntimeException {
    private PhoneException(String message) {
        super(message);
    }

    public static PhoneException incorrectPhoneChars(String phone) {
        return new PhoneException(phone);
    }

    public static PhoneException incorrectPhoneLength(String phone) {
        return new PhoneException(phone);
    }
}