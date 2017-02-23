package org.vaadin.gridutil.cell.filter;

import com.vaadin.server.SerializablePredicate;

/**
 * Created by marten on 22.02.17.
 */
public class EqualFilter implements SerializablePredicate<Object> {

    final Object toCompare;

    public EqualFilter(Object toCompare) {
        this.toCompare = toCompare;
    }

    @Override
    public boolean test(Object value) {
        if (value == null && toCompare == null) {
            return true;
        }
        if (value == null || toCompare == null) {
            return false;
        }
        return value.equals(toCompare);
    }
}
