package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 *
 */
public class ObjectToDecimal implements IJavaDynamicTypeConverter {
    /**
     * Default constructor.
     */
    public ObjectToDecimal() {
    }

    /**
     * Method convert.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Decimal rVal;

        if (valueToConvert == null) {
            throw new JavaTypeTranslatorException("Null passed toObjectToDecimal.convert()");
        }

        // If we get a string we will convert it using the default money format
        // MONEY_NONNEGATIVE_11_2.
        if (valueToConvert instanceof String stringValue) {
            rVal = new Decimal(stringValue, 2);
        } else {
            throw new JavaTypeTranslatorException("internal error parsing value");
        }

        return (rVal);
    }

    /**
     * Method supportsConversion.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return boolean
     */
    public boolean supportsConversion(Class<?> targetType, Object valueToConvert) {
        // TODO: This is not strictly correct.

        return (Decimal.class.equals(targetType));
    }
}
