package org.trade.base;

import javax.swing.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Id: BaseList.java,v 1.4 2001/11/06 22:37:27 simon Exp $
 */
public class BaseList extends JList<Object> {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3629905211019895353L;

    private String method = null;
    protected MessageNotifier notifier = new MessageNotifier();

    /**
     * CustomButton() - constructor
     *
     * @param p      BasePanel
     * @param UICode String
     * @param items  List<Object>
     */
    public BaseList(BasePanel p, String UICode, List<Object> items) {

        super(items.toArray(new Object[0]));
        jbInit(p, UICode);
    }

    /**
     * CustomButton() - constructor
     *
     * @param p      BasePanel
     * @param UICode String
     */
    public BaseList(BasePanel p, String UICode) {
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

        org.trade.core.valuetype.UIComponentProperties basePropertyCodes = org.trade.core.valuetype.UIComponentProperties.newInstance(UICode);

        setMethod(basePropertyCodes.getMethod());
        this.setName(basePropertyCodes.getDisplayName());
        this.setEnabled(basePropertyCodes.isEnabled());
        this.setToolTipText(basePropertyCodes.getToolTip());
        this.addListSelectionListener(_ -> doValueChanged());
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
    private void doValueChanged() {
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
