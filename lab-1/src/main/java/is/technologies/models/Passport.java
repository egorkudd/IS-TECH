package is.technologies.models;

import is.technologies.exceptions.PassportException;

/**
 * Passport class, contains data and validation
 */
public record Passport(String series, String number) {
    public Passport {
        checkInputData(series, number);
    }

    /**
     * Check input data
     *
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