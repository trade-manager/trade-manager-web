package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Byte. Conversion is done if the String instance is in the correct
 * format, and it uses the Byte(String aString) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Byte
 */
public class StringToByteConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToByteConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Byte .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Byte.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a Byte by using the Byte(String
     * aString) constructor.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a Byte * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        return Byte.valueOf(aString);
    }
}
