package org.vaadin.gridutil.cell.filter;

/**
 * Created by marten on 22.02.17.
 */
public class EqualFilter<T> extends CellFilter<T> {

    public final T toCompare;

    public EqualFilter(String columnId, T toCompare) {
        super(columnId);
        this.toCompare = toCompare;
    }

    @Override
    public String toString() {
        return "EqualFilter{columnId='" + columnId + '\'' + ", toCompare=" + toCompare + '}';
    }
}
