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
package org.trade.persistent;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.persistent.dao.Account;
import org.trade.persistent.dao.Candle;
import org.trade.persistent.dao.CodeType;
import org.trade.persistent.dao.Contract;
import org.trade.persistent.dao.Portfolio;
import org.trade.persistent.dao.Rule;
import org.trade.persistent.dao.Strategy;
import org.trade.persistent.dao.TradeOrder;
import org.trade.persistent.dao.TradeOrderfill;
import org.trade.persistent.dao.TradePosition;
import org.trade.persistent.dao.TradelogReport;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.persistent.dao.TradestrategyLite;
import org.trade.persistent.dao.TradestrategyOrders;
import org.trade.persistent.dao.Tradingday;
import org.trade.persistent.dao.Tradingdays;
import org.trade.strategy.data.CandleSeries;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 *
 */
public interface IPersistentModel {

    String _persistentModel = "PersistentModel";

    /**
     * Method persistTradingday.
     *
     * @param transientInstance Tradingday
     */
    void persistTradingday(Tradingday transientInstance) throws PersistentModelException;

    /**
     * Method persistContract.
     *
     * @param transientInstance Contract
     * @return Contract
     */
    Contract persistContract(Contract transientInstance) throws PersistentModelException;

    /**
     * Method persistPortfolio.
     *
     * @param instance Portfolio
     * @return Portfolio
     */

    Portfolio persistPortfolio(Portfolio instance) throws PersistentModelException;

    /**
     * Method persistTradeOrder.
     *
     * @param transientInstance TradeOrder
     * @return TradeOrder
     */
    TradeOrder persistTradeOrder(TradeOrder transientInstance) throws PersistentModelException;

    /**
     * Method persistTradeOrderfill.
     *
     * @param tradeOrder TradeOrder
     * @return TradeOrder
     */
    TradeOrder persistTradeOrderfill(TradeOrder tradeOrder) throws PersistentModelException;

    /**
     * Method persistCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    void persistCandleSeries(CandleSeries candleSeries) throws PersistentModelException;

    /**
     * Method persistCandle.
     *
     * @param candle Candle
     * @return Candle
     */
    Candle persistCandle(Candle candle) throws PersistentModelException;

    /**
     * Method findAccountById.
     *
     * @param id Integer
     * @return Account
     */
    Account findAccountById(Integer id) throws PersistentModelException;

    /**
     * Method findAccountByNumber.
     *
     * @param accountNumber String
     * @return Account
     */
    Account findAccountByNumber(String accountNumber) throws PersistentModelException;

    /**
     * Method findContractById.
     *
     * @param idContract Integer
     * @return Contract
     */
    Contract findContractById(Integer idContract) throws PersistentModelException;

    /**
     * Method findTradeOrderById.
     *
     * @param idTradeOrder Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderById(Integer idTradeOrder) throws PersistentModelException;

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
                                     ZonedDateTime expiry) throws PersistentModelException;

    /**
     * Method findTradestrategyById.
     *
     * @param tradestrategy Tradestrategy
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Tradestrategy tradestrategy) throws PersistentModelException;

    /**
     * Method findTradestrategyById.
     *
     * @param idTradestrategy Integer
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Integer idTradestrategy) throws PersistentModelException;

    /**
     * Method existTradestrategyById.
     *
     * @param id Integer
     * @return boolean
     * @see IPersistentModel#existTradestrategyById(Integer)
     */
    boolean existTradestrategyById(Integer id);

    /**
     * Method findTradestrategyLiteById.
     *
     * @param id Integer
     * @return TradestrategyLite
     */
    TradestrategyLite findTradestrategyLiteById(Integer id) throws PersistentModelException;

    /**
     * Method findPositionOrdersByTradestrategyId.
     *
     * @param idTradestrategy Integer
     * @return PositionOrders
     */
    TradestrategyOrders findPositionOrdersByTradestrategyId(Integer idTradestrategy) throws PersistentModelException;

