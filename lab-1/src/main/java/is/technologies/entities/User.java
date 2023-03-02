package is.technologies.entities;

import is.technologies.exceptions.UserException;
import is.technologies.models.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User class, contains person's data and accounts
 */
@Getter
@ToString
public class User {
    private final UUID id;
    private final String name;
    private final String surname;
    private Passport passport;
    private Address address;
    private PhoneNumber phoneNumber;
    private final ArrayList<Account> accounts;

    public User(UUID id, UserData data) {
        this.id = id;
        this.name = data.name();
        this.surname = data.surname();
        this.passport = data.passport();
        this.address = data.address();
        this.phoneNumber = data.phoneNumber();
        accounts = new ArrayList<>();
    }

    public static UserCreator getCreator() {
        return new UserCreator();
    }

    /**
     * If user has full data, he is trusted and vice versa
     * @return boolean
     */
    public boolean isTrusted() {
        return passport != null && address != null && phoneNumber != null;
    }

    /**
     * Add account to user's pool
     * @param account to add
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /**
     * @return list of data of all accounts
     */
    public List<AccountData> GetAccountsData() {
        return accounts.stream().map(Account::getAccountData).toList();
    }

    /**
     * Add address to user's data
     * @param address to add
     * @exception UserException if user already has address
     */
    public void addAddress(Address address) {
        if (address == null) {
            throw new NullPointerException("address"); // TODO : NPE
        }

        if (this.address != null) {
            throw UserException.addressIntroduced(id);
        }

        this.address = address;
    }

    /**
     * Add passport to user's data
     * @param passport to add
     * @exception UserException if user already has passport
     */
    public void AddPassport(Passport passport) {
        if (passport == null) {
            throw new NullPointerException("passport"); // TODO : NPE
        }

        if (this.passport != null) {
            throw UserException.passportIntroduced(id);
        }

        this.passport = passport;
    }

    /**
     * Add phone number to user's data
     * @param phoneNumber to add
     * @exception UserException if user already has phone number
     */
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            throw new NullPointerException("phoneNumber"); // TODO : NPE
        }

        if (this.phoneNumber != null) {
            throw UserException.phoneNumberIntroduced(id);
        }

        this.phoneNumber = phoneNumber;
    }
}
