
package org.trade.core.conversion;

/**
 * This is an abstract class which is inherited by all java.lang.Number to
 * java.lang.Number subclass converter classes.
 *
 * @author Simon Allen
 */
public abstract class NumberToNumberConverter implements IJavaTypeConverter {
    /**
     * Default constructor.
     */
    public NumberToNumberConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method is used by the JavaTypeTranslator to convert a source object
     * of type java.lang.Number to an instance of a subclass of type
     * java.lang.Number.
     * <p>
     * Subclasses must implement the getConvertedNumber() method for it to work
     * properly.
     *
     * @param valueToConvert the java.lang.Number value to convert
     * @return Object the converted value * @exception IllegalArgumentException
     * thrown if the valueToConvert is not of type java.lang.Number
     * * @see
     * org.trade.core.conversion.IJavaTypeConverter#convert(Object)
     */
    public Object convert(Object valueToConvert) throws IllegalArgumentException {
        if (valueToConvert instanceof Number) {
            return getConvertedNumber((Number) valueToConvert);
        }

        throw new IllegalArgumentException("The source object must be of type: " + getSourceType().getName());
    }

    /**
     * This method returns the source type or class that the converter converts
     * from. In this case java.lang.Number .
     *
     * @return Class the class of the source value which will be converted
     * * @see
     * org.trade.core.conversion.IJavaTypeConverter#getSourceType()
     */
    public Class<?> getSourceType() {
        return Number.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method should be implemented by a subclass such that it returns the
     * converted value of the Number.
     *
     * @param aNumber the number to be converted
     * @return Number the converted Number
     */
    protected abstract Number getConvertedNumber(Number aNumber);
}
