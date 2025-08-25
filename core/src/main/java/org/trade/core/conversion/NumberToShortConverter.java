package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.Number to instances of
 * java.lang.Short. Conversion is done using the shortValue() method of the
 * java.lang.Number class and its subclasses.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Number
 */
public class NumberToShortConverter extends NumberToNumberConverter {
    /**
     * Default constructor.
     */
    public NumberToShortConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.Short .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return Short.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method converts the Number value to a Short by using the
     * shortValue() method of the java.lang.Number class.
     *
     * @param aNumber the number to be converted
     * @return Short the Number converted to a Short
     */
    protected Number getConvertedNumber(Number aNumber) {
        return aNumber.shortValue();
    }
}
