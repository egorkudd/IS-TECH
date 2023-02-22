package is.technologies.models;

import is.technologies.exceptions.AddressException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Address {
    private final String town;
    private final String street;
    private final int houseNumber;
    private final int flat;

    public Address(String town, String street, int houseNumber, int flat) {
        checkInputData(town, street, houseNumber, flat);
        this.town = town;
        this.street = street;
        this.houseNumber = houseNumber;
        this.flat = flat;
    }

    private void checkInputData(String town, String street, int houseNumber, int flat) {
        if (town.isBlank()) throw AddressException.incorrectTownName(town);
        if (street.isBlank()) throw AddressException.incorrectStreetName(street);
        if (houseNumber < 1) throw AddressException.incorrectHouseNumber(houseNumber);
        if (flat < 1) throw AddressException.incorrectFlatNumber(flat);
    }
}