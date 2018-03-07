package org.vaadin.gridutil.cell;

import com.vaadin.data.Converter;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import org.vaadin.gridutil.cell.filter.BetweenFilter;
import org.vaadin.gridutil.cell.filter.EqualFilter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by georg.hicker on 03.08.2017.
 */
public class RangeCellFilterComponentFactory {

    public static <T extends Number & Comparable<? super T>> RangeCellFilterComponentTyped<T, TextField, HorizontalLayout> createForNumberType(String propertyId, Class<T> propertyType, String converterErrorMessage, String smallestInputPrompt, String biggestInputPrompt, BiConsumer<SerializablePredicate<T>, String> filterReplaceConsumer, Consumer<String> filterRemoveConsumer) {
        if (Integer.class.equals(propertyType)
                || Long.class.equals(propertyType)
                || Double.class.equals(propertyType)
                || Float.class.equals(propertyType)
                || BigInteger.class.equals(propertyType)
                || BigDecimal.class.equals(propertyType)) {
            return new RangeCellFilterComponentTyped<T, TextField, HorizontalLayout>() {
                private TextField smallest, biggest;

                @Override
                public TextField getSmallestField() {
                    if (smallest == null) {
                        smallest = genNumberField(SMALLEST, NumberUtil.getConverter(propertyType, converterErrorMessage), smallestInputPrompt);
                    }
                    return smallest;
                }

                @Override
                public TextField getBiggestField() {
                    if (biggest == null) {
                        biggest = genNumberField(BIGGEST, NumberUtil.getConverter(propertyType, converterErrorMessage), biggestInputPrompt);
                    }
                    return biggest;
                }

                private TextField genNumberField(final String propertyId, final Converter converter, final String inputPrompt) {
                    return FieldFactory.genNumberField(getBinder(), propertyId, converter, inputPrompt);
                }

                @Override
                public HorizontalLayout layoutComponent() {
                    getHLayout().addComponent(getSmallestField());
                    getHLayout().addComponent(getBiggestField());
                    getHLayout().setExpandRatio(getSmallestField(), 1);
                    getHLayout().setExpandRatio(getBiggestField(), 1);

                    initBinderValueChangeHandler();

                    return getHLayout();
                }

                private void initBinderValueChangeHandler() {
                    getBinder().addValueChangeListener(e -> {
                        final T smallest = getBinder().getBean()
                                .getSmallest();
                        final T biggest = getBinder().getBean()
                                .getBiggest();
                        if (smallest != null || biggest != null) {
                            //final T smallestValue = checkObject(smallest);
                            //final T biggestValue = checkObject(biggest);
                            if (smallest != null && biggest != null && smallest.equals(biggest)) {
                                filterReplaceConsumer.accept(
                                        new EqualFilter(smallest),
                                        propertyId);
                            } else {
                                filterReplaceConsumer.accept(
                                        new BetweenFilter(
                                                (smallest != null ? smallest : NumberUtil.getBoundaryValue(propertyType, false)),
                                                (biggest != null ? biggest : NumberUtil.getBoundaryValue(propertyType, true))
                                        ),
                                        propertyId);
                            }
                        } else {
                            filterRemoveConsumer.accept(propertyId);
                        }

                    });
                }

                private T checkObject(Object value) {
                    if (value != null && value.getClass()
                            .equals(propertyType)) {
                        return propertyType.cast(value);
                    }
                    return null;
                }

                @Override
                public void clearFilter() {
                    getBinder().setBean(new TwoValueObjectTyped<T>());
                }
            };
        }
        return null;
    }
}