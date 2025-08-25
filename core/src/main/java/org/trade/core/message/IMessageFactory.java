package org.trade.core.message;

import org.trade.core.exception.ExceptionContext;
import org.trade.core.exception.ExceptionMessage;

/**
 * A class that implements this interface is capable of creating
 * <code>ExceptionMessage</code> objects based on a variety of contextual
 * elements.
 *
 * @author Simon Allen
 */
public interface IMessageFactory {
    /**
     * Method create.
     *
     * @return ExceptionMessage
     */
    ExceptionMessage create();

    /**
     * @param fieldSequence This should be used when checking repeating groups because it
     *                      will cause a group number to be appended to each field
     *                      reference.
     * @return ExceptionMessage
     */
    ExceptionMessage create(int fieldSequence);

    /**
     * Convenience method to add context to the exception message.
     *
     * @param exceptionContext ExceptionContext
     * @return ExceptionMessage
     */
    ExceptionMessage create(ExceptionContext exceptionContext);

    /**
     * Convenience method to add context to the exception message.
     *
     * @param exceptionContext1 ExceptionContext
     * @param exceptionContext2 ExceptionContext
     * @return ExceptionMessage
     */
    ExceptionMessage create(ExceptionContext exceptionContext1, ExceptionContext exceptionContext2);
}
