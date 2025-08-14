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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectRepository;
import org.trade.core.dao.AspectServiceImpl;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.AccountRepository;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CandleRepository;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.CodeTypeRepository;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.ContractLite;
import org.trade.core.persistent.dao.ContractRepository;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.PortfolioRepository;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.RuleRepository;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.StrategyRepository;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderRepository;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.TradeOrderfillRepository;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.TradePositionRepository;
import org.trade.core.persistent.dao.TradelogDetail;
import org.trade.core.persistent.dao.TradelogDetailRepository;
import org.trade.core.persistent.dao.TradelogReport;
import org.trade.core.persistent.dao.TradelogSummary;
import org.trade.core.persistent.dao.TradelogSummaryRepository;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyLite;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.TradestrategyRepository;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.TradingdayRepository;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TradestrategyStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 */
@Service
public class TradeServiceImpl extends AspectServiceImpl implements TradeService {

    private final static Logger _log = LoggerFactory.getLogger(TradeServiceImpl.class);

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

    @Autowired
    private TradelogDetailRepository tradelogDetailRepository;

    @Autowired
    private TradelogSummaryRepository tradelogSummaryRepository;

    @Autowired
    private TradeOrderfillRepository tradeOrderfillRepository;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    @Autowired
    private TradePositionRepository tradePositionRepository;

    @Autowired
    private TradestrategyRepository tradestrategyRepository;

    @Autowired
    private TradingdayRepository tradingdayRepository;

    private static final int SCALE_5 = 5;
    private static final int SCALE_2 = 2;

    public AspectRepository<Aspect, Long> getAspectRepository() {
        return this.aspectRepository;
    }

    public void deleteAspect(Aspect instance) {

        this.getAspectRepository().delete(instance);
    }

    public void deleteAllAspects(Iterable<? extends Aspect> entities) {

        this.getAspectRepository().deleteAll(entities);
    }

    public Optional<Contract> findContractBySymbol(String symbol) {

        return contractRepository.findBySymbol(symbol);
    }

    public Iterable<Contract> findAllContracts() {

        return contractRepository.findAll();
    }

    public TradelogReport findTradelogReport(final Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                             boolean filter, String symbol, BigDecimal winLossAmount) throws IOException {

        TradelogReport tradelogReport = new TradelogReport();
        List<TradelogDetail> reportDetails = tradelogDetailRepository.findByTradelogDetail(portfolio, start, end, filter, symbol, winLossAmount);
        tradelogReport.setTradelogDetail(reportDetails);
        List<TradelogSummary> reportSummary = tradelogSummaryRepository.findByTradelogSummary(portfolio, start, end, symbol, winLossAmount);
        tradelogReport.setTradelogSummary(reportSummary);
        return tradelogReport;
    }

    public Account findAccountById(final Long id) {

        return accountRepository.findById(id).isPresent() ? accountRepository.findById(id).get() : null;
    }

    public Account findAccountByAccountNumber(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Tradingday findTradingdayById(final Long tradingdayId) {

        return tradingdayRepository.findById(tradingdayId).isPresent() ? tradingdayRepository.findById(tradingdayId).get() : null;
    }

    public Tradingday findTradingdayByOpenCloseDate(final ZonedDateTime openDate, final ZonedDateTime closeDate) {

        return tradingdayRepository.findByOpenCloseDateOrderByOpenAsc(openDate, closeDate);
    }

    public Contract findContractById(final Long id) {

        return contractRepository.findById(id).isPresent() ? contractRepository.findById(id).get() : null;
    }

    public ContractLite findContractLiteById(final Long id) {

        return contractRepository.findContractLiteById(id);
    }

    public TradeOrder findTradeOrderById(final Long id) {

        return tradeOrderRepository.findById(id).isPresent() ? tradeOrderRepository.findById(id).get() : null;
    }

    public Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                            ZonedDateTime expiry) {

        List<Contract> contracts = contractRepository.findContractByUniqueKey(SECType, symbol, exchange, currency, expiry);
        return contracts.isEmpty() ? null : contracts.getFirst();
    }

    public Tradestrategy findTradestrategyById(final Tradestrategy tradestrategy) {

        Tradestrategy instance = this.findTradestrategyById(tradestrategy.getId());
        instance.setStrategyData(tradestrategy.getStrategyData());
        return instance;
    }

