package org.trade.core.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * This class converts instances of java.lang.String to instances of
 * java.util.Date. Conversion is done if the String instance is in the correct
 * format, and it uses the parse() method of the java.text.DateFormat class.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see DateFormat
 */
public class StringToDateConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToDateConverter() {
        // Default is short version of date and time:
        // MM/DD/YY HH:MI:SS AM|PM TZ
        // Timezone is assumed to be the local system timezone.
        m_dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.util.Date .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Date.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a java.util.Date by using the
     * parse() method of the java.text.DateFormat class.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a java.util.Date * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        if ((aString == null) || (aString.trim().isEmpty())) {
            return null; // Return A null Date
        } else {
            try {
                return m_dateFormatter.parse(aString);
            } catch (ParseException pe) {
                throw new IllegalArgumentException(pe.getMessage());
            }
        }
    }

    // Private
    DateFormat m_dateFormatter;
}
