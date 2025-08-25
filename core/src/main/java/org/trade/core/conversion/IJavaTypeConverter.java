package org.trade.core.conversion;

/**
 * This interface must be implemented by any converter class which is to be used
 * with the JavaTypeTranslator.
 *
 * @author Simon Allen
 */
public interface IJavaTypeConverter {
    /**
     * This method is used by the JavaTypeTranslator to convert a source object
     * to a given target type or class.
     *
     * @param valueToConvert the object value to convert
     * @return Object the converted object * @exception IllegalArgumentException
     */
    Object convert(Object valueToConvert) throws IllegalArgumentException;

    /**
     * This method returns the target type or class that the converter converts
     * to. It is used by the JavaTypeTranslator to determine, upon registration
     * of a particular converter, what target type the converter creates.
     *
     * @return Class the class the source value will be converted to
     */
    Class<?> getTargetType();

    /**
     * This method returns the source type or class that the converter converts
     * from. It is used by the JavaTypeTranslator to determine, upon
     * registration of a particular converter, what source type the converter
     * accepts.
     *
     * @return Class the class of the source value which will be converted
     */
    Class<?> getSourceType();
}
