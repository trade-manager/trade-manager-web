package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AccountType extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ACCOUNT_TYPE";
    public static final String INDIVIDUAL = "INDIVIDUAL";

    public AccountType() {
        super(DECODE, true);
    }

    /**
     * Method newInstance.
     *
     * @param value Integer
     * @return BarSize
     */
    public static AccountType newInstance(String value) {
        final AccountType returnInstance = new AccountType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return BarSize
     */
    public static AccountType newInstance() {
        final AccountType returnInstance = new AccountType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}