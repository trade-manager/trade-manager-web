package org.trade.core.valuetype;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradestrategyStatus extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "TRADESTRATEGY_STATUS";
    public static final String TO = "TO";
    public static final String GB = "GB";
    public static final String RB = "RB";
    public static final String PERCENT = "PERCENT";
    public static final String TTBT = "TTBT";
    public static final String NBB = "NBB";
    public static final String FIVE_MIN_LOW_BROKEN = "FIVE_MIN_LOW_BROKEN";
    public static final String FIVE_MIN_HIGH_BROKEN = "FIVE_MIN_HIGH_BROKEN";
    public static final String CLOSED = "CLOSED";
    public static final String CANCELLED = "CANCELLED";
    public static final String OPEN = "OPEN";

    public TradestrategyStatus() {
        super(DECODE);
    }

    /**
     * Method newInstance.
     *
     * @param value String
     * @return TradestrategyStatus
     */
    public static TradestrategyStatus newInstance(String value) {

        final TradestrategyStatus returnInstance = new TradestrategyStatus();
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return TradestrategyStatus
     */
    public static TradestrategyStatus newInstance() {

        final TradestrategyStatus returnInstance = new TradestrategyStatus();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}