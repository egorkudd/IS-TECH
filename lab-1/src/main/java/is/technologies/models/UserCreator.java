package is.technologies.models;

/**
 * User creator class, helps to create user
 */
public class UserCreator {
    private Passport passport;
    private Address address;
    private PhoneNumber number;

    /**
     * Add passport
     * @param passport to add
     * @return UserCreator
     */
    public UserCreator withPassport(Passport passport) {
        if (passport != null) {
            this.passport = passport;
        }

        return this;
    }

    /**
     * Add address
     * @param address to add
     * @return UserCreator
     */
    public UserCreator withAddress(Address address) {
        if (address != null) {
            this.address = address;
        }
        return this;
    }

    /**
     * Add phone number
     * @param phoneNumber to add
     * @return UserCreator
     */
    public UserCreator withPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            number = phoneNumber;
        }
        return this;
    }

    /**
     * Create user's data
     * @param name to add
     * @param surname to add
     * @return UserData
     */
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