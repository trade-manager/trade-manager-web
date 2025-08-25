package org.trade.core.message;

import org.trade.core.exception.ExceptionContext;

/**
 * Exception context is used as the key for retrieving an exception message.
 * Each context can be either a static string from the properties file or a
 * dynamic value filled at runtime by converting an object to a string.
 *
 * @author Simon Allen
 */
public interface IMessageContextFactory {
    /**
     * Method create.
     *
     * @return ExceptionContext
     */
    ExceptionContext create();

    // This is to allow contexts to have dynamic parameters

    /**
     * Method create.
     *
     * @param context ExceptionContext
     * @return ExceptionContext
     */
    ExceptionContext create(ExceptionContext context);

    /**
     * Method create.
     *
     * @param dynamicValue Object
     * @return ExceptionContext
     */
    ExceptionContext create(Object dynamicValue);
}
