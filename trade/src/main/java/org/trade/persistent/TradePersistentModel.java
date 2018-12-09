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
import org.trade.core.dao.AspectHome;
import org.trade.core.dao.Aspects;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.TradingCalendar;
import org.trade.core.valuetype.Money;
import org.trade.dictionary.valuetype.Action;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.dictionary.valuetype.Side;
import org.trade.dictionary.valuetype.TradestrategyStatus;
import org.trade.persistent.dao.*;
import org.trade.strategy.data.CandleSeries;

import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.List;

/**
 *
 */
public class TradePersistentModel implements IPersistentModel {

    private CodeTypeHome m_codeTypeHome = null;
    private ContractHome m_contractHome = null;
    private StrategyHome m_strategyHome = null;
    private TradingdayHome m_tradingdayHome = null;
    private TradeOrderHome m_tradeOrderHome = null;
    private TradeOrderfillHome m_tradeOrderfillHome = null;
    private TradePositionHome m_tradePositionHome = null;
    private TradelogHome m_tradelogHome = null;
    private AccountHome m_accountHome = null;
    private PortfolioHome m_portfolioHome = null;
    private TradestrategyHome m_tradestrategyHome = null;
    private CandleHome m_candleHome = null;
    private AspectHome m_aspectHome = null;
    private RuleHome m_ruleHome = null;

    private static final int SCALE_5 = 5;
    private static final int SCALE_2 = 2;

    public TradePersistentModel() {
        m_codeTypeHome = new CodeTypeHome();
        m_contractHome = new ContractHome();
        m_strategyHome = new StrategyHome();
        m_tradingdayHome = new TradingdayHome();
        m_tradeOrderHome = new TradeOrderHome();
        m_tradeOrderfillHome = new TradeOrderfillHome();
        m_tradePositionHome = new TradePositionHome();
        m_tradelogHome = new TradelogHome();
        m_accountHome = new AccountHome();
        m_portfolioHome = new PortfolioHome();
        m_tradestrategyHome = new TradestrategyHome();
        m_candleHome = new CandleHome();
        m_aspectHome = new AspectHome();
        m_ruleHome = new RuleHome();
    }

    public TradelogReport findTradelogReport(final Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                             boolean filter, String symbol, BigDecimal winLossAmount) {
        return m_tradelogHome.findByTradelogReport(portfolio, start, end, filter, symbol, winLossAmount);
    }


    public Account findAccountById(final Integer id) throws PersistentModelException {
        Account instance = m_accountHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Account not found for id: " + id);
        return instance;
    }


    public Account findAccountByNumber(String accountNumber) {
        return m_accountHome.findByAccountNumber(accountNumber);
    }


    public Tradingday findTradingdayById(final Integer id) throws PersistentModelException {
        Tradingday instance = m_tradingdayHome.findTradingdayById(id);
        if (null == instance)
            throw new PersistentModelException("Tradingday not found for id: " + id);
        return instance;
    }

    public Tradingday findTradingdayByOpenCloseDate(final ZonedDateTime openDate, final ZonedDateTime closeDate)
            throws PersistentModelException {
        return m_tradingdayHome.findByOpenCloseDate(openDate, closeDate);
    }

    public Contract findContractById(final Integer id) throws PersistentModelException {
        Contract instance = m_contractHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Contract not found for id: " + id);
        return instance;
    }

    public TradeOrder findTradeOrderById(final Integer id) throws PersistentModelException {
        TradeOrder instance = m_tradeOrderHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Contract not found for id: " + id);
        return instance;
    }

