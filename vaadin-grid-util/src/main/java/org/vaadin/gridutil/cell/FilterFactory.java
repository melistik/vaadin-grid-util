package org.vaadin.gridutil.cell;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializablePredicate;

public interface FilterFactory {

    /**
     * Creates a new predicate from the given predicate and value provider. This
     * allows using a predicate of the value providers return type with objects
     * of the value providers type.
     *
     * @param valueProvider
     *            the value provider to use
     * @param valueFilter
     *            the original predicate
     * @return the created predicate
     * See {@link com.vaadin.data.provider.InMemoryDataProviderHelpers#createValueProviderFilter(ValueProvider, SerializablePredicate)}
     */
    <T, V> SerializablePredicate<T> createValueProviderFilter(
            ValueProvider<T, V> valueProvider,
            SerializablePredicate<V> valueFilter);

}
