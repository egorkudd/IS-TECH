package is.technologies.exceptions;

import java.util.UUID;

public class CentralBankException extends RuntimeException {
    private CentralBankException(String message) {
        super(message);
    }

    public static CentralBankException incorrectUserId(UUID id) {
        return new CentralBankException("There is no user's id = %s".formatted(id));
    }

    public static CentralBankException incorrectAccountId(UUID id) {
        return new CentralBankException("There is no account's id = %s".formatted(id));
    }

    public static CentralBankException incorrectBankName(String name) {
        return new CentralBankException("There is no bank's name = %s".formatted(name));
    }

    public static CentralBankException accountFromIsAccountTo(UUID from, UUID to) {
        return new CentralBankException(
                "Incorrect transaction from account to the same account: id from : %s, id to : %s"
                        .formatted(from, to)
        );
    }

    public static CentralBankException noTrust(UUID userId) {
        return new CentralBankException(
                "Transaction is not valid. User (%s) is not trusted".formatted(userId)
        );
    }
}