package org.trade.core.valuetype;

import org.trade.core.persistent.strategy.Strategy;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DAOStrategy extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "STRATEGY";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOStrategy() {
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

            final Strategy strategy = (Strategy) decode.getObject();
            boolean isMgr = false;
            if (!strategy.hasStrategyManager()) {

                for (final Decode mgrdecode : decodesAll) {

                    final Strategy strategyMgr = (Strategy) mgrdecode.getObject();
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
     * @return DAOStrategy
     */
    public static DAOStrategy newInstance(String displayName) {

        final DAOStrategy returnInstance = new DAOStrategy();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return DAOStrategy
     */
    public static DAOStrategy newInstance() {

        final DAOStrategy returnInstance = new DAOStrategy();
        returnInstance.setDefaultCode();
        return returnInstance;
    }
}