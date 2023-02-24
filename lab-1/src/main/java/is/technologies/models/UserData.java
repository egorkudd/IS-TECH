package is.technologies.models;

import lombok.Getter;
import lombok.ToString;

/**
 * User's data class, contains user's data
 */
@Getter
@ToString
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
}