package org.trade.core.conversion;

/**
 * This class converts instances of java.lang.Object to instances of
 * java.lang.String. Conversion is done using the toString() method of the
 * java.lang.Object class.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see Object
 */
public class ObjectToStringConverter implements IJavaTypeConverter {
    /**
     * Default constructor.
     */
    public ObjectToStringConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method is used by the JavaTypeTranslator to convert a source object
     * of type java.lang.Object to an instance of type java.lang.String.
     * <p>
     * Conversion is done using the toString() method of the java.lang.Object
     * class.
     *
     * @param valueToConvert the value to convert
     * @return Object the String representation of the valueToConvert
     * * @exception IllegalArgumentException which should never be
     * thrown * @see
     * org.trade.core.conversion.IJavaTypeConverter#convert(Object)
     */
    public Object convert(Object valueToConvert) throws IllegalArgumentException {
        if (null == valueToConvert) {
            return (null);
        }

        return valueToConvert.toString();
    }

    /**
     * This method returns the source type or class that the converter converts
     * from. In this case java.lang.Object .
     *
     * @return Class the class of the source value which will be converted
     * * @see
     * org.trade.core.conversion.IJavaTypeConverter#getSourceType()
     */
    public Class<?> getSourceType() {
        return Object.class;
    }

    /**
     * This method returns the target type or class that the converter converts
     * to. In this case java.lang.String .
     *
     * @return Class the class the source value will be converted to * @see
     * org.trade.core.conversion.IJavaTypeConverter#getTargetType()
     */
    public Class<?> getTargetType() {
        return String.class;
    }
}
