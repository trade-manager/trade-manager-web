package org.trade.core.validator;

import org.trade.core.exception.ExceptionMessage;


/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IExceptionMessageListener {
    /**
     * Method addExceptionMessage.
     *
     * @param message ExceptionMessage
     */
    void addExceptionMessage(ExceptionMessage message);
}
