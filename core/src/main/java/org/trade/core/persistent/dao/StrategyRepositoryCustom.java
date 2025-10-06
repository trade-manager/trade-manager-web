package org.trade.core.persistent.dao;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface StrategyRepositoryCustom {

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    Strategy findStrategyByName(String name);
}
