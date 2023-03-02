package is.technologies.models;

import lombok.Getter;
import lombok.ToString;

/**
 * User's data class, contains user's data
 */
public record UserData(
        String name,
        String surname,
        Passport passport,
        Address address,
        PhoneNumber phoneNumber
) {
    public static final UserCreator creator = new UserCreator();
}