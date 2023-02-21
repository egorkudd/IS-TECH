package is.technologies.models;

import is.technologies.exceptions.PhoneException;
import lombok.Getter;

@Getter
public class PhoneNumber {
    private final String number;

    public PhoneNumber(String number) {
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

        this.number = number;
    }

    @Override
    public String toString() {
        return number;
    }
}