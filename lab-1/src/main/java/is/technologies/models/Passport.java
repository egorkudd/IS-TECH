package is.technologies.models;

import is.technologies.exceptions.PassportException;
import lombok.Getter;
import lombok.ToString;

/**
 * Passport class, contains data and validation
 */
@Getter
@ToString
public class Passport {
    private final String series;
    private final String number;

    public Passport(String series, String number) {
        checkInputData(series, number);
        this.series = series;
        this.number = number;
    }

    /**
     * Check input data
     * @param series to check
     * @param number to check
     */
    private void checkInputData(String series, String number) {
        if (!series.matches("^[0-9]{4}$")) {
            throw PassportException.incorrectSeries(series);
        }

        if (!number.matches("^[0-9]{6}$")) {
            throw PassportException.incorrectNumber(number);
        }
    }
}