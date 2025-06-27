/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.persistent;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.TradelogReport;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyLite;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface TradeService extends AspectService {

    /**
     * @param entities
     */
    void deleteAllAspects(Iterable<? extends Aspect> entities);

    /**
     * @param symbol
     * @return
     */
    Optional<Contract> findContractBySymbol(String symbol);

    /**
     * @return
     */
    Iterable<Contract> findAllContracts();

    /**
     * Method persistTrading.
     *
     * @param transientInstance Tradingday
     */
    Tradingday saveTradingday(Tradingday transientInstance);

    /**
     * Method persistPortfolio.
     *
     * @param instance Portfolio
     * @return Portfolio
     */

    Portfolio savePortfolio(Portfolio instance);

    /**
     * Method persistTradeOrder.
     *
     * @param instance TradeOrder
     * @return TradeOrder
     */
    TradeOrder saveTradeOrder(TradeOrder instance);

    /**
     * Method persistTradeOrderfill.
     *
     * @param tradeOrder TradeOrder
     * @return TradeOrder
     */
    TradeOrder saveTradeOrderfill(TradeOrder tradeOrder);

    /**
     * Method persistCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    void saveCandleSeries(CandleSeries candleSeries);

    /**
     * Method persistCandle.
     *
     * @param candle Candle
     * @return Candle
     */
    Candle saveCandle(Candle candle);

    /**
     * Method findAccountById.
     *
     * @param id Integer
     * @return Account
     */
    Account findAccountById(Integer id);

    /**
     * Method findAccountByNumber.
     *
     * @param accountNumber String
     * @return Account
     */
    Account findAccountByAccountNumber(String accountNumber);

    /**
     * Method findContractById.
     *
     * @param contractId Integer
     * @return Contract
     */
    Contract findContractById(Integer contractId);

    /**
     * Method findTradeOrderById.
     *
     * @param tradeOrderId Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderById(Integer tradeOrderId);

    /**
     * Method findContractByUniqueKey.
     *
     * @param SECType  String
     * @param symbol   String
     * @param exchange String
     * @param currency String
     * @return Contract
     */
    Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                     ZonedDateTime expiry);

    /**
     * Method findTradestrategyById.
     *
     * @param tradestrategy Tradestrategy
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Tradestrategy tradestrategy);

    /**
     * Method findTradestrategyById.
     *
     * @param tradestrategyId Integer
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Integer tradestrategyId);

    /**
     * Method existTradestrategyById.
     *
     * @param id Integer
     * @return boolean
     */
    boolean existTradestrategyById(Integer id);

    /**
     * Method findTradestrategyLiteById.
     *
     * @param id Integer
     * @return TradestrategyLite
     */
    TradestrategyLite findTradestrategyLiteById(Integer id);

    /**
     * Method findPositionOrdersByTradestrategyId.
     *
     * @param tradestrategyId Integer
     * @return PositionOrders
     */
    TradestrategyOrders findPositionOrdersByTradestrategyId(Integer tradestrategyId);

    /**
     * Method refreshPositionOrdersByTradestrategyId.
     *
     * @param positionOrders PositionOrders
     * @return PositionOrders
     */
    TradestrategyOrders refreshPositionOrdersByTradestrategyId(TradestrategyOrders positionOrders);

    /**
     * Method findTradestrategyByUniqueKeys.
     *
     * @param open          ZonedDateTime
     * @param strategy      String
     * @param idContract    Integer
     * @param portfolioName String
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyByUniqueKeys(ZonedDateTime open, String strategy, Integer idContract,
                                                String portfolioName);

    /**
     * Method findTradestrategyDistinctByDateRange.
     *
     * @param fromOpen ZonedDateTime
     * @param toOpen   ZonedDateTime
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findTradestrategyDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    /**
     * Method findTradestrategyContractDistinctByDateRange.
     *
     * @param fromOpen Date
     * @param toOpen   Date
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findTradestrategyContractDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    /**
     * Method findAllTradestrategies.
     *
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findAllTradestrategies();

    /**
     * Method findTradePositionById.
     *
     * @param tradePositionId Integer
     * @return TradePosition
     */
    TradePosition findTradePositionById(Integer tradePositionId);

    /**
     * Method findPortfolioById.
     *
     * @param id Integer
     * @return Portfolio
     */
    Portfolio findPortfolioById(Integer id);

    /**
     * Method findPortfolioByName.
     *
     * @param name String
     * @return Portfolio
     */

    Portfolio findPortfolioByName(String name);

    /**
     * Method findPortfolioDefault.
     *
     * @return Portfolio
     */
    Portfolio findPortfolioDefault();

    /**
     * Method resetDefaultPortfolio.
     *
     * @param instance Portfolio
     */
    void resetDefaultPortfolio(Portfolio instance);

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
     * Method findTradeOrderByKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderByKey(Integer orderKey);

    /**
     * Method findTradeOrderfillByExecId.
     *
     * @param execId String
     * @return TradeOrderfill
     */
    TradeOrderfill findTradeOrderfillByExecId(String execId);

    /**
     * Method findTradeOrderByMaxKey.
     *
     * @return Integer
     */
    Integer findTradeOrderByMaxKey();

    /**
     * Method findTradingdayById.
     *
     * @param tradingdayId Integer
     * @return Tradingday
     */
    Tradingday findTradingdayById(Integer tradingdayId);

    /**
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findTradingdayByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate);

    /**
     * Method findTradingdaysByDateRange.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate);

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
     * Method findCandlesByContractDateRangeBarSize.
     *
     * @param contractId Integer
     * @param startDate  ZonedDateTime
     * @param endDate    ZonedDateTime
     * @param barSize    Integer
     * @return List<Candle>
     */
    List<Candle> findCandlesByContractDateRangeBarSize(Integer contractId, ZonedDateTime startDate,
                                                       ZonedDateTime endDate, Integer barSize);

    /**
     * Method findCandleCount.
     *
     * @param tradingdayId Integer
     * @param contractId   Integer
     * @return Long
     */
    Long findCandleCount(Integer tradingdayId, Integer contractId);

    /**
     * Method findRuleById.
     *
     * @param ruleId Integer
     * @return Rule
     */
    Rule findRuleById(Integer ruleId);

    /**
     * Method findRuleByMaxVersion.
     *
     * @param strategy Strategy
     * @return Integer
     */
    Integer findRuleByMaxVersion(Strategy strategy);

    /**
     * Method findStrategyById.
     *
     * @param id Integer
     * @return Strategy
     */
    Strategy findStrategyById(Integer id);

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    Strategy findStrategyByName(String name);

    /**
     * Method findStrategies.
     *
     * @return List<Strategy>
     */
    List<Strategy> findStrategies();

    /**
     * Method saveAspect.
     *
     * @param instance Aspect
     * @return Aspect
     */
    //<T extends Aspect> T findAspectById(T instance);

    /**
     * Method findAspectById.
     *
     * @param instance Aspect
     * @return Aspect
     */
    Aspect findAspectById(final Aspect instance) throws ClassNotFoundException;

    /**
     * Method saveAspect.
     *
     * @param instance Aspect
     * @return Aspect
     */
    <T extends Aspect> T saveAspect(T instance);

    /**
     * Method saveAspect.
     *
     * @param instance        Aspect
     * @param overrideVersion boolean
     * @return Aspect
     */
    <T extends Aspect> T saveAspect(T instance, boolean overrideVersion);

    /**
     * Method removeAspect.
     *
     * @param instance Aspect
     */
    void deleteAspect(Aspect instance);

    /**
     * Method reassignStrategy.
     *
     * @param fromStrategy Strategy
     * @param toStrategy   Strategy
     * @param tradingday   Tradingday
     */
    void reassignStrategy(Strategy fromStrategy, Strategy toStrategy, Tradingday tradingday);

    /**
     * Method findCodeTypeByNameType.
     *
     * @param name String
     * @param type String
     * @return CodeType
     */
    CodeType findCodeTypeByNameType(String name, String type);
}
