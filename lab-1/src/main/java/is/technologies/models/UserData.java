package is.technologies.models;

import lombok.Getter;

@Getter
public class UserData {
    public static final UserCreator creator = new UserCreator();
    private final String name;
    private final String surname;
    private final Passport passport;
    private final Address address;
    private final PhoneNumber phoneNumber;

    public UserData(String name, String surname, Passport passport, Address address, PhoneNumber phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.passport = passport;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User:\n\tName: %s\n\tSurname: %s\n\tPassport: %s\n\tAddress: %s\n\tPhone number: %s"
                .formatted(name, surname, passport, address, phoneNumber);
    }
}