    public Tradestrategy findTradestrategyByRequestId(Integer requestId) {

        return tradestrategyRepository.findByRequestId(requestId);
    }

    public TradestrategyOrders refreshPositionOrdersByTradestrategyId(final TradestrategyOrders positionOrders) {

        Integer version = tradestrategyRepository.findVersionByTradestrategyId(Objects.requireNonNull(positionOrders.getId()));

        if (positionOrders.getVersion().equals(version)) {

            return positionOrders;
        } else {

            return tradestrategyRepository
                    .findPositionOrdersByTradestrategyId(positionOrders.getId());
        }
    }

    @Transactional
    public TradestrategyOrders findPositionOrdersByTradestrategyId(final Long tradestrategyId) {

        TradestrategyOrders instance = tradestrategyRepository.findPositionOrdersByTradestrategyId(tradestrategyId);

        /*
         * If we have an open position get all the orders for that position.
         * Note the position could have been opened by a different
         * tradestrategy. So this set of orders is for the position.
         */
        if (instance.hasOpenTradePosition()) {

            instance.getOpenTradePosition().getTradeOrders().size();
        }

        return instance;
    }

    @Transactional
    public Tradestrategy findTradestrategyById(final Long id) {

        Optional<Tradestrategy> tradestrategyOpt = tradestrategyRepository.findById(id);

        if (tradestrategyOpt.isPresent()) {

            Tradestrategy tradestrategy = tradestrategyOpt.get();

            for (TradeOrder tradeOrder : tradestrategy.getTradeOrders()) {

                tradeOrder.getTradeOrderfills().size();
            }

            return tradestrategy;
        }

        return null;
    }

    public boolean existTradestrategyByRequestId(final Integer requestId) {

        Tradestrategy instance = tradestrategyRepository.findByRequestId(requestId);
        return instance != null;
    }

    public TradestrategyLite findTradestrategyLiteByTradestrategy(final Tradestrategy tradestrategy) {

        return tradestrategyRepository.findTradestrategyLiteByTradestrategy(tradestrategy);
    }

    public TradePosition findTradePositionById(final Long id) {

        Optional<TradePosition> tradePosition = tradePositionRepository.findById(id);

        return tradePosition.orElse(null);

    }

    @Transactional
    public Portfolio findPortfolioById(final Long id) {

        Optional<Portfolio> portfolio = portfolioRepository.findById(id);

        if (portfolio.isPresent()) {

            portfolio.get().getTradestrategies().size();
            return portfolio.get();
        }

        return null;
    }

    @Transactional
    public Portfolio findPortfolioByName(String name) {

        Portfolio portfolio = portfolioRepository.findByName(name);

        if (null != portfolio) {

            portfolio.getAccounts().size();
            return portfolio;
        }

        return null;
    }

    public Portfolio findPortfolioDefault() {

        return portfolioRepository.findDefault();
    }

    @Transactional
    public void resetDefaultPortfolio(final Portfolio instance) {

        List<Portfolio> items = portfolioRepository.findAllPortfolios();

        for (Portfolio item : items) {

            item.setIsDefault(Objects.equals(item.getId(), instance.getId()));
            item = this.saveAspect(item);
            instance.setIsDefault(item.getIsDefault());
        }
    }

    @Transactional
    public Portfolio savePortfolio(Portfolio instance) {

        List<Account> accounts = new ArrayList<>();

        for (Account account : instance.getAccounts()) {

            Account current = this.findAccountByAccountNumber(account.getAccountNumber());

            accounts.add(Objects.requireNonNullElse(current, account));
        }

        instance.getAccounts().clear();
        instance.setAccounts(accounts);
        return portfolioRepository.save(instance);
    }

    public List<Tradestrategy> findAllTradestrategies() {

        return tradestrategyRepository.findAll();
    }

    public Tradestrategy findTradestrategyByUniqueKeys(final ZonedDateTime open, final String strategy,
                                                       final Contract contract, final String portfolioName) {

        return tradestrategyRepository.findTradestrategyByUniqueKeys(open, strategy, contract, portfolioName);
    }

    public List<Tradestrategy> findTradestrategyDistinctByDateRange(final ZonedDateTime fromOpen,
                                                                    final ZonedDateTime toOpen) {

        return tradestrategyRepository.findTradestrategyDistinctByDateRange(fromOpen, toOpen);
    }

