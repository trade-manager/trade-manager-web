package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class MoneyToObject implements IJavaDynamicTypeConverter {

    public MoneyToObject() {
    }

    /**
     * Method convert.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Object rVal;

        if (valueToConvert instanceof Money) {
            rVal = JavaTypeTranslator.convert(targetType, valueToConvert.toString());
        } else {
            throw new JavaTypeTranslatorException("Value to convert must be a Money");
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

        return (valueToConvert instanceof Money);
    }
}
