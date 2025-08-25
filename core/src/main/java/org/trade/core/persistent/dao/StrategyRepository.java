package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface StrategyRepository extends AspectRepository<Strategy, Long>, StrategyRepositoryCustom {

    Strategy findByName(String name);
}
