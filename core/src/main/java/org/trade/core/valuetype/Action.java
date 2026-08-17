package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("Action")
public class Action extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ACTION";
    public static final String SELL = "SELL";
    public static final String BUY = "BUY";

    public Action() {
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
    public Action(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return Action
     */
    public static Action newInstance(String value) {
        final Action returnInstance = new Action();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Action
     */
    public static Action newInstance() {
        final Action returnInstance = new Action();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}