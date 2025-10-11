package org.trade.core.persistent;

import org.trade.core.aspect.AspectService;
import org.trade.core.persistent.account.AccountService;
import org.trade.core.persistent.candle.CandleService;
import org.trade.core.persistent.codetype.CodeTypeService;
import org.trade.core.persistent.contract.ContractService;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.portfolio.PortfolioService;
import org.trade.core.persistent.rule.RuleService;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.StrategyService;
import org.trade.core.persistent.tradelogdetail.TradelogDetailService;
import org.trade.core.persistent.tradelogdetail.TradelogReport;
import org.trade.core.persistent.tradelogsummary.TradelogSummaryService;
import org.trade.core.persistent.tradeorder.TradeOrder;
import org.trade.core.persistent.tradeorder.TradeOrderService;
import org.trade.core.persistent.tradeorderfill.TradeOrderfillService;
import org.trade.core.persistent.tradeposition.TradePositionService;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradestrategy.TradestrategyOrders;
import org.trade.core.persistent.tradestrategy.TradestrategyService;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.TradingdayService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;


/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradeService {

    String PERSISTENT_PACKAGE = "org.trade.core.persistent.dao.";

    int SCALE_5 = 5;
    int SCALE_2 = 2;

    /**
     * Method getAspectService.
     *
     * @return AspectService
     */
    AspectService getAspectService();

    /**
     * Method getTradingdayService.
     *
     * @return TradingdayService
     */
    TradingdayService getTradingdayService();

    /**
     * Method getCodeTypeService.
     *
     * @return TradingdayService
     */
    CodeTypeService getCodeTypeService();

    /**
     * Method getAccountService.
     *
     * @return AccountService
     */
    AccountService getAccountService();

    /**
     * Method getPortfolioService.
     *
     * @return PortfolioService
     */
    PortfolioService getPortfolioService();

    /**
     * Method getRuleService.
     *
     * @return RuleService
     */
    RuleService getRuleService();

    /**
     * Method getStrategyService.
     *
     * @return StrategyService
     */
    StrategyService getStrategyService();

    /**
     * Method getContractService.
     *
     * @return ContractService
     */
    ContractService getContractService();

    /**
     * Method getCandleService.
     *
     * @return CandleService
     */
    CandleService getCandleService();

    /**
     * Method getTradelogDetailService.
     *
     * @return TradelogDetailService
     */
    TradelogDetailService getTradelogDetailService();

    /**
     * Method getTradelogSummaryService.
     *
     * @return TradelogSummaryService
     */
    TradelogSummaryService getTradelogSummaryService();

    /**
     * Method getTradestrategyService.
     *
     * @return TradestrategyService
     */
    TradestrategyService getTradestrategyService();

    /**
     * Method getTradeOrderService.
     *
     * @return TradeOrderService
     */
    TradeOrderService getTradeOrderService();

    /**
     * Method getTradeOrderfillService.
     *
     * @return TradeOrderfillService
     */
    TradeOrderfillService getTradeOrderfillService();

    /**
     * Method getTradePositionService.
     *
     * @return TradePositionService
     */
    TradePositionService getTradePositionService();

    /**
     * Method saveTrading.
     *
     * @param instance Tradingday
     */
    Tradingday saveTradingday(Tradingday instance);

    /**
     * Method savePortfolio.
     *
     * @param instance Portfolio
     * @return Portfolio
     */
    Portfolio savePortfolio(Portfolio instance);


    /**
     * Method saveTradeOrder.
     *
     * @param instance TradeOrder
     * @return TradeOrder
     */
    TradeOrder saveTradeOrder(TradeOrder instance);

    /**
     * Method saveTradeOrderfill.
     *
     * @param tradeOrder TradeOrder
     * @return TradeOrder
     */
    TradeOrder saveTradeOrderfill(TradeOrder tradeOrder);

    /**
     * Method saveCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    void saveCandleSeries(CandleSeries candleSeries);

    /**
     * Method findPositionOrdersByTradestrategyId.
     *
     * @param tradestrategyId Long
     * @return PositionOrders
     */
    TradestrategyOrders findPositionOrdersByTradestrategyId(Long tradestrategyId);

    /**
     * Method refreshPositionOrdersByTradestrategyId.
     *
     * @param positionOrders PositionOrders
     * @return PositionOrders
     */
    TradestrategyOrders refreshPositionOrdersByTradestrategyId(TradestrategyOrders positionOrders);

    /**
     * Method removeTradingdayTradeOrders.
     *
     * @param instance Tradingday
     */
    void deleteTradingdayTradeOrders(Tradingday instance);

    /**
     * Method removeTradestrategyTradeOrders.
     *
     * @param instance Tradestrategy
     */
    void deleteTradestrategyTradeOrders(Tradestrategy instance);


    /**
     * Method findTradelogReport.
     *
     * @param portfolio     Portfolio
     * @param start         ZonedDateTime
     * @param end           ZonedDateTime
     * @param filter        boolean
     * @param symbol        String
     * @param winLossAmount BigDecimal
     * @return TradelogReport
     */
    TradelogReport findTradelogReport(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end, boolean filter,
                                      String symbol, BigDecimal winLossAmount) throws IOException;


    /**
     * Method reassignStrategy.
     *
     * @param fromStrategy Strategy
     * @param toStrategy   Strategy
     * @param tradingday   Tradingday
     */
    void reassignStrategy(Strategy fromStrategy, Strategy toStrategy, Tradingday tradingday);
}
