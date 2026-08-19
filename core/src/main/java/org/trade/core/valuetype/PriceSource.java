package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class PriceSource extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "PRICE_SOURCE";
    public static final Integer CLOSE = 1;
    public static final Integer MEDIAN = 5;

    public PriceSource() {
        super(DECODE, false);
    }

    /**
     * Method newInstance.
     *
     * @param value Integer
     * @return BarSize
     */
    public static PriceSource newInstance(Integer value) {
        final PriceSource returnInstance = new PriceSource();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return BarSize
     */
    public static PriceSource newInstance() {
        final PriceSource returnInstance = new PriceSource();
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