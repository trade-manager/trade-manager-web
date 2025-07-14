package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface PortfolioRepository extends AspectRepository<Portfolio, Long>, PortfolioRepositoryCustom {

    Portfolio findByName(String name);
}
