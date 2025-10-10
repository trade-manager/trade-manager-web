package org.trade.core.persistent.tradestrategy;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradestrategyRepository extends AspectRepository<Tradestrategy, Long> {

    /**
     * Method findByRequestId.
     *
     * @param requestId Integer
     * @return Tradestrategy
     */
    Tradestrategy findByRequestId(Integer requestId);
}
