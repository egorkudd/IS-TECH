package is.technologies.models;

public class UserCreator {
    private Passport passport;
    private Address address;
    private PhoneNumber number;

    public UserCreator withPassport(Passport passport) {
        if (passport != null) {
            this.passport = passport;
        }

        return this;
    }

    public UserCreator withAddress(Address address) {
        if (address != null) {
            this.address = address;
        }
        return this;
    }

    public UserCreator withPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            number = phoneNumber;
        }
        return this;
    }

    public UserData create(String name, String surname) {
        if (name.isBlank()) {
            throw new NullPointerException("name"); // TODO : NPE
        }

        if (surname.isBlank()) {
            throw new NullPointerException("surname"); // TODO : NPE
        }

        return new UserData(name, surname, passport, address, number);
    }
}