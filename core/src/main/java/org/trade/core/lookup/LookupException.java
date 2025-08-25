package org.trade.core.lookup;

import org.trade.core.exception.NestingException;

import java.io.Serial;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class LookupException extends NestingException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6136398466762813824L;

    public LookupException() {
        super();
    }

    /**
     * Constructor allowing a reference to another exception to be embedded.
     *
     * @param t The <code>Throwable</code> to be nested.
     */
    public LookupException(Throwable t) {
        super(t);
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     */
    public LookupException(String message) {
        super(message);
    }

    /**
     * Constructor allows the developer to set the exception message
     *
     * @param t       java.lang.Throwable
     * @param message the exception message
     */
    public LookupException(Throwable t, String message) {
        super(t, message);
    }
}
