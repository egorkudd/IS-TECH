package is.technologies.models;

import is.technologies.exceptions.BankTimerException;

import java.time.LocalDateTime;

/**
 * Banks' timer class is time system
 */
public class BankTimer {
    private static int daysToShift = 0;

    private BankTimer() {}

    /**
     * Gets real time plus time to shift
     * @return BankTime
     */
    public static BankTime getTime() {
        return new BankTime(LocalDateTime.now().plusDays(daysToShift));
    }

    /**
     * Add days to shift
     * @param daysToShift to add to real time
     * @exception BankTimerException if days' count is less than 0
     */
    public static void rewindTime(int daysToShift) {
        if (daysToShift > 0) {
            BankTimer.daysToShift += daysToShift;
        } else {
            throw BankTimerException.incorrectShiftTime(daysToShift);
        }
    }
}