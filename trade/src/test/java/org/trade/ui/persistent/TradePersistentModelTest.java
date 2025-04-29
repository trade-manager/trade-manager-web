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
package org.trade.ui.persistent;

import com.ib.client.Execution;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.TradeAppLoadConfig;
import org.trade.core.broker.TWSBrokerModel;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.IPersistentModel;
import org.trade.core.persistent.PersistentModelException;
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
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.IIndicatorDataset;
import org.trade.core.persistent.dao.series.indicator.IndicatorSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.SECType;
import org.trade.core.valuetype.Side;
import org.trade.indicator.IndicatorSeriesUI;
import org.trade.indicator.candle.CandleItemUI;
import org.trade.ui.models.TradingdayTableModel;
import org.trade.ui.tables.TradingdayTable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradePersistentModelTest {

    private final static Logger _log = LoggerFactory.getLogger(TradePersistentModelTest.class);

    private IPersistentModel tradePersistentModel = null;
    private Tradestrategy tradestrategy = null;
    private Integer clientId = null;

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {
        TradeAppLoadConfig.loadAppProperties();
        clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        this.tradePersistentModel = (IPersistentModel) ClassFactory
                .getServiceForInterface(IPersistentModel._persistentModel, this);
        String symbol = "TEST";
        this.tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(this.tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        TradestrategyBase.clearDBData();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testAddTradestrategy() throws Exception {

        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();

        String symbol = "TEST1";
        Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);

        ZonedDateTime open = TradingCalendar.getTradingDayStart(
                TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        ZonedDateTime close = TradingCalendar.getTradingDayEnd(open);
        Tradingdays tradingdays = this.tradePersistentModel.findTradingdaysByDateRange(open, open);
        Tradingday tradingday = tradingdays.getTradingday(open, close);
        if (null == tradingday) {
            tradingday = Tradingday.newInstance(open);
            tradingdays.add(tradingday);
        }

        Tradestrategy tradestrategy = new Tradestrategy(contract, tradingday, strategy, portfolio,
                new BigDecimal(100), "BUY", "0", true, ChartDays.TWO_DAYS, BarSize.FIVE_MIN);
        if (tradingday.existTradestrategy(tradestrategy)) {
            _log.info("Tradestrategy Sysmbol: {} already exists.", tradestrategy.getContract().getSymbol());
        } else {
            tradingday.addTradestrategy(tradestrategy);
            this.tradePersistentModel.persistTradingday(tradingday);
            _log.info("testTradingdaysSave IdTradeStrategy:{}", tradestrategy.getId());
        }
        tradingday.getTradestrategies().remove(tradestrategy);
        this.tradePersistentModel.persistTradingday(tradingday);
        _log.info("testTradingdaysRemoce IdTradeStrategy:{}", tradestrategy.getId());
        assertNotNull(tradingday.getId());
    }

    @Test
    public void testFindOpenTradePositionByTradestrategyId() throws Exception {

        TradestrategyOrders positionOrders = this.tradePersistentModel
                .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());
        if (!positionOrders.hasOpenTradePosition()) {
            TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                    TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

            tradePosition = this.tradePersistentModel.persistAspect(tradePosition);
            this.tradestrategy.getContract().setTradePosition(tradePosition);
            this.tradePersistentModel.persistAspect(this.tradestrategy.getContract());
            positionOrders = this.tradePersistentModel
                    .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());

            assertNotNull(positionOrders.getOpenTradePosition());
        }
    }

    @Test
    public void testLifeCycleTradeOrder() throws Exception {

        String side = this.tradestrategy.getSide();
        String action = Action.BUY;
        if (side.equals(Side.SLD)) {
            action = Action.SELL;
        }

        /*
         * Create an order for the trade.
         */
        double risk = this.tradestrategy.getRiskAmount().doubleValue();

        double stop = 0.20;
        BigDecimal price = new BigDecimal(20);
        int quantity = (int) ((int) risk / stop);
        ZonedDateTime createDate = this.tradestrategy.getTradingday().getOpen().plusMinutes(5);
        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.STPLMT, quantity, price,
                price.add(new BigDecimal(4)), createDate);
        tradeOrder.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        /*
         * Save the trade order i.e. doPlaceOrder()
         */
        tradeOrder = this.tradePersistentModel.persistTradeOrder(tradeOrder);
        assertNotNull(tradeOrder.getId());
        /*
         * Update the order to Submitted via openOrder(), orderStatus
         */
        TradeOrder tradeOrderOpenPosition = this.tradePersistentModel.findTradeOrderByKey(tradeOrder.getOrderKey());
        tradeOrderOpenPosition.setStatus(OrderStatus.SUBMITTED);

        tradeOrderOpenPosition = this.tradePersistentModel.persistTradeOrder(tradeOrderOpenPosition);
        assertNotNull(tradeOrderOpenPosition.getId());
        /*
         * Fill the order via execDetails()
         */
        TradeOrder tradeOrderFilled = this.tradePersistentModel
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
        tradeOrderFilled = this.tradePersistentModel.persistTradeOrder(tradeOrderFilled);
        assertNotNull(tradeOrderFilled.getTradeOrderfills().getFirst().getId());

        /*
         * Update the status to filled. Check to see if anything has changed
         * as this method gets fired twice on order fills.
         */
        TradeOrder tradeOrderFilledStatus = this.tradePersistentModel.findTradeOrderByKey(tradeOrder.getOrderKey());
        tradeOrderFilledStatus.setStatus(OrderStatus.FILLED);
        double commisionAmt = tradeOrderFilledStatus.getFilledQuantity() * 0.005d;

        if (OrderStatus.FILLED.equals(tradeOrderFilledStatus.getStatus()) && !tradeOrderFilledStatus.getIsFilled()
                && !((new Money(commisionAmt)).equals(new Money(Double.MAX_VALUE)))) {
            tradeOrderFilledStatus.setIsFilled(true);
            tradeOrderFilledStatus.setCommission(new BigDecimal(commisionAmt));
            tradeOrderFilledStatus = this.tradePersistentModel.persistTradeOrder(tradeOrderFilledStatus);
            assertNotNull(tradeOrderFilledStatus);
        }

        /*
         * Add the stop and target orders.
         */
        Tradestrategy tradestrategyStpTgt = this.tradePersistentModel
                .findTradestrategyById(this.tradestrategy.getId());
        assertTrue(tradestrategyStpTgt.isThereOpenTradePosition());

        int buySellMultiplier = 1;
        if (action.equals(Action.BUY)) {
            action = Action.SELL;

        } else {
            action = Action.BUY;
            buySellMultiplier = -1;
        }

        TradeOrder tradeOrderTgt1 = new TradeOrder(this.tradestrategy, action, OrderType.LMT, quantity / 2, null,
                price.add(new BigDecimal((stop * 3) * buySellMultiplier)), createDate);

        tradeOrderTgt1.setClientId(clientId);
        tradeOrderTgt1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderTgt1.setOcaType(2);
        tradeOrderTgt1.setOcaGroupName(this.tradestrategy.getId() + "q1w2e3");
        tradeOrderTgt1.setTransmit(true);
        tradeOrderTgt1.setStatus(OrderStatus.UNSUBMIT);

        tradeOrderTgt1 = this.tradePersistentModel.persistTradeOrder(tradeOrderTgt1);
        assertNotNull(tradeOrderTgt1);
        TradeOrder tradeOrderTgt2 = new TradeOrder(this.tradestrategy, action, OrderType.LMT, quantity / 2, null,
                price.add(new BigDecimal((stop * 4) * buySellMultiplier)), createDate);
        tradeOrderTgt2.setClientId(clientId);
        tradeOrderTgt2.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderTgt2.setOcaType(2);
        tradeOrderTgt2.setOcaGroupName(this.tradestrategy.getId() + "w2e3r4");
        tradeOrderTgt2.setTransmit(true);
        tradeOrderTgt2.setStatus(OrderStatus.UNSUBMIT);

        tradeOrderTgt2 = this.tradePersistentModel.persistTradeOrder(tradeOrderTgt2);
        assertNotNull(tradeOrderTgt2);
        TradeOrder tradeOrderStp1 = new TradeOrder(this.tradestrategy, action, OrderType.STP, quantity / 2,
                price.add(new BigDecimal(stop * buySellMultiplier * -1)), null, createDate);

        tradeOrderStp1.setClientId(clientId);
        tradeOrderStp1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderStp1.setOcaType(2);
        tradeOrderStp1.setOcaGroupName(this.tradestrategy.getId() + "q1w2e3");
        tradeOrderStp1.setTransmit(true);
        tradeOrderStp1.setStatus(OrderStatus.UNSUBMIT);

        tradeOrderStp1 = this.tradePersistentModel.persistTradeOrder(tradeOrderStp1);
        assertNotNull(tradeOrderStp1);
        TradeOrder tradeOrderStp2 = new TradeOrder(this.tradestrategy, action, OrderType.STP, quantity / 2,
                price.add(new BigDecimal(stop * buySellMultiplier * -1)), null, createDate);

        tradeOrderStp2.setClientId(clientId);
        tradeOrderStp2.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderStp2.setOcaType(2);
        tradeOrderStp2.setOcaGroupName(this.tradestrategy.getId() + "w2e3r4");
        tradeOrderStp2.setTransmit(true);
        tradeOrderStp2.setStatus(OrderStatus.UNSUBMIT);

        tradeOrderStp2 = this.tradePersistentModel.persistTradeOrder(tradeOrderStp2);
        assertNotNull(tradeOrderStp2);
        /*
         * Update Stop/target orders to Submitted.
         */

        TradestrategyOrders positionOrders = this.tradePersistentModel
                .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());
        for (TradeOrder tradeOrderOca : positionOrders.getTradeOrders()) {
            TradeOrder tradeOrderOcaUnsubmit = this.tradePersistentModel
                    .findTradeOrderByKey(tradeOrderOca.getOrderKey());
            if (tradeOrderOcaUnsubmit.getStatus().equals(OrderStatus.UNSUBMIT)
                    && (null != tradeOrderOcaUnsubmit.getOcaGroupName())) {
                tradeOrderOcaUnsubmit.setStatus(OrderStatus.SUBMITTED);
                tradeOrderOcaUnsubmit = this.tradePersistentModel.persistTradeOrder(tradeOrderOcaUnsubmit);
                assertNotNull(tradeOrderOcaUnsubmit);
            }
        }

        /*
         * Fill the stop orders.
         */
        positionOrders = this.tradePersistentModel
                .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());
        for (TradeOrder tradeOrderOca : positionOrders.getTradeOrders()) {
            TradeOrder tradeOrderOcaSubmit = this.tradePersistentModel
                    .findTradeOrderByKey(tradeOrderOca.getOrderKey());
            if (OrderStatus.SUBMITTED.equals(tradeOrderOcaSubmit.getStatus())
                    && (null != tradeOrderOcaSubmit.getOcaGroupName())) {
                if (OrderType.STP.equals(tradeOrderOcaSubmit.getOrderType())) {
                    Execution executionOCA = new Execution();
                    executionOCA.side(positionOrders.getContract().getTradePosition().getSide());
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
                    tradeOrderOcaSubmit = this.tradePersistentModel.persistTradeOrder(tradeOrderOcaSubmit);

                    for (TradeOrderfill item : tradeOrderOcaSubmit.getTradeOrderfills()) {
                        assertNotNull(item.getId());
                    }
                }
            }
        }
        /*
         * Update Stop/target orders status to filled and cancelled.
         */
        positionOrders = this.tradePersistentModel
                .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());

        for (TradeOrder tradeOrderOca : positionOrders.getTradeOrders()) {
            TradeOrder tradeOrderOcaSubmit = this.tradePersistentModel
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
                tradeOrderOcaSubmit = this.tradePersistentModel.persistTradeOrder(tradeOrderOcaSubmit);
                assertNotNull(tradeOrderOcaSubmit);
                if (!positionOrders.hasOpenTradePosition()) {
                    _log.info("TradePosition closed: ");
                }
            }
        }
    }

    @Test
    public void testPersistTradingday() throws Exception {

        this.tradePersistentModel.persistTradingday(this.tradestrategy.getTradingday());
        assertNotNull(this.tradestrategy.getTradingday().getId());
    }

    @Test
    public void testPersistTradestrategy() throws Exception {

        Tradestrategy result = this.tradePersistentModel.persistAspect(this.tradestrategy);
        assertNotNull(result.getId());
    }

    @Test
    public void testPersistContract() throws Exception {

        Contract result = this.tradePersistentModel.persistContract(this.tradestrategy.getContract());
        assertNotNull(result.getId());
    }

    @Test
    public void testResetDefaultPortfolio() throws Exception {

        this.tradePersistentModel.resetDefaultPortfolio(this.tradestrategy.getPortfolio());
        assertTrue(this.tradestrategy.getPortfolio().getIsDefault());
    }

    @Test
    public void testPersistTradeOrder() throws Exception {

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.MKT, 1000, null, null,
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder.validate();
        TradeOrder result = this.tradePersistentModel.persistTradeOrder(tradeOrder);
        assertNotNull(result.getId());
    }

    @Test
    public void testPersistTradeOrderFilledLong() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrderBuy = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(2)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderBuy.validate();
        tradeOrderBuy = this.tradePersistentModel.persistTradeOrder(tradeOrderBuy);
        tradeOrderBuy.setStatus(OrderStatus.SUBMITTED);
        tradeOrderBuy = this.tradePersistentModel.persistTradeOrder(tradeOrderBuy);

        TradeOrderfill orderfill = new TradeOrderfill(tradeOrderBuy, "Paper", price,
                tradeOrderBuy.getQuantity() / 2, "ISLAND", "1a", price, tradeOrderBuy.getQuantity() / 2,
                this.tradestrategy.getSide(), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill);

        tradeOrderBuy = this.tradePersistentModel.persistTradeOrderfill(tradeOrderBuy);

        TradeOrderfill orderfill1 = new TradeOrderfill(tradeOrderBuy, "Paper", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity(), "BATS", "1b", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity() / 2, this.tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill1);
        tradeOrderBuy.setCommission(new BigDecimal("5.0"));

        tradeOrderBuy = this.tradePersistentModel.persistTradeOrderfill(tradeOrderBuy);

        TradeOrder tradeOrderSell = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.LMT,
                tradeOrderBuy.getQuantity(), null, new BigDecimal("105.00"),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderSell = this.tradePersistentModel.persistTradeOrder(tradeOrderSell);
        tradeOrderSell.setStatus(OrderStatus.SUBMITTED);
        tradeOrderSell.validate();
        tradeOrderSell = this.tradePersistentModel.persistTradeOrder(tradeOrderSell);

        TradeOrderfill orderfill2 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, "ISLAND", "2a", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, this.tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill2);
        tradeOrderSell = this.tradePersistentModel.persistTradeOrderfill(tradeOrderSell);

        TradeOrderfill orderfill3 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity(), "BATS", "2b", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, this.tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill3);
        tradeOrderSell.setCommission(new BigDecimal("5.0"));

        TradeOrder result = this.tradePersistentModel.persistTradeOrderfill(tradeOrderSell);
        assertFalse(result.getTradePosition().isOpen());

        assertEquals((new Money(4000.00)).getBigDecimalValue(), result.getTradePosition().getTotalNetValue());

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
    public void testPersistTradeOrderFilledShort() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrderBuy = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.STPLMT, 1000, price,
                price.subtract(new BigDecimal(2)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderBuy = this.tradePersistentModel.persistTradeOrder(tradeOrderBuy);
        tradeOrderBuy.setStatus(OrderStatus.SUBMITTED);
        tradeOrderBuy.validate();
        tradeOrderBuy = this.tradePersistentModel.persistTradeOrder(tradeOrderBuy);

        TradeOrderfill orderfill = new TradeOrderfill(tradeOrderBuy, "Paper", price,
                tradeOrderBuy.getQuantity() / 2, "ISLAND", "1a", price, tradeOrderBuy.getQuantity() / 2,
                this.tradestrategy.getSide(), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill);

        tradeOrderBuy = this.tradePersistentModel.persistTradeOrderfill(tradeOrderBuy);

        TradeOrderfill orderfill1 = new TradeOrderfill(tradeOrderBuy, "Paper", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity(), "BATS", "1b", tradeOrderBuy.getLimitPrice(),
                tradeOrderBuy.getQuantity() / 2, this.tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderBuy.addTradeOrderfill(orderfill1);
        tradeOrderBuy.setCommission(new BigDecimal("5.0"));

        tradeOrderBuy = this.tradePersistentModel.persistTradeOrderfill(tradeOrderBuy);

        TradeOrder tradeOrderSell = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.LMT,
                tradeOrderBuy.getQuantity(), null, new BigDecimal("95.00"),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrderSell = this.tradePersistentModel.persistTradeOrder(tradeOrderSell);
        tradeOrderSell.setStatus(OrderStatus.SUBMITTED);
        tradeOrderSell = this.tradePersistentModel.persistTradeOrder(tradeOrderSell);

        TradeOrderfill orderfill2 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, "ISLAND", "2a", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, this.tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill2);
        tradeOrderSell = this.tradePersistentModel.persistTradeOrderfill(tradeOrderSell);

        TradeOrderfill orderfill3 = new TradeOrderfill(tradeOrderSell, "Paper", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity(), "BATS", "2b", tradeOrderSell.getLimitPrice(),
                tradeOrderSell.getQuantity() / 2, this.tradestrategy.getSide(),
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrderSell.addTradeOrderfill(orderfill3);
        tradeOrderSell.setCommission(new BigDecimal("5.0"));

        TradeOrder result = this.tradePersistentModel.persistTradeOrderfill(tradeOrderSell);
        assertFalse(result.getTradePosition().isOpen());

        assertEquals((new Money(4000.00)).getBigDecimalValue(), result.getTradePosition().getTotalNetValue());

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
    public void testPersistTradePosition() throws Exception {

        TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        TradePosition result = this.tradePersistentModel.persistAspect(tradePosition);
        assertNotNull(result.getId());
    }

    @Test
    public void testPersistCandleSeries() throws Exception {

        CandleSeries candleSeries = new CandleSeries(this.tradestrategy.getStrategyData().getBaseCandleSeries(),
                BarSize.FIVE_MIN, this.tradestrategy.getTradingday().getOpen(),
                this.tradestrategy.getTradingday().getClose());
        StrategyData.doDummyData(candleSeries, this.tradestrategy.getTradingday(), 5, BarSize.FIVE_MIN, true, 0);
        long timeStart = System.currentTimeMillis();
       // this.tradePersistentModel.persistCandleSeries(candleSeries);
        _log.info("Total time: {}", (System.currentTimeMillis() - timeStart) / 1000);
        assertFalse(candleSeries.isEmpty());
        assertNotNull(((CandleItem) candleSeries.getDataItem(0)).getCandle().getId());
    }

    @Test
    public void testPersistCandle() throws Exception {

        ZonedDateTime date = TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone());
        CandleItemUI candleItem = new CandleItemUI(this.tradestrategy.getContract(), this.tradestrategy.getTradingday(),
                new CandlePeriod(date, 300), 100.23, 100.23, 100.23, 100.23, 10000000L, 100.23, 100, date);
        Candle candle = this.tradePersistentModel.persistCandle(candleItem.getCandle());
        assertNotNull(candle.getId());
    }

    @Test
    public void testFindAccountById() throws Exception {

        Portfolio result = this.tradePersistentModel
                .findPortfolioById(this.tradestrategy.getPortfolio().getId());
        assertNotNull(result);
    }

    @Test
    public void testFindAccountByNumber() throws Exception {

        Account result = this.tradePersistentModel
                .findAccountByNumber(this.tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber());
        assertNotNull(result);
    }

    @Test
    public void testFindContractById() throws Exception {

        Contract result = this.tradePersistentModel
                .findContractById(this.tradestrategy.getContract().getId());
        assertNotNull(result);
    }

    @Test
    public void testFindContractByUniqueKey() throws Exception {

        Contract result = this.tradePersistentModel.findContractByUniqueKey(
                this.tradestrategy.getContract().getSecType(), this.tradestrategy.getContract().getSymbol(),
                this.tradestrategy.getContract().getExchange(), this.tradestrategy.getContract().getCurrency(),
                null);
        assertNotNull(result);
    }

    @Test
    public void testFindTradestrategyByTradestrategy() throws Exception {

        Tradestrategy result = this.tradePersistentModel.findTradestrategyById(this.tradestrategy);
        assertNotNull(result);
    }

    @Test
    public void testFindTradestrategyById() throws Exception {

        Tradestrategy result = this.tradePersistentModel
                .findTradestrategyById(this.tradestrategy.getId());
        assertNotNull(result);
    }

    @Test
    public void testFindTradestrategyByUniqueKeys() throws Exception {

        Tradestrategy result = this.tradePersistentModel.findTradestrategyByUniqueKeys(
                this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getStrategy().getName(),
                this.tradestrategy.getContract().getId(), this.tradestrategy.getPortfolio().getName());
        assertNotNull(result);
    }

    @Test
    public void testFindAllTradestrategies() throws Exception {

        List<Tradestrategy> result = this.tradePersistentModel.findAllTradestrategies();
        assertNotNull(result);
    }

    @Test
    public void testFindTradePositionById() throws Exception {

        TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        TradePosition resultTrade = this.tradePersistentModel.persistAspect(tradePosition);
        TradePosition result = this.tradePersistentModel.findTradePositionById(resultTrade.getId());
        assertNotNull(result);
    }

    @Test
    public void testFindPositionOrdersByTradestrategyId() throws Exception {

        TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

        TradePosition resultTrade = this.tradePersistentModel.persistAspect(tradePosition);
        resultTrade.getContract().setTradePosition(resultTrade);
        this.tradePersistentModel.persistAspect(resultTrade.getContract());

        assertNotNull(resultTrade);
        TradestrategyOrders result = this.tradePersistentModel
                .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());
        assertNotNull(result);
        resultTrade.getContract().setTradePosition(null);
        this.tradePersistentModel.persistAspect(resultTrade.getContract());
    }

    @Test
    public void testRefreshPositionOrdersByTradestrategyId() throws Exception {

        TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        this.tradestrategy.getContract().setTradePosition(tradePosition);
        TradePosition resultTrade = this.tradePersistentModel.persistAspect(tradePosition);
        assertNotNull(resultTrade);
        TradestrategyOrders positionOrders = this.tradePersistentModel
                .findPositionOrdersByTradestrategyId(this.tradestrategy.getId());

        _log.info("testFindVersionById IdTradeStrategy:{} version: {}", positionOrders.getId(), positionOrders.getVersion());

        positionOrders.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
        TradestrategyOrders result = this.tradePersistentModel.persistAspect(positionOrders);

        _log.info("testFindVersionById IdTradeStrategy:{} version: {}", result.getId(), result.getVersion());
        result = this.tradePersistentModel.refreshPositionOrdersByTradestrategyId(positionOrders);
        _log.info("testFindVersionById IdTradeStrategy:{} prev version: {} current version: {}", result.getId(), positionOrders.getVersion(), result.getVersion());

        assertNotNull(result);
    }

    @Test
    public void testRemoveTradingdayTradeOrders() throws Exception {

        TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        this.tradePersistentModel.persistAspect(tradePosition);
        Tradingday result = this.tradePersistentModel
                .findTradingdayById(this.tradestrategy.getTradingday().getId());
        assertNotNull(result);
        this.tradePersistentModel.removeTradingdayTradeOrders(result);
    }

    @Test
    public void testRemoveTradestrategyTradeOrders() throws Exception {

        TradePosition tradePosition = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);
        this.tradePersistentModel.persistAspect(tradePosition);
        Tradestrategy result = this.tradePersistentModel
                .findTradestrategyById(this.tradestrategy.getId());
        assertNotNull(result);
        this.tradePersistentModel.removeTradestrategyTradeOrders(result);
    }

    @Test
    public void testFindTradeOrderById() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        TradeOrder resultTradeOrder = this.tradePersistentModel.persistTradeOrder(tradeOrder);
        TradeOrder result = this.tradePersistentModel.findTradeOrderById(resultTradeOrder.getId());
        assertNotNull(result);
    }

    @Test
    public void testFindTradeOrderByKey() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        TradeOrder resultTradeOrder = this.tradePersistentModel.persistTradeOrder(tradeOrder);
        TradeOrder result = this.tradePersistentModel.findTradeOrderByKey(resultTradeOrder.getOrderKey());
        assertNotNull(result);
    }

    @Test
    public void testFindTradeOrderfillByExecId() throws Exception {

        BigDecimal price = new BigDecimal("100.00");
        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.STPLMT, 1000, price,
                price.add(new BigDecimal(4)), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        TradeOrderfill tradeOrderfill = new TradeOrderfill(tradeOrder, "Paper", new BigDecimal("100.23"),
                1000, Exchange.SMART, "123efgr567", new BigDecimal("100.23"), 1000,
                Side.BOT, TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.addTradeOrderfill(tradeOrderfill);
        TradeOrder resultTradeOrder = this.tradePersistentModel.persistTradeOrder(tradeOrder);
        TradeOrderfill result = this.tradePersistentModel
                .findTradeOrderfillByExecId(resultTradeOrder.getTradeOrderfills().getFirst().getExecId());
        assertNotNull(result);
    }

    @Test
    public void testFindTradeOrderByMaxKey() throws Exception {

        Integer result = this.tradePersistentModel.findTradeOrderByMaxKey();
        assertNotNull(result);
    }

    @Test
    public void testFindTradingdayById() throws Exception {

        Tradingday result = this.tradePersistentModel
                .findTradingdayById(this.tradestrategy.getTradingday().getId());
        assertNotNull(result);
    }

    @Test
    public void testFindTradingdayByOpenDate() throws Exception {

        Tradingday result = this.tradePersistentModel.findTradingdayByOpenCloseDate(
                this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getTradingday().getClose());
        assertNotNull(result);
    }

    @Test
    public void testFindTradingdaysByDateRange() throws Exception {

        Tradingdays result = this.tradePersistentModel.findTradingdaysByDateRange(
                this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getTradingday().getOpen());
        assertNotNull(result);
    }

    @Test
    public void testFindTradestrategyDistinctByDateRange() {

        List<Tradestrategy> result = this.tradePersistentModel.findTradestrategyDistinctByDateRange(
                this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getTradingday().getOpen());
        assertNotNull(result);
    }

    @Test
    public void testFindTradelogReport() throws Exception {

        TradelogReport result = this.tradePersistentModel.findTradelogReport(this.tradestrategy.getPortfolio(),
                TradingCalendar.getYearStart(), this.tradestrategy.getTradingday().getClose(), true, null,
                new BigDecimal(0));
        assertNotNull(result);
    }

    @Test
    public void testFindCandlesByContractAndDateRange() throws Exception {

        List<Candle> result = this.tradePersistentModel.findCandlesByContractDateRangeBarSize(
                this.tradestrategy.getContract().getId(), this.tradestrategy.getTradingday().getOpen(),
                this.tradestrategy.getTradingday().getClose(), this.tradestrategy.getBarSize());
        assertNotNull(result);
    }

    @Test
    public void testFindCandleCount() throws Exception {

        Long result = this.tradePersistentModel.findCandleCount(
                this.tradestrategy.getTradingday().getId(),
                this.tradestrategy.getContract().getId());
        assertNotNull(result);
    }

    @Test
    public void testPersistRule() throws Exception {

        Integer version = this.tradePersistentModel.findRuleByMaxVersion(this.tradestrategy.getStrategy()) + 1;
        Rule rule = new Rule(this.tradestrategy.getStrategy(), version, "Test",
                TradingCalendar.getDateTimeNowMarketTimeZone(), TradingCalendar.getDateTimeNowMarketTimeZone());
        Aspect result = this.tradePersistentModel.persistAspect(rule);
        assertNotNull(result);
        this.tradePersistentModel.removeAspect(rule);
    }

    @Test
    public void testFindRuleById() throws Exception {

        Integer version = this.tradePersistentModel.findRuleByMaxVersion(this.tradestrategy.getStrategy()) + 1;
        Rule rule = new Rule(this.tradestrategy.getStrategy(), version, "Test",
                TradingCalendar.getDateTimeNowMarketTimeZone(), TradingCalendar.getDateTimeNowMarketTimeZone());
        Aspect resultAspect = this.tradePersistentModel.persistAspect(rule);
        assertNotNull(resultAspect);
        Rule result = this.tradePersistentModel.findRuleById(resultAspect.getId());
        assertNotNull(result);
        this.tradePersistentModel.removeAspect(rule);
    }

    @Test
    public void testFindRuleByMaxVersion() throws Exception {

        Integer result = this.tradePersistentModel.findRuleByMaxVersion(this.tradestrategy.getStrategy());
        assertNotNull(result);
    }

    @Test
    public void testFindStrategyById() throws Exception {

        Strategy result = this.tradePersistentModel
                .findStrategyById(this.tradestrategy.getStrategy().getId());
        assertNotNull(result);
    }

    @Test
    public void testFindStrategyByName() throws Exception {

        Strategy result = this.tradePersistentModel.findStrategyByName(this.tradestrategy.getStrategy().getName());
        assertNotNull(result);
    }

    @Test
    public void testFindCodeTypeByNameType() throws Exception {

        String indicatorName = IndicatorSeriesUI.MovingAverageSeries.substring(0,
                IndicatorSeriesUI.MovingAverageSeries.indexOf("Series"));
        CodeType result = this.tradePersistentModel.findCodeTypeByNameType(indicatorName,
                CodeType.IndicatorParameters);
        assertNotNull(result);
    }

    @Test
    public void testRemoveRule() throws Exception {

        Integer version = this.tradePersistentModel.findRuleByMaxVersion(this.tradestrategy.getStrategy()) + 1;
        Rule rule = new Rule(this.tradestrategy.getStrategy(), version, "Test",
                TradingCalendar.getDateTimeNowMarketTimeZone(), TradingCalendar.getDateTimeNowMarketTimeZone());
        Rule resultAspect = this.tradePersistentModel.persistAspect(rule);
        assertNotNull(resultAspect);
        this.tradePersistentModel.removeAspect(resultAspect);
    }

    @Test
    public void testFindStrategies() throws Exception {

        List<Strategy> result = this.tradePersistentModel.findStrategies();
        assertNotNull(result);
    }

    @Test
    public void testFindAspectsByClassName() throws Exception {

        Aspects result = this.tradePersistentModel.findAspectsByClassName(this.tradestrategy.getClass().getName());
        assertNotNull(result);
    }

    @Test
    public void testFindAspectsByClassNameFieldName() throws Exception {

        for (IIndicatorDataset indicator : this.tradestrategy.getStrategyData().getIndicators()) {
            IndicatorSeries series = indicator.getSeries(0);
            String indicatorName = series.getType().substring(0, series.getType().indexOf("Series"));
            Aspects result = this.tradePersistentModel.findAspectsByClassNameFieldName(CodeType.class.getName(),
                    "name", indicatorName);
            assertNotNull(result);
        }
    }

    @Test
    public void testFindAspectById() throws Exception {

        Aspect result = this.tradePersistentModel.findAspectById(this.tradestrategy);
        assertNotNull(result);
    }

    @Test
    public void testPersistAspect() throws Exception {

        Aspect result = this.tradePersistentModel.persistAspect(this.tradestrategy);
        assertNotNull(result);
    }

    @Test
    public void testRemoveAspect() throws Exception {

        this.tradePersistentModel.removeAspect(this.tradestrategy);
        assertThrows(PersistentModelException.class,
                () -> {
                    this.tradePersistentModel.findAspectById(this.tradestrategy);
                });


    }

    @Test
    public void testReassignStrategy() throws Exception {

        Tradingday tradingday = this.tradePersistentModel
                .findTradingdayById(this.tradestrategy.getTradingday().getId());
        assertFalse(tradingday.getTradestrategies().isEmpty());
        Strategy toStrategy = (Strategy) DAOStrategy.newInstance().getObject();
        toStrategy = this.tradePersistentModel.findStrategyById(toStrategy.getId());
        this.tradePersistentModel.reassignStrategy(this.tradestrategy.getStrategy(), toStrategy, tradingday);
        assertEquals(toStrategy, tradingday.getTradestrategies().getFirst().getStrategy());
    }

    @Test
    public void testReplaceTradingday() throws Exception {

        Tradingdays tradingdays = new Tradingdays();

        Tradingday instance1 = tradePersistentModel
                .findTradingdayById(this.tradestrategy.getTradingday().getId());
        tradingdays.add(instance1);

        TradingdayTableModel tradingdayModel = new TradingdayTableModel();
        tradingdayModel.setData(tradingdays);
        TradingdayTable tradingdayTable = new TradingdayTable(tradingdayModel);
        tradingdayTable.setRowSelectionInterval(0, 0);

        this.tradestrategy.getContract().setIndustry("Computer");
        Contract result = this.tradePersistentModel.persistContract(this.tradestrategy.getContract());
        assertNotNull(result);
        Tradingday instance2 = tradePersistentModel
                .findTradingdayById(this.tradestrategy.getTradingday().getId());
        tradingdays.replaceTradingday(instance2);
        int selectedRow = tradingdayTable.getSelectedRow();
        tradingdayModel.setData(tradingdays);
        if (selectedRow > -1) {
            tradingdayTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        org.trade.core.valuetype.Date openDate = (org.trade.core.valuetype.Date) tradingdayModel
                .getValueAt(tradingdayTable.convertRowIndexToModel(0), 0);
        org.trade.core.valuetype.Date closeDate = (org.trade.core.valuetype.Date) tradingdayModel
                .getValueAt(tradingdayTable.convertRowIndexToModel(0), 1);
        Tradingday transferObject = tradingdayModel.getData().getTradingday(openDate.getZonedDateTime(),
                closeDate.getZonedDateTime());
        assertNotNull(transferObject);

        assertNotNull(tradingdays.getTradingday(instance1.getOpen(), instance1.getClose()));
        String industry = transferObject.getTradestrategies().getFirst().getContract().getIndustry();
        assertNotNull("4", industry);
    }
}
