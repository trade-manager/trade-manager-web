package org.trade.core.persistent.dao;

import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface PortfolioRepositoryCustom {

    Portfolio findDefault();

    List<Portfolio> findAllPortfolios();
}
