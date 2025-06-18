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
package org.trade.core.broker;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trade.core.broker.client.Broker;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyBase;
import org.trade.core.persistent.dao.series.indicator.IndicatorSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.movingaverage.MovingAverageItem;
import org.trade.core.persistent.dao.series.indicator.vwap.VwapItem;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BrokerModelIT extends TradestrategyBase implements IBrokerChangeListener {

    private final static Logger _log = LoggerFactory.getLogger(BrokerModelIT.class);

    @Autowired
    private TradeService tradeService;

    private IBrokerModel backTestbrokerModel;
    private final BigDecimal price = new BigDecimal("108.85");
    private Tradestrategy tradestrategy = null;
    private static Integer port = null;
    private static String host = null;
    private static Integer clientId;
    private static Timer timer = null;
    private boolean connectionFailed = false;
    private static AtomicInteger timerRunning = null;
    private final static Object lockCoreUtilsTest = new Object();
    private final static String _broker = IBrokerModel._brokerTest;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

        TradeAppLoadConfig.loadAppProperties();
        clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        port = Integer.valueOf(ConfigProperties.getPropAsString("trade.tws.port"));
        host = ConfigProperties.getPropAsString("trade.tws.host");

        timer = new Timer(250, _ -> {
            synchronized (lockCoreUtilsTest) {
                timerRunning.addAndGet(250);
                lockCoreUtilsTest.notifyAll();
            }
        });
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        String symbol = "NVDA";
        this.tradestrategy = this.getTestTradestrategy(tradeService, symbol);
        backTestbrokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(_broker, BrokerModelIT.class);
        backTestbrokerModel.onConnect(host, port, clientId);
        assertNotNull(this.tradestrategy);

        backTestbrokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(_broker, BrokerModelIT.class);
        backTestbrokerModel.onConnect(host, port, clientId);

        timerRunning = new AtomicInteger(0);
        timer.start();
        // Note isConnected always returns false for the
        // BackTestBrokerModel.
        synchronized (lockCoreUtilsTest) {
            while (backTestbrokerModel.isConnected() && !connectionFailed) {
                lockCoreUtilsTest.wait();
            }
        }
        timer.stop();
        if (backTestbrokerModel.isConnected()) {
            _log.warn("Could not connect to TWS test will be ignored. Connected: {}", backTestbrokerModel.isConnected());
        }
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        // Wait for the BackTestBroker to complete. These tests use the testing
        // client from org.trade.brokerclient that runs its own thread.
        Broker backTestBroker = backTestbrokerModel.getBackTestBroker(this.tradestrategy.getId());
        if (null != backTestBroker) {
            // Ping the broker to see if its completed. Not isConnected always
            // returns false for BackTestBrokerModel.
            timer.start();
            synchronized (lockCoreUtilsTest) {

                while (!backTestbrokerModel.isConnected() && !connectionFailed && !backTestBroker.isDone()) {

                    lockCoreUtilsTest.wait();
                }
            }
            timer.stop();
        }

        if (backTestbrokerModel.isConnected()) {
            backTestbrokerModel.onDisconnect();
        }
        this.clearDBData(tradeService);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void testSubmitBuyOrder() throws Exception {

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.STPLMT, 100, price,
                price.add(new BigDecimal("0.02")), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setClientId(clientId);
        tradeOrder.setTransmit(true);
        tradeOrder.setStatus(OrderStatus.UNSUBMIT);

        tradeOrder = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder);

        _log.info("IdTradeOrder: {} OrderKey: {}", tradeOrder.getId(), tradeOrder.getOrderKey());
        assertNotNull(tradeOrder);
    }

    @Test
    public void testSubmitSellShortOrder() throws Exception {

        _log.info("Symbol: {}", this.tradestrategy.getContract().getSymbol());

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.STPLMT, 100,
                price.subtract(new BigDecimal("0.70")), price.subtract(new BigDecimal("0.73")),
                TradingCalendar.getDateTimeNowMarketTimeZone());

        tradeOrder.setClientId(clientId);
        tradeOrder.setTransmit(true);
        tradeOrder.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder);

        _log.info("IdTradeOrder: {} OrderKey: {}", tradeOrder.getId(), tradeOrder.getOrderKey());
        assertNotNull(tradeOrder.getId());
    }

    @Test
    public void testSubmitComboOrder() throws Exception {

        String ocaID = Integer.toString((BigDecimal.valueOf(Math.random() * 1000000)).intValue());

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.LMT, 50, null,
                price.add(new BigDecimal("1.0")), TradingCalendar.getDateTimeNowMarketTimeZone());

        tradeOrder.setClientId(clientId);
        tradeOrder.setOcaType(2);
        tradeOrder.setOcaGroupName(ocaID);
        tradeOrder.setTransmit(true);
        tradeOrder.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder);
        assertNotNull(tradeOrder.getId());

        TradeOrder tradeOrder1 = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.LMT, 50,
                price.subtract(new BigDecimal("1.0")), price.add(new BigDecimal("2.0")),
                TradingCalendar.getDateTimeNowMarketTimeZone());

        tradeOrder1.setClientId(clientId);
        tradeOrder1.setOcaType(2);
        tradeOrder1.setOcaGroupName(ocaID);
        tradeOrder1.setTransmit(false);
        tradeOrder1.setStatus(OrderStatus.UNSUBMIT);

        tradeOrder1 = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder1);
        assertNotNull(tradeOrder1.getId());

        TradeOrder tradeOrder2 = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.STP, 50,
                price.subtract(new BigDecimal("1.0")), null, TradingCalendar.getDateTimeNowMarketTimeZone());
        ocaID = ocaID + "abc";
        tradeOrder2.setClientId(clientId);
        tradeOrder2.setOcaType(2);
        tradeOrder2.setOcaGroupName(ocaID);
        tradeOrder2.setTransmit(true);
        tradeOrder2.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder2 = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder2);
        assertNotNull(tradeOrder2.getId());

        TradeOrder tradeOrder3 = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.STP, 50,
                price.subtract(new BigDecimal("2.0")), null, TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder3.setClientId(clientId);
        tradeOrder3.setOcaType(2);
        tradeOrder3.setOcaGroupName(ocaID);
        tradeOrder3.setTransmit(false);
        tradeOrder3.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder3 = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder3);
        assertNotNull(tradeOrder3.getId());
        _log.info("IdTradeOrder: {} OrderKey2: {} OrderKey2 Price: {} OrderKey3: {} OrderKey3 Price: {}", tradeOrder3.getId(), tradeOrder2.getOrderKey(), tradeOrder2.getLimitPrice(), tradeOrder3.getOrderKey(), tradeOrder3.getAuxPrice());
        // Update the Stop price
        tradeOrder2.setAuxPrice(price.subtract(new BigDecimal("0.9")));
        tradeOrder2.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder2 = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder2);

        tradeOrder3.setAuxPrice(price.subtract(new BigDecimal("0.9")));
        tradeOrder3.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder3 = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder3);
        _log.info("IdTradeOrder: {} OrderKey2: {} OrderKey2 Price: {} OrderKey3: {} OrderKey3 Price: {}", tradeOrder3.getId(), tradeOrder2.getOrderKey(), tradeOrder2.getLimitPrice(), tradeOrder3.getOrderKey(), tradeOrder3.getAuxPrice());

        tradeOrder3.setTransmit(true);
        tradeOrder3.setStatus(OrderStatus.UNSUBMIT);
        tradeOrder3 = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder3);

        _log.info("IdTradeOrder: {} OrderKey: {}", tradeOrder2.getId(), tradeOrder3.getOrderKey());
        assertNotNull(tradeOrder3);
    }

    @Test
    public void testOnBrokerData() throws Exception {

        StrategyData.doDummyData(this.tradestrategy.getStrategyData().getCandleDataset().getSeries(0),
                tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), true, 0);
        backTestbrokerModel.setBrokerDataOnly(true);
        backTestbrokerModel.onBrokerData(tradestrategy, tradestrategy.getTradingday().getClose());

        assertFalse(this.tradestrategy.getStrategyData().getCandleDataset().getSeries(0).isEmpty());

        IndicatorSeries candleseries = this.tradestrategy.getStrategyData().getCandleDataset().getSeries(0);
        IndicatorSeries sma1Series = this.tradestrategy.getStrategyData()
                .getIndicatorByType(IndicatorSeries.MovingAverageSeries).getSeries(0);
        IndicatorSeries sma2Series = this.tradestrategy.getStrategyData()
                .getIndicatorByType(IndicatorSeries.MovingAverageSeries).getSeries(1);
        IndicatorSeries vwapSeries = this.tradestrategy.getStrategyData()
                .getIndicatorByType(IndicatorSeries.VwapSeries).getSeries(0);
        IndicatorSeries heikinAshiSeries = this.tradestrategy.getStrategyData()
                .getIndicatorByType(IndicatorSeries.HeikinAshiSeries).getSeries(0);

        for (int i = 0; i < candleseries.getItemCount(); i++) {
            CandleItem candle = (CandleItem) candleseries.getDataItem(i);
            RegularTimePeriod period = candle.getPeriod();

            CandleItem heikinAshiCandle = null;
            VwapItem vwap = null;
            MovingAverageItem sma1 = null;
            MovingAverageItem sma2 = null;

            int b = heikinAshiSeries.indexOf(period);
            if (b > -1) {
                heikinAshiCandle = (CandleItem) heikinAshiSeries.getDataItem(b);
            }

            int c = vwapSeries.indexOf(period.getMiddleMillisecond());
            if (c > -1) {
                vwap = (VwapItem) vwapSeries.getDataItem(c);
            }
            int d = sma1Series.indexOf(period.getMiddleMillisecond());
            if (d > -1) {
                sma1 = (MovingAverageItem) sma1Series.getDataItem(d);
            }
            int e = sma2Series.indexOf(period.getMiddleMillisecond());
            if (e > -1) {
                sma2 = (MovingAverageItem) sma2Series.getDataItem(e);
            }
            _log.info("    Period Start: {} Period End: {} H: {} L: {} O: {} C: {} Vol: {} Vwap: {}", period.getStart(), period.getEnd(), new Money(candle.getHigh()), new Money(candle.getLow()), new Money(candle.getOpen()), new Money(candle.getClose()), new Money(candle.getVolume()), new Money(candle.getVwap()));
            if (null != heikinAshiCandle) {
                _log.info("HA  Period Start: {} Period End: {} HA H: {} HA L: {} HA O: {} HA C: {} HA Vol: {} HA Vwap: {}", period.getStart(), period.getEnd(), new Money(heikinAshiCandle.getHigh()), new Money(heikinAshiCandle.getLow()), new Money(heikinAshiCandle.getOpen()), new Money(heikinAshiCandle.getClose()), new Money(heikinAshiCandle.getVolume()), new Money(heikinAshiCandle.getVwap()));
            }
            if (null != vwap) {
                _log.info("Vwp Period Start: {} Vwap: {}", period, new Money(vwap.getY()));
            }
            if (null != sma1) {
                _log.info("S8  Period Start: {} Sma 8: {}", period, new Money(sma1.getY()));
            }
            if (null != sma2) {
                _log.info("S20 Period Start: {} Sma 20: {}", period, new Money(sma2.getY()));
            }
        }
    }

    @Test
    public void testOnConnect() {

        backTestbrokerModel.onConnect(host, port, clientId);
        assertFalse(backTestbrokerModel.isConnected());
    }

    @Test
    public void testDisconnect() {

        backTestbrokerModel.onDisconnect();
        assertFalse(backTestbrokerModel.isConnected());
    }

    @Test
    public void testGetNextRequestId() {

        Integer id = backTestbrokerModel.getNextRequestId();
        assertNotNull(id);
    }

    @Test
    public void testOnSubscribeAccountUpdates() throws Exception {

        backTestbrokerModel.onSubscribeAccountUpdates(true,
                tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber());
        assertFalse(backTestbrokerModel
                .isAccountUpdatesRunning(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber()));
    }

    @Test
    public void testOnCancelAccountUpdates() throws Exception {

        backTestbrokerModel.onSubscribeAccountUpdates(true,
                tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber());
        backTestbrokerModel
                .onCancelAccountUpdates(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber());
        assertFalse(backTestbrokerModel
                .isAccountUpdatesRunning(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber()));
    }

    @Test
    public void testOnReqManagedAccount() throws Exception {

        backTestbrokerModel.onReqManagedAccount();
        assertFalse(backTestbrokerModel
                .isAccountUpdatesRunning(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber()));
    }

    @Test
    public void testOnReqAllOpenOrders() throws Exception {

        backTestbrokerModel.onReqAllOpenOrders();
    }

    @Test
    public void testOnReqOpenOrders() throws Exception {

        backTestbrokerModel.onReqOpenOrders();
    }

    @Test
    public void testOnReqRealTimeBars() throws Exception {

        this.tradestrategy.getContract().addTradestrategy(this.tradestrategy);
        backTestbrokerModel.onReqRealTimeBars(this.tradestrategy.getContract(), false);
        assertFalse(backTestbrokerModel.isRealtimeBarsRunning(tradestrategy));
    }

    @Test
    public void testOnReqAllExecutions() throws Exception {

        backTestbrokerModel.onReqAllExecutions(this.tradestrategy.getTradingday().getOpen());
    }

    @Test
    public void testOnReqExecutions() throws Exception {

        backTestbrokerModel.onReqExecutions(this.tradestrategy, false);

    }

    @Test
    public void testIsRealtimeBarsRunning() {

        backTestbrokerModel.onCancelRealtimeBars(this.tradestrategy);
        assertFalse(backTestbrokerModel.isRealtimeBarsRunning(this.tradestrategy));

    }

    @Test
    public void testIsAccountUpdatesRunning() {

        backTestbrokerModel
                .onCancelAccountUpdates(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber());
        assertFalse(backTestbrokerModel
                .isAccountUpdatesRunning(tradestrategy.getPortfolio().getIndividualAccount().getAccountNumber()));
    }

    @Test
    public void testIsHistoricalDataRunningTradestrategy() {

        backTestbrokerModel.onCancelBrokerData(this.tradestrategy);
        assertFalse(backTestbrokerModel.isHistoricalDataRunning(this.tradestrategy));
    }

    @Test
    public void testIsHistoricalDataRunningContract() {

        backTestbrokerModel.onCancelBrokerData(this.tradestrategy.getContract());
        assertFalse(backTestbrokerModel.isHistoricalDataRunning(this.tradestrategy.getContract()));
    }

    @Test
    public void testOnCancelAllRealtimeData() {

        backTestbrokerModel.onCancelAllRealtimeData();
        assertFalse(backTestbrokerModel.isRealtimeBarsRunning(this.tradestrategy));
    }

    @Test
    public void testOnCancelRealtimeBars() {

        backTestbrokerModel.onCancelRealtimeBars(this.tradestrategy);
        assertFalse(backTestbrokerModel.isRealtimeBarsRunning(this.tradestrategy));
    }

    @Test
    public void testOnCancelBrokerData() {

        backTestbrokerModel.onCancelBrokerData(this.tradestrategy);
        assertFalse(backTestbrokerModel.isHistoricalDataRunning(this.tradestrategy));
    }

    @Test
    public void testOnCancelContractDetails() {

        backTestbrokerModel.onCancelContractDetails(this.tradestrategy.getContract());
    }

    @Test
    public void testOnContractDetails() throws Exception {

        backTestbrokerModel.onContractDetails(this.tradestrategy.getContract());
    }

    @Test
    public void testGetHistoricalData() {

        ConcurrentHashMap<Integer, Tradestrategy> historicalDataList = backTestbrokerModel.getHistoricalData();
        assertNotNull(historicalDataList);
    }

    @Test
    public void testOnPlaceOrder() throws Exception {

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.MKT, 1000, null, null,
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder);
        assertNotNull(tradeOrder);
    }

    @Test
    public void testOnCancelOrder() throws Exception {

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, Action.BUY, OrderType.MKT, 1000, null, null,
                TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder = backTestbrokerModel.onPlaceOrder(this.tradestrategy.getContract(), tradeOrder);
        assertNotNull(tradeOrder);
        backTestbrokerModel.onCancelOrder(tradeOrder);
    }

    @Test
    public void testIsBrokerDataOnly() {

        boolean result = backTestbrokerModel.isBrokerDataOnly();
        assertFalse(result);
    }

    public void connectionOpened() {
        _log.info("Connection opened");
    }

    public void connectionClosed(boolean forced) {
        connectionFailed = true;
        _log.info("Connection closed");
    }

    /**
     * Method executionDetailsEnd.
     *
     * @param execDetails ConcurrentHashMap<Integer,TradeOrder>
     */
    public void executionDetailsEnd(ConcurrentHashMap<Integer, TradeOrder> execDetails) {

    }

    /**
     * Method historicalDataComplete.
     *
     * @param tradestrategy Tradestrategy
     */
    public void historicalDataComplete(Tradestrategy tradestrategy) {
    }

    /**
     * Method managedAccountsUpdated.
     *
     * @param accountNumber String
     */
    public void managedAccountsUpdated(String accountNumber) {

    }

    /**
     * Method fAAccountsCompleted. Notifies all registered listeners that the
     * brokerManagerModel has received all FA Accounts information.
     */
    public void fAAccountsCompleted() {

    }

    /**
     * Method updateAccountTime.
     *
     * @param accountNumber String
     */
    public void updateAccountTime(String accountNumber) {

    }

    /**
     * Method brokerError.
     *
     * @param ex BrokerModelException
     */
    public void brokerError(BrokerModelException ex) {

        if (502 == ex.getErrorCode()) {
            _log.info("TWS is not running test will not be run");
            return;
        }
        if (ex.getErrorId() == 1) {
            _log.error("Error: {}, msg: {}, ex: {}", ex.getErrorCode(), ex.getMessage(), ex);
        } else if (ex.getErrorId() == 2) {
            _log.warn("Warning: {}", ex.getMessage());
        } else if (ex.getErrorId() == 3) {
            _log.info("Information: {}", ex.getMessage());
        } else {
            _log.error("Unknown Error Id Code: {}, msg: {}, ex:: {}", ex.getErrorCode(), ex.getMessage(), ex);
        }
    }

    /**
     * Method tradeOrderFilled.
     *
     * @param tradeOrder TradeOrder
     */
    public void tradeOrderFilled(TradeOrder tradeOrder) {

    }

    /**
     * Method tradeOrderCancelled.
     *
     * @param tradeOrder TradeOrder
     */
    public void tradeOrderCancelled(TradeOrder tradeOrder) {

    }

    /**
     * Method tradeOrderStatusChanged.
     *
     * @param tradeOrder TradeOrder
     */
    public void tradeOrderStatusChanged(TradeOrder tradeOrder) {

    }

    /**
     * Method positionClosed.
     *
     * @param trade Trade
     */
    public void positionClosed(TradePosition trade) {

    }

    /**
     * Method openOrderEnd.
     *
     * @param openOrders ConcurrentHashMap<Integer,TradeOrder>
     */
    public void openOrderEnd(ConcurrentHashMap<Integer, TradeOrder> openOrders) {

    }
}
