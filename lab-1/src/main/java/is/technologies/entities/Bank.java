package is.technologies.entities;

import is.technologies.models.Config;
import lombok.Getter;

import java.util.ArrayList;

public class Bank {
    @Getter
    private final String name;
    private Config config;
    @Getter
    private final ArrayList<AbstractAccount> subscribers;

    public Bank(String name, Config config) {
        if (!name.isBlank()) {
            this.name = name;
        } else {
            throw new NullPointerException("name"); // TODO : NPE
        }

        if (config == null) throw new NullPointerException("config"); // TODO : NPE
        this.config = config.clone();
        subscribers = new ArrayList<>();
    }

    public Config getConfig() {
        return config.clone();
    }

    public void changeConfig(Config config) {
        if (config != null) {
            this.config = config;
        } else {
            throw new NullPointerException("config"); // TODO : NPE
        }

        subscribers.stream().forEach(account -> account.changeConfig(config));
    }
}
