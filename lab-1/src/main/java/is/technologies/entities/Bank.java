package is.technologies.entities;

import is.technologies.models.Config;
import lombok.Getter;

public class Bank {
    @Getter
    private final String name;
    private Config config;

    public Bank(String name, Config config) {
        if (!name.isBlank()) {
            this.name = name;
        } else {
            throw new NullPointerException("name"); // TODO : NPE
        }

        if (config == null) throw new NullPointerException("config"); // TODO : NPE
        this.config = config.clone();
    }

    // TODO : Сделать подписку аккаунтов на банки,
    //        чтобы при изменении конфига в аккаунтах он тоже менялся

    public Config getConfig() {
        return config.clone();
    }

    public void changeConfig(Config config) {
        if (config != null) {
            this.config = config;
        } else {
            throw new NullPointerException("config"); // TODO : NPE
        }

        // TODO : Здесь нужно отправлять аккаунтам изменение
    }
}