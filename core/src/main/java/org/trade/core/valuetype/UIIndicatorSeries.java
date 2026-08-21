package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("UIIndicatorSeries")
public class UIIndicatorSeries extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "INDICATOR_SERIES";

    public UIIndicatorSeries() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return IndicatorSeries
     */
    public static UIIndicatorSeries newInstance(String value) {
        final UIIndicatorSeries returnInstance = new UIIndicatorSeries();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return IndicatorSeries
     */
    public static UIIndicatorSeries newInstance() {
        final UIIndicatorSeries returnInstance = new UIIndicatorSeries();
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