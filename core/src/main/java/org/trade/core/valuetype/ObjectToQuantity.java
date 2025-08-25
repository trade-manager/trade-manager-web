package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslatorException;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ObjectToQuantity implements IJavaDynamicTypeConverter {
    /**
     * Default constructor.
     */
    public ObjectToQuantity() {
    }

    /**
     * Method convert.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Quantity rVal;

        if (valueToConvert == null) {
            throw new JavaTypeTranslatorException("Null passed toObjectToMoney.convert()");
        }

        // If we get a string we will convert it using the default money format
        // MONEY_NONNEGATIVE_11_2.
        if (valueToConvert instanceof String stringValue) {
            rVal = new Quantity(stringValue);
        } else {
            throw new JavaTypeTranslatorException("internal error parsing value");
        }

        return (rVal);
    }

    // from com.cbsinc.esc.devtools.conversion.IJavaDynamicTypeConverter

    /**
     * Method supportsConversion.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return boolean
     */
    public boolean supportsConversion(Class<?> targetType, Object valueToConvert) {
        // TODO: This is not strictly correct.

        return (Quantity.class.equals(targetType));
    }
}
