package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

public interface TradestrategyRepositoryCustom {

    TradestrategyLite findTradestrategyLiteById(Integer id);

    Integer findVersionById(Integer id);

    TradestrategyOrders findPositionOrdersByTradestrategyId(Integer tradestrategyId);

    Tradestrategy findByTradeOrderId(Integer tradeOrderId);

    Tradestrategy findTradestrategyByUniqueKeys(ZonedDateTime open, String strategyName, Integer contractId,
                                                String portfolioName);

    List<Tradestrategy> findTradestrategyDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    List<Tradestrategy> findTradestrategyContractDistinctByDateRange(ZonedDateTime fromOpen,
                                                                     ZonedDateTime toOpen);
}
