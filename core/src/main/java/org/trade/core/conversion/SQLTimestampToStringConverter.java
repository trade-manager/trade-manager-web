package org.trade.core.conversion;

/**
 * This class converts instances of java.sql.Timestamp to instances of
 * java.lang.String. Conversion is done using the toString() method of the
 * java.sql.Timestamp class.
 * <p>
 * An instance of this class is registered as a default converter with the
 * JavaTypeTranslator class.
 *
 * @author Simon Allen
 * @see java.sql.Timestamp
 */
public class SQLTimestampToStringConverter implements IJavaTypeConverter {
    /**
     * Default constructor.
     */
    public SQLTimestampToStringConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method is used by the JavaTypeTranslator to convert a source object
     * of type java.sql.Timestamp to an instance of type java.lang.String.
     * <p>
     * Conversion is done using the toString() method of the java.sql.Timestamp
     * class.
     *
     * @param valueToConvert the java.sql.Timestamp value to convert
     * @return Object the String representation of the valueToConvert
     * * @exception IllegalArgumentException thrown if the
     * valueToConvert is not of type java.sql.Timestamp * @see
     * org.trade.core.conversion.IJavaTypeConverter#convert(Object)
     */
    public Object convert(Object valueToConvert) throws IllegalArgumentException {
        if (valueToConvert instanceof java.sql.Timestamp) {
            return valueToConvert.toString();
        }

        throw new IllegalArgumentException("The source object must be of type: " + getSourceType().getName());
    }

    /**
     * This method returns the source type or class that the converter converts
     * from. In this case java.sql.Timestamp .
     *
     * @return Class the class of the source value which will be converted
     * * @see
     * org.trade.core.conversion.IJavaTypeConverter#getSourceType()
     */
    public Class<?> getSourceType() {
        return java.sql.Timestamp.class;
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
