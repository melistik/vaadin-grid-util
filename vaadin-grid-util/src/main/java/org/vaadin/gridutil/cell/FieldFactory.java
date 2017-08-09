package org.vaadin.gridutil.cell;

import com.vaadin.data.Binder;
import com.vaadin.data.Converter;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import static org.vaadin.gridutil.cell.GridCellFilter.STYLENAME_GRIDCELLFILTER;

/**
 * Created by georg.hicker on 03.08.2017.
 */
public class FieldFactory {

    public static <T> TextField genNumberField(Binder<T> binder, String propertyId, Converter converter, String inputPrompt) {
        final TextField field = new TextField();
        field.setWidth("100%");
        field.addStyleName(STYLENAME_GRIDCELLFILTER);
        field.addStyleName(ValoTheme.TEXTFIELD_TINY);
        field.addValueChangeListener(e -> {
            if (binder.isValid()) {
                field.setComponentError(null);
            }
        });
        binder.forField(field)
                .withNullRepresentation("")
                // .withValidator(text -> text != null && text.length() > 0, "invalid")
                .withConverter(converter)
                .bind(propertyId);
        field.setPlaceholder(inputPrompt);
        return field;
    }

    public static <T> DateField genDateField(Binder<T> binder, String propertyId, final java.text.SimpleDateFormat dateFormat) {
        DateField dateField = new DateField();

        binder.bind(dateField, propertyId);
        if (dateFormat != null) {
            dateField.setDateFormat(dateFormat.toPattern());
        }
        dateField.setWidth("100%");

        dateField.setResolution(DateResolution.DAY);
        dateField.addStyleName(STYLENAME_GRIDCELLFILTER);
        dateField.addStyleName(ValoTheme.DATEFIELD_TINY);
        dateField.addValueChangeListener(e -> {
            if (binder.isValid()) {
                dateField.setComponentError(null);
            }
        });
        return dateField;
    }
}
