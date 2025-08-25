package org.trade.core.xml;

import org.trade.core.exception.ExceptionMessage;
import org.trade.core.exception.NestingException;

import java.io.Serial;

public class XMLModelException extends NestingException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3222050041414383567L;

    public XMLModelException() {
        super();
    }

    /**
     * Constructor allowing a reference to another exception to be embedded.
     *
     * @param t The <code>Throwable</code> to be nested.
     */
    public XMLModelException(Throwable t) {
        super(t, t.getMessage());
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     */
    public XMLModelException(String message) {
        super(message);
    }

    public XMLModelException(ExceptionMessage exceptionMessage, String gruesomeDetails) {
        super(exceptionMessage, gruesomeDetails);
    }

    public XMLModelException(ExceptionMessage exceptionMessage, String gruesomeDetails, Throwable t) {
        super(exceptionMessage, gruesomeDetails, t);
    }

    public XMLModelException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }

    public XMLModelException(ExceptionMessage exceptionMessage, Throwable t) {
        super(exceptionMessage, t);
    }
}