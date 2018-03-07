package org.vaadin.gridutil.cell.filter;

import com.vaadin.server.SerializablePredicate;

/**
 * Created by georg.hicker on 01.08.2017.
 */
public class BetweenFilter<T extends Comparable<? super T>> implements SerializablePredicate<Comparable<T>> {
    private final T startValue;
    private final T endValue;

    public BetweenFilter(T startValue, T endValue) {
        this.startValue = startValue;
        this.endValue = endValue;
    }

    @Override
    public boolean test(Comparable<T> value) {
        if (value == null) {
            return startValue == null && endValue == null;
        }
        return isAfterStart(value) && isBeforeEnd(value);
    }

    private boolean isAfterStart(final Comparable<T> value) {
        return startValue == null || value.compareTo(startValue) >= 0;
    }

    private boolean isBeforeEnd(final Comparable<T> value) {
        return endValue == null || value.compareTo(endValue) <= 0;
    }

}
