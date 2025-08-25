
package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Float. Conversion is done if the String instance is in the correct
 * format, and it uses the Float(String aString) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Float
 */
public class StringToFloatConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToFloatConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Float .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Float.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a Float by using the
     * Float(String aString) constructor.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a Float * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        return Float.valueOf(aString);
    }
}
