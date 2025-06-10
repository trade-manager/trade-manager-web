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

import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectRepository;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.AccountRepository;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CandleRepository;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.CodeTypeRepository;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.ContractRepository;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.PortfolioRepository;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.RuleRepository;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.StrategyRepository;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderHome;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.TradeOrderfillHome;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.TradePositionHome;
import org.trade.core.persistent.dao.TradelogHome;
import org.trade.core.persistent.dao.TradelogReport;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyHome;
import org.trade.core.persistent.dao.TradestrategyLite;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.TradingdayHome;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TradestrategyStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 */
public class TradeServiceImpl implements TradeService {

    @Autowired
    private AspectRepository aspectRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CandleRepository candleRepository;

    @Autowired
    private CodeTypeRepository codeTypeRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private StrategyRepository strategyRepository;

    private final TradingdayHome m_tradingdayHome;
    private final TradeOrderHome m_tradeOrderHome;
    private final TradeOrderfillHome m_tradeOrderfillHome;
    private final TradePositionHome m_tradePositionHome;
    private final TradelogHome m_tradelogHome;
    private final TradestrategyHome m_tradestrategyHome;

    private static final int SCALE_5 = 5;
    private static final int SCALE_2 = 2;

    public TradeServiceImpl() {
        m_tradingdayHome = new TradingdayHome();
        m_tradeOrderHome = new TradeOrderHome();
        m_tradeOrderfillHome = new TradeOrderfillHome();
        m_tradePositionHome = new TradePositionHome();
        m_tradelogHome = new TradelogHome();
        m_tradestrategyHome = new TradestrategyHome();
    }

    public Aspect save(Aspect instance) {

        return aspectRepository.save(instance);
    }

    public void deleteById(Integer id) {

        aspectRepository.deleteById(id);
    }

    public void deleteAll(Iterable<? extends Aspect> entities) {

        aspectRepository.deleteAll(entities);
    }

    public Optional<Contract> findBySymbol(String symbol) {

        return contractRepository.findBySymbol(symbol);
    }

    public Iterable<Contract> findAll() {

        return contractRepository.findAll();
    }

    public TradelogReport findTradelogReport(final Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                             boolean filter, String symbol, BigDecimal winLossAmount) {
        return m_tradelogHome.findByTradelogReport(portfolio, start, end, filter, symbol, winLossAmount);
    }


    public Account findAccountById(final Integer id) {
        return accountRepository.findById(id).isPresent() ? accountRepository.findById(id).get() : null;
    }

