package is.technologies.models;

import lombok.EqualsAndHashCode;

/**
 * Money class, contains arithmetic methods
 */
@EqualsAndHashCode
public class Money implements Comparable {
    public static final Money ZERO = new Money(0);
    public static final Money MAX = new Money(Double.MAX_VALUE);
    public static final Money MIN = new Money(Double.MIN_VALUE);

    private final long count;

    public Money(double count) {
        this.count = Math.round(count * 100);
    }

    /**
     * Gets money
     * @return double
     */
    public double getMoneyValue() {
        return (double) count / 100;
    }

    /**
     * Plus operation
     * @param money to add
     * @return Money
     */
    public Money plus(Money money) {
        return new Money((double) (count + money.count) / 100);
    }

    /**
     * Minus operation
     * @param money to subtract
     * @return Money
     */
    public Money minus(Money money) {
        return new Money((double) (count - money.count) / 100);
    }

    /**
     * Multiply operation
     * @param k to multiply
     * @return Money
     */
    public Money multiply(double k) {
        return new Money( count * k / 100);
    }

    public int compareTo(Money money) {
        return Long.compare(count, money.count);
    }

    @Override
    public int compareTo(Object o) {
        return compareTo((Money) o);
    }

    @Override
    public String toString() {
        return String.valueOf(count / 100);
    }
}
