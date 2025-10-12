package org.trade.core.persistent.tradeorderfill;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradeOrderfillRepository extends AspectRepository<TradeOrderfill, Long> {

    /**
     * Method findByExecId.
     *
     * @param execId String
     * @return TradeOrderfill
     */
    TradeOrderfill findByExecId(String execId);
}
