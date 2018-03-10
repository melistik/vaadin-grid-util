package org.vaadin.gridutil.datasource;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.SerializablePredicate;

import java.util.stream.Stream;

public class FilteredDataProvider<T> extends AbstractDataProvider<T, SerializablePredicate<T>> {
    private final DataProvider<T, SerializablePredicate<T>> dataProvider;

    private SerializablePredicate<T> filter;

    public FilteredDataProvider(DataProvider<T, SerializablePredicate<T>> dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public boolean isInMemory() {
        return dataProvider.isInMemory();
    }

    @Override
    public int size(Query<T, SerializablePredicate<T>> query) {
        return this.dataProvider.size(filterQuery(query));
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        return this.dataProvider.fetch(filterQuery(query));
    }

    private Query<T, SerializablePredicate<T>> filterQuery(Query<T, SerializablePredicate<T>> query) {
        return new Query<>(query.getOffset(), query.getLimit(), query.getSortOrders(), query.getInMemorySorting(), filter);
    }

    public SerializablePredicate<T> getFilter() {
        return filter;
    }

    public void setFilter(SerializablePredicate<T> filter) {
        this.filter = filter;
        this.refreshAll();
    }
}
