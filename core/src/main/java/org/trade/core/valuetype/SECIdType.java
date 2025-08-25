package org.trade.core.valuetype;

import java.io.Serial;

/**
 *
 */
public class SECIdType extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "SEC_ID_TYPE";

    public SECIdType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return SECIdType
     */
    public static SECIdType newInstance(String value) {
        final SECIdType returnInstance = new SECIdType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return SECIdType
     */
    public static SECIdType newInstance() {
        final SECIdType returnInstance = new SECIdType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}