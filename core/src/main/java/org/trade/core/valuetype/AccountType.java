package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("AccountType")
public class AccountType extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "ACCOUNT_TYPE";
    public static final String INDIVIDUAL = "INDIVIDUAL";

    public AccountType() {
        super(DECODE, true);
    }

    /**
     * Constructor for CodeType.
     *
     * @param type        String
     * @param category    String
     * @param name        String
     * @param description String
     */
    public AccountType(String type, String category, String name, String description) {

        super(type, category, name, description);
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