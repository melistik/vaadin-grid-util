package org.vaadin.gridutil.cell.filter;

/**
 * Created by marten on 22.02.17.
 */
public class SimpleStringFilter extends EqualFilter<String> {

    public final boolean ignoreCase;
    public final boolean onlyMatchPrefix;

    public SimpleStringFilter(String columnId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
        super(columnId, filterString);
        this.ignoreCase = ignoreCase;
        this.onlyMatchPrefix = onlyMatchPrefix;
    }

    @Override
    public String toString() {
        return "SimpleStringFilter{columnId='" + columnId + '\'' + "ignoreCase=" + ignoreCase + ", onlyMatchPrefix=" + onlyMatchPrefix + ", toCompare=" + toCompare + '}';
    }
}
