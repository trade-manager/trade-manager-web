package org.trade.base;

import org.trade.core.valuetype.Decode;

import javax.swing.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

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

    private String method = null;
    protected MessageNotifier notifier = new MessageNotifier();

    /**
     * CustomButton() - constructor
     *
     * @param p      BasePanel
     * @param UICode String
     * @param items  List<Object>
     */

    public BaseComboBox(BasePanel p, String UICode, List<Decode> items) {

        super(items.toArray(new Decode[0]));
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
        notifier.add(listener);
    }

    /**
     * removeMessageListener() -
     *
     * @param listener IMessageListener
     */
    public void removeMessageListener(IMessageListener listener) {
        notifier.remove(listener);
    }

    /**
     * messageEvent() -
     *
     * @param selection String
     */
    protected void messageEvent(String selection) {
        notifier.notifyEvent(new MessageEvent(selection), new ArrayList<>());
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
        this.method = method;
    }

    /**
     * getMethod() - button action performed
     *
     * @return String
     */
    private String getMethod() {
        return method;
    }
}
