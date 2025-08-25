package org.trade.core.validator;

/**
 *
 */
public interface IValidator {
    /**
     * Method isValid.
     *
     * @param value          Object
     * @param invalidValue   String
     * @param expectedFormat String
     * @param receiver       IExceptionMessageListener
     * @return boolean
     */
    boolean isValid(Object value, String invalidValue, String expectedFormat, IExceptionMessageListener receiver);
}
