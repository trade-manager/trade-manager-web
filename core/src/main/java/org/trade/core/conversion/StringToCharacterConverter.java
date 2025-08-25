
package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.String to instances of
 * java.lang.Chracter. Conversion is done by taking the character at index 0 of
 * the String and using the Character(char aChar) constructor.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Character
 */
public class StringToCharacterConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToCharacterConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Character .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Character.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a Character by using the
     * Character(char aChar) constructor on the character at index 0 of the
     * String.
     *
     * @param aString the String to be converted
     * @return Object the String converted to a Character * @exception
     * IllegalArgumentException should never be thrown
     */
    protected Object getConvertedString(String aString) throws IllegalArgumentException {
        try {
            return aString.charAt(0);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
