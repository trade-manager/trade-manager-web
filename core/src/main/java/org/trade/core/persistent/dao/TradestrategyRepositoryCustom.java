package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

public interface TradestrategyRepositoryCustom {

    TradestrategyLite findTradestrategyLiteByTradestrategy(Tradestrategy tradestrategy);

    Integer findVersionByTradestrategyId(Long tradestrategyId);

    TradestrategyOrders findPositionOrdersByTradestrategyId(Long tradestrategyId);

    Tradestrategy findByTradeOrder(TradeOrder tradeOrder);

    Tradestrategy findTradestrategyByUniqueKeys(ZonedDateTime open, String strategyName, Contract contract,
                                                String portfolioName);

    List<Tradestrategy> findTradestrategyDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    List<Tradestrategy> findTradestrategyContractDistinctByDateRange(ZonedDateTime fromOpen,
                                                                     ZonedDateTime toOpen);
}
