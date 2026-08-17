package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("OverrideConstraints")
public class OverrideConstraints extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "OVERRIDE_CONSTRAINTS";
    public static final int YES = 1;

    public OverrideConstraints() {
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
    public OverrideConstraints(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Create a new instance of this object
     *
     * @return OverrideConstraints
     */

    public static OverrideConstraints newInstance() {
        final OverrideConstraints returnInstance = new OverrideConstraints();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}