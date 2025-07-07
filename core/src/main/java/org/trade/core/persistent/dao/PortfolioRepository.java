package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface PortfolioRepository extends AspectRepository<Portfolio, Integer>, PortfolioRepositoryCustom {

    Portfolio findByName(String name);

}
