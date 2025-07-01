package org.trade.core.persistent.dao;


import java.util.List;

public interface PortfolioRepositoryCustom {

    Portfolio findDefault();

    List<Portfolio> findAllPortfolios();
}
