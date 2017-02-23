package org.vaadin.gridutil;

import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.ValueProvider;

/**
 * Created by marten on 22.02.17.
 */
public final class ShortValueProvider {

    public static ValueProvider getter(Class type, String propertyId) {
        return ((PropertyDefinition)BeanPropertySet.get(type).getProperty(propertyId).get()).getGetter();
    }
}
