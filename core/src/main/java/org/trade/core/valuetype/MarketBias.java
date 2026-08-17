package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("MarketBias")
public class MarketBias extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "MKT_BIAS";

    public MarketBias() {
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
    public MarketBias(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return MarketBias
     */
    public static MarketBias newInstance(String value) {
        final MarketBias returnInstance = new MarketBias();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return MarketBias
     */
    public static MarketBias newInstance() {
        final MarketBias returnInstance = new MarketBias();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}