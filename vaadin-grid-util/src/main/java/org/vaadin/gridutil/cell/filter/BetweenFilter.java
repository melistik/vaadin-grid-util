package org.vaadin.gridutil.cell.filter;

/**
 * Created by georg.hicker on 01.08.2017.
 */
public class BetweenFilter<T extends Comparable<? super T>> extends CellFilter<T> {

    public final T startValue;
    public final T endValue;

    public BetweenFilter(String columnId, T startValue, T endValue) {
        super(columnId);
        this.startValue = startValue;
        this.endValue = endValue;
    }

    @Override
    public String toString() {
        return "BetweenFilter{" + ", columnId='" + columnId + '\'' + ", startValue=" + startValue + ", endValue=" + endValue + '}';
    }
}
