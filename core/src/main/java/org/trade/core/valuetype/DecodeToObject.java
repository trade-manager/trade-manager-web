package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.conversion.JavaTypeTranslatorException;

/**
 * This class converts instances of
 * com.cbsinc.esc.devtoold.valuetype.base.CodeDecodeValueType to instances of
 * the object code they where set up with. The conversion will return the code
 * the valuetype is representing.
 *
 * @author Simon Allen
 * @version $Id: DecodeToObject.java,v 1.1 2001/11/06 16:51:55 simon Exp $
 */
public class DecodeToObject implements IJavaDynamicTypeConverter {

    public DecodeToObject() {
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

        if (valueToConvert instanceof Decode) {
            rVal = ((Decode) valueToConvert).getCode();
            rVal = JavaTypeTranslator.convert(targetType, rVal);
        } else {
            throw new JavaTypeTranslatorException(
                    "Value to convert must be a com.aceva.devtools.valuetype.base.Decode");
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

        return (valueToConvert instanceof Decode);
    }
}
