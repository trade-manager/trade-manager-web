package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("Tier")
public class Tier extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "GAP_TIER";
    public static final String NO_GAP = "0";

    public Tier() {
        super(DECODE, true);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return Tier
     */
    public static Tier newInstance(String value) {
        final Tier returnInstance = new Tier();
        returnInstance.setDisplayName(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Tier
     */
    public static Tier newInstance() {
        final Tier returnInstance = new Tier();
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