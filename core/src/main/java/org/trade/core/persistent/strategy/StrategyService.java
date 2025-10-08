package org.trade.core.persistent.strategy;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface StrategyService {

    /**
     * Method findById.
     *
     * @param id
     * @return
     */
    Strategy findById(Long id);

    /**
     * Method findAll.
     *
     * @return List<Strategy>
     */
    List<Strategy> findAll();

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    Strategy findByName(String name);

    /**
     * Method validateAndGet.
     *
     * @param name
     * @return
     */
    Strategy validateAndGet(String name);
}
