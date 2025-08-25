package org.trade.base;

import javax.swing.event.EventListenerList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Id: MessageNotifier.java,v 1.1 2001/10/18 01:32:15 simon Exp $
 */
public class MessageNotifier {
    private final EventListenerList listeners;

    /**
     * MessageNotifier() - constructor
     */
    public MessageNotifier() {
        this.listeners = new EventListenerList();
    }

    /**
     * addMessageListener() -
     *
     * @param listener IMessageListener
     */
    public void add(IMessageListener listener) {
        this.listeners.add(IMessageListener.class, listener);
    }

    /**
     * removeMessageListener() -
     *
     * @param listener IMessageListener
     */
    public void remove(IMessageListener listener) {
        this.listeners.remove(IMessageListener.class, listener);

    }

    /**
     * removeMessageListener() -
     */
    public void removeAll() {
        Object[] listenerList = this.listeners.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == IMessageListener.class) {
                remove(((IMessageListener) listenerList[i + 1]));
            }
        }
    }

    /**
     * notifyEvent() -
     *
     * @param e      MessageEvent
     * @param params List<Object>
     */
    public void notifyEvent(MessageEvent e, List<Object> params) {
        Object[] listenerList = this.listeners.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == IMessageListener.class) {
                ((IMessageListener) listenerList[i + 1]).handleEvent(e, params);
            }
        }
    }
}
