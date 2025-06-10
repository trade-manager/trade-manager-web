package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StrategyRepository extends JpaRepository<Strategy, Integer>, StrategyRepositoryCustom {
    Strategy findByName(String name);
}