    public Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                            ZonedDateTime expiry) throws PersistentModelException {
        return m_contractHome.findByUniqueKey(SECType, symbol, exchange, currency, expiry);
    }

    public Tradestrategy findTradestrategyById(final Tradestrategy tradestrategy) throws PersistentModelException {
        if (null == tradestrategy.getId())
            throw new PersistentModelException(
                    "Please save Tradestrategy for symbol: " + tradestrategy.getContract().getSymbol());

        Tradestrategy instance = m_tradestrategyHome.findById(tradestrategy.getId());
        if (null == instance)
            throw new PersistentModelException("Tradestrategy not found for id: " + tradestrategy.getId());

        instance.setStrategyData(tradestrategy.getStrategyData());
        return instance;
    }

    public TradestrategyOrders refreshPositionOrdersByTradestrategyId(final TradestrategyOrders positionOrders)
            throws PersistentModelException {

        Integer version = m_tradestrategyHome.findVersionById(positionOrders.getId());

        if (positionOrders.getVersion().equals(version)) {
            return positionOrders;
        } else {
            TradestrategyOrders instance = m_tradestrategyHome
                    .findPositionOrdersByTradestrategyId(positionOrders.getId());
            if (null == instance)
                throw new PersistentModelException(
                        "Tradestrategy not found for id: " + positionOrders.getId());
            return instance;
        }
    }

    public TradestrategyOrders findPositionOrdersByTradestrategyId(final Integer idTradestrategy)
            throws PersistentModelException {

        TradestrategyOrders instance = m_tradestrategyHome.findPositionOrdersByTradestrategyId(idTradestrategy);
        if (null == instance)
            throw new PersistentModelException("Tradestrategy not found for id: " + idTradestrategy);
        return instance;
    }

    public Tradestrategy findTradestrategyById(final Integer id) throws PersistentModelException {
        Tradestrategy instance = m_tradestrategyHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Tradestrategy not found for id: " + id);
        return instance;
    }

    public boolean existTradestrategyById(final Integer id) {
        Tradestrategy instance = m_tradestrategyHome.findById(id);
        if (null == instance)
            return false;
        return true;
    }

    public TradestrategyLite findTradestrategyLiteById(final Integer id) throws PersistentModelException {
        TradestrategyLite instance = m_tradestrategyHome.findTradestrategyLiteById(id);
        if (null == instance)
            throw new PersistentModelException("TradestrategyLite not found for id: " + id);
        return instance;
    }

    public TradePosition findTradePositionById(final Integer id) throws PersistentModelException {
        TradePosition instance = m_tradePositionHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("TradePosition not found for id: " + id);
        return instance;
    }

    public Portfolio findPortfolioById(final Integer id) throws PersistentModelException {
        Portfolio instance = m_portfolioHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Portfolio not found for id: " + id);
        return instance;
    }

    public Portfolio findPortfolioByName(String name) {
        return m_portfolioHome.findByName(name);
    }

    public Portfolio findPortfolioDefault() {
        return m_portfolioHome.findDefault();
    }

    public void resetDefaultPortfolio(final Portfolio transientInstance) throws PersistentModelException {
        try {
            m_portfolioHome.resetDefaultPortfolio(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error setting default portfolio. Please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving Portfolio: " + transientInstance.getName() + "\n Msg: " + e.getMessage());
        }

    }

    public Portfolio persistPortfolio(final Portfolio instance) throws PersistentModelException {
        try {
            return m_portfolioHome.persistPortfolio(instance);
        } catch (Exception ex) {
            throw new PersistentModelException("Error saving PortfolioAccount: " + ex.getMessage());
        }
    }

    public List<Tradestrategy> findAllTradestrategies() {
        return m_tradestrategyHome.findAll();

    }

    public Tradestrategy findTradestrategyByUniqueKeys(final ZonedDateTime open, final String strategy,
                                                       final Integer idContract, final String portfolioName) {
        return m_tradestrategyHome.findTradestrategyByUniqueKeys(open, strategy, idContract, portfolioName);
    }

    public List<Tradestrategy> findTradestrategyDistinctByDateRange(final ZonedDateTime fromOpen,
                                                                    final ZonedDateTime toOpen) {
        return m_tradestrategyHome.findTradestrategyDistinctByDateRange(fromOpen, toOpen);
    }

    public List<Tradestrategy> findTradestrategyContractDistinctByDateRange(final ZonedDateTime fromOpen,
                                                                            final ZonedDateTime toOpen) {
        return m_tradestrategyHome.findTradestrategyContractDistinctByDateRange(fromOpen, toOpen);
    }

    public void removeTradingdayTradeOrders(final Tradingday transientInstance) throws PersistentModelException {
        for (Tradestrategy tradestrategy : transientInstance.getTradestrategies()) {
            this.removeTradestrategyTradeOrders(tradestrategy);
        }
    }

    public void removeTradestrategyTradeOrders(final Tradestrategy tradestrategy) throws PersistentModelException {

        try {
            /*
             * Refresh the trade strategy as orders across tradePosition could
             * have been deleted if this is a bulk delete of tradestrategies.
             */
            Tradestrategy transientInstance = m_tradestrategyHome.findById(tradestrategy.getId());
            transientInstance.setStatus(null);
            m_aspectHome.persist(transientInstance);

            Hashtable<Integer, TradePosition> tradePositions = new Hashtable<Integer, TradePosition>();
            for (TradeOrder tradeOrder : transientInstance.getTradeOrders()) {
                if (tradeOrder.hasTradePosition())
                    tradePositions.put(tradeOrder.getTradePosition().getId(),
                            tradeOrder.getTradePosition());

                if (null != tradeOrder.getId()) {
                    m_aspectHome.remove(tradeOrder);
                }
            }
            for (TradePosition tradePosition : tradePositions.values()) {
                tradePosition = this.findTradePositionById(tradePosition.getId());
                /*
                 * Remove the open trade position from contract if this is a
                 * tradePosition to be deleted.
                 */
                if (tradePosition.equals(transientInstance.getContract().getTradePosition())) {
                    transientInstance.getContract().setTradePosition(null);
                    m_aspectHome.persist(transientInstance.getContract());
                }
                m_aspectHome.remove(tradePosition);
            }

            transientInstance.getTradeOrders().clear();
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException(
                    "Error removing Tradestrategy TradePositions. Please refresh before remove.");

        } catch (Exception ex) {
            throw new PersistentModelException("Error removing Tradestrategy TradePositions: "
                    + tradestrategy.getContract().getSymbol() + "\n Msg: " + ex.getMessage());
        }
    }

    public TradeOrder findTradeOrderByKey(final Integer orderKey) {
        return m_tradeOrderHome.findTradeOrderByKey(orderKey);
    }

    public TradeOrderfill findTradeOrderfillByExecId(String execId) throws PersistentModelException {
        return m_tradeOrderfillHome.findOrderFillByExecId(execId);
    }

    public Integer findTradeOrderByMaxKey() {
        return m_tradeOrderHome.findTradeOrderByMaxKey();
    }

    public Tradingdays findTradingdaysByDateRange(final ZonedDateTime startDate, final ZonedDateTime endDate) {
        return m_tradingdayHome.findTradingdaysByDateRange(startDate, endDate);
    }

    public List<Candle> findCandlesByContractDateRangeBarSize(final Integer idContract, final ZonedDateTime startDate,
                                                              final ZonedDateTime endDate, final Integer barSize) {
        return m_candleHome.findCandlesByContractDateRangeBarSize(idContract, startDate, endDate, barSize);
    }

    public Long findCandleCount(final Integer idTradingday, final Integer idContract) {
        return m_candleHome.findCandleCount(idTradingday, idContract);
    }

    public Contract persistContract(final Contract transientInstance) throws PersistentModelException {

        try {
            if (null == transientInstance.getId()) {
                Contract currentContract = m_contractHome.findByUniqueKey(transientInstance.getSecType(),
                        transientInstance.getSymbol(), transientInstance.getExchange(), transientInstance.getCurrency(),
                        transientInstance.getExpiry());
                if (null != currentContract) {
                    transientInstance.setId(currentContract.getId());
                }
            }

            return m_aspectHome.persist(transientInstance, true);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error saving Contract please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving Contract: " + transientInstance.getSymbol() + "\n Msg: " + e.getMessage());
        }
    }

    public void persistCandleSeries(final CandleSeries candleSeries) throws PersistentModelException {
        try {
            /*
             * This can happen when an indicator is a contract that has never
             * been used.
             */
            if (null == candleSeries.getContract().getId()) {
                Contract contract = this.persistContract(candleSeries.getContract());
                candleSeries.getContract().setId(contract.getId());
                candleSeries.getContract().setVersion(contract.getVersion());
            }
            m_candleHome.persistCandleSeries(candleSeries);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error saving CandleSeries please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving CandleSeries: " + candleSeries.getDescription() + "\n Msg: " + e.getMessage());
        }
    }

    public Candle persistCandle(final Candle candle) throws PersistentModelException {
        try {
            synchronized (candle) {
                if (null == candle.getTradingday().getId()) {

                    Tradingday tradingday = this.findTradingdayByOpenCloseDate(candle.getTradingday().getOpen(),
                            candle.getTradingday().getClose());

                    if (null == tradingday) {
                        tradingday = m_aspectHome.persist(candle.getTradingday());
                    }
                    candle.setTradingday(tradingday);
                }
                if (null == candle.getId()) {
                    Candle currCandle = m_candleHome.findByUniqueKey(candle.getTradingday().getId(),
                            candle.getContract().getId(), candle.getStartPeriod(), candle.getEndPeriod(),
                            candle.getBarSize());
                    /*
                     * Candle exists set the id and version so we can merge the
                     * incoming candle.
                     */
                    if (null != currCandle) {
                        candle.setId(currCandle.getId());
                        candle.setVersion(currCandle.getVersion());
                    }
                }
                Candle item = m_aspectHome.persist(candle);
                candle.setVersion(item.getVersion());
                return item;
            }
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error saving Candle please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving CandleItem: " + candle.getOpen() + "\n Msg: " + e.getMessage());
        }
    }

    public void persistTradingday(final Tradingday transientInstance) throws PersistentModelException {

        try {
            m_tradingdayHome.persist(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error saving Tradingday please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving Tradingday: " + transientInstance.getOpen() + "\n Msg: " + e.getMessage());
        }
    }

    public synchronized TradeOrder persistTradeOrder(final TradeOrder tradeOrder) throws PersistentModelException {
        try {

            if (null == tradeOrder.getOrderKey()) {
                throw new PersistentModelException("Order key cannot be null.");
            }

            /*
             * This is a new order set the status to UNSUBMIT
             */
            if (null == tradeOrder.getId() && null == tradeOrder.getStatus()) {
                tradeOrder.setStatus(OrderStatus.UNSUBMIT);
            }

            if (!tradeOrder.getIsFilled()
                    && CoreUtils.nullSafeComparator(tradeOrder.getQuantity(), tradeOrder.getFilledQuantity()) == 0) {
                tradeOrder.setIsFilled(true);
                tradeOrder.setStatus(OrderStatus.FILLED);
            }

            /*
             * If a partial filled order is cancelled mark the order as filled.
             */
            if (OrderStatus.CANCELLED.equals(tradeOrder.getStatus()) && !tradeOrder.getIsFilled()
                    && CoreUtils.nullSafeComparator(tradeOrder.getFilledQuantity(), new Integer(0)) == 1) {
                tradeOrder.setIsFilled(true);
                tradeOrder.setStatus(OrderStatus.FILLED);
            }

            Integer tradestrategyId = null;
            if (null == tradeOrder.getTradestrategyId()) {
                tradestrategyId = tradeOrder.getTradestrategy().getId();
                tradeOrder.setTradestrategyId(this.findTradestrategyLiteById(tradestrategyId));
            } else {
                tradestrategyId = tradeOrder.getTradestrategyId().getId();
            }

            /*
             * If the filled qty is > 0 and we have no TradePosition then create
             * one.
             */
            TradePosition tradePosition = null;
            TradestrategyOrders tradestrategyOrders = null;

            if (!tradeOrder.hasTradePosition()) {
                if (CoreUtils.nullSafeComparator(tradeOrder.getFilledQuantity(), 0) == 1) {

                    tradestrategyOrders = this.findPositionOrdersByTradestrategyId(tradestrategyId);

                    if (tradestrategyOrders.hasOpenTradePosition()) {
                        tradePosition = this.findTradePositionById(
                                tradestrategyOrders.getContract().getTradePosition().getId());
                        if (!tradePosition.containsTradeOrder(tradeOrder)) {
                            tradePosition.addTradeOrder(tradeOrder);
                        }

                    } else {
                        /*
                         * Note Order status can be fired before execDetails
                         * this could result in a new tradeposition. OrderStatus
                         * does not contain the filled date so we must set it
                         * here.
                         */
                        ZonedDateTime positionOpenDate = tradeOrder.getFilledDate();
                        if (null == positionOpenDate) {
                            positionOpenDate = TradingCalendar.getDateTimeNowMarketTimeZone();
                        }

                        tradePosition = new TradePosition(tradestrategyOrders.getContract(), positionOpenDate,
                                (Action.BUY.equals(tradeOrder.getAction()) ? Side.BOT : Side.SLD));
                        tradeOrder.setIsOpenPosition(true);
                        tradestrategyOrders.setStatus(TradestrategyStatus.OPEN);
                        tradestrategyOrders.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                        this.persistAspect(tradestrategyOrders);
                        tradePosition.addTradeOrder(tradeOrder);
                        tradePosition = this.persistAspect(tradePosition);
                    }
                    tradeOrder.setTradePosition(tradePosition);
                } else {
                    /*
                     * If the order has not been filled and it has no
                     * TradePosition this is the first order that has just been
                     * update.
                     */
                    return this.persistAspect(tradeOrder);
                }
            } else {
                tradePosition = this.findTradePositionById(tradeOrder.getTradePosition().getId());
                tradeOrder.setTradePosition(tradePosition);
            }

            boolean allOrdersCancelled = true;
            int totalBuyQuantity = 0;
            int totalSellQuantity = 0;
            double totalCommission = 0;
            double totalBuyValue = 0;
            double totalSellValue = 0;

            for (TradeOrder order : tradePosition.getTradeOrders()) {

                if (order.getOrderKey().equals(tradeOrder.getOrderKey())) {
                    order = tradeOrder;
                }

                /*
                 * If all orders are cancelled and not filled then we need to
                 * update the tradestrategy status to cancelled.
                 */
                if (!OrderStatus.CANCELLED.equals(order.getStatus())) {
                    allOrdersCancelled = false;
                }

                if (null != order.getFilledQuantity()) {

                    if (Action.BUY.equals(order.getAction())) {
                        totalBuyQuantity = totalBuyQuantity + order.getFilledQuantity();
                        totalBuyValue = totalBuyValue + (order.getAverageFilledPrice().doubleValue()
                                * order.getFilledQuantity().doubleValue());
                    } else {
                        totalSellQuantity = totalSellQuantity + order.getFilledQuantity();
                        totalSellValue = totalSellValue + (order.getAverageFilledPrice().doubleValue()
                                * order.getFilledQuantity().doubleValue());
                    }
                    if (null != order.getCommission()) {
                        totalCommission = totalCommission + order.getCommission().doubleValue();
                    }
                }
            }
            /*
             * totalFilledQuantity has changed for the trade update the trade
             * values.
             */
            Money comms = new Money(totalCommission);
            if (CoreUtils.nullSafeComparator(new Integer(totalBuyQuantity), tradePosition.getTotalBuyQuantity()) != 0
                    || CoreUtils.nullSafeComparator(new Integer(totalSellQuantity),
                    tradePosition.getTotalSellQuantity()) != 0) {

                int openQuantity = totalBuyQuantity - totalSellQuantity;
                tradePosition.setOpenQuantity(openQuantity);
                tradePosition.setTotalBuyQuantity(totalBuyQuantity);
                tradePosition.setTotalBuyValue(
                        (new BigDecimal(totalBuyValue)).setScale(SCALE_5, RoundingMode.HALF_EVEN));
                tradePosition.setTotalSellQuantity(totalSellQuantity);
                tradePosition.setTotalSellValue(
                        (new BigDecimal(totalSellValue)).setScale(SCALE_5, RoundingMode.HALF_EVEN));
                tradePosition.setTotalNetValue(
                        (new BigDecimal(totalSellValue - totalBuyValue)).setScale(SCALE_5, RoundingMode.HALF_EVEN));
                tradePosition.setTotalCommission(comms.getBigDecimalValue());
                if (openQuantity > 0) {
                    tradePosition.setSide(Side.BOT);
                }
                if (openQuantity < 0) {
                    tradePosition.setSide(Side.SLD);
                }

                /*
                 * Position should be closed if openQuantity = 0
                 */
                if (tradePosition.equals(tradePosition.getContract().getTradePosition())) {
                    if (openQuantity == 0) {
                        tradePosition.setPositionCloseDate(tradeOrder.getFilledDate());
                        tradePosition.getContract().setTradePosition(null);
                        this.persistAspect(tradePosition.getContract());
                    }
                } else {
                    tradePosition.getContract().setTradePosition(tradePosition);
                    this.persistAspect(tradePosition.getContract());
                }

                // Partial fills case.
                if (null == tradestrategyOrders) {
                    tradestrategyOrders = this.findPositionOrdersByTradestrategyId(tradestrategyId);
                }

                if (!tradePosition.isOpen() && !TradestrategyStatus.CLOSED.equals(tradestrategyOrders.getStatus())) {
                    /*
                     * Now update all the tradestrategies as there could be many
                     * if the position is across multiple days.
                     */
                    for (TradeOrder item : tradePosition.getTradeOrders()) {
                        if (!item.getTradestrategyId().getId()
                                .equals(tradestrategyOrders.getId())) {
                            item.getTradestrategyId().setStatus(TradestrategyStatus.CLOSED);
                            item.getTradestrategyId().setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                            this.persistAspect(item.getTradestrategyId());
                        }
                    }
                    tradestrategyOrders.setStatus(TradestrategyStatus.CLOSED);
                    tradestrategyOrders.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                    this.persistAspect(tradestrategyOrders);
                }

                tradePosition.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                tradePosition = this.persistAspect(tradePosition);

            } else {
                if (allOrdersCancelled) {
                    if (null == tradestrategyOrders) {
                        tradestrategyOrders = this.findPositionOrdersByTradestrategyId(tradestrategyId);
                    }
                    if (!TradestrategyStatus.CANCELLED.equals(tradestrategyOrders.getStatus())) {
                        if (null == tradestrategyOrders.getStatus()) {
                            tradestrategyOrders.setStatus(TradestrategyStatus.CANCELLED);
                            tradestrategyOrders.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                            this.persistAspect(tradestrategyOrders);
                        }
                    }
                }
                /*
                 * If the commissions (note these are updated by the orderState
                 * event after the order may have been filled) have changed
                 * update the trade.
                 */
                if (CoreUtils.nullSafeComparator(comms.getBigDecimalValue(), tradePosition.getTotalCommission()) == 1) {
                    tradePosition.setTotalCommission(comms.getBigDecimalValue());
                    tradePosition.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                    tradePosition = this.persistAspect(tradePosition);
                }
            }

            return this.persistAspect(tradeOrder);

        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error saving TradeOrder please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving TradeOrder: " + tradeOrder.getOrderKey() + "\n Msg: " + e.getMessage());
        }
    }

    public synchronized TradeOrder persistTradeOrderfill(final TradeOrder tradeOrder) throws PersistentModelException {
        try {

            ZonedDateTime filledDate = null;
            double filledValue = 0;
            double commission = 0;
            int filledQuantity = 0;
            for (TradeOrderfill tradeOrderfill : tradeOrder.getTradeOrderfills()) {

                if (null != tradeOrderfill.getCommission())
                    commission = commission + tradeOrderfill.getCommission().doubleValue();

                filledQuantity = filledQuantity + tradeOrderfill.getQuantity();
                filledValue = filledValue + (tradeOrderfill.getPrice().doubleValue() * tradeOrderfill.getQuantity());
                if (null == filledDate)
                    filledDate = tradeOrderfill.getTime();

                if (filledDate.isBefore(tradeOrderfill.getTime()))
                    filledDate = tradeOrderfill.getTime();
            }

            if (filledQuantity > 0) {
                BigDecimal avgFillPrice = (new BigDecimal(filledValue / filledQuantity)).setScale(SCALE_5,
                        RoundingMode.HALF_EVEN);
                BigDecimal commissionAmount = (new BigDecimal(commission)).setScale(SCALE_2,
                        RoundingMode.HALF_EVEN);

                /*
                 * If filled qty is greater than current filled qty set the new
                 * value. Note openOrder can update the filled order quantity
                 * before the orderFills have arrived.
                 */
                if (CoreUtils.nullSafeComparator(filledQuantity, tradeOrder.getFilledQuantity()) == 1) {
                    tradeOrder.setAverageFilledPrice(avgFillPrice);
                    tradeOrder.setFilledQuantity(filledQuantity);
                    tradeOrder.setFilledDate(filledDate);
                    /*
                     * If the commission amount is greater than the TradeOrder
                     * commission set this amount. Note tradeOrder commission
                     * can be set via the commissionReport event i.e each
                     * execution or by the openOrder event.
                     */
                    if (CoreUtils.nullSafeComparator(commissionAmount, tradeOrder.getCommission()) == 1)
                        tradeOrder.setCommission(commissionAmount);

                    tradeOrder.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                }
            }
            return persistTradeOrder(tradeOrder);

        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException("Error saving TradeOrderfill please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving TradeOrderfill: " + tradeOrder.getOrderKey() + "\n Msg: " + e.getMessage());
        }
    }

    public Rule findRuleById(final Integer id) throws PersistentModelException {
        Rule instance = m_ruleHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Rule not found for Id: " + id);
        return instance;
    }

    public Integer findRuleByMaxVersion(final Strategy strategy) {
        return m_ruleHome.findByMaxVersion(strategy);
    }

    public Strategy findStrategyById(final Integer id) throws PersistentModelException {
        Strategy instance = m_strategyHome.findById(id);
        if (null == instance)
            throw new PersistentModelException("Strategy not found for Id: " + id);
        return instance;
    }

    public Strategy findStrategyByName(String name) {
        return m_strategyHome.findByName(name);
    }

    public List<Strategy> findStrategies() {
        return m_strategyHome.findAll();
    }

    public Aspects findAspectsByClassName(String aspectClassName) throws PersistentModelException {
        try {

            if ("org.trade.persistent.dao.Strategy".equals(aspectClassName)) {
                /*
                 * Relationship Strategy -> IndicatorSeries is LAZY so we need
                 * to call size() on Rule/IndicatorSeries.
                 */
                List<Strategy> items = m_strategyHome.findAll();
                Aspects aspects = new Aspects();
                for (Object item : items) {
                    aspects.add((Aspect) item);
                }
                aspects.setDirty(false);
                return aspects;
            } else if ("org.trade.persistent.dao.Portfolio".equals(aspectClassName)) {
                /*
                 * Relationship Portfolio -> PortfilioAccount iis LAZY so we
                 * need to call size() on PortfolioAccount.
                 */
                List<Portfolio> items = m_portfolioHome.findAll();
                Aspects aspects = new Aspects();
                for (Object item : items) {
                    aspects.add((Aspect) item);
                }
                aspects.setDirty(false);
                return aspects;
            } else {
                return m_aspectHome.findByClassName(aspectClassName);
            }

        } catch (Exception ex) {
            throw new PersistentModelException("Error finding Aspects: " + ex.getMessage());
        }
    }

    public Aspects findAspectsByClassNameFieldName(String className, String fieldname, String value)
            throws PersistentModelException {
        try {
            return m_aspectHome.findByClassNameFieldName(className, fieldname, value);
        } catch (Exception ex) {
            throw new PersistentModelException("Error finding Aspects: " + ex.getMessage());
        }
    }

    public Aspect findAspectById(final Aspect aspect) throws PersistentModelException {
        Aspect instance = m_aspectHome.findById(aspect);
        if (null == instance)
            throw new PersistentModelException("Aspect not found for Id: " + aspect.getId());
        return instance;
    }

    public <T extends Aspect> T persistAspect(final T transientInstance) throws PersistentModelException {
        try {
            return m_aspectHome.persist(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException(
                    "Error saving " + transientInstance.getClass().getSimpleName() + " please refresh before save.");
        } catch (Exception ex) {
            throw new PersistentModelException(
                    "Error saving  " + transientInstance.getClass().getSimpleName() + " : " + ex.getMessage());
        }
    }

    public <T extends Aspect> T persistAspect(final T transientInstance, boolean overrideVersion)
            throws PersistentModelException {
        try {
            return m_aspectHome.persist(transientInstance, overrideVersion);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException(
                    "Error saving " + transientInstance.getClass().getSimpleName() + " please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error saving  " + transientInstance.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void removeAspect(final Aspect transientInstance) throws PersistentModelException {
        try {
            m_aspectHome.remove(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new PersistentModelException(
                    "Error removing " + transientInstance.getClass().getSimpleName() + " please refresh before save.");
        } catch (Exception e) {
            throw new PersistentModelException(
                    "Error removing  " + transientInstance.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void reassignStrategy(final Strategy fromStrategy, final Strategy toStrategy, final Tradingday tradingday)
            throws PersistentModelException {

        try {
            for (Tradestrategy item : tradingday.getTradestrategies()) {
                if (item.getStrategy().getId().equals(fromStrategy.getId())) {
                    item.setStrategy(toStrategy);
                    item.setDirty(true);
                    item.setStrategyData(null);
                    m_aspectHome.persist(item);
                }
            }

        } catch (Exception ex) {
            throw new PersistentModelException("Error reassign Strategy: " + ex.getMessage());
        }
    }

    public CodeType findCodeTypeByNameType(String name, String type) throws PersistentModelException {
        try {
            return m_codeTypeHome.findByNameAndType(name, type);
        } catch (Exception ex) {
            throw new PersistentModelException("Error finding CodeType: " + ex.getMessage());
        }
    }
}