    public Account findAccountByNumber(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber);
    }


    public Tradingday findTradingdayById(final Integer id) {
        return m_tradingdayHome.findTradingdayById(id);
    }

    public Tradingday findTradingdayByOpenCloseDate(final ZonedDateTime openDate, final ZonedDateTime closeDate) {
        return m_tradingdayHome.findByOpenCloseDate(openDate, closeDate);
    }

    public Contract findContractById(final Integer id) {
        return contractRepository.findById(id).isPresent() ? contractRepository.findById(id).get() : null;
    }

    public TradeOrder findTradeOrderById(final Integer id) {
        return m_tradeOrderHome.findById(id);
    }

    public Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                            ZonedDateTime expiry) {
        return contractRepository.findContractByUniqueKey(SECType, symbol, exchange, currency, expiry);
    }

    public Tradestrategy findTradestrategyById(final Tradestrategy tradestrategy) throws ServiceException {

        if (null == tradestrategy.getId()) {

            throw new ServiceException(
                    "Please save Tradestrategy for symbol: " + tradestrategy.getContract().getSymbol());
        }

        Tradestrategy instance = m_tradestrategyHome.findById(tradestrategy.getId());

        instance.setStrategyData(tradestrategy.getStrategyData());
        return instance;
    }

    public TradestrategyOrders refreshPositionOrdersByTradestrategyId(final TradestrategyOrders positionOrders) {

        Integer version = m_tradestrategyHome.findVersionById(Objects.requireNonNull(positionOrders.getId()));

        if (positionOrders.getVersion().equals(version)) {
            return positionOrders;
        } else {
            return m_tradestrategyHome
                    .findPositionOrdersByTradestrategyId(positionOrders.getId());
        }
    }

    public TradestrategyOrders findPositionOrdersByTradestrategyId(final Integer idTradestrategy) {

        return m_tradestrategyHome.findPositionOrdersByTradestrategyId(idTradestrategy);
    }

    public Tradestrategy findTradestrategyById(final Integer id) throws ServiceException {
        return m_tradestrategyHome.findById(id);
    }

    public boolean existTradestrategyById(final Integer id) {

        return null != m_tradestrategyHome.findById(id);
    }

    public TradestrategyLite findTradestrategyLiteById(final Integer id) {
        return m_tradestrategyHome.findTradestrategyLiteById(id);
    }

    public TradePosition findTradePositionById(final Integer id) {
        return m_tradePositionHome.findById(id);
    }

    public Portfolio findPortfolioById(final Integer id) {
        return portfolioRepository.findById(id).isPresent() ? portfolioRepository.findById(id).get() : null;
    }

    public Portfolio findPortfolioByName(String name) {
        return portfolioRepository.findByName(name);
    }

    public Portfolio findPortfolioDefault() {
        return portfolioRepository.findDefault();
    }

    public void resetDefaultPortfolio(final Portfolio transientInstance) throws ServiceException {
        try {
            portfolioRepository.resetDefaultPortfolio(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new ServiceException("Error setting default portfolio. Please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving Portfolio: " + transientInstance.getName() + "\n Msg: " + e.getMessage());
        }

    }

    public Portfolio persistPortfolio(final Portfolio instance) throws ServiceException {
        try {
            return portfolioRepository.persistPortfolio(instance);
        } catch (Exception ex) {
            throw new ServiceException("Error saving PortfolioAccount: " + ex.getMessage());
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

    public void removeTradingdayTradeOrders(final Tradingday transientInstance) throws ServiceException {
        for (Tradestrategy tradestrategy : transientInstance.getTradestrategies()) {
            this.removeTradestrategyTradeOrders(tradestrategy);
        }
    }

    public void removeTradestrategyTradeOrders(final Tradestrategy tradestrategy) throws ServiceException {

        try {
            /*
             * Refresh the trade strategy as orders across tradePosition could
             * have been deleted if this is a bulk delete of tradestrategies.
             */
            Tradestrategy transientInstance = m_tradestrategyHome.findById(Objects.requireNonNull(tradestrategy.getId()));
            transientInstance.setStatus(null);
            aspectRepository.save(transientInstance);

            Hashtable<Integer, TradePosition> tradePositions = new Hashtable<>();
            for (TradeOrder tradeOrder : transientInstance.getTradeOrders()) {
                if (tradeOrder.hasTradePosition())
                    tradePositions.put(tradeOrder.getTradePosition().getId(),
                            tradeOrder.getTradePosition());

                if (null != tradeOrder.getId()) {
                    aspectRepository.delete(tradeOrder);
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
                    aspectRepository.save(transientInstance.getContract());
                }
                aspectRepository.delete(tradePosition);
            }

            transientInstance.getTradeOrders().clear();
        } catch (OptimisticLockException ex1) {
            throw new ServiceException(
                    "Error removing Tradestrategy TradePositions. Please refresh before remove.");

        } catch (Exception ex) {
            throw new ServiceException("Error removing Tradestrategy TradePositions: "
                    + tradestrategy.getContract().getSymbol() + "\n Msg: " + ex.getMessage());
        }
    }

    public TradeOrder findTradeOrderByKey(final Integer orderKey) {
        return m_tradeOrderHome.findTradeOrderByKey(orderKey);
    }

    public TradeOrderfill findTradeOrderfillByExecId(String execId) {
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
        return candleRepository.findCandlesByContractDateRangeBarSize(idContract, startDate, endDate, barSize);
    }

    public Long findCandleCount(final Integer idTradingday, final Integer idContract) {
        return candleRepository.findCandleCount(idTradingday, idContract);
    }

    public Contract persistContract(final Contract transientInstance) {

        return aspectRepository.save(transientInstance);
    }

    public void persistCandleSeries(final CandleSeries candleSeries) throws ServiceException {
        try {
            /*
             * This can happen when an indicator is a contract that has never
             * been used.
             */
            if (null == candleSeries.getContract().getId()) {
                Contract contract = this.persistContract(candleSeries.getContract());
                // candleSeries.getContract().setId(contract.getId());
                candleSeries.getContract().setVersion(contract.getVersion());
            }
            candleRepository.persistCandleSeries(candleSeries);
        } catch (OptimisticLockException ex1) {
            throw new ServiceException("Error saving CandleSeries please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving CandleSeries: " + candleSeries.getDescription() + "\n Msg: " + e.getMessage());
        }
    }

    public Candle persistCandle(final Candle candle) throws ServiceException {

        try {

            synchronized (candle) {

                if (null == candle.getTradingday().getId()) {

                    Tradingday tradingday = this.findTradingdayByOpenCloseDate(candle.getTradingday().getOpen(),
                            candle.getTradingday().getClose());

                    if (null == tradingday) {
                        tradingday = aspectRepository.save(candle.getTradingday());
                    }
                    candle.setTradingday(tradingday);
                }
                if (null == candle.getId()) {
                    Candle currCandle = candleRepository.findByUniqueKey(candle.getTradingday().getId(),
                            candle.getContract().getId(), candle.getStartPeriod(), candle.getEndPeriod(),
                            candle.getBarSize());
                    /*
                     * Candle exists set the id and version so we can merge the
                     * incoming candle.
                     */
                    // candle.setId(currCandle.getId());
                    candle.setVersion(currCandle.getVersion());
                }
                Candle item = aspectRepository.save(candle);
                candle.setVersion(item.getVersion());
                return item;
            }
        } catch (OptimisticLockException ex1) {
            throw new ServiceException("Error saving Candle please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving CandleItem: " + candle.getOpen() + "\n Msg: " + e.getMessage());
        }
    }

    public void persistTradingday(final Tradingday transientInstance) throws ServiceException {

        try {
            m_tradingdayHome.persist(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new ServiceException("Error saving Tradingday please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving Tradingday: " + transientInstance.getOpen() + "\n Msg: " + e.getMessage());
        }
    }

    public synchronized TradeOrder persistTradeOrder(final TradeOrder tradeOrder) throws ServiceException {
        try {

            /*
             * This is a new order set the status to UNSUBMIT
             */

            if (!tradeOrder.getIsFilled()
                    && CoreUtils.nullSafeComparator(tradeOrder.getQuantity(), tradeOrder.getFilledQuantity()) == 0) {
                tradeOrder.setIsFilled(true);
                tradeOrder.setStatus(OrderStatus.FILLED);
            }

            /*
             * If a partial filled order is cancelled mark the order as filled.
             */
            if (OrderStatus.CANCELLED.equals(tradeOrder.getStatus()) && !tradeOrder.getIsFilled()
                    && CoreUtils.nullSafeComparator(tradeOrder.getFilledQuantity(), 0) == 1) {
                tradeOrder.setIsFilled(true);
                tradeOrder.setStatus(OrderStatus.FILLED);
            }

            Integer tradestrategyId;
            tradestrategyId = tradeOrder.getTradestrategyId().getId();

            /*
             * If the filled qty is > 0 and we have no TradePosition then create
             * one.
             */
            TradePosition tradePosition;
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

                if (Action.BUY.equals(order.getAction())) {
                    totalBuyQuantity = totalBuyQuantity + order.getFilledQuantity();
                    totalBuyValue = totalBuyValue + (order.getAverageFilledPrice().doubleValue()
                            * order.getFilledQuantity().doubleValue());
                } else {
                    totalSellQuantity = totalSellQuantity + order.getFilledQuantity();
                    totalSellValue = totalSellValue + (order.getAverageFilledPrice().doubleValue()
                            * order.getFilledQuantity().doubleValue());
                }
                totalCommission = totalCommission + order.getCommission().doubleValue();
            }
            /*
             * totalFilledQuantity has changed for the trade update the trade
             * values.
             */
            Money comms = new Money(totalCommission);
            if (CoreUtils.nullSafeComparator(totalBuyQuantity, tradePosition.getTotalBuyQuantity()) != 0
                    || CoreUtils.nullSafeComparator(totalSellQuantity,
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

                        if (!Objects.equals(item.getTradestrategyId().getId(), tradestrategyOrders.getId())) {

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
                this.persistAspect(tradePosition);

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
                    this.persistAspect(tradePosition);
                }
            }

            return this.persistAspect(tradeOrder);

        } catch (OptimisticLockException ex1) {
            throw new ServiceException("Error saving TradeOrder please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving TradeOrder: " + tradeOrder.getOrderKey() + "\n Msg: " + e.getMessage());
        }
    }

    public synchronized TradeOrder persistTradeOrderfill(final TradeOrder tradeOrder) throws ServiceException {
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
            throw new ServiceException("Error saving TradeOrderfill please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving TradeOrderfill: " + tradeOrder.getOrderKey() + "\n Msg: " + e.getMessage());
        }
    }

    public Rule findRuleById(final Integer id) {
        return ruleRepository.findById(id).isPresent() ? ruleRepository.findById(id).get() : null;
    }

    public Integer findRuleByMaxVersion(final Strategy strategy) {
        return ruleRepository.findByMaxVersion(strategy);
    }

    public Strategy findStrategyById(final Integer id) throws ServiceException {
        return strategyRepository.findById(id).isPresent() ? strategyRepository.findById(id).get() : null;

    }

    public Strategy findStrategyByName(String name) {
        return strategyRepository.findByName(name);
    }

    public List<Strategy> findStrategies() {
        return strategyRepository.findAll();
    }

    public Aspects findAspectsByClassName(String aspectClassName) throws ServiceException {
        try {

            if ("org.trade.persistent.dao.Strategy".equals(aspectClassName)) {
                /*
                 * Relationship Strategy -> IndicatorSeries is LAZY so we need
                 * to call size() on Rule/IndicatorSeries.
                 */
                List<Strategy> items = strategyRepository.findAll();
                Aspects aspects = new Aspects();
                for (Aspect item : items) {
                    aspects.add(item);
                }
                aspects.setDirty(false);
                return aspects;
            } else if ("org.trade.persistent.dao.Portfolio".equals(aspectClassName)) {
                /*
                 * Relationship Portfolio -> PortfilioAccount iis LAZY so we
                 * need to call size() on PortfolioAccount.
                 */
                List<Portfolio> items = portfolioRepository.findAll();
                Aspects aspects = new Aspects();
                for (Aspect item : items) {
                    aspects.add(item);
                }
                aspects.setDirty(false);
                return aspects;
            } else {
                return aspectRepository.findByClassName(aspectClassName);
            }

        } catch (Exception ex) {
            throw new ServiceException("Error finding Aspects: " + ex.getMessage());
        }
    }

    public Aspects findAspectsByClassNameFieldName(String className, String fieldname, String value)
            throws ServiceException {
        try {
            return aspectRepository.findByClassNameFieldName(className, fieldname, value);
        } catch (Exception ex) {
            throw new ServiceException("Error finding Aspects: " + ex.getMessage());
        }
    }

    public Aspect findAspectById(final Aspect aspect) throws ServiceException {

        Optional<Aspect> instance = aspectRepository.findById(aspect.getId());
        if (instance.isEmpty()) {
            throw new ServiceException("Aspect not found for Id: " + aspect.getId());
        }

        return instance.get();
    }

    public <T extends Aspect> T persistAspect(final T transientInstance) throws ServiceException {
        try {
            return aspectRepository.save(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new ServiceException(
                    "Error saving " + transientInstance.getClass().getSimpleName() + " please refresh before save.");
        } catch (Exception ex) {
            throw new ServiceException(
                    "Error saving  " + transientInstance.getClass().getSimpleName() + " : " + ex.getMessage());
        }
    }

    public <T extends Aspect> T persistAspect(final T transientInstance, boolean overrideVersion)
            throws ServiceException {
        try {
            return aspectRepository.save(transientInstance);
        } catch (OptimisticLockException ex1) {
            throw new ServiceException(
                    "Error saving " + transientInstance.getClass().getSimpleName() + " please refresh before save.");
        } catch (Exception e) {
            throw new ServiceException(
                    "Error saving  " + transientInstance.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void removeAspect(final Aspect transientInstance) throws ServiceException {

        try {

            aspectRepository.delete(transientInstance);
        } catch (OptimisticLockException ex1) {

            throw new ServiceException(
                    "Error removing " + transientInstance.getClass().getSimpleName() + " please refresh before save.");
        } catch (Exception ex) {

            throw new ServiceException(
                    "Error removing  " + transientInstance.getClass().getSimpleName() + " : " + ex.getMessage());
        }
    }

    public void reassignStrategy(final Strategy fromStrategy, final Strategy toStrategy, final Tradingday tradingday)
            throws ServiceException {

        try {
            for (Tradestrategy item : tradingday.getTradestrategies()) {
                if (item.getStrategy().getId().equals(fromStrategy.getId())) {
                    item.setStrategy(toStrategy);
                    item.setDirty(true);
                    item.setStrategyData(null);
                    aspectRepository.save(item);
                }
            }

        } catch (Exception ex) {
            throw new ServiceException("Error reassign Strategy: " + ex.getMessage());
        }
    }

    public CodeType findCodeTypeByNameType(String name, String type) {

        return codeTypeRepository.findByNameAndType(name, type);
    }
}
