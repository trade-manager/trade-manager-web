package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 * This class converts instances of java.lang.Object to instances of
 * com.cbsinc.esc.devtools.valuetype.base.CodeDecodeValueType. The conversion
 * will set the value passed as the code that this valuetype represents.
 *
 * @author Simon Allen
 */
public class ObjectToDecode implements IJavaDynamicTypeConverter {

    public ObjectToDecode() {
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

        if (Decode.class.isAssignableFrom(targetType)) {
            try {
                Decode vt = (Decode) targetType.getDeclaredConstructor().newInstance();

                // Assign the value for the valuetype
                vt.setValue(valueToConvert);

                rVal = vt;
            } catch (Exception ex) {
                throw new JavaTypeTranslatorException(ex, "Unable to set code");
            }
        } else {
            throw new JavaTypeTranslatorException("Target type must be a com.aceva.devtools.valuetype.base.Decode");
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

        return (Decode.class.isAssignableFrom(targetType));
    }
}
