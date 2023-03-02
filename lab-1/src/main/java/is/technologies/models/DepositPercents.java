package is.technologies.models;

import is.technologies.exceptions.DepositPercentException;
import lombok.ToString;

import java.util.TreeMap;

/**
 * Deposits percents' data class, contains percent intervals
 */
@ToString
public class DepositPercents implements Cloneable {
    private final TreeMap<Money, Double> data;

    public DepositPercents(TreeMap<Money, Double> data) {
        if (data.size() < 1) {
            throw new IndexOutOfBoundsException("Intervals' count is 0");
        }

        if (!data.containsKey(Money.ZERO)) {
            throw DepositPercentException.incorrectFirstLowLimit(
                    data.keySet().stream().min(Money::compareTo).orElseThrow()
            );
        }

        data.values().stream()
                .mapToDouble(percent -> percent)
                .filter(percent -> percent <= 0)
                .findAny()
                .ifPresent(
                        percent -> {
                            throw DepositPercentException.incorrectPercent(percent);
                        }
                );
        this.data = (TreeMap<Money, Double>) data.clone();
    }

    /**
     * Gets copy of deposit intervals
     *
     * @return deposit intervals
     */
    public TreeMap<Money, Double> getData() {
        return (TreeMap<Money, Double>) data.clone();
    }

    public double getPercent(Money money) {
        return data.get(data.keySet().stream()
                .filter(x -> x.compareTo(money) < 1)
                .reduce((first, second) -> second).get()
        );
    }

    @Override
    public DepositPercents clone() {
        return new DepositPercents((TreeMap<Money, Double>) data.clone());
    }
}
