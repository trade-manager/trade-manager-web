package org.trade.base;

import java.util.EventListener;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Id: IMessageListener.java,v 1.1 2001/10/18 01:32:15 simon Exp $
 */
public interface IMessageListener extends EventListener {
    /**
     * Method handleEvent.
     *
     * @param e      MessageEvent
     * @param params List<Object>
     */
    void handleEvent(MessageEvent e, List<Object> params);
}
