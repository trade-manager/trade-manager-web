package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface PortfolioRepository extends AspectRepository<Portfolio, Long>, PortfolioRepositoryCustom {

    Portfolio findByName(String name);
}
