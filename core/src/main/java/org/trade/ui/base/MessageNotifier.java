/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.base;

import javax.swing.event.EventListenerList;
import java.util.Vector;

/**
 * @author Simon Allen
 * @version $Id: MessageNotifier.java,v 1.1 2001/10/18 01:32:15 simon Exp $
 */
public class MessageNotifier {
    private EventListenerList listeners;

    /**
     * MessageNotifier() - constructor
     *
     * @throws *
     * @see
     */
    public MessageNotifier() {
        this.listeners = new EventListenerList();
    }

    /**
     * addMessageListener() -
     *
     * @param listener IMessageListener
     * @throws *
     * @see
     */
    public void add(IMessageListener listener) {
        this.listeners.add(IMessageListener.class, listener);
    }

    /**
     * removeMessageListener() -
     *
     * @param listener IMessageListener
     * @throws *
     * @see
     */
    public void remove(IMessageListener listener) {
        this.listeners.remove(IMessageListener.class, listener);

    }

    /**
     * removeMessageListener() -
     *
     * @throws *
     * @see
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
     * @param e    MessageEvent
     * @param parm Vector<Object>
     * @throws *
     * @see
     */
    public void notifyEvent(MessageEvent e, Vector<Object> parm) {
        Object[] listenerList = this.listeners.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == IMessageListener.class) {
                ((IMessageListener) listenerList[i + 1]).handleEvent(e, parm);
            }
        }
    }
}
