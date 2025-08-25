package org.trade.core.valuetype;

import java.io.Serial;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class CalculationType extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "CALC_TYPE";
    public static final String EXPONENTIAL = "EXPONENTIAL";
    public static final String LINEAR = "LINEAR";
    public static final String WEIGHTED = "WEIGHTED";
    public static final String WEIGHTED_VOLUME = "WEIGHTED_VOLUME";
    public static final String TRIANGULAR = "TRIANGULAR";

    public CalculationType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return CalculationType
     */
    public static CalculationType newInstance(String value) {
        final CalculationType returnInstance = new CalculationType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return CalculationType
     */
    public static CalculationType newInstance() {
        final CalculationType returnInstance = new CalculationType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}