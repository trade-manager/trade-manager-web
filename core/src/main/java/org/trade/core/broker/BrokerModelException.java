package org.trade.core.broker;

import org.trade.core.exception.ModelException;

import java.io.Serial;


/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BrokerModelException extends ModelException {

    @Serial
    private static final long serialVersionUID = -504416118492103075L;

    public BrokerModelException() {
        super();
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     * @param id      Integer
     * @param code    Integer
     */
    public BrokerModelException(Integer id, Integer code, String message) {

        super(id, code, message);
    }
}
