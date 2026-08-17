package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("IndicatorSeriesUI")
public class IndicatorSeriesUI extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "INDICATOR_SERIES";

    public IndicatorSeriesUI() {
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
    public IndicatorSeriesUI(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return IndicatorSeries
     */
    public static IndicatorSeriesUI newInstance(String value) {
        final IndicatorSeriesUI returnInstance = new IndicatorSeriesUI();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return IndicatorSeries
     */
    public static IndicatorSeriesUI newInstance() {
        final IndicatorSeriesUI returnInstance = new IndicatorSeriesUI();
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