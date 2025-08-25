package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaFormatForObject;
import org.trade.core.conversion.JavaTypeTranslatorException;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ObjectToDate implements IJavaDynamicTypeConverter {

    public ObjectToDate() {
    }

    /**
     * Method convert.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object)
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Date rVal;

        if (!Date.class.equals(targetType)) {
            throw new JavaTypeTranslatorException("The target type must be an Date");
        }

        if (valueToConvert == null) {
            throw new JavaTypeTranslatorException("The object to be converted cannot be null");
        }

        String dateFormat = null;

        if (valueToConvert instanceof JavaFormatForObject) {

            valueToConvert = ((JavaFormatForObject) valueToConvert).getForObject();
            dateFormat = ((JavaFormatForObject) valueToConvert).getFormat();
        }

        if (valueToConvert instanceof String val) {

            rVal = new Date(val, dateFormat); // dateFormat may be null, but
            // that is okay
        } else {
            throw new JavaTypeTranslatorException(
                    "The object to be converted cannot be a " + valueToConvert.getClass());
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

        return (Date.class.equals(targetType));
    }
}
