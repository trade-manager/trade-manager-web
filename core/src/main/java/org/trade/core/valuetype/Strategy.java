package org.trade.core.valuetype;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Strategy extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "STRATEGY";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public Strategy() {
        super(DECODE);
    }

    /**
     * Method getCodesDecodes.
     *
     * @return List<Decode>
     */
    @Override
    public List<Decode> getCodesDecodes() throws ValueTypeException {

        final List<Decode> decodes = new ArrayList<>();

        final List<Decode> decodesAll = super.getCodesDecodes();
        for (final Decode decode : decodesAll) {

            final org.trade.core.persistent.strategy.Strategy strategy = (org.trade.core.persistent.strategy.Strategy) decode.getObject();
            boolean isMgr = false;
            if (!strategy.hasStrategyManager()) {

                for (final Decode mgrdecode : decodesAll) {

                    final org.trade.core.persistent.strategy.Strategy strategyMgr = (org.trade.core.persistent.strategy.Strategy) mgrdecode.getObject();
                    if (strategyMgr.hasStrategyManager()) {

                        if (strategyMgr.getStrategyManager().equals(strategy)) {

                            isMgr = true;
                            break;
                        }
                    }
                }
            }
            if (!isMgr) {
                decodes.add(decode);
            }
        }
        return decodes;
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return Strategy
     */
    public static Strategy newInstance(String displayName) {

        final Strategy returnInstance = new Strategy();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return Strategy
     */
    public static Strategy newInstance() {

        final Strategy returnInstance = new Strategy();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}