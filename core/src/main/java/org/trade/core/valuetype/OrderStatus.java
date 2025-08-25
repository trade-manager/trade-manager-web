package org.trade.core.valuetype;

import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class OrderStatus extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ORDER_STATUS";
    public static final String UNSUBMIT = "UNSUBMIT";
    public static final String FILLED = "FILLED";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String PRESUBMITTED = "PRESUBMITTED";
    public static final String CANCELLED = "CANCELLED";
    public static final String INACTIVE = "INACTIVE";
    public static final String PARTIALFILLED = "PARTIALFILLED";

    public OrderStatus() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return OrderStatus
     */
    public static OrderStatus newInstance(String value) {
        final OrderStatus returnInstance = new OrderStatus();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return OrderStatus
     */
    public static OrderStatus newInstance() {
        final OrderStatus returnInstance = new OrderStatus();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}