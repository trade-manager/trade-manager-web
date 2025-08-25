package org.trade.base;

import java.io.Serial;
import java.util.EventObject;

/**
 * @author Simon Allen
 * @version $Id: MessageEvent.java,v 1.1 2001/10/18 01:32:15 simon Exp $
 */
public class MessageEvent extends EventObject {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6303732297647598634L;

    /**
     * MessageEvent() -
     *
     * @param source Object
     */
    public MessageEvent(Object source) {
        super(source);
    }
}
