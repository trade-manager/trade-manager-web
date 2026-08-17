package org.trade.core.valuetype;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("YesNo")
public class YesNo extends Decode {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1288975993214301679L;

    public static final String NO = "false";
    public static final String YES = "true";
    public static final String DECODE = "YES_NO";

    public YesNo() {
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
    public YesNo(String type, String category, String name, String description) {

        super(type, category, name, description);
    }

    /**
     * isYes
     *
     * @return boolean
     */

    public boolean isYes() {
        return YES.equals(getCode());
    }

    /**
     * isNo
     *
     * @return boolean
     */

    public boolean isNo() {
        return NO.equals(getCode());
    }

    /**
     * Create a new instance of this object
     *
     * @param code String
     * @return YesNo
     */

    public static YesNo newInstance(String code) {
        final YesNo returnInstance = new YesNo();
        returnInstance.setValue(code);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @param code Boolean
     * @return YesNo
     */
    public static YesNo newInstance(Boolean code) {
        final YesNo returnInstance = new YesNo();
        returnInstance.setValue(code.toString());
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