    public List<Tradestrategy> findTradestrategyContractDistinctByDateRange(final ZonedDateTime fromOpen,
                                                                            final ZonedDateTime toOpen) {

        return tradestrategyRepository.findTradestrategyContractDistinctByDateRange(fromOpen, toOpen);
    }

    public void deleteTradingdayTradeOrders(final Tradingday instance) {

        for (Tradestrategy tradestrategy : instance.getTradestrategies()) {

            this.deleteTradestrategyTradeOrders(tradestrategy);
        }
    }

    @Transactional
    public void deleteTradestrategyTradeOrders(final Tradestrategy instance) {

        /*
         * Refresh the trade strategy as orders across tradePosition could
         * have been deleted if this is a bulk delete of tradestrategies.
         */
        Tradestrategy tradestrategy = tradestrategyRepository.findById(Objects.requireNonNull(instance.getId())).get();
        Hashtable<Long, TradePosition> tradePositions = new Hashtable<>();

        for (TradeOrder tradeOrder : tradestrategy.getTradeOrders()) {

            if (tradeOrder.hasTradePosition()) {

                tradePositions.put(tradeOrder.getTradePosition().getId(),
                        tradeOrder.getTradePosition());
            }
        }

        /*
         * Remove the open trade position from contract.
         */
        if (null != tradestrategy.getContractLite().getTradePosition()) {

            tradestrategy.getContractLite().setTradePosition(null);
            getAspectRepository().save(tradestrategy.getContractLite());
        }

        if (null != tradestrategy.getStatus() || !tradestrategy.getTradeOrders().isEmpty()) {

            tradestrategy.setStatus(null);
            tradestrategy.getTradeOrders().clear();
            getAspectRepository().save(tradestrategy);
        }

        for (TradePosition tradePosition : tradePositions.values()) {

            tradePosition = this.findTradePositionById(tradePosition.getId());

            if (null != tradePosition) {

                this.deleteAspect(tradePosition);
            }
        }
    }

    public TradeOrder findTradeOrderByKey(final Integer orderKey) {

        return tradeOrderRepository.findByOrderKey(orderKey);
    }

    public TradeOrderfill findTradeOrderfillByExecId(String execId) {

        return tradeOrderfillRepository.findByExecId(execId);
    }

    public Integer findTradeOrderByMaxKey() {

        return tradeOrderRepository.findByMaxKey();
    }

    public Tradingdays findTradingdaysByDateRange(final ZonedDateTime startDate, final ZonedDateTime endDate) {

        return tradingdayRepository.findTradingdaysByDateRangeOrderByOpenAsc(startDate, endDate);
    }

    public List<Candle> findCandlesByContractDateRangeBarSize(final Contract contract, final ZonedDateTime startDate,
                                                              final ZonedDateTime endDate, final Integer barSize) {

        return candleRepository.findCandlesByContractDateRangeBarSize(contract, startDate, endDate, barSize);
    }

    public List<Candle> findCandlesByContractAndBarSize(Contract contract, Integer barSize) {

        return candleRepository.findByContractAndBarSizeOrderByStartPeriodAsc(contract, barSize);
    }

    public Long findCandleCount(final Contract contract) {

        return candleRepository.findCandleCount(contract);
    }

    // READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void saveCandleSeries(final CandleSeries candleSeries) {

        if (candleSeries.isEmpty()) {

            return;
        }

        Optional<Contract> contract = findContractBySymbol(candleSeries.getContract().getSymbol());
        List<Candle> newCandles = new ArrayList<>();

        for (int i = 0; i < candleSeries.getItemCount(); i++) {

            CandleItem candleItem = (CandleItem) candleSeries.getDataItem(i);
            Candle candle = candleItem.getCandle();
            List<Candle> candles = candleRepository.findCandlesByContractDateRangeBarSize(contract.get(), candle.getStartPeriod(), candle.getEndPeriod(), candle.getBarSize());

            if (candles.size() == 1) {

                if (candle.equals(candles.getFirst())) {

                    continue;
                } else {

                    this.deleteAspect(candles.getFirst());
                }
            }

            candle.setContract(contract.get());
            newCandles.add(candle);
        }

