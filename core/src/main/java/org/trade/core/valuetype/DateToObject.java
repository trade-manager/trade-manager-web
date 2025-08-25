package org.trade.core.valuetype;

import org.trade.core.conversion.IJavaDynamicTypeConverter;
import org.trade.core.conversion.JavaFormatForObject;
import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.conversion.JavaTypeTranslatorException;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DateToObject implements IJavaDynamicTypeConverter {
    public DateToObject() {
    }

    /**
     * Method convert.
     *
     * @param targetType     Class<?>
     * @param valueToConvert Object
     * @return Object
     * Object)
     */
    public Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException {
        Object rVal;

        if (valueToConvert instanceof Date) {
            // If converting to a String want to send it back as a preformatted
            // string in GMT
            if (String.class.equals(targetType)) {
                rVal = valueToConvert.toString();
            } else {
                rVal = JavaTypeTranslator.convert(targetType, ((Date) valueToConvert).getDate());
            }
        } else if (valueToConvert instanceof JavaFormatForObject) {
            // If the target is a Formatted object
            // Get the object and translate it to a formatted date
            // representation
            // before trying to translate it to the target type
            Object getFor = ((JavaFormatForObject) valueToConvert).getForObject();
            String format = ((JavaFormatForObject) valueToConvert).getFormat();
            if (getFor instanceof Date) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String sDate = formatter.format(((Date) getFor).getDate());

                    rVal = JavaTypeTranslator.convert(targetType, sDate);
                } catch (Exception ex) {
                    throw new JavaTypeTranslatorException(ex,
                            "Unable to convert Date to GMT Date representation using format '" + format + "'");
                }
            } else {
                throw new JavaTypeTranslatorException("Value of JavaFormatForObject.getForObject() must be a Date");
            }
        } else {
            throw new JavaTypeTranslatorException("Value to convert must be a Date");
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

        return ((valueToConvert instanceof Date) || ((valueToConvert instanceof JavaFormatForObject)
                && (((JavaFormatForObject) valueToConvert).getForObject() instanceof Date)));
    }
}
