package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface StrategyRepository extends AspectRepository<Strategy, Integer>, StrategyRepositoryCustom {

    Strategy findByName(String name);
}
