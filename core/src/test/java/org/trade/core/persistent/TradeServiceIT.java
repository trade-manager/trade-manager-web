package org.trade.core.persistent;

import com.ib.client.Execution;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.aspect.Aspect;
import org.trade.core.aspect.Aspects;
import org.trade.core.broker.TWSBrokerModel;
import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.candle.Candle;
import org.trade.core.persistent.codetype.CodeType;
import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.contract.ContractLite;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.series.indicator.IIndicatorDataset;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.rule.Rule;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.tradelogdetail.TradelogReport;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.Tradingdays;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.ContentType;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.SECType;
import org.trade.core.valuetype.Side;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some tests for the  TradeServiceIT class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradeServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradeServiceIT.class);

    private static Tradestrategy tradestrategy;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);
    private static final String comment = "TEST-" + TradestrategyBase.getRandomNumber(8);
    // Default value is java
    private static final String contentType = ContentType.JAVA;
    private Integer clientId;

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void addTradestrategy() {

        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
        Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
        ZonedDateTime open = TradingCalendar.getTradingDayStart(
                TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        ZonedDateTime close = TradingCalendar.getTradingDayEnd(open);
        Tradingdays tradingdays = this.tradeService.getTradingdayService().findTradingdaysByDateRangeOrderByOpenAsc(open, open);
        Tradingday tradingday = tradingdays.getTradingday(open, close);

        if (null == tradingday) {

            tradingday = Tradingday.newInstance(open);
            tradingdays.add(tradingday);
        }

        Tradestrategy tradestrategy = new Tradestrategy(contract, tradingday, strategy, portfolio,
                new BigDecimal(100), "BUY", "0", true, ChartDays.ONE_DAY, BarSize.FIVE_MIN);

        if (tradingday.existTradestrategy(tradestrategy)) {

            _log.info("Tradestrategy Sysmbol: {} already exists.", tradestrategy.getContract().getSymbol());
        } else {

            tradingday.addTradestrategy(tradestrategy);
            tradingday = this.tradeService.saveTradingday(tradingday);
            _log.info("testTradingdaysSave tradestrategyId:{}", tradestrategy.getId());
        }

        tradingday.getTradestrategies().remove(tradingday.getTradestrategies().getLast());
        tradingday = this.tradeService.saveTradingday(tradingday);
        _log.info("testTradingdaysRemoce tradestrategyId:{}", tradestrategy.getId());
        assertNotNull(tradingday.getId());
        this.addRecord(tradingday);
        Optional<Contract> contractOpt = this.tradeService.getContractService().findBySymbol(symbol);
        assertTrue(contractOpt.isPresent());
        this.addRecord(contractOpt.get());
    }

    @Test
    public void findOpenTradePositionByTradestrategyId() {

        TradestrategyOrders positionOrders = this.tradeService
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());

        if (!positionOrders.hasOpenTradePosition()) {

            TradePosition tradePosition = new TradePosition(tradestrategy.getContractLite(),
                    TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

            tradePosition = this.tradeService.getAspectService().save(tradePosition);
            this.addRecord(tradePosition);
            tradestrategy.getContractLite().setTradePosition(tradePosition);
            ContractLite contractLite = this.tradeService.getAspectService().save(tradestrategy.getContractLite());
            tradestrategy.setContractLite(contractLite);
            positionOrders = this.tradeService
                    .findPositionOrdersByTradestrategyId(tradestrategy.getId());

            assertNotNull(positionOrders.getOpenTradePosition());
        }
    }

    @Test
    public void lifeCycleTradeOrder() {

        String side = tradestrategy.getSide();
        String action = Action.BUY;

        if (side.equals(Side.SLD)) {

            action = Action.SELL;
        }

        /*
         * Create an order for the trade.
         */
        double risk = tradestrategy.getRiskAmount().doubleValue();
        double stop = 0.20;
        BigDecimal price = new BigDecimal(20);
        int quantity = (int) ((int) risk / stop);
        ZonedDateTime createDate = tradestrategy.getTradingday().getOpen().plusMinutes(5);
        TradeOrder tradeOrder = new TradeOrder(tradestrategy, Action.BUY, OrderType.STPLMT, quantity, price,
                price.add(new BigDecimal(4)), createDate);
        tradeOrder.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());

        /*
         * Save the trade order i.e. doPlaceOrder()
         */
        tradeOrder = this.tradeService.saveTradeOrder(tradeOrder);
        assertNotNull(tradeOrder.getId());

        /*
         * Update the order to Submitted via openOrder(), orderStatus
         */
        TradeOrder tradeOrderOpenPosition = this.tradeService.findTradeOrderByKey(tradeOrder.getOrderKey());
        tradeOrderOpenPosition.setStatus(OrderStatus.SUBMITTED);
        tradeOrderOpenPosition = this.tradeService.saveTradeOrder(tradeOrderOpenPosition);
        assertNotNull(tradeOrderOpenPosition.getId());

        /*
         * Fill the order via execDetails()
         */
        TradeOrder tradeOrderFilled = this.tradeService
                .findTradeOrderByKey(tradeOrderOpenPosition.getOrderKey());
        Execution execution = new Execution();
        execution.side("BOT");
        execution.time(TradingCalendar.getFormattedDate(TradingCalendar.getDateTimeNowMarketTimeZone(),
                "yyyyMMdd HH:mm:ss"));
        execution.exchange("ISLAND");
        execution.shares(tradeOrder.getQuantity());
        execution.price(tradeOrder.getLimitPrice().doubleValue());
        execution.avgPrice(tradeOrder.getLimitPrice().doubleValue());
        execution.cumQty(tradeOrder.getQuantity());
        execution.execId("1234");
        TradeOrderfill tradeOrderfill = new TradeOrderfill();
        TWSBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);
        tradeOrderfill.setTradeOrder(tradeOrderFilled);
        tradeOrderFilled.addTradeOrderfill(tradeOrderfill);
        tradeOrderFilled.setAverageFilledPrice(tradeOrderfill.getAveragePrice());
        tradeOrderFilled.setFilledQuantity(tradeOrderfill.getCumulativeQuantity());
        tradeOrderFilled.setFilledDate(tradeOrderfill.getTime());
        tradeOrderFilled = this.tradeService.saveTradeOrder(tradeOrderFilled);
        assertNotNull(tradeOrderFilled.getTradeOrderfills().getFirst().getId());

        /*
         * Update the status to filled. Check to see if anything has changed
         * as this method gets fired twice on order fills.
         */
        TradeOrder tradeOrderFilledStatus = this.tradeService.findTradeOrderByKey(tradeOrder.getOrderKey());
        tradeOrderFilledStatus.setStatus(OrderStatus.FILLED);
        double commisionAmt = tradeOrderFilledStatus.getFilledQuantity() * 0.005d;

        if (OrderStatus.FILLED.equals(tradeOrderFilledStatus.getStatus()) && !tradeOrderFilledStatus.getIsFilled()
                && !((new Money(commisionAmt)).equals(new Money(Double.MAX_VALUE)))) {

            tradeOrderFilledStatus.setIsFilled(true);
            tradeOrderFilledStatus.setCommission(new BigDecimal(commisionAmt));
            tradeOrderFilledStatus = this.tradeService.saveTradeOrder(tradeOrderFilledStatus);
            assertNotNull(tradeOrderFilledStatus);
        }

        /*
         * Add the stop and target orders.
         */
        Tradestrategy tradestrategyStpTgt = this.tradeService
                .findTradestrategyById(tradestrategy.getId());
        assertTrue(tradestrategyStpTgt.isThereOpenTradePosition());

        int buySellMultiplier = 1;

        if (action.equals(Action.BUY)) {

            action = Action.SELL;

        } else {

            action = Action.BUY;
            buySellMultiplier = -1;
        }

        TradeOrder tradeOrderTgt1 = new TradeOrder(tradestrategy, action, OrderType.LMT, quantity / 2, null,
                price.add(new BigDecimal((stop * 3) * buySellMultiplier)), createDate);
        tradeOrderTgt1.setClientId(clientId);
        tradeOrderTgt1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderTgt1.setOcaType(2);
        tradeOrderTgt1.setOcaGroupName(tradestrategy.getId() + "q1w2e3");
        tradeOrderTgt1.setTransmit(true);
        tradeOrderTgt1.setStatus(OrderStatus.UNSUBMIT);
        tradeOrderTgt1 = this.tradeService.saveTradeOrder(tradeOrderTgt1);
        assertNotNull(tradeOrderTgt1);

        TradeOrder tradeOrderTgt2 = new TradeOrder(tradestrategy, action, OrderType.LMT, quantity / 2, null,
                price.add(new BigDecimal((stop * 4) * buySellMultiplier)), createDate);
        tradeOrderTgt2.setClientId(clientId);
        tradeOrderTgt2.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderTgt2.setOcaType(2);
        tradeOrderTgt2.setOcaGroupName(tradestrategy.getId() + "w2e3r4");
        tradeOrderTgt2.setTransmit(true);
        tradeOrderTgt2.setStatus(OrderStatus.UNSUBMIT);
        tradeOrderTgt2 = this.tradeService.saveTradeOrder(tradeOrderTgt2);
        assertNotNull(tradeOrderTgt2);
        TradeOrder tradeOrderStp1 = new TradeOrder(tradestrategy, action, OrderType.STP, quantity / 2,
                price.add(new BigDecimal(stop * buySellMultiplier * -1)), null, createDate);

        tradeOrderStp1.setClientId(clientId);
        tradeOrderStp1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderStp1.setOcaType(2);
        tradeOrderStp1.setOcaGroupName(tradestrategy.getId() + "q1w2e3");
        tradeOrderStp1.setTransmit(true);
        tradeOrderStp1.setStatus(OrderStatus.UNSUBMIT);
        tradeOrderStp1 = this.tradeService.saveTradeOrder(tradeOrderStp1);
        assertNotNull(tradeOrderStp1);
        TradeOrder tradeOrderStp2 = new TradeOrder(tradestrategy, action, OrderType.STP, quantity / 2,
                price.add(new BigDecimal(stop * buySellMultiplier * -1)), null, createDate);

        tradeOrderStp2.setClientId(clientId);
        tradeOrderStp2.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderStp2.setOcaType(2);
        tradeOrderStp2.setOcaGroupName(tradestrategy.getId() + "w2e3r4");
        tradeOrderStp2.setTransmit(true);
        tradeOrderStp2.setStatus(OrderStatus.UNSUBMIT);
        tradeOrderStp2 = this.tradeService.saveTradeOrder(tradeOrderStp2);
        assertNotNull(tradeOrderStp2);

        /*
         * Update Stop/target orders to Submitted.
         */
        TradestrategyOrders positionOrders = this.tradeService
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());

        for (TradeOrder tradeOrderOca : positionOrders.getTradeOrders()) {

            TradeOrder tradeOrderOcaUnsubmit = this.tradeService
                    .findTradeOrderByKey(tradeOrderOca.getOrderKey());

            if (tradeOrderOcaUnsubmit.getStatus().equals(OrderStatus.UNSUBMIT)
                    && (null != tradeOrderOcaUnsubmit.getOcaGroupName())) {

                tradeOrderOcaUnsubmit.setStatus(OrderStatus.SUBMITTED);
                tradeOrderOcaUnsubmit = this.tradeService.saveTradeOrder(tradeOrderOcaUnsubmit);
                assertNotNull(tradeOrderOcaUnsubmit);
            }
        }

        /*
         * Fill the stop orders.
         */
        positionOrders = this.tradeService
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());

        for (TradeOrder tradeOrderOca : positionOrders.getTradeOrders()) {

            TradeOrder tradeOrderOcaSubmit = this.tradeService
                    .findTradeOrderByKey(tradeOrderOca.getOrderKey());

            if (OrderStatus.SUBMITTED.equals(tradeOrderOcaSubmit.getStatus())
                    && (null != tradeOrderOcaSubmit.getOcaGroupName())) {

                if (OrderType.STP.equals(tradeOrderOcaSubmit.getOrderType())) {

                    Execution executionOCA = new Execution();
                    executionOCA.side(positionOrders.getContractLite().getTradePosition().getSide());
                    executionOCA.time(TradingCalendar
                            .getFormattedDate(TradingCalendar.getDateTimeNowMarketTimeZone(), "yyyyMMdd HH:mm:ss"));
                    executionOCA.exchange("ISLAND");
                    executionOCA.shares(tradeOrderOcaSubmit.getQuantity());
                    executionOCA.price(tradeOrderOcaSubmit.getAuxPrice().doubleValue());
                    executionOCA.avgPrice(tradeOrderOcaSubmit.getAuxPrice().doubleValue());
                    executionOCA.cumQty(tradeOrderOcaSubmit.getQuantity());
                    executionOCA.execId("1234");
                    TradeOrderfill tradeOrderfillOCA = new TradeOrderfill();
                    TWSBrokerModel.populateTradeOrderfill(executionOCA, tradeOrderfillOCA);
                    tradeOrderfillOCA.setTradeOrder(tradeOrderOcaSubmit);
                    tradeOrderOcaSubmit.addTradeOrderfill(tradeOrderfillOCA);
                    tradeOrderOcaSubmit.setAverageFilledPrice(tradeOrderfillOCA.getAveragePrice());
                    tradeOrderOcaSubmit.setFilledQuantity(tradeOrderfillOCA.getCumulativeQuantity());
                    tradeOrderOcaSubmit.setFilledDate(tradeOrderfillOCA.getTime());
                    tradeOrderOcaSubmit = this.tradeService.saveTradeOrder(tradeOrderOcaSubmit);

                    for (TradeOrderfill item : tradeOrderOcaSubmit.getTradeOrderfills()) {

                        assertNotNull(item.getId());
                    }
                }
            }
        }

        /*
         * Update Stop/target orders status to filled and cancelled.
         */
        positionOrders = this.tradeService
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());

        for (TradeOrder tradeOrderOca : positionOrders.getTradeOrders()) {

            TradeOrder tradeOrderOcaSubmit = this.tradeService
                    .findTradeOrderByKey(tradeOrderOca.getOrderKey());
            if (tradeOrderOcaSubmit.getStatus().equals(OrderStatus.SUBMITTED)
                    && (null != tradeOrderOcaSubmit.getOcaGroupName())) {

                if (tradeOrderOcaSubmit.getOrderType().equals(OrderType.STP)) {


                    tradeOrderOcaSubmit.setStatus(OrderStatus.FILLED);
                    tradeOrderOcaSubmit
                            .setCommission(BigDecimal.valueOf(tradeOrderOcaSubmit.getFilledQuantity() * 0.005d));
                    tradeOrderOcaSubmit.setIsFilled(true);
                } else {

                    tradeOrderOcaSubmit.setStatus(OrderStatus.CANCELLED);
                }
                tradeOrderOcaSubmit = this.tradeService.saveTradeOrder(tradeOrderOcaSubmit);
                assertNotNull(tradeOrderOcaSubmit);

                if (!positionOrders.hasOpenTradePosition()) {

                    _log.info("TradePosition closed: ");
                }
            }
        }
    }

    @Test
    public void saveTradingday() {

        this.tradeService.saveTradingday(tradestrategy.getTradingday());
        assertNotNull(tradestrategy.getTradingday().getId());
    }

    @Test
    public void saveTradestrategy() {

        Tradestrategy result = this.tradeService.getAspectService().save(tradestrategy);
        assertNotNull(result.getId());
    }

    @Test
    public void saveContract() {

        Contract result = this.tradeService.getAspectService().save(tradestrategy.getContract());
        assertNotNull(result.getId());
    }

    @Test
    public void setDefaultPortfolio() {

        tradestrategy.getPortfolio().setIsDefault(false);
        this.tradeService.getAspectService().save(tradestrategy.getPortfolio());
        this.tradeService.getPortfolioService().resetDefault(tradestrategy.getPortfolio());
        assertTrue(tradestrategy.getPortfolio().getIsDefault());
    }

    @Test
    public void saveTradeOrder() throws Exception {

        TradeOrder tradeOrder = new TradeOrder(tradestrategy, Action.BUY, OrderType.MKT, 1000, null, null,
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder.validate();
        TradeOrder result = this.tradeService.saveTradeOrder(tradeOrder);
        assertNotNull(result.getId());
    }

    @Test
    public void saveTradeOrderFilledLong() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrderBuy = new TradeOrder(tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(2)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderBuy.validate();
        tradeOrderBuy = this.tradeService.saveTradeOrder(tradeOrderBuy);
        tradeOrderBuy.setStatus(OrderStatus.SUBMITTED);
        tradeOrderBuy = this.tradeService.saveTradeOrder(tradeOrderBuy);
        TradeOrderfill orderfill = new TradeOrderfill(tradeOrderBuy, "Paper", price,
                tradeOrderBuy.getQuantity() / 2, "ISLAND", "1a", price, tradeOrderBuy.getQuantity() / 2,
                tradestrategy.getSide(), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill);
        tradeOrderBuy = this.tradeService.saveTradeOrderfill(tradeOrderBuy);

        TradeOrderfill orderfill1 = new TradeOrderfill(tradeOrderBuy, "Paper", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity(), "BATS", "1b", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity() / 2, tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill1);
        tradeOrderBuy.setCommission(new BigDecimal("5.0"));
        tradeOrderBuy = this.tradeService.saveTradeOrderfill(tradeOrderBuy);

        TradeOrder tradeOrderSell = new TradeOrder(tradestrategy, Action.SELL, OrderType.LMT,
                tradeOrderBuy.getQuantity(), null, new BigDecimal("105.00"),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderSell = this.tradeService.saveTradeOrder(tradeOrderSell);
        tradeOrderSell.setStatus(OrderStatus.SUBMITTED);
        tradeOrderSell.validate();
        tradeOrderSell = this.tradeService.saveTradeOrder(tradeOrderSell);

        TradeOrderfill orderfill2 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, "ISLAND", "2a", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill2);
        tradeOrderSell = this.tradeService.saveTradeOrderfill(tradeOrderSell);

        TradeOrderfill orderfill3 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity(), "BATS", "2b", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill3);
        tradeOrderSell.setCommission(new BigDecimal("5.0"));

        TradeOrder result = this.tradeService.saveTradeOrderfill(tradeOrderSell);
        assertFalse(result.getTradePosition().isOpen());

        assertEquals((new Money(4000.00)), new Money(result.getTradePosition().getTotalNetValue()));

        double totalPriceMade = (result.getTradePosition().getTotalSellValue().doubleValue()
                / result.getTradePosition().getTotalSellQuantity().doubleValue())
                - (result.getTradePosition().getTotalBuyValue().doubleValue()
                / result.getTradePosition().getTotalBuyQuantity().doubleValue());
        assertEquals((new Money(4.00)).getBigDecimalValue(), (new Money(totalPriceMade)).getBigDecimalValue());
        assertEquals(Integer.valueOf(1000), result.getTradePosition().getTotalBuyQuantity());
        assertEquals(Integer.valueOf(1000), result.getTradePosition().getTotalSellQuantity());
        assertEquals(Integer.valueOf(0), result.getTradePosition().getOpenQuantity());
    }

    @Test
    public void saveTradeOrderFilledShort() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrderBuy = new TradeOrder(tradestrategy, Action.SELL, OrderType.STPLMT, 1000, price,
                price.subtract(new BigDecimal(2)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderBuy = this.tradeService.saveTradeOrder(tradeOrderBuy);
        tradeOrderBuy.setStatus(OrderStatus.SUBMITTED);
        tradeOrderBuy.validate();
        tradeOrderBuy = this.tradeService.saveTradeOrder(tradeOrderBuy);

        TradeOrderfill orderfill = new TradeOrderfill(tradeOrderBuy, "Paper", price,
                tradeOrderBuy.getQuantity() / 2, "ISLAND", "1a", price, tradeOrderBuy.getQuantity() / 2,
                tradestrategy.getSide(), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill);
        tradeOrderBuy = this.tradeService.saveTradeOrderfill(tradeOrderBuy);

        TradeOrderfill orderfill1 = new TradeOrderfill(tradeOrderBuy, "Paper", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity(), "BATS", "1b", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity() / 2, tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill1);
        tradeOrderBuy.setCommission(new BigDecimal("5.0"));
        tradeOrderBuy = this.tradeService.saveTradeOrderfill(tradeOrderBuy);

        TradeOrder tradeOrderSell = new TradeOrder(tradestrategy, Action.BUY, OrderType.LMT,
                tradeOrderBuy.getQuantity(), null, new BigDecimal("95.00"),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderSell = this.tradeService.saveTradeOrder(tradeOrderSell);
        tradeOrderSell.setStatus(OrderStatus.SUBMITTED);
        tradeOrderSell = this.tradeService.saveTradeOrder(tradeOrderSell);

        TradeOrderfill orderfill2 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, "ISLAND", "2a", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill2);
        tradeOrderSell = this.tradeService.saveTradeOrderfill(tradeOrderSell);

        TradeOrderfill orderfill3 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity(), "BATS", "2b", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill3);
        tradeOrderSell.setCommission(new BigDecimal("5.0"));
        TradeOrder result = this.tradeService.saveTradeOrderfill(tradeOrderSell);

        assertFalse(result.getTradePosition().isOpen());
        assertEquals(new Money(4000.00), new Money(result.getTradePosition().getTotalNetValue()));
        double totalPriceMade = (result.getTradePosition().getTotalSellValue().doubleValue()
                / result.getTradePosition().getTotalSellQuantity().doubleValue())
                - (result.getTradePosition().getTotalBuyValue().doubleValue()
                / result.getTradePosition().getTotalBuyQuantity().doubleValue());
        assertEquals((new Money(4.00)).getBigDecimalValue(), (new Money(totalPriceMade)).getBigDecimalValue());
        assertEquals(Integer.valueOf(1000), result.getTradePosition().getTotalBuyQuantity());
        assertEquals(Integer.valueOf(1000), result.getTradePosition().getTotalSellQuantity());
        assertEquals(Integer.valueOf(0), result.getTradePosition().getOpenQuantity());
    }

    @Test
    public void saveCandleSeries() throws Exception {

        tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), 5, tradestrategy.getBarSize(), true, 0);
        long timeStart = System.currentTimeMillis();
        this.tradeService.saveCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());
        _log.info("Total time: {}", (System.currentTimeMillis() - timeStart) / 1000);
        assertFalse(tradestrategy.getStrategyData().getCandles().isEmpty());
        assertNotNull(((CandleItem) tradestrategy.getStrategyData().getBaseCandleSeries().getDataItem(0)).getCandle().getId());
    }

    @Test
    public void findAccountById() {

        Portfolio result = this.tradeService
                .getPortfolioService().findById(tradestrategy.getPortfolio().getId());
        assertNotNull(result);
    }

    @Test
    public void findAccountByNumber() {

        Account result = this.tradeService.getAccountService().findByAccountNumber(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber());
        assertNotNull(result);
    }

    @Test
    public void findContractById() {

        Contract result = this.tradeService.getContractService().findById(tradestrategy.getContract().getId());
        assertNotNull(result);
    }

    @Test
    public void findContractByUniqueKey() {

        Contract result = this.tradeService.getContractService().findByUniqueKey(
                tradestrategy.getContract().getSecType(), tradestrategy.getContract().getSymbol(),
                tradestrategy.getContract().getExchange(), tradestrategy.getContract().getCurrency(),
                null);
        assertNotNull(result);
    }

    @Test
    public void findTradestrategyByTradestrategy() {

        Tradestrategy result = this.tradeService.findTradestrategyById(tradestrategy);
        assertNotNull(result);
    }

    @Test
    public void findTradestrategyById() {

        Tradestrategy result = this.tradeService
                .findTradestrategyById(tradestrategy.getId());
        assertNotNull(result);
    }

    @Test
    public void findTradestrategyByUniqueKeys() {

        Tradestrategy result = this.tradeService.findTradestrategyByUniqueKeys(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getStrategy().getName(),
                tradestrategy.getContract(), tradestrategy.getPortfolio().getName());
        assertNotNull(result);
    }

    @Test
    public void findAllTradestrategies() {

        List<Tradestrategy> result = this.tradeService.findAllTradestrategies();
        assertNotNull(result);
    }

    @Test
    public void findTradePositionById() {

        TradePosition tradePosition = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        TradePosition resultTrade = this.tradeService.getAspectService().save(tradePosition);
        TradePosition result = this.tradeService.findTradePositionById(resultTrade.getId());
        assertNotNull(result);
    }

    @Test
    public void findPositionOrdersByTradestrategyId() {

        TradePosition tradePosition = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

        TradePosition resultTrade = this.tradeService.getAspectService().save(tradePosition);
        resultTrade.getContractLite().setTradePosition(resultTrade);
        resultTrade.setContractLite(this.tradeService.getAspectService().save(resultTrade.getContractLite()));
        assertNotNull(resultTrade);

        TradestrategyOrders result = this.tradeService
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());
        assertNotNull(result);
        resultTrade.getContractLite().setTradePosition(null);
        resultTrade.setContractLite(this.tradeService.getAspectService().save(resultTrade.getContractLite()));
    }

    @Test
    public void refreshPositionOrdersByTradestrategyId() {

        TradePosition tradePosition = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        tradestrategy.getContractLite().setTradePosition(tradePosition);
        TradePosition resultTrade = this.tradeService.getAspectService().save(tradePosition);
        assertNotNull(resultTrade);

        TradestrategyOrders positionOrders = this.tradeService
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());
        _log.info("testFindVersionById tradestrategyId:{} version: {}", positionOrders.getId(), positionOrders.getVersion());

        TradestrategyOrders result = this.tradeService.getAspectService().save(positionOrders);

        _log.info("testFindVersionById tradestrategyId:{} version: {}", result.getId(), result.getVersion());
        result = this.tradeService.refreshPositionOrdersByTradestrategyId(positionOrders);
        _log.info("testFindVersionById tradestrategyId:{} prev version: {} current version: {}", result.getId(), positionOrders.getVersion(), result.getVersion());
        assertNotNull(result);
    }

    @Test
    public void removeTradingdayTradeOrders() {

        TradePosition tradePosition = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        this.tradeService.getAspectService().save(tradePosition);
        Tradingday result = this.tradeService.getTradingdayService()
                .findById(tradestrategy.getTradingday().getId());
        assertNotNull(result);
        this.tradeService.deleteTradingdayTradeOrders(result);
    }

    @Test
    public void saveTradePosition() {

        TradePosition tradePosition = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        TradePosition result = this.tradeService.getAspectService().save(tradePosition);
        assertNotNull(result.getId());
    }

    @Test
    public void removeTradestrategyTradeOrders() {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder = this.tradeService.saveTradeOrder(tradeOrder);

        TradeOrderfill orderfill = new TradeOrderfill(tradeOrder, "Paper", price,
                tradeOrder.getQuantity(), "ISLAND", "1a", price, tradeOrder.getQuantity(),
                tradestrategy.getSide(), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.addTradeOrderfill(orderfill);
        tradeOrder = this.tradeService.saveTradeOrderfill(tradeOrder);

        tradestrategy = this.tradeService
                .findTradestrategyById(tradestrategy.getId());
        assertNotNull(tradestrategy);
        this.tradeService.deleteTradestrategyTradeOrders(tradestrategy);
        tradestrategy = this.tradeService
                .findTradestrategyById(tradestrategy.getId());
        assertTrue(tradestrategy.getTradeOrders().isEmpty());
        assertFalse(tradestrategy.isThereOpenTradePosition());
        assertNull(tradestrategy.getContractLite().getTradePosition());
    }

    @Test
    public void findTradeOrderById() {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder = this.tradeService.saveTradeOrder(tradeOrder);
        tradeOrder = this.tradeService.findTradeOrderById(tradeOrder.getId());
        assertNotNull(tradeOrder);
    }

    @Test
    public void findTradeOrderByKey() {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        TradeOrder resultTradeOrder = this.tradeService.saveTradeOrder(tradeOrder);
        TradeOrder result = this.tradeService.findTradeOrderByKey(resultTradeOrder.getOrderKey());
        assertNotNull(result);
    }

    @Test
    public void findTradeOrderfillByExecId() {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        TradeOrderfill tradeOrderfill = new TradeOrderfill(tradeOrder, "Paper", new BigDecimal("100.23"),
                1000, Exchange.SMART, "123efgr567", new BigDecimal("100.23"), 1000,
                Side.BOT, TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.addTradeOrderfill(tradeOrderfill);
        TradeOrder resultTradeOrder = this.tradeService.saveTradeOrder(tradeOrder);
        TradeOrderfill result = this.tradeService
                .findTradeOrderfillByExecId(resultTradeOrder.getTradeOrderfills().getFirst().getExecId());
        assertNotNull(result);
    }

    @Test
    public void findTradeOrderByMaxKey() {

        Integer result = this.tradeService.findTradeOrderByMaxKey();
        assertNotNull(result);
    }

    @Test
    public void findTradingdayById() {

        Tradingday result = this.tradeService.getTradingdayService()
                .findById(tradestrategy.getTradingday().getId());
        assertNotNull(result);
    }

    @Test
    public void findTradingdayByOpenDate() {

        Tradingday result = this.tradeService.getTradingdayService().findByOpenCloseDate(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getClose());
        assertNotNull(result);
    }

    @Test
    public void findTradingdaysByDateRange() {

        Tradingdays result = this.tradeService.getTradingdayService().findTradingdaysByDateRangeOrderByOpenAsc(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());
        assertNotNull(result);
    }

    @Test
    public void findTradestrategyDistinctByDateRange() {

        List<Tradestrategy> result = this.tradeService.findTradestrategyDistinctByDateRange(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());
        assertNotNull(result);
    }

    @Test
    public void findTradelogReport() throws Exception {

        TradelogReport result = this.tradeService.findTradelogReport(tradestrategy.getPortfolio(),
                TradingCalendar.getYearStart(), tradestrategy.getTradingday().getClose(), true, null,
                new BigDecimal(0));
        assertNotNull(result);
    }

    @Test
    public void findCandlesByContractAndDateRange() {

        List<Candle> result = this.tradeService.getCandleService().findByContractDateRangeBarSize(
                tradestrategy.getContract(), tradestrategy.getTradingday().getOpen(),
                tradestrategy.getTradingday().getClose(), tradestrategy.getBarSize());
        assertNotNull(result);
    }

    @Test
    public void findCandleCount() {

        Long result = this.tradeService.getCandleService().findCount(tradestrategy.getContract());
        assertNotNull(result);
    }

    @Test
    public void saveRule() {

        String contentType = ContentType.JAVASCRIPT;
        String content = "function (){console.log('Hi');}";
        Strategy strategy = tradestrategy.getStrategy();
        strategy = this.tradeService.getStrategyService().findById(strategy.getId());
        Rule rule = new Rule(strategy, true, 0, comment, content.getBytes(), contentType);
        strategy.getRules().add(rule);
        strategy = this.tradeService.getAspectService().save(strategy);
        rule = this.tradeService.getRuleService().findByMaxVersion(strategy, contentType);
        assertEquals(0, rule.getRuleVersion());
        assertEquals(contentType, rule.getContentType());
    }

    @Test
    public void findRuleById() {

        Strategy strategy = tradestrategy.getStrategy();
        strategy = this.tradeService.getStrategyService().findById(strategy.getId());
        Rule rule = new Rule(strategy, true, 0, comment);
        strategy.getRules().add(rule);
        strategy = this.tradeService.getAspectService().save(strategy);
        Rule latestRule = this.tradeService.getRuleService().findByMaxVersion(strategy, contentType);
        Integer version = 0;

        if (null != latestRule) {

            version = latestRule.getRuleVersion() + 1;
        }

        rule = new Rule(strategy, true, version, comment);
        strategy.getRules().add(rule);
        strategy = this.tradeService.getAspectService().save(strategy);
        rule = strategy.getRules().getLast();
        assertEquals(1, rule.getRuleVersion());
    }

    @Test
    public void findRuleByMaxVersion() {

        Rule latestRule = this.tradeService.getRuleService().findByMaxVersion(tradestrategy.getStrategy(), contentType);
        assertNull(latestRule);
    }

    @Test
    public void findStrategyById() {

        Strategy result = this.tradeService.getStrategyService().findById(tradestrategy.getStrategy().getId());
        assertNotNull(result);
    }

    @Test
    public void findStrategyByName() {

        Strategy result = this.tradeService.getStrategyService().findByName(tradestrategy.getStrategy().getName());
        assertNotNull(result);
    }

    @Test
    public void removeRule() {

        Strategy strategy = tradestrategy.getStrategy();
        strategy = this.tradeService.getStrategyService().findById(strategy.getId());
        Rule latestRule = this.tradeService.getRuleService().findByMaxVersion(strategy, contentType);
        Integer version = 0;

        if (null != latestRule) {

            version = latestRule.getRuleVersion() + 1;
        }

        Rule rule = new Rule(strategy, true, version, comment);
        strategy.getRules().add(rule);
        strategy = this.tradeService.getAspectService().save(strategy);
        rule = strategy.getRules().getFirst();
        assertEquals(0, rule.getRuleVersion());
        strategy.getRules().clear();
        this.tradeService.getAspectService().save(strategy);
    }

    @Test
    public void findStrategies() {

        List<Strategy> result = this.tradeService.getStrategyService().findAll();
        assertNotNull(result);
    }

    @Test
    public void findAspectsByClassName() throws Exception {

        Aspects result = this.tradeService.getAspectService().findByClassName(tradestrategy.getClass().getName());
        assertNotNull(result);
    }

    @Test
    public void findAspectsByClassNameFieldName() throws Exception {

        for (IIndicatorDataset indicator : tradestrategy.getStrategyData().getIndicators()) {

            org.trade.core.persistent.dao.series.indicator.IndicatorSeries series = indicator.getSeries(0);
            String indicatorName = series.getType().substring(0, series.getType().indexOf("Series"));
            Aspects result = this.tradeService.getAspectService().findByClassNameAndFieldName(CodeType.class.getName(),
                    "name", indicatorName);
            assertNotNull(result);
        }
    }

    @Test
    public void findAspectById() throws ClassNotFoundException {

        Aspect result = this.tradeService.getAspectService().findById(tradestrategy);
        assertNotNull(result);
    }

    @Test
    public void saveAspect() {

        Aspect result = this.tradeService.getAspectService().save(tradestrategy);
        assertNotNull(result);
    }

    @Test
    public void removeAspect() {

        final String symbol = "SAA-" + TradestrategyBase.getRandomNumber(4);
        Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
        contract = tradeService.getAspectService().save(contract);
        this.tradeService.getAspectService().delete(contract);

        Contract finalContract = contract;
        assertDoesNotThrow(
                () -> assertNull(this.tradeService.getAspectService().findById(finalContract)));
    }

    @Test
    public void reassignStrategy() {

        Tradingday tradingday = this.tradeService.getTradingdayService()
                .findById(tradestrategy.getTradingday().getId());
        assertFalse(tradingday.getTradestrategies().isEmpty());
        Strategy toStrategy = (Strategy) DAOStrategy.newInstance().getObject();
        toStrategy = this.tradeService.getStrategyService().findById(toStrategy.getId());
        this.tradeService.reassignStrategy(tradestrategy.getStrategy(), toStrategy, tradingday);
        assertEquals(toStrategy, tradingday.getTradestrategies().getFirst().getStrategy());
    }

    @Test
    public void fetchData() {

        /*Test data retrieval*/
        Optional<Contract> contract = tradeService.getContractService().findBySymbol(symbol);
        assertNotNull(contract);
        Iterable<Contract> item = tradeService.getContractService().findAll();
        assertTrue(item.iterator().hasNext());
    }

    @Test
    public void findBySymbol() {

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime expiry = now.atZone(ZoneId.systemDefault());
        Contract contract = new Contract("STK", "Test3", "SMART", "USD", expiry, new BigDecimal(1));
        contract = tradeService.getAspectService().save(contract);

        Optional<Contract> contract1 = tradeService.getContractService().findBySymbol(contract.getSymbol());
        assertThat(contract1.get()).extracting(Contract::getSymbol).isEqualTo(contract.getSymbol());
        tradeService.getAspectService().delete(contract);
    }

}
