package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AllocationMethod extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ALLOCATION_METHOD";

    public AllocationMethod() {
        super(DECODE, true);
    }

    /**
     * Method newInstance.
     *
     * @param value Integer
     * @return BarSize
     */
    public static AllocationMethod newInstance(String value) {
        final AllocationMethod returnInstance = new AllocationMethod();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return BarSize
     */
    public static AllocationMethod newInstance() {
        final AllocationMethod returnInstance = new AllocationMethod();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * Method convertToUppercase.
     *
     * @return boolean
     */
    protected boolean convertToUppercase() {
        return false;
    }
}