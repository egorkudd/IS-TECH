package is.technologies.models;

import is.technologies.enums.AccountMode;

import java.util.UUID;

/**
 * Account's data class which contains account's data for user
 */
public record AccountData(UUID id, String bankName, AccountMode mode, Money money) {
}
