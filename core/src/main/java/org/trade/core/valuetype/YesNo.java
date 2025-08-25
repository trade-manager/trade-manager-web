package org.trade.core.valuetype;

import java.io.Serial;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class YesNo extends BaseDecode {
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