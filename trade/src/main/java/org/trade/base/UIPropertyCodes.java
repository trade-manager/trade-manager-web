package org.trade.base;

import java.io.Serial;

/**
 * Example implementation of how to subclass the CodeDecodeValueType Object this
 * object represents the State codes and Descriptions in the US.
 *
 * @author Simon Allen
 * @version $Id: BaseUIPropertyCodes.java,v 1.15 2002/01/22 22:48:21 simon Exp $
 */
public class UIPropertyCodes extends BaseUIPropertyCodes {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2178313262496336078L;

    public final static String UI_WIDGET_PROP = "UI_WIDGET_PROP";
    public final static String UI_WIDGET = "UI_WIDGET";
    public final static String UI_WIDGET_TOOL_TIP = "UI_WIDGET_TOOL_TIP";
    public final static String UI_WIDGET_ENABLED = "UI_WIDGET_ENABLED";
    public final static String UI_WIDGET_MNEMONIC = "UI_WIDGET_MNEMONIC";
    public final static String UI_WIDGET_IMAGE = "UI_WIDGET_IMAGE";
    public final static String UI_WIDGET_METHOD = "UI_WIDGET_METHOD";

    public final static String COMPILE = "COMPILE";
    public final static String REASSIGN = "REASSIGN";
    public final static String STRATEGY_PARMS = "STRATEGY_PARMS";

    /**
     * Default Constructor
     */
    public UIPropertyCodes() {
        super(UI_WIDGET_PROP, UI_WIDGET);
    }

    /**
     * Method isEnabled.
     *
     * @return boolean
     */
    public boolean isEnabled() {

        return getValue(UI_WIDGET_ENABLED).equalsIgnoreCase("true");
    }

    /**
     * Method getToolTip.
     *
     * @return String
     */
    public String getToolTip() {
        return getValue(UI_WIDGET_TOOL_TIP);
    }

    /**
     * Method getImage.
     *
     * @return String
     */
    public String getImage() {
        return getValue(UI_WIDGET_IMAGE);
    }

    /**
     * Method getMethod.
     *
     * @return String
     */
    public String getMethod() {
        return getValue(UI_WIDGET_METHOD);
    }

    /**
     * Method getMnemonic.
     *
     * @return int
     */
    public int getMnemonic() {
        int returnValue = 0;

        if ((null != getValue(UI_WIDGET_MNEMONIC)) && (!getValue(UI_WIDGET_MNEMONIC).isEmpty())) {
            returnValue = getValue(UI_WIDGET_MNEMONIC).charAt(0);
        }

        return returnValue;
    }

    /**
     * Create a new instance of this object
     *
     * @param code String
     * @return UIPropertyCodes
     */
    public static UIPropertyCodes newInstance(String code) {
        UIPropertyCodes returnInstance;
        returnInstance = new UIPropertyCodes();
        returnInstance.setValue(code);
        return returnInstance;
    }
}
