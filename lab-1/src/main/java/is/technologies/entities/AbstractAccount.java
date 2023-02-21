package is.technologies.entities;

import is.technologies.enums.AccountMode;
import is.technologies.models.AccountData;
import is.technologies.models.Config;
import is.technologies.models.Money;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class AbstractAccount implements CanMakeTransaction {
    protected UUID id;
    protected AccountMode mode;
    protected String bankName;
    protected Money money;
    protected double percent;
    protected Money highLimit;
    protected Money lowLimit;
    protected Money trustLimit;
    protected UUID userId;

    public AccountData getAccountData() {
        return new AccountData(id, bankName, mode, getMoney());
    }
    abstract Money getMoney();
    abstract void changeConfig(Config config);
}
