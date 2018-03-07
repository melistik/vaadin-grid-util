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
        this.ignoreCase = ignoreCase;
        // ignoreCase has to be applied to filterstring too, otherwise uppercase input won't work
        this.filterString = this.ignoreCase ? filterString.toLowerCase() : filterString;
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
