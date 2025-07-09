package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface TradeOrderRepository extends AspectRepository<TradeOrder, Long>, TradeOrderRepositoryCustom {

    TradeOrder findByOrderKey(Integer orderKey);
}
