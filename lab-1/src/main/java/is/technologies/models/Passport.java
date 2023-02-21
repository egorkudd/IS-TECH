package is.technologies.models;

import is.technologies.exceptions.PassportException;
import lombok.Getter;

@Getter
public class Passport {
    private final String series;
    private final String number;

    public Passport(String series, String number) {
        CheckInputData(series, number);
        this.series = series;
        this.number = number;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(series, number);
    }

    private void CheckInputData(String series, String number) {
        if (!series.matches("^[0-9]{4}$")) {
            throw PassportException.incorrectSeries(series);
        }

        if (!number.matches("^[0-9]{6}$")) {
            throw PassportException.incorrectNumber(number);
        }
    }
}