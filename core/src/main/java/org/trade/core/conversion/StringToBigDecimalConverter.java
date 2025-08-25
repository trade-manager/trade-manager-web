package org.trade.core.conversion;

import java.math.BigDecimal;

/**
 * This class converts instances of java.lang.String to instances of
 * java.util.Date. Conversion is done if the String instance is in the correct
 * format, and it uses the parse() method of the java.text.DateFormat class.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see java.text.DateFormat
 */
public class StringToBigDecimalConverter extends StringToObjectConverter {
    /**
     * Default constructor.
     */
    public StringToBigDecimalConverter() {
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
        return BigDecimal.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the String value to a java.math.BigDecimal by using
     * the BigDecimal(String aString) constructor
     *
     * @param aString the String to be converted
     * @return Object the String converted to a BigDecimal * @exception
     * NumberFormatException thrown if the String to convert is not in
     * the correct format
     */
    protected Object getConvertedString(String aString) throws NumberFormatException {
        try {
            if ((aString == null) || (aString.trim().isEmpty())) {
                return null;
            } else {
                return new BigDecimal(aString);
            }
        } catch (NumberFormatException pe) {
            throw new NumberFormatException(pe.getMessage());
        }
    }
}
