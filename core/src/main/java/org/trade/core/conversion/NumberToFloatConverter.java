package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.Number to instances of
 * java.lang.Float. Conversion is done using the floatValue() method of the
 * java.lang.Number class and its subclasses.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Number
 */
public class NumberToFloatConverter extends NumberToNumberConverter {
    /**
     * Default constructor.
     */
    public NumberToFloatConverter() {
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
     * This method converts the Number value to a Float by using the
     * floatValue() method of the java.lang.Number class.
     *
     * @param aNumber the number to be converted
     * @return Float the Number converted to a Float
     */
    protected Number getConvertedNumber(Number aNumber) {
        return aNumber.floatValue();
    }
}
