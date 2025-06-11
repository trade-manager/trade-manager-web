package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradingdayRepository extends JpaRepository<Tradingday, Integer>, TradingdayRepositoryCustom {

}
