package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class SECType extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "SEC_TYPE";
    public static final String STOCK = "STK";
    public static final String FUTURE = "FUT";

    public SECType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return SECType
     */
    public static SECType newInstance(String value) {
        final SECType returnInstance = new SECType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return SECType
     */
    public static SECType newInstance() {
        final SECType returnInstance = new SECType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}