    /**
     * Method refreshPositionOrdersByTradestrategyId.
     *
     * @param positionOrders PositionOrders
     * @return PositionOrders
     */
    TradestrategyOrders refreshPositionOrdersByTradestrategyId(TradestrategyOrders positionOrders)
            throws PersistentModelException;

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
                                                String portfolioName) throws PersistentModelException;

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
    List<Tradestrategy> findAllTradestrategies() throws PersistentModelException;

    /**
     * Method findTradePositionById.
     *
     * @param idTradePosition Integer
     * @return TradePosition
     */
    TradePosition findTradePositionById(Integer idTradePosition) throws PersistentModelException;

    /**
     * Method findPortfolioById.
     *
     * @param id Integer
     * @return Portfolio
     */
    Portfolio findPortfolioById(Integer id) throws PersistentModelException;

    /**
     * Method findPortfolioByName.
     *
     * @param name String
     * @return Portfolio
     */

    Portfolio findPortfolioByName(String name) throws PersistentModelException;

    /**
     * Method findPortfolioDefault.
     *
     * @return Portfolio
     */
    Portfolio findPortfolioDefault() throws PersistentModelException;

    /**
     * Method resetDefaultPortfolio.
     *
     * @param transientInstance Portfolio
     */
    void resetDefaultPortfolio(Portfolio transientInstance) throws PersistentModelException;

    /**
     * Method removeTradingdayTradeOrders.
     *
     * @param transientInstance Tradingday
     */
    void removeTradingdayTradeOrders(Tradingday transientInstance) throws PersistentModelException;

    /**
     * Method removeTradestrategyTradeOrders.
     *
     * @param transientInstance Tradestrategy
     */
    void removeTradestrategyTradeOrders(Tradestrategy transientInstance) throws PersistentModelException;

    /**
     * Method findTradeOrderByKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderByKey(Integer orderKey) throws PersistentModelException;

    /**
     * Method findTradeOrderfillByExecId.
     *
     * @param execId String
     * @return TradeOrderfill
     */
    TradeOrderfill findTradeOrderfillByExecId(String execId) throws PersistentModelException;

    /**
     * Method findTradeOrderByMaxKey.
     *
     * @return Integer
     */
    Integer findTradeOrderByMaxKey() throws PersistentModelException;

    /**
     * Method findTradingdayById.
     *
     * @param idTradingday Integer
     * @return Tradingday
     */
    Tradingday findTradingdayById(Integer idTradingday) throws PersistentModelException;

    /**
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findTradingdayByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate)
            throws PersistentModelException;

    /**
     * Method findTradingdaysByDateRange.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate)
            throws PersistentModelException;

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
                                      String symbol, BigDecimal winLossAmount) throws PersistentModelException;

    /**
     * Method findCandlesByContractDateRangeBarSize.
     *
     * @param idContract Integer
     * @param startDate  ZonedDateTime
     * @param endDate    ZonedDateTime
     * @param barSize    Integer
     * @return List<Candle>
     */
    List<Candle> findCandlesByContractDateRangeBarSize(Integer idContract, ZonedDateTime startDate,
                                                       ZonedDateTime endDate, Integer barSize) throws PersistentModelException;

    /**
     * Method findCandleCount.
     *
     * @param idTradingday Integer
     * @param idContract   Integer
     * @return Long
     */
    Long findCandleCount(Integer idTradingday, Integer idContract) throws PersistentModelException;

    /**
     * Method findRuleById.
     *
     * @param idRule Integer
     * @return Rule
     */
    Rule findRuleById(Integer idRule) throws PersistentModelException;

    /**
     * Method findRuleByMaxVersion.
     *
     * @param strategy Strategy
     * @return Integer
     */
    Integer findRuleByMaxVersion(Strategy strategy) throws PersistentModelException;

    /**
     * Method findStrategyById.
     *
     * @param id Integer
     * @return Strategy
     */
    Strategy findStrategyById(Integer id) throws PersistentModelException;

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    Strategy findStrategyByName(String name) throws PersistentModelException;

    /**
     * Method findStrategies.
     *
     * @return List<Strategy>
     */
    List<Strategy> findStrategies() throws PersistentModelException;

    /**
     * Method findAspectsByClassName.
     *
     * @param aspectClassName String
     * @return Aspects
     */
    Aspects findAspectsByClassName(String aspectClassName) throws PersistentModelException;

    /**
     * Method findAspectsByClassNameFieldName.
     *
     * @param className String
     * @param fieldname String
     * @param value     String
     * @return Aspects
     */
    Aspects findAspectsByClassNameFieldName(String className, String fieldname, String value)
            throws PersistentModelException;

    /**
     * Method findAspectById.
     *
     * @param transientInstance Aspect
     * @return Aspect
     */
    Aspect findAspectById(Aspect transientInstance) throws PersistentModelException;

    /**
     * Method persistAspect.
     *
     * @param transientInstance Aspect
     * @return Aspect
     */
    <T extends Aspect> T persistAspect(T transientInstance) throws PersistentModelException;

    /**
     * Method persistAspect.
     *
     * @param transientInstance Aspect
     * @param overrideVersion   boolean
     * @return Aspect
     */
    <T extends Aspect> T persistAspect(T transientInstance, boolean overrideVersion) throws PersistentModelException;

    /**
     * Method removeAspect.
     *
     * @param aspect Aspect
     */
    void removeAspect(Aspect aspect) throws PersistentModelException;

    /**
     * Method reassignStrategy.
     *
     * @param fromStrategy Strategy
     * @param toStrategy   Strategy
     * @param tradingday   Tradingday
     */
    void reassignStrategy(Strategy fromStrategy, Strategy toStrategy, Tradingday tradingday)
            throws PersistentModelException;

    /**
     * Method findCodeTypeByNameType.
     *
     * @param name String
     * @param type String
     * @return CodeType
     */
    CodeType findCodeTypeByNameType(String name, String type) throws PersistentModelException;
}
