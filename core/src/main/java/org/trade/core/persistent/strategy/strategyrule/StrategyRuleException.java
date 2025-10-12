package org.trade.core.persistent.strategy.strategyrule;

import org.trade.core.exception.ModelException;

import java.io.Serial;


/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StrategyRuleException extends ModelException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4770595163148499444L;

    public StrategyRuleException() {
        super();
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     * @param id      Integer
     * @param code    Integer
     */
    public StrategyRuleException(Integer id, Integer code, String message) {
        super(id, code, message);

    }
}
