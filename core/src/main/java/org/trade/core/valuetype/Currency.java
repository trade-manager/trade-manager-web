package org.trade.core.valuetype;

import java.io.Serial;

/**
 *
 */
public class Currency extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "CURRENCY";
    public static final String USD = "USD";

    public Currency() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return Currency
     */
    public static Currency newInstance(String value) {
        Currency returnInstance = new Currency();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Currency
     */
    public static Currency newInstance() {
        Currency returnInstance = new Currency();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}