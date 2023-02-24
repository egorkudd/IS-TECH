package is.technologies.models;

import is.technologies.enums.AccountMode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * Account's data class which contains account's data for user
 */
@Getter
@ToString
public class AccountData {
    private final UUID id;
    private final String bankName;
    private final AccountMode mode;
    private final Money money;

    public AccountData(UUID id, String bankName, AccountMode mode, Money money) {
        this.id = id;
        this.bankName = bankName;
        this.mode = mode;
        this.money = money;
    }
}
