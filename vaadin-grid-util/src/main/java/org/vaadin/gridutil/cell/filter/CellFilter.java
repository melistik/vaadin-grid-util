package org.vaadin.gridutil.cell.filter;

public abstract class CellFilter<T> {

    public final String columnId;

    protected CellFilter(String columnId, Object... values) {
        this.columnId = columnId;
    }
}
