package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeOrderRepository extends JpaRepository<TradeOrder, Integer>, TradeOrderRepositoryCustom {

    TradeOrder findByOrderKey(Integer orderKey);
}
