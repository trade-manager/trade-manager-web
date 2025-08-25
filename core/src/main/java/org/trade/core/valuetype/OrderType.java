package org.trade.core.valuetype;

import java.io.Serial;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class OrderType extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ORDER_TYPE";
    public static final String LMT = "LMT";
    public static final String STP = "STP";
    public static final String STPLMT = "STPLMT";
    public static final String MKT = "MKT";
    public static final String TRAIL = "TRAIL";
    public static final String TRAILLIMIT = "TRAILLIMIT ";

    public OrderType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return OrderType
     */
    public static OrderType newInstance(String value) {
        final OrderType returnInstance = new OrderType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return OrderType
     */
    public static OrderType newInstance() {
        final OrderType returnInstance = new OrderType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}