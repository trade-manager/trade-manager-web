
package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Long. Conversion is done if the String instance is in the correct
 * format, and it uses the Long(String aString) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Long
 */
public class StringToLongConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToLongConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Long .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Long.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a Long by using the Long(String
     * aString) constructor.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a Long * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        return Long.valueOf(aString);
    }
}
