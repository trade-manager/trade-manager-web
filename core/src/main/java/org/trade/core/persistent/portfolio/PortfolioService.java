package org.trade.core.persistent.portfolio;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface PortfolioService {

    /**
     * Method findDefault.
     *
     * @return Portfolio
     */
    Portfolio findDefault();

    /**
     * Method findById.
     *
     * @return Portfolio
     */
    Portfolio findById(Long id);

    /**
     * Method findByName.
     *
     * @return Portfolio
     */
    Portfolio findByName(String name);

    /**
     * Method validateAndGet.
     *
     * @param name String
     * @return Portfolio
     */
    Portfolio validateAndGet(String name);

    /**
     * Method findAll.
     *
     * @return List<Portfolio>
     */
    List<Portfolio> findAll();

    /**
     * Method resetDefault.
     *
     * @param instance
     */
    void resetDefault(final Portfolio instance);
    /**
     * Method save.
     *
     * @param portfolio Portfolio
     * @return Portfolio
     */
    Portfolio save(Portfolio portfolio);

    /**
     * Method delete.
     *
     * @param portfolio Portfolio
     */
    void delete(Portfolio portfolio);
}
