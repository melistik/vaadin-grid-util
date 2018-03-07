package org.vaadin.gridutil.cell.filter;

import java.util.Set;

public class ChoicesFilter<T> extends CellFilter<Set<T>> {

    public final Set<T> choices;

    public ChoicesFilter(String columnId, Set<T> choices) {
        super(columnId);
        this.choices = choices;
    }

    @Override
    public String toString() {
        return "ChoicesFilter{columnId='" + columnId + '\'' + ", choices=" + choices + '}';
    }
}
