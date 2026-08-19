package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Side extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "SIDE";
    public static final String BOT = "BOT";
    public static final String SLD = "SLD";

    public Side() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return Side
     */
    public static Side newInstance(String value) {
        final Side returnInstance = new Side();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Side
     */
    public static Side newInstance() {
        final Side returnInstance = new Side();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}