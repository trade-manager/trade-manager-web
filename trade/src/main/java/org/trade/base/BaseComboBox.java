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
package org.trade.base;

import org.trade.core.valuetype.Decode;

import javax.swing.*;
import java.io.Serial;
import java.util.Vector;

/**
 * @author Simon Allen
 * @version $Id: BaseComboBox.java,v 1.3 2001/11/06 22:37:27 simon Exp $
 */
public class BaseComboBox extends JComboBox<Decode> {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6866452735597474525L;

    private String m_method = null;

    protected MessageNotifier m_notifier = new MessageNotifier();

    /**
     * CustomButton() - constructor
     *
     * @param p      BasePanel
     * @param UICode String
     * @param items  Vector<Object>
     */

    public BaseComboBox(BasePanel p, String UICode, Vector<Decode> items) {
        super(items);

        jbInit(p, UICode);
    }

    /**
     * CustomButton() - constructor
     *
     * @param p      BasePanel
     * @param UICode String
     */
    public BaseComboBox(BasePanel p, String UICode) {
        jbInit(p, UICode);
    }

    /**
     * Method jbInit.
     *
     * @param p      BasePanel
     * @param UICode String
     */
    private void jbInit(BasePanel p, String UICode) {
        if (p != null) {
            this.addMessageListener(p);
        }

        BaseUIPropertyCodes basePropertyCodes = BaseUIPropertyCodes.newInstance(UICode);

        setMethod(basePropertyCodes.getMethod());
        this.setName(basePropertyCodes.getDisplayName());
        this.setEnabled(basePropertyCodes.isEnabled());
        this.setToolTipText(basePropertyCodes.getToolTip());
        this.addActionListener(_ -> itemChanged());
    }

    /**
     * addMessageListener() -
     *
     * @param listener IMessageListener
     */
    public void addMessageListener(IMessageListener listener) {
        m_notifier.add(listener);
    }

    /**
     * removeMessageListener() -
     *
     * @param listener IMessageListener
     */
    public void removeMessageListener(IMessageListener listener) {
        m_notifier.remove(listener);
    }

    /**
     * messageEvent() -
     *
     * @param selection String
     */
    protected void messageEvent(String selection) {
        m_notifier.notifyEvent(new MessageEvent(selection), new Vector<>());
    }

    /**
     * actionPerformed() - combo box action performed
     */
    private void itemChanged() {
        if (getMethod() != null) {
            this.messageEvent(getMethod());
        }
    }

    /**
     * setMethod() - button action performed
     *
     * @param method String
     */
    private void setMethod(String method) {
        m_method = method;
    }

    /**
     * getMethod() - button action performed
     *
     * @return String
     */
    private String getMethod() {
        return m_method;
    }
}
