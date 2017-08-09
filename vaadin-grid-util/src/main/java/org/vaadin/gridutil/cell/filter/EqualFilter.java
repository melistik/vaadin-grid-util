package org.vaadin.gridutil.cell.filter;

import com.vaadin.server.SerializablePredicate;

/**
 * Created by marten on 22.02.17.
 */
public class EqualFilter<T> implements SerializablePredicate<T> {

    final T toCompare;

    public EqualFilter(T toCompare) {
        this.toCompare = toCompare;
    }

    @Override
    public boolean test(T value) {
        if (value == null && toCompare == null) {
            return true;
        }
        if (value == null || toCompare == null) {
            return false;
        }
        return value.equals(toCompare);
    }
}
