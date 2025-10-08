package org.trade.core.persistent.strategy;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface StrategyRepository extends AspectRepository<Strategy, Long> {

    /**
     * Method findByName.
     *
     * @param name
     * @return
     */
    Optional<Strategy> findByName(String name);
}
