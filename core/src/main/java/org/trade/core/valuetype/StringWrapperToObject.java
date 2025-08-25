package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 *
 */
public class StringWrapperToObject implements IJavaDynamicTypeConverter {
    /**
     * Default constructor.
     */
    public StringWrapperToObject() {
    }

    //
    // IJavaDynamicTypeConverter interface methods
    //

    /**
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Object rVal;

        if (valueToConvert instanceof StringWrapper) {
            rVal = JavaTypeTranslator.convert(targetType, valueToConvert.toString());
        } else {
            throw new JavaTypeTranslatorException("Value to convert must be a StringWrapper");
        }

        return (rVal);
    }

    /**
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return boolean
     */
    public boolean supportsConversion(Class<?> targetType, Object valueToConvert) {
        return (valueToConvert instanceof StringWrapper);
    }
}
