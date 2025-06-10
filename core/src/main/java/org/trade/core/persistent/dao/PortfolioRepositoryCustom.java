package org.trade.core.persistent.dao;


public interface PortfolioRepositoryCustom {
    Portfolio findDefault();

    void resetDefaultPortfolio(final Portfolio defaultPortfolio);

    Portfolio persistPortfolio(final Portfolio instance);
}
