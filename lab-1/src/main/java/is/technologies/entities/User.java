package is.technologies.entities;

import is.technologies.exceptions.UserException;
import is.technologies.models.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class User {
    private final UUID id;
    private final String name;
    private final String surname;
    private Passport passport;
    private Address address;
    private PhoneNumber phoneNumber;
    private final ArrayList<AbstractAccount> accounts;

    public User(UUID id, UserData data) {
        this.id = id;
        this.name = data.getName();
        this.surname = data.getSurname();
        this.passport = data.getPassport();
        this.address = data.getAddress();
        this.phoneNumber = data.getPhoneNumber();
        accounts = new ArrayList<>();
    }

    public static UserCreator getCreator() {
        return new UserCreator();
    }

    public boolean isTrusted() {
        return passport != null && address != null && phoneNumber != null;
    }

    public List<AbstractAccount> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public void addAccount(AbstractAccount account) {
        accounts.add(account);
    }

    public List<AccountData> GetAccountsData() {
        return accounts.stream().map(AbstractAccount::getAccountData).toList();
    }

    public void AddAddress(Address address) {
        if (address == null) {
            throw new NullPointerException("address"); // TODO : NPE
        }

        if (this.address != null) {
            throw UserException.addressIntroduced(id);
        }

        this.address = address;
    }

    public void AddPassport(Passport passport) {
        if (passport == null) {
            throw new NullPointerException("passport"); // TODO : NPE
        }

        if (this.passport != null) {
            throw UserException.passportIntroduced(id);
        }

        this.passport = passport;
    }

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
