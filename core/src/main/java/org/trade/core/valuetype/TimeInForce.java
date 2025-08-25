package org.trade.core.valuetype;

import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TimeInForce extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "TIME_IN_FORCE";
    public static final String DAY = "DAY";
    public static final String GTC = "GTC";

    public TimeInForce() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return TimeInForce
     */
    public static TimeInForce newInstance(String value) {
        final TimeInForce returnInstance = new TimeInForce();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return TimeInForce
     */
    public static TimeInForce newInstance() {
        final TimeInForce returnInstance = new TimeInForce();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}