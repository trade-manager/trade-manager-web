package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PortfolioRepository extends JpaRepository<Portfolio, Integer>, PortfolioRepositoryCustom {

    Portfolio findByName(String name);

    Portfolio findByAccountNumber(String accountNumber);


}
