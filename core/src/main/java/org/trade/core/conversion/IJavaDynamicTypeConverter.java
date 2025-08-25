package org.trade.core.conversion;

/**
 * This interface must be implemented by any converter class which is to be used
 * with the JavaTypeTranslator.
 *
 * @author Simon Allen
 */
public interface IJavaDynamicTypeConverter {
    /**
     * This method is used by the JavaTypeTranslator to convert a source object
     * to a given target type or class.
     *
     * @param targetType     the target type or class to convert to
     * @param valueToConvert the object value to convert
     * @return Object the converted object * @exception
     * JavaTypeTranslatorException
     */
    Object convert(Class<?> targetType, Object valueToConvert) throws JavaTypeTranslatorException;

    /**
     * This method is used by the JavaTypeTranslator to determine wether or not
     * the dynamic converter supports the conversion.
     *
     * @param targetType     the target type or class to convert to
     * @param valueToConvert the object value to convert
     * @return boolean is the object supports the conversion.
     */
    boolean supportsConversion(Class<?> targetType, Object valueToConvert);
}
