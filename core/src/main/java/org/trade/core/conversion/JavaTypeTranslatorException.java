
package org.trade.core.conversion;

import org.trade.core.exception.NestingException;

import java.io.Serial;

/**
 * This class is just a wrapper (via inheritance) around a BSFException in order
 * to provide a clean interface around the Exception handling in this package.
 *
 * @author Simon Allen
 */
public class JavaTypeTranslatorException extends NestingException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1403791193190441733L;

    public JavaTypeTranslatorException() {
        super();
    }

    /**
     * Constructor allowing a reference to another exception to be embedded.
     *
     * @param t The <code>Throwable</code> to be nested.
     */
    public JavaTypeTranslatorException(Throwable t) {
        super(t);
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     */
    public JavaTypeTranslatorException(String message) {
        super(message);
    }

    /**
     * Constructor allows the developer to set the exception message
     *
     * @param t       java.lang.Throwable
     * @param message the exception message
     */
    public JavaTypeTranslatorException(Throwable t, String message) {
        super(t, message);
    }
}
