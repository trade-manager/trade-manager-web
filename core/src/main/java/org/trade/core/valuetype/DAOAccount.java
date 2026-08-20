package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DAOAccount extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ACCOUNT";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOAccount() {
        super(DECODE, true);
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return DAOAccount
     */
    public static DAOAccount newInstance(String displayName) {

        final DAOAccount returnInstance = new DAOAccount();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return DAOAccount
     */
    public static DAOAccount newInstance() {

        final DAOAccount returnInstance = new DAOAccount();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * Method convertToUppercase.
     *
     * @return boolean
     */
    protected boolean convertToUppercase() {
        return false;
    }
}