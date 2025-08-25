
package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Boolean. Conversion is done if the trimmed String instance is equal
 * to "true" or "false", and it uses the Boolean(String aString) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Boolean
 */
public class StringToBooleanConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToBooleanConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Boolean .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Boolean.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the trimmed String value to a Boolean if it is equal
     * to "true" or "false" (any case supported). or if it is equal to an
     * integer 0 = false non zero = true;
     *
     * @param aString the String to be converted
     * @return Object the String converted to a Boolean * @exception
     * IllegalArgumentException thrown if the String to convert is not
     * in the correct format
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        if (aString.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (aString.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }

        // Suuport integer representation of a boolean
        try {
            int iVal = Integer.parseInt(aString);

            if (iVal == 0) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
}
