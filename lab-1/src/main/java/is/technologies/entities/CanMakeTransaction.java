package is.technologies.entities;

import is.technologies.enums.MoneyActionMode;
import is.technologies.models.Money;

public interface CanMakeTransaction {
    boolean makeTransaction(Money money, MoneyActionMode mode);
}
