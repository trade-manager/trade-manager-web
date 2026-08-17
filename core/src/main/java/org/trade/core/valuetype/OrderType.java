package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("OrderType")
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
     * Constructor for CodeType.
     *
     * @param type        String
     * @param category    String
     * @param name        String
     * @param description String
     */
    public OrderType(String type, String category, String name, String description) {

        super(type, category, name, description);
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