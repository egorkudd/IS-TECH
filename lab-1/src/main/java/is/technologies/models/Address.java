package is.technologies.models;

import is.technologies.exceptions.AddressException;

/**
 * Address class check validation of address
 */
public record Address(String town, String street, int houseNumber, int flat) {
    public Address {
        checkInputData(town, street, houseNumber, flat);
    }

    /**
     * Check address data to validation
     *
     * @param town        to check
     * @param street      to check
     * @param houseNumber to check
     * @param flat        to check
     * @throws AddressException is some part of data is invalid
     */
    private void checkInputData(String town, String street, int houseNumber, int flat) {
        if (town.isBlank()) {
            throw AddressException.incorrectTownName(town);
        }

        if (street.isBlank()) {
            throw AddressException.incorrectStreetName(street);
        }

        if (houseNumber < 1) {
            throw AddressException.incorrectHouseNumber(houseNumber);
        }

        if (flat < 1) {
            throw AddressException.incorrectFlatNumber(flat);
        }
    }
}