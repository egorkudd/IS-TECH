package is.technologies.models;

import is.technologies.exceptions.DepositPercentException;

import java.util.List;
import java.util.StringJoiner;
import java.util.TreeMap;

public class DepositPercents {
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

        for (double percent : data.values()) {
            if (percent <= 0) {
                throw DepositPercentException.incorrectPercent(percent);
            }
        }

        this.data = data;
    }

    public TreeMap<Money, Double> getData() {
        var copyData = new TreeMap<Money, Double>();
        for (Money dataKey : data.keySet()) {
            copyData.put(dataKey, data.get(dataKey));
        }

        return copyData;
    }

    public double GetPercent(Money money) {
        return data.get(data.keySet().stream()
                .filter(x -> x.compareTo(money) < 1)
                .reduce((first, second) -> second).get()
        );
    }

    // TODO : Возможно потребуется переобределить клоинрование

    @Override
    public String toString() {
        List<String> values = data.keySet().stream()
                .map(key -> "(%s - %s %%)".formatted(key, data.get(key) * 100))
                .toList();
        StringJoiner joiner = new StringJoiner(", ");
        values.forEach(joiner::add);
        return joiner.toString();
    }
}
