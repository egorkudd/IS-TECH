package is.technologies.entities;

import is.technologies.enums.MoneyActionMode;
import is.technologies.models.Money;

/**
 * Interface for opportunity to make transactions
 */
public interface CanMakeTransaction {
    boolean makeTransaction(Money money, MoneyActionMode mode);
}
