package org.trade.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;

/**
 * @author Simon Allen
 * @version $Id: BaseMenuItem.java,v 1.6 2001/11/09 18:24:58 garrick Exp $
 */
public class BaseMenuItem extends JMenuItem {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5816221538464868893L;

    private final static Logger _log = LoggerFactory.getLogger(BaseMenuItem.class);
    protected MessageNotifier notifier = new MessageNotifier();
    private String method = null;

    /**
     * BaseMenuItem() - constructor
     *
     * @param p                 BasePanel
     * @param basePropertyCodes BaseUIPropertyCodes
     */
    public BaseMenuItem(BasePanel p, BaseUIPropertyCodes basePropertyCodes) {
        try {
            if (p != null) {
                this.addMessageListener(p);
            }

            if (basePropertyCodes.getDisplayName().isEmpty()) {
                setIcon(ImageBuilder.getImageIcon(basePropertyCodes.getImage()));
            } else {
                setText(basePropertyCodes.getDisplayName());
                setMnemonic(basePropertyCodes.getMnemonic());
            }

            setMargin(new Insets(2, 2, 2, 2));
            setHorizontalTextPosition(0);
            setToolTipText(basePropertyCodes.getToolTip());
            setEnabled(basePropertyCodes.isEnabled());
            setMethod(basePropertyCodes.getMethod());
            this.addActionListener(_ -> buttonPressed());
        } catch (Exception ex) {
            _log.error(" Error instanciating Base Menu Item ", ex);
        }
    }

    /**
     * BaseMenuItem() - constructor
     *
     * @param p      BasePanel
     * @param UICode String
     */
    public BaseMenuItem(BasePanel p, String UICode) {
        try {
            if (p != null) {
                this.addMessageListener(p);
            }

            BaseUIPropertyCodes basePropertyCodes = BaseUIPropertyCodes.newInstance(UICode);

            if (basePropertyCodes.getDisplayName().isEmpty()) {
                setIcon(ImageBuilder.getImageIcon(basePropertyCodes.getImage()));
            } else {
                setText(basePropertyCodes.getDisplayName());
                setMnemonic(basePropertyCodes.getMnemonic());
            }

            setMargin(new Insets(2, 2, 2, 2));
            setHorizontalTextPosition(0);
            setToolTipText(basePropertyCodes.getToolTip());
            setEnabled(basePropertyCodes.isEnabled());
            setMethod(basePropertyCodes.getMethod());
            this.addActionListener(_ -> buttonPressed());
        } catch (Exception ex) {
            _log.error(" Error instanciating Base Menu Item ", ex);
        }
    }

    /**
     * actionPerformed() - button action performed
     */
    private void buttonPressed() {
        if (getMethod() != null) {
            this.messageEvent(getMethod());
        }
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
    public String getMethod() {
        return method;
    }
}
