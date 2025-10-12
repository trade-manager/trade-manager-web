package org.trade.core.persistent.tradestrategy;

import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.tradeorder.TradeOrder;
import org.trade.core.persistent.tradingday.Tradingday;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradestrategyService {


    /**
     * Method findById.
     *
     * @param id Long
     * @return Tradestrategy
     */
    Tradestrategy findById(Long id);

    /**
     * Method validateAndGet.
     *
     * @param id Long
     * @return Tradestrategy
     */
    Tradestrategy validateAndGet(Long id);

    /**
     * Method findAll.
     *
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findAll();

    /**
     * Method findByRequestId.
     *
     * @param requestId Integer
     * @return Tradestrategy
     */
    Tradestrategy findByRequestId(Integer requestId);

    /**
     * Method findByTradestrategy.
     *
     * @param tradestrategy Tradestrategy
     * @return TradestrategyLite
     */
    TradestrategyLite findByTradestrategy(Tradestrategy tradestrategy);

    /**
     * Method findVersionById.
     *
     * @param id Long
     * @return Integer
     */
    Integer findVersionById(Long id);

    /**
     * Method findPositionOrdersById.
     *
     * @param id Long
     * @return TradestrategyOrders
     */
    TradestrategyOrders findPositionOrdersById(Long id);

    /**
     * Method findByTradeOrder.
     *
     * @param tradeOrder TradeOrder
     * @return Tradestrategy
     */
    Tradestrategy findByTradeOrder(TradeOrder tradeOrder);

    /**
     * Method findByUniqueKeys.
     *
     * @param open          ZonedDateTime
     * @param strategyName  String
     * @param contract      Contract
     * @param portfolioName String
     * @return Tradestrategy
     */
    Tradestrategy findByUniqueKeys(ZonedDateTime open, String strategyName, Contract contract,
                                   String portfolioName);

    /**
     * Method findByDateRangeDistinctBarSizeAndChartDaysAndStrategy.
     *
     * @param fromOpen ZonedDateTime
     * @param toOpen   ZonedDateTime
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findByDateRangeDistinctBarSizeAndChartDaysAndStrategy(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    /**
     * Method findByDateRangeDistinctContract.
     *
     * @param fromOpen ZonedDateTime
     * @param toOpen   ZonedDateTime
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findByDateRangeDistinctContract(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    /**
     * Method findByTradingday.
     *
     * @param tradingday Tradingday
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findByTradingday(Tradingday tradingday);
}
