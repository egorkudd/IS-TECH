package is.technologies.models;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Banks' time class
 */
public class BankTime {
    @Getter
    private final LocalDateTime time;

    public BankTime(LocalDateTime time) {
        this.time = time;
    }
}

