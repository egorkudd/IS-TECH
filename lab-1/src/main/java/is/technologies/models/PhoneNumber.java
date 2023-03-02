package is.technologies.models;

import is.technologies.exceptions.PhoneException;

/**
 * Phone number class, contains data and validation
 */
public record PhoneNumber(String number) {
    public PhoneNumber {
        if (number.length() < 11) {
            throw PhoneException.incorrectPhoneLength(number);
        }

        if (number.substring(1).contains("+7")) {
            throw PhoneException.incorrectPhoneChars(number);
        }

        number = number
                .replace("+7", "8")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");

        if (!number.matches("^[0-9]+$")) {
            throw PhoneException.incorrectPhoneChars(number);
        }

        if (number.length() != 11) {
            throw PhoneException.incorrectPhoneLength(number);
        }

    }
}