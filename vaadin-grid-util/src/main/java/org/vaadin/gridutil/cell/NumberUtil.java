package org.vaadin.gridutil.cell;

import com.vaadin.data.Converter;
import com.vaadin.data.converter.*;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by georg.hicker on 03.08.2017.
 */
public class NumberUtil {
    private NumberUtil() {
    }

    public static <T extends Number & Comparable<? super T>> Converter getConverter(final Class<T> type, final String converterErrorMessage) {
        if (Integer.class.equals(type)) {
            return new StringToIntegerConverter(converterErrorMessage);
        } else if (Double.class.equals(type)) {
            return new StringToDoubleConverter(converterErrorMessage);
        } else if (Float.class.equals(type)) {
            return new StringToFloatConverter(converterErrorMessage);
        } else if (BigInteger.class.equals(type)) {
            return new StringToBigIntegerConverter(converterErrorMessage);
        } else if (BigDecimal.class.equals(type)) {
            return new StringToBigDecimalConverter(converterErrorMessage);
        } else {
            return new StringToLongConverter(converterErrorMessage);
        }
    }

    public static <T extends Number & Comparable<? super T>> T getBoundaryValue(final Class<T> type, final boolean max) {
        if (Integer.class.equals(type)) {
            return (T) new Integer(max ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        } else if (Double.class.equals(type)) {
            return (T) new Double(max ? Double.MAX_VALUE : Double.MIN_VALUE);
        } else if (Float.class.equals(type)) {
            return (T) new Float(max ? Float.MAX_VALUE : Float.MIN_VALUE);
        } else if (BigInteger.class.equals(type)) {
            return (T) (max ? new BigInteger(String.valueOf(Long.MAX_VALUE)) : new BigInteger(String.valueOf(Long.MIN_VALUE)));
        } else if (BigDecimal.class.equals(type)) {
            return (T) (max ? new BigDecimal(String.valueOf(Long.MAX_VALUE)) : new BigDecimal(String.valueOf(Long.MIN_VALUE)));
        } else {
            return (T) new Long(max ? Long.MAX_VALUE : Long.MIN_VALUE);
        }
    }
}
