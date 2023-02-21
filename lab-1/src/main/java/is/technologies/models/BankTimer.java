package is.technologies.models;

import java.time.LocalDateTime;

public class BankTimer {
    private static int daysToShift = 0;

    public static BankTime getTime() {
        return new BankTime(LocalDateTime.now().plusDays(daysToShift));
    }

    public static void rewindTime(int daysToShift) {
        BankTimer.daysToShift += daysToShift;
    }
}
// TODO : do singleton

