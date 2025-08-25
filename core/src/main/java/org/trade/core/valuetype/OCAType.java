package org.trade.core.valuetype;

import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class OCAType extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "OCA_TYPE";

    public OCAType() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return OCAType
     */
    public static OCAType newInstance(String value) {
        final OCAType returnInstance = new OCAType();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return OCAType
     */
    public static OCAType newInstance() {
        final OCAType returnInstance = new OCAType();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}