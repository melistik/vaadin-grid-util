package org.vaadin.gridutil.converter;

import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

/**
 * SimpleStringConverter shorten the Converter a lot for simple String or HTML presentations<br>
 * mainly the convertToModel return's null!
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public abstract class SimpleStringConverter<MODEL> implements Converter<String, MODEL> {

	private final Class<MODEL> type;

	/**
	 * init a short version of a converter
	 * 
	 * @param type
	 *            the type of the model that should get converted to String
	 */
	public SimpleStringConverter(final Class<MODEL> type) {
		this.type = type;
	}

	@Override
	public MODEL convertToModel(final String value, final Class<? extends MODEL> targetType, final Locale locale)
			throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
		return null;
	}

	@Override
	public Class<MODEL> getModelType() {
		return this.type;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
