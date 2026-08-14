package org.trade.core.persistent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.aspect.Aspect;
import org.trade.core.aspect.AspectService;
import org.trade.core.aspect.Aspects;
import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.account.AccountService;
import org.trade.core.persistent.candle.Candle;
import org.trade.core.persistent.candle.CandleService;
import org.trade.core.persistent.codetype.CodeTypeService;
import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.contract.ContractService;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.portfolio.PortfolioService;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.rule.RuleService;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.StrategyService;
import org.trade.core.persistent.strategy.series.indicator.CandleSeries;
import org.trade.core.persistent.strategy.series.indicator.candle.CandleItem;
import org.trade.core.persistent.tradelogdetail.TradelogDetail;
import org.trade.core.persistent.tradelogdetail.TradelogDetailService;
import org.trade.core.persistent.tradelogdetail.TradelogReport;
import org.trade.core.persistent.tradelogsummary.TradelogSummary;
import org.trade.core.persistent.tradelogsummary.TradelogSummaryService;
import org.trade.core.persistent.tradeorder.TradeOrder;
import org.trade.core.persistent.tradeorder.TradeOrderService;
import org.trade.core.persistent.tradeorderfill.TradeOrderfill;
import org.trade.core.persistent.tradeorderfill.TradeOrderfillService;
import org.trade.core.persistent.tradeposition.TradePosition;
import org.trade.core.persistent.tradeposition.TradePositionService;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradestrategy.TradestrategyOrders;
import org.trade.core.persistent.tradestrategy.TradestrategyService;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.TradingdayService;
import org.trade.core.persistent.user.UserService;
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
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradeServiceImpl implements TradeService {

    private final static Logger _log = LoggerFactory.getLogger(TradeServiceImpl.class);

    private final AspectService aspectService;
    private final DomainService domainService;
    private final UserService userService;
    private final RoleService roleService;
    private final EmployeeService employeeService;
    private final TradingdayService tradingdayService;
    private final CodeTypeService codeTypeService;
    private final AccountService accountService;
    private final PortfolioService portfolioService;
    private final RuleService ruleService;
    private final StrategyService strategyService;
    private final ContractService contractService;
    private final CandleService candleService;
    private final TradelogDetailService tradelogDetailService;
    private final TradelogSummaryService tradelogSummaryService;
    private final TradestrategyService tradestrategyService;
    private final TradeOrderService tradeOrderService;
    private final TradeOrderfillService tradeOrderfillService;
    private final TradePositionService tradePositionService;

    public TradeServiceImpl(final AspectService aspectService, final DomainService domainService, final UserService userService, final RoleService roleService, EmployeeService employeeService, final TradingdayService tradingdayService, final CodeTypeService codeTypeService, final AccountService accountService, final PortfolioService portfolioService,
                            final RuleService ruleService, final StrategyService strategyService, final ContractService contractService, final CandleService candleService, final TradelogDetailService tradelogDetailService,
                            final TradelogSummaryService tradelogSummaryService, final TradestrategyService tradestrategyService, final TradeOrderService tradeOrderService, final TradeOrderfillService tradeOrderfillService, final TradePositionService tradePositionService) {

        this.aspectService = aspectService;
        this.domainService = domainService;
        this.userService = userService;
        this.roleService = roleService;
        this.employeeService = employeeService;
        this.tradingdayService = tradingdayService;
        this.codeTypeService = codeTypeService;
        this.accountService = accountService;
        this.portfolioService = portfolioService;
        this.ruleService = ruleService;
        this.strategyService = strategyService;
        this.contractService = contractService;
        this.candleService = candleService;
        this.tradelogDetailService = tradelogDetailService;
        this.tradelogSummaryService = tradelogSummaryService;
        this.tradestrategyService = tradestrategyService;
        this.tradeOrderService = tradeOrderService;
        this.tradeOrderfillService = tradeOrderfillService;
        this.tradePositionService = tradePositionService;
    }

    public AspectService getAspectService() {

        return this.aspectService;
    }

    public DomainService getDomainService() {

        return this.domainService;
    }

    public UserService getUserService() {

        return this.userService;
    }

    public RoleService getRoleService() {
        return roleService;
    }

    public EmployeeService getEmployeeService() {

        return this.employeeService;
    }


    public TradingdayService getTradingdayService() {

        return this.tradingdayService;
    }

    public CodeTypeService getCodeTypeService() {

        return this.codeTypeService;
    }

    public AccountService getAccountService() {

        return this.accountService;
    }

    public PortfolioService getPortfolioService() {

        return this.portfolioService;
    }

    public RuleService getRuleService() {

        return this.ruleService;
    }

    public StrategyService getStrategyService() {

        return this.strategyService;
    }

    public ContractService getContractService() {

        return this.contractService;
    }

    public CandleService getCandleService() {

        return this.candleService;
    }

    public TradelogDetailService getTradelogDetailService() {

        return this.tradelogDetailService;
    }

    public TradelogSummaryService getTradelogSummaryService() {

        return this.tradelogSummaryService;
    }

    public TradestrategyService getTradestrategyService() {

        return this.tradestrategyService;
    }

    public TradeOrderService getTradeOrderService() {

        return this.tradeOrderService;
    }

    public TradeOrderfillService getTradeOrderfillService() {

        return this.tradeOrderfillService;
    }


    public TradePositionService getTradePositionService() {

        return this.tradePositionService;
    }

    public TradelogReport findTradelogReport(final Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                             boolean filter, String symbol, BigDecimal winLossAmount) throws IOException {

        TradelogReport tradelogReport = new TradelogReport();
        List<TradelogDetail> reportDetails = tradelogDetailService.findByTradelogDetail(portfolio, start, end, filter, symbol, winLossAmount);
        tradelogReport.setTradelogDetail(reportDetails);
        List<TradelogSummary> reportSummary = tradelogSummaryService.findByTradelogSummary(portfolio, start, end, symbol, winLossAmount);
        tradelogReport.setTradelogSummary(reportSummary);
        return tradelogReport;
    }

    @Transactional
    public Portfolio savePortfolio(Portfolio instance) {

        List<Account> accounts = new ArrayList<>();

        for (Account account : instance.getAccounts()) {

            Account current = this.getAccountService().findByAccountNumber(account.getAccountNumber());
            accounts.add(Objects.requireNonNullElse(current, account));
        }

        instance.getAccounts().clear();
        instance.setAccounts(accounts);
        return portfolioService.save(instance);
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
        Tradestrategy tradestrategy = this.tradestrategyService.findById(Objects.requireNonNull(instance.getId()));
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
            aspectService.save(tradestrategy.getContractLite());
        }

        if (null != tradestrategy.getStatus() || !tradestrategy.getTradeOrders().isEmpty()) {

            tradestrategy.setStatus(null);
            tradestrategy.getTradeOrders().clear();
            aspectService.save(tradestrategy);
        }

        for (TradePosition tradePosition : tradePositions.values()) {

            tradePosition = this.tradePositionService.findById(tradePosition.getId());

            if (null != tradePosition) {

                aspectService.delete(tradePosition);
            }
        }
    }

    // READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void saveCandleSeries(final CandleSeries candleSeries) {

        if (candleSeries.isEmpty()) {

            return;
        }

        Optional<Contract> contract = contractService.findBySymbol(candleSeries.getContract().getSymbol());
        List<Candle> newCandles = new ArrayList<>();

        for (int i = 0; i < candleSeries.getItemCount(); i++) {

            CandleItem candleItem = (CandleItem) candleSeries.getDataItem(i);
            Candle candle = candleItem.getCandle();
            List<Candle> candles = candleService.findByContractDateRangeBarSize(contract.get(), candle.getStartPeriod(), candle.getEndPeriod(), candle.getBarSize());

            if (candles.size() == 1) {

                if (candle.equals(candles.getFirst())) {

                    continue;
                } else {

                    aspectService.delete(candles.getFirst());
                }
            }

            candle.setContract(contract.get());
            newCandles.add(candle);
        }

        aspectService.saveAll(newCandles);
    }

    @Transactional
    public Tradingday saveTradingday(Tradingday instance) {

        for (Tradestrategy tradestrategy : instance.getTradestrategies()) {

            /*
             * The strategy will always exist as these cannot be created
             * via this tab, as they are a dropdown list. So find the
             * persisted one and set this.
             */
            Strategy strategy = strategyService.findByName(tradestrategy.getStrategy().getName());

            if (null != strategy) {

                tradestrategy.setStrategy(strategy);
            }

            /*
             * Check to see if the contract exists if it does merge and
             * set the new persisted one. If no persist the contract.
             */
            Contract contract = contractService.findByUniqueKey(tradestrategy.getContract().getSecType(),
                    tradestrategy.getContract().getSymbol(), tradestrategy.getContract().getExchange(),
                    tradestrategy.getContract().getCurrency(), tradestrategy.getContract().getExpiry());

            if (null != contract) {

                tradestrategy.setContract(contract);
            }
        }

        instance.setDirty(false);
        instance = aspectService.save(instance);
        return instance;
    }

    @Transactional
    public Tradestrategy saveTradestrategy(Tradestrategy instance) {


        Tradingday tradingday = tradingdayService.findByOpenCloseDate(instance.getTradingday().getOpen(), instance.getTradingday().getClose());

        if (null != tradingday) {

            instance.setTradingday(tradingday);
        }
        /*
         * The strategy will always exist as these cannot be created
         * via this tab, as they are a dropdown list. So find the
         * persisted one and set this.
         */
        Strategy strategy = strategyService.findByName(instance.getStrategy().getName());

        if (null != strategy) {

            instance.setStrategy(strategy);
        }

        /*
         * Check to see if the contract exists if it does merge and
         * set the new persisted one. If no persist the contract.
         */
        Contract contract = contractService.findByUniqueKey(instance.getContract().getSecType(),
                instance.getContract().getSymbol(), instance.getContract().getExchange(),
                instance.getContract().getCurrency(), instance.getContract().getExpiry());

        if (null != contract) {

            instance.setContract(contract);
        }

        instance.setDirty(false);
        instance = aspectService.save(instance);
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

                tradestrategyOrders = this.tradestrategyService.findPositionOrdersById(tradestrategyId);

                if (tradestrategyOrders.hasOpenTradePosition()) {

                    tradePosition = tradePositionService.findById(
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
                    tradestrategyOrders = aspectService.save(tradestrategyOrders);
                    tradePosition.addTradeOrder(tradeOrder);
                    tradePosition = aspectService.save(tradePosition);
                }

                tradeOrder.setTradePosition(tradePosition);
            } else {

                /*
                 * If the order has not been filled, and it has no
                 * TradePosition this is the first order that has just been
                 * update.
                 */
                return aspectService.save(tradeOrder);
            }
        } else {

            tradePosition = tradePositionService.findById(tradeOrder.getTradePosition().getId());
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
                    tradePosition.setContractLite(this.aspectService.save(tradePosition.getContractLite()));
                }
            } else {

                tradePosition.getContractLite().setTradePosition(tradePosition);
                tradePosition.setContractLite(this.aspectService.save(tradePosition.getContractLite()));
            }

            // Partial fills case.
            if (null == tradestrategyOrders) {

                tradestrategyOrders = this.tradestrategyService.findPositionOrdersById(tradestrategyId);
            }

            if (!tradePosition.isOpen() && !TradestrategyStatus.CLOSED.equals(tradestrategyOrders.getStatus())) {

                /*
                 * Now update all the tradestrategies as there could be many
                 * if the position is across multiple days.
                 */
                for (TradeOrder item : tradePosition.getTradeOrders()) {

                    if (!Objects.equals(item.getTradestrategyLite().getId(), tradestrategyOrders.getId())) {

                        item.getTradestrategyLite().setStatus(TradestrategyStatus.CLOSED);
                        this.aspectService.save(item.getTradestrategyLite());
                    }
                }

                tradestrategyOrders.setStatus(TradestrategyStatus.CLOSED);
                this.aspectService.save(tradestrategyOrders);
            }

            this.aspectService.save(tradePosition);
        } else {

            if (allOrdersCancelled) {

                if (null == tradestrategyOrders) {

                    tradestrategyOrders = this.tradestrategyService.findPositionOrdersById(tradestrategyId);
                }

                if (!TradestrategyStatus.CANCELLED.equals(tradestrategyOrders.getStatus())) {

                    if (null == tradestrategyOrders.getStatus()) {

                        tradestrategyOrders.setStatus(TradestrategyStatus.CANCELLED);
                        this.aspectService.save(tradestrategyOrders);
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
                this.aspectService.save(tradePosition);
            }
        }

        return this.aspectService.save(tradeOrder);
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

    public Aspects findByClassName(String aspectClassName) throws ClassNotFoundException {

        if (("org.trade.core.persistent.strategy.Strategy").equals(aspectClassName)) {

            /*
             * Relationship Strategy -> IndicatorSeries is LAZY so we need
             * to call size() on Rule/IndicatorSeries.
             */
            List<Strategy> items = strategyService.findAll();
            Aspects aspects = new Aspects();

            for (Aspect item : items) {

                aspects.add(item);
            }

            aspects.setDirty(false);
            return aspects;
        } else if (("org.trade.core.persistent.portfolio.Portfolio").equals(aspectClassName)) {

            /*
             * Relationship Portfolio -> PortfolioAccount is LAZY so we
             * need to call size() on PortfolioAccount.
             */
            List<Portfolio> items = portfolioService.findAll();
            Aspects aspects = new Aspects();

            for (Aspect item : items) {

                aspects.add(item);
            }

            aspects.setDirty(false);
            return aspects;
        } else {
            return aspectService.findByClassName(aspectClassName);
        }
    }

    public void reassignStrategy(final Strategy fromStrategy, final Strategy toStrategy, final Tradingday tradingday) {

        for (Tradestrategy item : tradingday.getTradestrategies()) {

            if (Objects.requireNonNull(item.getStrategy().getId()).equals(fromStrategy.getId())) {

                item.setStrategy(toStrategy);
                item.setDirty(true);
                item.setStrategyData(null);
                aspectService.save(item);
            }
        }
    }
}
