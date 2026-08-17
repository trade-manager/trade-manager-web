package org.trade.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BaseButton extends JButton {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -47827936580637959L;
    private final static Logger _log = LoggerFactory.getLogger(BaseButton.class);
    protected MessageNotifier notifier = new MessageNotifier();
    private String method = null;
    private Object transferObject = null;

    /**
     * Constructor for BaseButton.
     *
     * @param basePanel         BasePanel
     * @param basePropertyCodes BaseUIPropertyCodes
     */
    public BaseButton(BasePanel basePanel, org.trade.core.valuetype.UIComponentProperties basePropertyCodes) {
        this(basePanel, basePropertyCodes, 2);
    }

    /**
     * Constructor for BaseButton.
     *
     * @param basePanel         BasePanel
     * @param basePropertyCodes BaseUIPropertyCodes
     * @param margin            int
     */
    public BaseButton(BasePanel basePanel, org.trade.core.valuetype.UIComponentProperties basePropertyCodes, int margin) {
        try {
            if (basePanel != null) {
                this.addMessageListener(basePanel);
            }

            if (!basePropertyCodes.getImage().isEmpty()) {
                setIcon(ImageBuilder.getImageIcon(basePropertyCodes.getImage()));
            } else {
                setText(basePropertyCodes.getDisplayName());
                setMnemonic(basePropertyCodes.getMnemonic());
            }

            setMargin(new Insets(margin, margin, margin, margin));
            setHorizontalTextPosition(0);
            setToolTipText(basePropertyCodes.getToolTip());
            setEnabled(basePropertyCodes.isEnabled());
            setMethod(basePropertyCodes.getMethod());
            this.addActionListener(_ -> buttonPressed());
        } catch (Exception ex) {
            _log.error(" Error instantiating Base Button ", ex);
        }
    }

    /**
     * Constructor for BaseButton.
     *
     * @param basePanel BasePanel
     * @param UICode    String
     * @param margin    int
     */
    public BaseButton(BasePanel basePanel, String UICode, int margin) {
        try {
            if (basePanel != null) {
                this.addMessageListener(basePanel);
            }

            org.trade.core.valuetype.UIComponentProperties basePropertyCodes = org.trade.core.valuetype.UIComponentProperties.newInstance(UICode);

            if (!basePropertyCodes.getImage().isEmpty()) {
                setIcon(ImageBuilder.getImageIcon(basePropertyCodes.getImage()));
            } else {
                setText(basePropertyCodes.getDisplayName());
                setMnemonic(basePropertyCodes.getMnemonic());
            }

            setMargin(new Insets(margin, margin, margin, margin));
            setHorizontalTextPosition(0);
            setToolTipText(basePropertyCodes.getToolTip());
            setEnabled(basePropertyCodes.isEnabled());
            setMethod(basePropertyCodes.getMethod());
            this.addActionListener(_ -> buttonPressed());
        } catch (Exception ex) {
            _log.error(" Error instanciating Base Button ", ex);
        }
    }

    /**
     * Constructor for BaseButton.
     *
     * @param basePanel BasePanel
     * @param UICode    String
     */
    public BaseButton(BasePanel basePanel, String UICode) {
        this(basePanel, UICode, 2);
    }

    protected void buttonPressed() {
        if (getMethod() != null) {
            this.messageEvent(getMethod());
        }
    }

    /**
     * Method addMessageListener.
     *
     * @param listener IMessageListener
     */
    public void addMessageListener(IMessageListener listener) {
        notifier.add(listener);
    }

    /**
     * Method remove.
     *
     * @param listener IMessageListener
     */
    public void remove(IMessageListener listener) {
        notifier.remove(listener);
    }

    /**
     * Method setTransferObject.
     *
     * @param transferObject Object
     */
    public void setTransferObject(Object transferObject) {
        this.transferObject = transferObject;
    }

    /**
     * Method getTransferObject.
     *
     * @return Object
     */
    public Object getTransferObject() {
        return this.transferObject;
    }

    /**
     * Method messageEvent.
     *
     * @param selection String
     */
    protected void messageEvent(String selection) {

        List<Object> transferObjects = new ArrayList<>();

        if (null != this.transferObject) {

            transferObjects.add(this.transferObject);
        }
        notifier.notifyEvent(new MessageEvent(selection), transferObjects);
    }

    /**
     * Method setMethod.
     *
     * @param method String
     */
    private void setMethod(String method) {
        this.method = method;
    }

    /**
     * Method getMethod.
     *
     * @return String
     */
    public String getMethod() {
        return method;
    }
}
