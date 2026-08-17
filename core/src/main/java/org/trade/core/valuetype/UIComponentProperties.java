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

    public final static String UI_COMPONENT = "UI_COMPONENT";
    public final static String UI_COMPONENT_PROPERTY = UI_COMPONENT + "_PROPERTY";
    public final static String UI_COMPONENT_TOOL_TIP = UI_COMPONENT + "_TOOL_TIP";
    public final static String UI_COMPONENT_ENABLED = UI_COMPONENT + "_ENABLED";
    public final static String UI_COMPONENT_MNEMONIC = UI_COMPONENT + "_MNEMONIC";
    public final static String UI_COMPONENT_IMAGE = UI_COMPONENT + "_IMAGE";
    public final static String UI_COMPONENT_METHOD = UI_COMPONENT + "_METHOD";

    public final static String ABOUT = "ABOUT";
    public final static String CALCULATE = "CALCULATE";
    public final static String CANCEL = "CANCEL";
    public final static String CASCADE = "CASCADE";
    public final static String CASCADE_ALL = "CASCADE_ALL";
    public final static String CLEAR = "CLEAR";
    public final static String CLOSE = "CLOSE";
    public final static String CLOSE_ALL = "CLOSE_ALL";
    public final static String CLOSE_FILE = "CLOSE_FILE";
    public final static String CONTENTS = "CONTENTS";
    public final static String COPY = "COPY";
    public final static String COMMIT = "COMMIT";
    public final static String CUT = "CUT";
    public final static String CONNECT = "CONNECT";
    public final static String DELETE = "DELETE";
    public final static String DISCONNECT = "DISCONNECT";
    public final static String DISCLAIMER = "DISCLAIMER";
    public final static String EXECUTE = "EXECUTE";
    public final static String EXECUTE_STATEMENT = "EXECUTE_STATEMENT";
    public final static String EXIT = "EXIT";
    public final static String FIND = "FIND";
    public final static String FETCH = "FETCH";
    public final static String HELP = "HELP";
    public final static String INSERT = "INSERT";
    public final static String NEW = "NEW";
    public final static String NEXT = "NEXT";
    public final static String OPEN_FILE = "OPEN_FILE";
    public final static String PASTE = "PASTE";
    public final static String PREV = "PREV";
    public final static String PRINT = "PRINT";
    public final static String PRINT_PREVIEW = "PRINT_PREVIEW";
    public final static String PRINT_OPTIONS = "PRINT_OPTIONS";
    public final static String REDO = "REDO";
    public final static String REPLACE = "REPLACE";
    public final static String REFRESH = "REFRESH";
    public final static String RESULTS = "RESULTS";
    public final static String RETRIEVE = "RETRIEVE";
    public final static String SAVE = "SAVE";
    public final static String SAVE_AS = "SAVE_AS";
    public final static String SEARCH = "SEARCH";
    public final static String TABLE_LIST = "TABLE_LIST";
    public final static String TILE_ALL = "TILE_ALL";
    public final static String UNDO = "UNDO";
    public final static String VALID = "VALID";
    public final static String VALID_ALL = "VALID_ALL";
    public final static String PROPERTIES = "PROPERTIES";
    public final static String CLEAR_ERROR = "CLEAR_ERROR";
    public final static String RUN = "RUN";
    public final static String DATA = "DATA";
    public final static String TEST = "TEST";
    public final static String TRANSFER = "TRANSFER";
    public final static String REMOVE = "REMOVE";

    public final static String COMPILE = "COMPILE";
    public final static String REASSIGN = "REASSIGN";
    public final static String STRATEGY_PARMS = "STRATEGY_PARMS";

    /**
     * Constructor for CodeType.
     *
     * @param type        String
     * @param category    String
     * @param name        String
     * @param description String
     */
    public UIComponentProperties(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

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
