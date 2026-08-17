package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("OptionType")
public class OptionType extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "OPTION_TYPE";
    public static final String PUT = "P";
    public static final String CALL = "C";

    public OptionType() {
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
    public OptionType(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return OptionType
     */
    public static OptionType newInstance(String value) {
        final OptionType returnInstance = new OptionType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return OptionType
     */
    public static OptionType newInstance() {
        final OptionType returnInstance = new OptionType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}