        this.saveAllAspects(newCandles);
    }

    @Transactional
    public Tradingday saveTradingday(Tradingday instance) {

        for (Tradestrategy tradestrategy : instance.getTradestrategies()) {

            /*
             * The strategy will always exist as these cannot be created
             * via this tab, as they are a dropdown list. So find the
             * persisted one and set this.
             */
            Strategy strategy = this.findStrategyByName(tradestrategy.getStrategy().getName());

            if (null != strategy) {

                tradestrategy.setStrategy(strategy);
            }

            /*
             * Check to see if the contract exists if it does merge and
             * set the new persisted one. If no persist the contract.
             */
            Contract contract = this.findContractByUniqueKey(tradestrategy.getContract().getSecType(),
                    tradestrategy.getContract().getSymbol(), tradestrategy.getContract().getExchange(),
                    tradestrategy.getContract().getCurrency(), tradestrategy.getContract().getExpiry());

            if (null != contract) {

                tradestrategy.setContract(contract);
            }
        }

        instance.setDirty(false);
        instance = this.saveAspect(instance);
        return instance;
    }

    // READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public TradeOrder saveTradeOrder(TradeOrder tradeOrder) {

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

        Long tradestrategyId = tradeOrder.getTradestrategy().getId();
        TradePosition tradePosition;
        TradestrategyOrders tradestrategyOrders = null;

        /*
         * If the filled qty is > 0 and we have no TradePosition then create
         * one.
         */
        if (!tradeOrder.hasTradePosition()) {

            if (CoreUtils.nullSafeComparator(tradeOrder.getFilledQuantity(), 0) == 1) {

                tradestrategyOrders = this.findPositionOrdersByTradestrategyId(tradestrategyId);

                if (tradestrategyOrders.hasOpenTradePosition()) {

                    tradePosition = this.findTradePositionById(
                            tradestrategyOrders.getContractLite().getTradePosition().getId());

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
                    tradePosition = new TradePosition(tradestrategyOrders.getContractLite(), positionOpenDate,
                            (Action.BUY.equals(tradeOrder.getAction()) ? Side.BOT : Side.SLD));
                    tradeOrder.setIsOpenPosition(true);
                    tradestrategyOrders.setStatus(TradestrategyStatus.OPEN);
                    tradestrategyOrders = this.saveAspect(tradestrategyOrders);
                    tradePosition.addTradeOrder(tradeOrder);
                    tradePosition = this.saveAspect(tradePosition);
                }

                tradeOrder.setTradePosition(tradePosition);
            } else {

                /*
                 * If the order has not been filled, and it has no
                 * TradePosition this is the first order that has just been
                 * update.
                 */
                return this.saveAspect(tradeOrder);
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

            totalCommission = totalCommission + (order.getCommission() == null ? 0 : order.getCommission().doubleValue());
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
            if (tradePosition.equals(tradePosition.getContractLite().getTradePosition())) {

                if (openQuantity == 0) {

                    tradePosition.setCloseDate(tradeOrder.getFilledDate());
                    tradePosition.getContractLite().setTradePosition(null);
                    tradePosition.setContractLite(this.saveAspect(tradePosition.getContractLite()));
                }
            } else {

                tradePosition.getContractLite().setTradePosition(tradePosition);
                tradePosition.setContractLite(this.saveAspect(tradePosition.getContractLite()));
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

                    if (!Objects.equals(item.getTradestrategyLite().getId(), tradestrategyOrders.getId())) {

                        item.getTradestrategyLite().setStatus(TradestrategyStatus.CLOSED);
                        this.saveAspect(item.getTradestrategyLite());
                    }
                }

                tradestrategyOrders.setStatus(TradestrategyStatus.CLOSED);
                this.saveAspect(tradestrategyOrders);
            }

            this.saveAspect(tradePosition);
        } else {

            if (allOrdersCancelled) {

                if (null == tradestrategyOrders) {

                    tradestrategyOrders = this.findPositionOrdersByTradestrategyId(tradestrategyId);
                }

                if (!TradestrategyStatus.CANCELLED.equals(tradestrategyOrders.getStatus())) {

                    if (null == tradestrategyOrders.getStatus()) {

                        tradestrategyOrders.setStatus(TradestrategyStatus.CANCELLED);
                        this.saveAspect(tradestrategyOrders);
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
                this.saveAspect(tradePosition);
            }
        }

        return this.saveAspect(tradeOrder);
    }

    public TradeOrder saveTradeOrderfill(final TradeOrder tradeOrder) {

        ZonedDateTime filledDate = null;
        double filledValue = 0;
        double commission = 0;
        int filledQuantity = 0;

        for (TradeOrderfill tradeOrderfill : tradeOrder.getTradeOrderfills()) {

            if (null != tradeOrderfill.getCommission()) {

                commission = commission + tradeOrderfill.getCommission().doubleValue();
            }

            filledQuantity = filledQuantity + tradeOrderfill.getQuantity();
            filledValue = filledValue + (tradeOrderfill.getPrice().doubleValue() * tradeOrderfill.getQuantity());

            if (null == filledDate) {

                filledDate = tradeOrderfill.getTime();
            }

            if (filledDate.isBefore(tradeOrderfill.getTime())) {

                filledDate = tradeOrderfill.getTime();
            }
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
                if (CoreUtils.nullSafeComparator(commissionAmount, tradeOrder.getCommission()) == 1) {

                    tradeOrder.setCommission(commissionAmount);
                }

                tradeOrder.setOrderUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            }
        }

        return saveTradeOrder(tradeOrder);
    }

    public Rule findRuleById(final Long ruleId) {

        return ruleRepository.findById(ruleId).isPresent() ? ruleRepository.findById(ruleId).get() : null;
    }

    public List<Rule> findRulesAll() {

        return ruleRepository.findAll();
    }

    public List<Rule> findRulesByStrategy(Strategy strategy) {

        return ruleRepository.findByStrategy(strategy);
    }

    public Rule findRuleByMaxVersion(final Strategy strategy, String contentType) {

        Integer version = ruleRepository.findByMaxRuleVersion(strategy, contentType);
        List<Rule> rules = ruleRepository.findByStrategyAndContentTypeAndRuleVersion(strategy, contentType, version);

        if (!rules.isEmpty()) {

            return rules.getFirst();
        }

        return null;
    }

    @Transactional
    public Strategy findStrategyById(final Long id) {

        Optional<Strategy> strategy = strategyRepository.findById(id);
        if (strategy.isPresent()) {

            strategy.get().getRules().size();
            return strategy.get();
        }

        return null;
    }

    @Transactional
    public Strategy findStrategyByName(String name) {

        Strategy strategy = strategyRepository.findByName(name);

        if (null != strategy) {

            strategy.getRules().size();
            return strategy;
        }

        return null;
    }

    @Transactional
    public List<Strategy> findStrategies() {

        List<Strategy> strategies = strategyRepository.findAll();

        for (Strategy strategy : strategies) {

            strategy.getRules().size();
        }

        return strategies;
    }

    public Aspects findByClassName(String aspectClassName) throws ClassNotFoundException {

        if ((PERSISTENT_PACKAGE + "Strategy").equals(aspectClassName)) {

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
        } else if ((PERSISTENT_PACKAGE + "Portfolio").equals(aspectClassName)) {

            /*
             * Relationship Portfolio -> PortfolioAccount is LAZY so we
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
            return super.findByClassName(aspectClassName);
        }
    }

    public Aspect findAspectById(final Aspect aspect) throws ClassNotFoundException {

        Aspects aspects = this.findByClassNameAndFieldName(aspect.getClass().getName(), "Id", Objects.requireNonNull(aspect.getId()).toString());

        if (!aspects.getAspects().isEmpty()) {

            return aspects.getAspects().getFirst();
        }

        return null;
    }

    public <T extends Aspect> T saveAspect(final T instance) {

        return getAspectRepository().save(instance);
    }

    public <S extends Aspect> List<S> saveAllAspects(final Iterable<S> entities) {

        return getAspectRepository().saveAll(entities);
    }

    public <T extends Aspect> T saveAspect(final T instance, boolean overrideVersion) {

        return getAspectRepository().save(instance);
    }

    public void reassignStrategy(final Strategy fromStrategy, final Strategy toStrategy, final Tradingday tradingday) {

        for (Tradestrategy item : tradingday.getTradestrategies()) {

            if (Objects.requireNonNull(item.getStrategy().getId()).equals(fromStrategy.getId())) {

                item.setStrategy(toStrategy);
                item.setDirty(true);
                item.setStrategyData(null);
                getAspectRepository().save(item);
            }
        }
    }

    public CodeType findCodeTypeByNameType(String name, String type) {

        List<CodeType> codeTypes = codeTypeRepository.findByNameAndType(name, type);
        return codeTypes.isEmpty() ? null : codeTypes.getFirst();
    }
}
