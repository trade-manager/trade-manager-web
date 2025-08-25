package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ObjectToStringWrapper implements IJavaDynamicTypeConverter {
    /**
     * Default constructor.
     */
    public ObjectToStringWrapper() {
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

        if (StringWrapper.class.isAssignableFrom(targetType)) {
            try {

                StringWrapper vt = (StringWrapper) targetType.getDeclaredConstructor().newInstance();

                if (valueToConvert instanceof String) {
                    vt.setValue((String) valueToConvert);
                } else {
                    throw new JavaTypeTranslatorException(
                            "The ObjectToStringWrapper convertor only supports strings at the moment");
                }

                rVal = vt;
            } catch (Exception ex) {
                throw new JavaTypeTranslatorException(ex, "Unable to set value for StringWrapper");
            }
        } else {
            throw new JavaTypeTranslatorException("Target type must be a StringWrapper");
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
        return (StringWrapper.class.isAssignableFrom(targetType));
    }
}
