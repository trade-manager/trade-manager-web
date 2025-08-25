package org.trade.core.validator;

import org.trade.core.exception.ExceptionMessage;

/**
 *
 */
public interface IExceptionMessageListener {
    /**
     * Method addExceptionMessage.
     *
     * @param message ExceptionMessage
     */
    void addExceptionMessage(ExceptionMessage message);
}
