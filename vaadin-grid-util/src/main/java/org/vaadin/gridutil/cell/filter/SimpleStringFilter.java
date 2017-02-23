package org.vaadin.gridutil.cell.filter;

import com.vaadin.server.SerializablePredicate;

/**
 * Created by marten on 22.02.17.
 */
public class SimpleStringFilter implements SerializablePredicate<String> {

    final String filterString;
    final boolean ignoreCase;
    final boolean onlyMatchPrefix;

    public SimpleStringFilter(String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
        this.filterString = filterString;
        this.ignoreCase = ignoreCase;
        this.onlyMatchPrefix = onlyMatchPrefix;
    }

    @Override
    public boolean test(String value) {
        if (filterString == null || value == null) {
            return false;
        }
        final String v = ignoreCase ? value.toString()
                .toLowerCase()
                : value.toString();

        if (onlyMatchPrefix) {
            if (!v.startsWith(filterString)) {
                return false;
            }
        } else {
            if (!v.contains(filterString)) {
                return false;
            }
        }
        return true;
    }
}
