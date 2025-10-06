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
     * Method findPortfolioById.
     *
     * @return Portfolio
     */
    Portfolio findPortfolioById(Long id);

    /**
     * Method findPortfolioByName.
     *
     * @return Portfolio
     */
    Portfolio findPortfolioByName(String name);

    /**
     * Method validateAndGetPortfolio.
     *
     * @param name String
     * @return Portfolio
     */
    Portfolio validateAndGetPortfolio(String name);

    /**
     * Method findAllPortfolios.
     *
     * @return List<Portfolio>
     */
    List<Portfolio> findAllPortfolios();

    /**
     * Method savePortfolio.
     *
     * @param portfolio Portfolio
     * @return Portfolio
     */
    Portfolio savePortfolio(Portfolio portfolio);

    /**
     * Method deletePortfolio.
     *
     * @param portfolio Portfolio
     */
    void deletePortfolio(Portfolio portfolio);
}
