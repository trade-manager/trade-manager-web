package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface TradeOrderRepository extends AspectRepository<TradeOrder, Integer>, TradeOrderRepositoryCustom {

    TradeOrder findByOrderKey(Integer orderKey);
}
