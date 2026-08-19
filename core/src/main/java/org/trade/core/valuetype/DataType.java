package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DataType extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "DATA_TYPE";

    public DataType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return DataType
     */
    public static DataType newInstance(String value) {
        final DataType returnInstance = new DataType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return DataType
     */
    public static DataType newInstance() {
        final DataType returnInstance = new DataType();
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