package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("MarketBar")
public class MarketBar extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "MKT_BAR";

    public MarketBar() {
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
    public MarketBar(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return MarketBar
     */
    public static MarketBar newInstance(String value) {
        final MarketBar returnInstance = new MarketBar();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return MarketBar
     */
    public static MarketBar newInstance() {
        final MarketBar returnInstance = new MarketBar();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}