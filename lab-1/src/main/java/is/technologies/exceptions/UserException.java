package is.technologies.exceptions;

import java.util.UUID;

public class UserException extends RuntimeException {
    private UserException(String message) {
        super(message);
    }

    public static UserException addressIntroduced(UUID userId) {
        return new UserException(
                "You can't add address more then 1 time for user %s".formatted(userId)
        );
    }

    public static UserException passportIntroduced(UUID userId) {
        return new UserException(
                "You can't add passport more then 1 time for user %s".formatted(userId)
        );
    }

    public static UserException phoneNumberIntroduced(UUID userId) {
        return new UserException(
                "You can't add phone number more then 1 time for user %s".formatted(userId)
        );
    }
}