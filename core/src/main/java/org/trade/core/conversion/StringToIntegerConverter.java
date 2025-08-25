
package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Integer. Conversion is done if the String instance is in the
 * correct format, and it uses the Integer(String aString) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Integer
 */
public class StringToIntegerConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToIntegerConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Integer .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Integer.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a Integer by using the
     * Integer(String aString) constructor.
     *
     * @param aString the String to be converted
     * @return Object the String converted to an Integer * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        if ((aString == null) || (aString.trim().isEmpty())) {
            return null;
        } else {
            return Integer.valueOf(aString);
        }
    }
}
