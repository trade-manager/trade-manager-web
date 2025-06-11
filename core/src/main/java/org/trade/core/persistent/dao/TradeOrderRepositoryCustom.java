package org.trade.core.persistent.dao;


public interface TradeOrderRepositoryCustom {

    TradeOrder persist(final TradeOrder transientInstance);

    Integer findByMaxKey();
}
