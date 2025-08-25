package org.trade.core.valuetype;

import java.io.Serial;

/**
 *
 */
public class DAOIndicatorSeries extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "INDICATOR_DATA";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOIndicatorSeries() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return DAOIndicatorSeries
     */
    public static DAOIndicatorSeries newInstance(String displayName) {
        final DAOIndicatorSeries returnInstance = new DAOIndicatorSeries();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return DAOIndicatorSeries
     */
    public static DAOIndicatorSeries newInstance() {
        final DAOIndicatorSeries returnInstance = new DAOIndicatorSeries();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}