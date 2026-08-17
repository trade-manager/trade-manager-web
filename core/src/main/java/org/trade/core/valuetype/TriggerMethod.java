package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("TriggerMethod")
public class TriggerMethod extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "TRIGGER_METHOD";
    public static final int DEFAULT = 0;

    public TriggerMethod() {
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
    public TriggerMethod(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return TriggerMethod
     */
    public static TriggerMethod newInstance(String value) {
        final TriggerMethod returnInstance = new TriggerMethod();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return TriggerMethod
     */
    public static TriggerMethod newInstance() {
        final TriggerMethod returnInstance = new TriggerMethod();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}