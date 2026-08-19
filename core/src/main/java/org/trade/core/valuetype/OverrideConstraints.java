package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class OverrideConstraints extends Decode {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "OVERRIDE_CONSTRAINTS";
    public static final int YES = 1;

    public OverrideConstraints() {
        super(DECODE);
    }

    /**
     * Create a new instance of this object
     *
     * @return OverrideConstraints
     */

    public static OverrideConstraints newInstance() {
        final OverrideConstraints returnInstance = new OverrideConstraints();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}