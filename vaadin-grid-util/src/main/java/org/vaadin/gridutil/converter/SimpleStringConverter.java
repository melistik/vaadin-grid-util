package org.vaadin.gridutil.converter;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;


/**
 * SimpleStringConverter shorten the Converter a lot for simple String or HTML presentations<br>
 * mainly the convertToModel return's null!
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public abstract class SimpleStringConverter<MODEL> implements Converter<String, MODEL> {

    @Override
    public Result<MODEL> convertToModel(String value, ValueContext context) {
        return null;
    }

}
