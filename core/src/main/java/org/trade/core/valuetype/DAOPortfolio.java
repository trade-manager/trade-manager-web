package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DAOPortfolio extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "PORTFOLIO";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOPortfolio() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return Portfolio
     */
    public static DAOPortfolio newInstance(String displayName) {
        final DAOPortfolio returnInstance = new DAOPortfolio();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Portfolio
     */
    public static DAOPortfolio newInstance() {

        try {
            final DAOPortfolio returnInstance = new DAOPortfolio();
            DAOPortfolio code = null;
            for (Decode decode : returnInstance.getCodesDecodes()) {
                code = (DAOPortfolio) decode;
                org.trade.core.persistent.portfolio.Portfolio portfolio = (org.trade.core.persistent.portfolio.Portfolio) code.getObject();
                if (portfolio.getIsDefault())
                    return code;
            }
            if (null == code) {
                code = returnInstance;
            }
            return code;
        } catch (ValueTypeException e) {
            return null;
        }
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