package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ReferenceTable extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "REF_TABLE";

    public ReferenceTable() {
        super(DECODE);
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