package is.technologies.entities;

import is.technologies.models.Config;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bank class, contains accounts and config
 */
public class Bank {
    @Getter
    private final String name;
    private Config config;
    @Getter
    private final List<Account> subscribers;

    public Bank(String name, Config config) {
        if (name.isBlank()) {
            throw new IllegalStateException("Name is blank");
        }

        this.name = name;
        if (config == null) {
            throw new NullPointerException("config"); // TODO : NPE
        }

        this.config = config.clone();
        subscribers = new ArrayList<>();
    }

    public Config getConfig() {
        return config.clone();
    }

    /**
     * Change config for this bank and for all bank's accounts
     *
     * @param config to be changed
     */
    public void changeConfig(Config config) {
        if (config == null) {
            throw new NullPointerException("config"); // TODO : NPE
        }

        this.config = config;
        subscribers.stream().forEach(account -> account.changeConfig(config));
    }
}
