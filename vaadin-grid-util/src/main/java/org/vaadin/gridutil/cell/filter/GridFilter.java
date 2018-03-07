package org.vaadin.gridutil.cell.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GridFilter {

    private final List<CellFilter> cellFilters = new ArrayList<>();

    public void addCellFilter(CellFilter cellFilter) {
        cellFilters.add(cellFilter);
    }

    public List<CellFilter> getCellFilters() {
        return cellFilters;
    }

    public Set<String> getFilterColumns() {
        return cellFilters.stream().map(f -> f.columnId).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "GridFilter{" + "cellFilters=" + cellFilters + '}';
    }
}
