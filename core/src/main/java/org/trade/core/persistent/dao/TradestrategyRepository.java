package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface TradestrategyRepository extends AspectRepository<Tradestrategy, Long>, TradestrategyRepositoryCustom {

    Tradestrategy findByRequestId(Integer requestId);
}
