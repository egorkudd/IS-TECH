package is.technologies.exceptions;

public class AddressException extends RuntimeException {
    private AddressException(String message) {
        super(message);
    }

    public static AddressException incorrectTownName(String town) {
        return new AddressException(town);
    }

    public static AddressException incorrectStreetName(String street) {
        return new AddressException(street);
    }

    public static AddressException incorrectHouseNumber(int house) {
        return new AddressException(String.valueOf(house));
    }

    public static AddressException incorrectFlatNumber(int flat) {
        return new AddressException(String.valueOf(flat));
    }
}