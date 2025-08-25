package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class IndicatorSeries extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "INDICATOR_SERIES";

    public IndicatorSeries() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return IndicatorSeries
     */
    public static IndicatorSeries newInstance(String value) {
        final IndicatorSeries returnInstance = new IndicatorSeries();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return IndicatorSeries
     */
    public static IndicatorSeries newInstance() {
        final IndicatorSeries returnInstance = new IndicatorSeries();
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