package org.trade.core.valuetype;

import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Exchange extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "EXCHANGE";
    public static final String SMART = "SMART";

    public Exchange() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return Exchange
     */
    public static Exchange newInstance(String value) {
        final Exchange returnInstance = new Exchange();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Exchange
     */
    public static Exchange newInstance() {
        final Exchange returnInstance = new Exchange();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}