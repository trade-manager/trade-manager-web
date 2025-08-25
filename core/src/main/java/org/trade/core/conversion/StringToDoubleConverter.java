package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Double. Conversion is done if the String instance is in the correct
 * format, and it uses the Double(String aString) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Double
 */
public class StringToDoubleConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToDoubleConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Double .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Double.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a Double by using the
     * Double(String aString) constructor.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a Double * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        return Double.valueOf(aString);
    }
}
