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
import org.trade.core.dao.Aspects;
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
     *
     * @param entities
     */
    void deleteAllAspects(Iterable<? extends Aspect> entities);

    /**
     *
     * @param symbol
     * @return
     */
    Optional<Contract> findContractBySymbol(String symbol);

    /**
     *
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
    TradeOrder saveTradeOrderfill(TradeOrder tradeOrder) throws ServiceException;

    /**
     * Method persistCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    void saveCandleSeries(CandleSeries candleSeries) throws ServiceException;

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
    Account findAccountById(Integer id) throws ServiceException;

    /**
     * Method findAccountByNumber.
     *
     * @param accountNumber String
     * @return Account
     */
    Account findAccountByAccountNumber(String accountNumber) throws ServiceException;

    /**
     * Method findContractById.
     *
     * @param contractId Integer
     * @return Contract
     */
    Contract findContractById(Integer contractId) throws ServiceException;

    /**
     * Method findTradeOrderById.
     *
     * @param tradeOrderId Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderById(Integer tradeOrderId) throws ServiceException;

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
                                     ZonedDateTime expiry) throws ServiceException;

    /**
     * Method findTradestrategyById.
     *
     * @param tradestrategy Tradestrategy
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Tradestrategy tradestrategy) throws ServiceException;

    /**
     * Method findTradestrategyById.
     *
     * @param tradestrategyId Integer
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Integer tradestrategyId) throws ServiceException;

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
    TradestrategyLite findTradestrategyLiteById(Integer id) throws ServiceException;

    /**
     * Method findPositionOrdersByTradestrategyId.
     *
     * @param tradestrategyId Integer
     * @return PositionOrders
     */
    TradestrategyOrders findPositionOrdersByTradestrategyId(Integer tradestrategyId) throws ServiceException;

    /**
     * Method refreshPositionOrdersByTradestrategyId.
     *
     * @param positionOrders PositionOrders
     * @return PositionOrders
     */
    TradestrategyOrders refreshPositionOrdersByTradestrategyId(TradestrategyOrders positionOrders)
            throws ServiceException;

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
                                                String portfolioName) throws ServiceException;

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
    List<Tradestrategy> findAllTradestrategies() throws ServiceException;

    /**
     * Method findTradePositionById.
     *
     * @param tradePositionId Integer
     * @return TradePosition
     */
    TradePosition findTradePositionById(Integer tradePositionId) throws ServiceException;

    /**
     * Method findPortfolioById.
     *
     * @param id Integer
     * @return Portfolio
     */
    Portfolio findPortfolioById(Integer id) throws ServiceException;

    /**
     * Method findPortfolioByName.
     *
     * @param name String
     * @return Portfolio
     */

    Portfolio findPortfolioByName(String name) throws ServiceException;

    /**
     * Method findPortfolioDefault.
     *
     * @return Portfolio
     */
    Portfolio findPortfolioDefault() throws ServiceException;

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
    void deleteTradingdayTradeOrders(Tradingday instance) throws ServiceException;

    /**
     * Method removeTradestrategyTradeOrders.
     *
     * @param instance Tradestrategy
     */
    void deleteTradestrategyTradeOrders(Tradestrategy instance) throws ServiceException;

    /**
     * Method findTradeOrderByKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderByKey(Integer orderKey) throws ServiceException;

    /**
     * Method findTradeOrderfillByExecId.
     *
     * @param execId String
     * @return TradeOrderfill
     */
    TradeOrderfill findTradeOrderfillByExecId(String execId) throws ServiceException;

    /**
     * Method findTradeOrderByMaxKey.
     *
     * @return Integer
     */
    Integer findTradeOrderByMaxKey() throws ServiceException;

    /**
     * Method findTradingdayById.
     *
     * @param tradingdayId Integer
     * @return Tradingday
     */
    Tradingday findTradingdayById(Integer tradingdayId) throws ServiceException;

    /**
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findTradingdayByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate)
            throws ServiceException;

    /**
     * Method findTradingdaysByDateRange.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate)
            throws ServiceException;

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
                                                       ZonedDateTime endDate, Integer barSize) throws ServiceException;

    /**
     * Method findCandleCount.
     *
     * @param tradingdayId Integer
     * @param contractId   Integer
     * @return Long
     */
    Long findCandleCount(Integer tradingdayId, Integer contractId) throws ServiceException;

    /**
     * Method findRuleById.
     *
     * @param ruleId Integer
     * @return Rule
     */
    Rule findRuleById(Integer ruleId) throws ServiceException;

    /**
     * Method findRuleByMaxVersion.
     *
     * @param strategy Strategy
     * @return Integer
     */
    Integer findRuleByMaxVersion(Strategy strategy) throws ServiceException;

    /**
     * Method findStrategyById.
     *
     * @param id Integer
     * @return Strategy
     */
    Strategy findStrategyById(Integer id) throws ServiceException;

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    Strategy findStrategyByName(String name) throws ServiceException;

    /**
     * Method findStrategies.
     *
     * @return List<Strategy>
     */
    List<Strategy> findStrategies() throws ServiceException;

    /**
     * Method findAspectById.
     *
     * @param instance Aspect
     * @return Aspect
     */
    Aspect findAspectById(Aspect instance) throws ServiceException;

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
    void reassignStrategy(Strategy fromStrategy, Strategy toStrategy, Tradingday tradingday)
            throws ServiceException;

    /**
     * Method findCodeTypeByNameType.
     *
     * @param name String
     * @param type String
     * @return CodeType
     */
    CodeType findCodeTypeByNameType(String name, String type);
}
