package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradePositionRepository extends JpaRepository<TradePosition, Integer>, TradePositionRepositoryCustom {

}
