package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("Exchange")
public class Exchange extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "EXCHANGE";
    public static final String SMART = "SMART";

    public Exchange() {
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
    public Exchange(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return Exchange
     */
    public static Exchange newInstance(String value) {
        final Exchange returnInstance = new Exchange();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Exchange
     */
    public static Exchange newInstance() {
        final Exchange returnInstance = new Exchange();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}