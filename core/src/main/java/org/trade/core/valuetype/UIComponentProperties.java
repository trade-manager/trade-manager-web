package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * Example implementation of how to subclass the CodeDecodeValueType Object this
 * object represents the State codes and Descriptions in the US.
 *
 * @author Simon Allen
 * @version $Id: BasePropertyCodes.java,v 1.15 2002/01/22 22:48:21 simon Exp $
 */
@Entity
@DiscriminatorValue("UIComponentProperties")
public class UIComponentProperties extends Decode {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2178313262496336078L;

    public static final String UI_COMPONENT = "UI_COMPONENT";
    public static final String UI_COMPONENT_PROPERTY = UI_COMPONENT + "_PROPERTY";
    public static final String UI_COMPONENT_TOOL_TIP = UI_COMPONENT + "_TOOL_TIP";
    public static final String UI_COMPONENT_ENABLED = UI_COMPONENT + "_ENABLED";
    public static final String UI_COMPONENT_MNEMONIC = UI_COMPONENT + "_MNEMONIC";
    public static final String UI_COMPONENT_IMAGE = UI_COMPONENT + "_IMAGE";
    public static final String UI_COMPONENT_METHOD = UI_COMPONENT + "_METHOD";

    public static final String ABOUT = "ABOUT";
    public static final String CALCULATE = "CALCULATE";
    public static final String CANCEL = "CANCEL";
    public static final String CASCADE = "CASCADE";
    public static final String CASCADE_ALL = "CASCADE_ALL";
    public static final String CLEAR = "CLEAR";
    public static final String CLOSE = "CLOSE";
    public static final String CLOSE_ALL = "CLOSE_ALL";
    public static final String CLOSE_FILE = "CLOSE_FILE";
    public static final String CONTENTS = "CONTENTS";
    public static final String COPY = "COPY";
    public static final String COMMIT = "COMMIT";
    public static final String CUT = "CUT";
    public static final String CONNECT = "CONNECT";
    public static final String DELETE = "DELETE";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String DISCLAIMER = "DISCLAIMER";
    public static final String EXECUTE = "EXECUTE";
    public static final String EXECUTE_STATEMENT = "EXECUTE_STATEMENT";
    public static final String EXIT = "EXIT";
    public static final String FIND = "FIND";
    public static final String FETCH = "FETCH";
    public static final String HELP = "HELP";
    public static final String INSERT = "INSERT";
    public static final String NEW = "NEW";
    public static final String NEXT = "NEXT";
    public static final String OPEN_FILE = "OPEN_FILE";
    public static final String PASTE = "PASTE";
    public static final String PREV = "PREV";
    public static final String PRINT = "PRINT";
    public static final String PRINT_PREVIEW = "PRINT_PREVIEW";
    public static final String PRINT_OPTIONS = "PRINT_OPTIONS";
    public static final String REDO = "REDO";
    public static final String REPLACE = "REPLACE";
    public static final String REFRESH = "REFRESH";
    public static final String RESULTS = "RESULTS";
    public static final String RETRIEVE = "RETRIEVE";
    public static final String SAVE = "SAVE";
    public static final String SAVE_AS = "SAVE_AS";
    public static final String SEARCH = "SEARCH";
    public static final String TABLE_LIST = "TABLE_LIST";
    public static final String TILE_ALL = "TILE_ALL";
    public static final String UNDO = "UNDO";
    public static final String VALID = "VALID";
    public static final String VALID_ALL = "VALID_ALL";
    public static final String PROPERTIES = "PROPERTIES";
    public static final String CLEAR_ERROR = "CLEAR_ERROR";
    public static final String RUN = "RUN";
    public static final String DATA = "DATA";
    public static final String TEST = "TEST";
    public static final String TRANSFER = "TRANSFER";
    public static final String REMOVE = "REMOVE";

    public static final String COMPILE = "COMPILE";
    public static final String REASSIGN = "REASSIGN";
    public static final String STRATEGY_PARMS = "STRATEGY_PARMS";

    /**
     * Default Constructor
     */
    public UIComponentProperties() {
        super(UI_COMPONENT_PROPERTY, UI_COMPONENT, false);
    }

    /**
     * Constructor for BaseUIPropertyCodes.
     *
     * @param propertyType String
     * @param propertyCode String
     */
    public UIComponentProperties(String propertyType, String propertyCode) {
        super(propertyType, propertyCode, false);
    }

    /**
     * Method isEnabled.
     *
     * @return boolean
     */
    public boolean isEnabled() {

        return getValue(UI_COMPONENT_ENABLED).equalsIgnoreCase("true");
    }

    /**
     * Method getToolTip.
     *
     * @return String
     */
    public String getToolTip() {
        return getValue(UI_COMPONENT_TOOL_TIP);
    }

    /**
     * Method getImage.
     *
     * @return String
     */
    public String getImage() {
        return getValue(UI_COMPONENT_IMAGE);
    }

    /**
     * Method getMethod.
     *
     * @return String
     */
    public String getMethod() {
        return getValue(UI_COMPONENT_METHOD);
    }

    /**
     * Method getMnemonic.
     *
     * @return int
     */
    public int getMnemonic() {
        int returnValue = 0;

        if ((null != getValue(UI_COMPONENT_MNEMONIC)) && (!getValue(UI_COMPONENT_MNEMONIC).isEmpty())) {
            returnValue = getValue(UI_COMPONENT_MNEMONIC).charAt(0);
        }

        return returnValue;
    }

    /**
     * Create a new instance of this object
     *
     * @param code String
     * @return BaseUIPropertyCodes
     */
    public static UIComponentProperties newInstance(String code) {

        UIComponentProperties returnInstance = new UIComponentProperties();
        returnInstance.setValue(code);
        return returnInstance;
    }
}
