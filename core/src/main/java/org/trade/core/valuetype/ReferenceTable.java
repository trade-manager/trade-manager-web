package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("ReferenceTable")
public class ReferenceTable extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "REF_TABLE";

    public ReferenceTable() {
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
    public ReferenceTable(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return ReferenceTable
     */
    public static ReferenceTable newInstance(String value) {
        final ReferenceTable returnInstance = new ReferenceTable();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return ReferenceTable
     */
    public static ReferenceTable newInstance() {
        final ReferenceTable returnInstance = new ReferenceTable();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * Method convertToUppercase.
     *
     * @return boolean
     */
    @Override
    protected boolean convertToUppercase() {
        return false;
    }
}