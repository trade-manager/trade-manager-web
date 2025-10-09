package org.trade.core.persistent.dao;

import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradestrategyRepository extends AspectRepository<Tradestrategy, Long>, TradestrategyRepositoryCustom {

    Tradestrategy findByRequestId(Integer requestId);
}
