package org.trade.core.conversion;

import java.sql.Date;

/**
 * This class converts instances of java.lang.String to instances of
 * java.sql.Date. Conversion is done if the String instance is in the correct
 * format, and it uses the valueOf() method of the java.sql.Date class.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Date
 */
public class StringToSQLDateConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToSQLDateConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.sql.Date .
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
     * This method converts the String value to a java.sql.Date by using the
     * valueOf() method of the java.sql.Date class.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a java.sql.Date * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        return Date.valueOf(aString);
    }
}
