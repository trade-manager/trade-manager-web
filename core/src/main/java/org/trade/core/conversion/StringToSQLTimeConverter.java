package org.trade.core.conversion;

import java.sql.Time;

/**
 * This class converts instances of java.lang.String to instances of
 * java.sql.Time. Conversion is done if the String instance is in the correct
 * format, and it uses the valueOf() method of the java.sql.Time class.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Time
 */
public class StringToSQLTimeConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToSQLTimeConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.sql.Time .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Time.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a java.sql.Time by using the
     * valueOf() method of the java.sql.Time class.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a java.sql.Time * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        return Time.valueOf(aString);
    }
}
