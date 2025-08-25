package org.trade.core.valuetype;

import org.trade.core.exception.ModelException;

import java.io.Serial;

/**
 * @author Simon Allen
 */
public class ValueTypeException extends ModelException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -7816030721289530099L;

    public ValueTypeException() {
        super();
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     * @param id      Integer
     * @param code    Integer
     */
    public ValueTypeException(Integer id, Integer code, String message) {
        super(id, code, message);

    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     */
    public ValueTypeException(String message) {
        super(message);
    }

    /**
     * Constructor allowing a reference to another exception to be embedded.
     *
     * @param t The <code>Throwable</code> to be nested.
     */
    public ValueTypeException(Throwable t) {
        super(t);
    }
}
