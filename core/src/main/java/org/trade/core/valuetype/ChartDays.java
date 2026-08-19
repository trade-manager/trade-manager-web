package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ChartDays extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "CHART_DAYS";
    public static final int ONE_DAY = 1;
    public static final int TWO_MONTHS = 60;

    public ChartDays() {
        super(DECODE);
    }

    public ChartDays(boolean optional) {
        super(DECODE, optional);
    }

    /**
     * Method newInstance.
     *
     * @param value Integer
     * @return ChartDays
     */
    public static ChartDays newInstance(Integer value) {

        final ChartDays returnInstance = new ChartDays();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return ChartDays
     */
    public static ChartDays newInstance() {

        final ChartDays returnInstance = new ChartDays();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}