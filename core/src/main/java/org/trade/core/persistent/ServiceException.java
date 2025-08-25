package org.trade.core.persistent;

import org.trade.core.exception.ModelException;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Id: ConfigProperties.java 1.0 2000/08/08 00:36:40Z Simon.Allen dev
 * $
 */
public class ServiceException extends ModelException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1955474015909463964L;

    public ServiceException() {
        super();
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     * @param id      Integer
     * @param code    Integer
     */
    public ServiceException(Integer id, Integer code, String message) {
        super(id, code, message);

    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     */
    public ServiceException(String message) {
        super(message);
    }
}
