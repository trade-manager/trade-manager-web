package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradestrategyRepository extends JpaRepository<Tradestrategy, Integer>, TradestrategyRepositoryCustom {

}
