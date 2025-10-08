package org.trade.core.persistent.dao;

import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.tradingday.Tradingday;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
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

    /**
     * Method findTradestrategyByTradingday.
     *
     * @param tradingday Tradingday
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findTradestrategyByTradingday(Tradingday tradingday);
}
