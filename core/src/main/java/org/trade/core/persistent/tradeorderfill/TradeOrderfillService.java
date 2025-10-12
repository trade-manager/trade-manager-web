package org.trade.core.persistent.tradeorderfill;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradeOrderfillService {

    /**
     * Method findByExecId.
     *
     * @param execId String
     * @return TradeOrderfill
     */
    TradeOrderfill findByExecId(String execId);
}
