package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("BarSize")
public class BarSize extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "BAR_SIZE";
    public static final Integer ONE_MIN = 60;
    public static final Integer FIVE_MIN = 300;
    public static final Integer HOUR_MIN = 3600;
    public static final Integer DAY = 1;

    public BarSize() {
        super(DECODE, true);
    }

    /**
     * Method newInstance.
     *
     * @param value Integer
     * @return BarSize
     */
    public static BarSize newInstance(Integer value) {

        final BarSize returnInstance = new BarSize();

        if (value > 3600) {
            value = 1;
        }

        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return BarSize
     */
    public static BarSize newInstance() {
        final BarSize returnInstance = new BarSize();
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