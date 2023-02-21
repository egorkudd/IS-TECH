package is.technologies.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Money implements Comparable {
    public static final Money ZERO = new Money(0);
    public static final Money MAX = new Money(Double.MAX_VALUE);
    public static final Money MIN = new Money(Double.MIN_VALUE);

    private final long count;

    public Money(double count) {
        this.count = Math.round(count * 100);
    }

    public int getMoneyValue() {
        return (int) (count / 100);
    }

    public Money plus(Money money) {
        return new Money((double) (count + money.count) / 100);
    }

    public Money minus(Money money) {
        return new Money((double) (count - money.count) / 100);
    }

    public Money multiply(int k) {
        return new Money((double) count * k / 100);
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
