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
package org.trade.core.strategy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.broker.BackTestBrokerModel;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Entrylimit;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.strategy.AbstractStrategyRule;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.DAOEntryLimit;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.OverrideConstraints;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TradestrategyStatus;
import org.trade.core.valuetype.TriggerMethod;

import java.io.File;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class AbstractStrategyIT {

    private final static Logger _log = LoggerFactory.getLogger(AbstractStrategyIT.class);

    @Autowired
    private TradeService tradeService;

    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static Tradestrategy tradestrategy;
    private static IBrokerModel brokerModel;
    private static String templateName;
    private static String strategyDir;
    private static StrategyRuleTest strategyProxy;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        List<Object> param = new ArrayList<>();
        param.add(tradeService);
        brokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._brokerTest, param, this);
        templateName = ConfigProperties.getPropAsString("trade.strategy.template");
        strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
        Integer clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        Integer port = Integer.valueOf(ConfigProperties.getPropAsString("trade.tws.port"));
        String host = ConfigProperties.getPropAsString("trade.tws.host");
        brokerModel.onConnect(host, port, clientId);

        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.HOUR_MIN);
        assertNotNull(tradestrategy);

        strategyProxy = new StrategyRuleTest(tradeService, brokerModel, tradestrategy.getStrategyData(),
                tradestrategy.getId());
        assertNotNull(strategyProxy);
        strategyProxy.execute();

        while (!strategyProxy.isWaiting()) {

            Thread.sleep(250);
        }
        _log.info(" Test Initialized");
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        brokerModel.onDisconnect();
        strategyProxy.cancel();
        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void entryRuleNoEntryByRT() {

        try {

            tradestrategy.setTrade(true);
            List<Object> param = new ArrayList<>();
            param.add(tradeService);
            param.add(brokerModel);
            param.add(tradestrategy.getStrategyData());
            param.add(tradestrategy.getId());
            DynamicCode dynacode = new DynamicCode();
            dynacode.addSourceDir(new File(strategyDir));
            IStrategyRule strategyProxy = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                    IStrategyRule.PACKAGE + templateName, param);
            strategyProxy.execute();

            while (!strategyProxy.isWaiting()) {

                Thread.sleep(250);
            }

            tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), Side.BOT.equals(tradestrategy.getSide()), 250);
            strategyProxy.cancel();
        } catch (Exception ex) {

            fail("Failed entryRuleNoEntryByRT msg: " + ex.getMessage());
        }
    }

    @Test
    public void entryRuleMoveStopToBE() throws Exception {

        Money price = new Money(37.99);
        TradeOrder openOrder = strategyProxy.createRiskOpenPosition(Action.BUY, price,
                price.subtract(new Money(0.2)), true, null, null, null, null);

        TradeOrderfill execution = new TradeOrderfill();
        execution.setTradeOrder(openOrder);
        execution.setTime(TradingCalendar.getDateTimeNowMarketTimeZone());
        execution.setExchange("SMART");
        execution.setSide(Side.BOT);
        execution.setQuantity(openOrder.getQuantity());
        execution.setAveragePrice(price.getBigDecimalValue());
        execution.setPrice(price.getBigDecimalValue());
        execution.setCumulativeQuantity(openOrder.getQuantity());
        execution.setCommission(new BigDecimal(0));

        ((BackTestBrokerModel) brokerModel).execDetails(openOrder.getOrderKey(), tradestrategy.getContract(),
                execution);
        this.reFreshPositionOrders();

        assertNotNull(strategyProxy.getOpenPositionOrder());
        /*
         * Position has been open submit the target and stop orders.
         */
        if (strategyProxy.isThereOpenPosition()) {
            if (null != strategyProxy.getOpenTradePosition().getOpenQuantity()) {
                /*
                 * Position has been opened submit the target and stop
                 * orders. Two targets at 3R and 6R
                 */
                _log.info("Open position submit Stop/Tgt orders Symbol: {}", openOrder.getTradestrategy().getContract().getSymbol());
                strategyProxy.createStopAndTargetOrder(strategyProxy.getOpenPositionOrder(), 1, new Money(0.01), 3,
                        new Money(0.02), strategyProxy.getOpenTradePosition().getOpenQuantity() / 2, true);

                strategyProxy.createStopAndTargetOrder(strategyProxy.getOpenPositionOrder(), 1, new Money(0.01), 3,
                        new Money(0.02), strategyProxy.getOpenTradePosition().getOpenQuantity() / 2, true);

                strategyProxy.isPositionCovered();
            }
        }

        //StrategyData.populateBaseCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries(),
        //        tradestrategy.getTradingday(), 1, BarSize.FIVE_MIN, Side.BOT.equals(tradestrategy.getSide()), 0);
        tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), Side.BOT.equals(tradestrategy.getSide()), 0);
        strategyProxy.cancel();
    }

    @Test
    public void addPennyAndRoundStop() throws Exception {

        // Buy entry Long position
        Money price = new Money(19.99);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.BUY, 0);
        assertEquals(20.01, price.doubleValue(), 0);

        // Target Long position
        price = new Money(21.01);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.SELL, 0);
        assertEquals(20.99, price.doubleValue(), 0);

        // Stop Long position
        price = new Money(19.01);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.SELL, 0);
        assertEquals(18.99, price.doubleValue(), 0);

        // Short entry Short position
        price = new Money(24.01);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.SELL, 0);
        assertEquals(23.99, price.doubleValue(), 0);

        // Target Short position
        price = new Money(22.99);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.BUY, 0);
        assertEquals(23.01, price.doubleValue(), 0);

        // Stop Short position
        price = new Money(24.99);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.BUY, 0);
        assertEquals(25.01, price.doubleValue(), 0);

        // Buy entry Long position
        price = new Money(19.49);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.BUY, 0);
        assertEquals(19.51, price.doubleValue(), 0);

        // Target Long position
        price = new Money(21.51);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.SELL, 0);
        assertEquals(21.49, price.doubleValue(), 0);

        // Stop Long position
        price = new Money(18.51);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.SELL, 0);
        assertEquals(18.49, price.doubleValue(), 0);

        // Short entry short position
        price = new Money(24.51);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.SELL, 0);
        assertEquals(24.49, price.doubleValue(), 0);

        // Target Short position
        price = new Money(22.49);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.BUY, 0);
        assertEquals(22.51, price.doubleValue(), 0);

        // Stop Short position
        price = new Money(25.49);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.BUY, 0);
        assertEquals(25.51, price.doubleValue(), 0);

        // Short entry short position
        price = new Money(34.00);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.SELL, 0);
        assertEquals(33.99, price.doubleValue(), 0);

        // Target Short position
        price = new Money(32.00);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.BUY, 0);
        assertEquals(32.01, price.doubleValue(), 0);

        // Stop Short position
        price = new Money(35.00);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.SLD, Action.BUY, 0);
        assertEquals(35.01, price.doubleValue(), 0);

        // Buy entry Long position
        price = new Money(19.19);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.BUY, 0);
        assertEquals(19.19, price.doubleValue(), 0);

        // Target Long position
        price = new Money(21.62);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.SELL, 0);
        assertEquals(21.62, price.doubleValue(), 0);

        // Stop Long position
        price = new Money(18.57);
        price = strategyProxy.addPennyAndRoundStop(price.doubleValue(), Side.BOT, Action.SELL, 0);
        assertEquals(18.57, price.doubleValue(), 0);
    }

    @Test
    public void closePosition() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        TradeOrder order = strategyProxy.closePosition(true);
        assertNotNull(order);
    }

    @Test
    public void createOrder() throws Exception {

        TradeOrder result = strategyProxy.createOrder(tradestrategy.getContract(), Action.BUY,
                OrderType.STPLMT, new Money(100.04), new Money(100.01), 1000, null, null, TriggerMethod.DEFAULT,
                OverrideConstraints.YES, TimeInForce.DAY, true, true, null, null, null, null, null, null);
        assertNotNull(result);
    }

    @Test
    public void trailOrder() throws Exception {

        TradeOrder orderTrail = strategyProxy.createOrder(tradestrategy.getContract(), Action.SELL,
                OrderType.TRAIL, null, new Money(0.1), 100, null, null, TriggerMethod.DEFAULT,
                OverrideConstraints.YES, TimeInForce.GTC, false, true, new Money(191.60), null, null, null, null,
                null);

        assertNotNull(orderTrail);
    }

    @Test
    public void createRiskOpenPosition() throws Exception {

        TradeOrder result = strategyProxy.createRiskOpenPosition(Action.BUY, new Money(100.00),
                new Money(99.00), true, null, null, null, null);
        assertNotNull(result);
    }

    @Test
    public void createRiskOpenPositionMargin() throws Exception {

        /*
         * Standard test account has $100,000 margin and % of Margin 50% i.e
         * $50,000 with $100 risk. So 2cent stop after round over whole
         * number give 3cent stop. Risk/Stop = 3333 shares * $20 = $66,666
         * which is > than 50% of $100,000 So we should see it adjust to
         * $50,000/$20.01 = 2498 rounded to nearest 100 i.e Quantity = 2500.
         */
        Money price = new Money(20.00);
        DAOEntryLimit entryLimits = new DAOEntryLimit();
        Entrylimit entryLimit = entryLimits.getValue(price);
        entryLimit.setPercentOfMargin(new BigDecimal("0.5"));
        entryLimit = tradeService.saveAspect(entryLimit);

        TradeOrder result = strategyProxy.createRiskOpenPosition(Action.BUY, new Money(20.00),
                new Money(19.98), true, null, null, null, null);

        assertEquals(2500, result.getQuantity(), 0);
        entryLimit.setPercentOfMargin(new BigDecimal(0));
        entryLimit = tradeService.saveAspect(entryLimit);
        assertEquals(new BigDecimal(0), entryLimit.getPercentOfMargin());
    }

    @Test
    public void createRiskOpenPositionMargin1() throws Exception {

        /*
         * Standard test account has $100,000 margin and % of Margin 50% i.e
         * $50,000 with $100 risk. So 2cent stop after round over whole
         * number give 3cent stop. Risk/Stop = 3333 shares * $20 = $66,666
         * which is > than 50% of $100,000 So we should see it adjust to
         * $50,000/$20.01 = 2498 rounded to nearest 100 i.e Quantity = 2500.
         */
        Money price = new Money(45.75);
        DAOEntryLimit entryLimits = new DAOEntryLimit();
        Entrylimit entryLimit = entryLimits.getValue(price);
        entryLimit.setPercentOfMargin(new BigDecimal("0.5"));
        entryLimit = tradeService.saveAspect(entryLimit);

        TradeOrder openOrder = strategyProxy.createRiskOpenPosition(Action.SELL, new Money(45.75),
                new Money(46.00), true, null, null, null, null);

        assertEquals(400, openOrder.getQuantity(), 0);

        TradeOrderfill orderFill = new TradeOrderfill(openOrder, "Paper", new BigDecimal("45.74"),
                openOrder.getQuantity(), tradestrategy.getContract().getExchange(), "1234567",
                new BigDecimal("45.74"), openOrder.getQuantity(), Side.SLD,
                TradingCalendar.getDateTimeNowMarketTimeZone());
        openOrder.addTradeOrderfill(orderFill);
        openOrder.setStatus(OrderStatus.FILLED);
        openOrder = tradeService.saveTradeOrderfill(openOrder);
        assertNotNull(openOrder);

        reFreshPositionOrders();

        assertNotNull(strategyProxy.getOpenPositionOrder());

        /*
         * Position has been opened and not covered submit the target and
         * stop orders for the open quantity. Two targets at 4R and 7R Stop
         * and 2X actual stop this will be managed to 1R below
         *
         * Make the stop -2R and manage to the Vwap MA of the opening bar.
         */
        strategyProxy.createStopAndTargetOrder(strategyProxy.getOpenPositionOrder(), 1, new Money(0.01),
                4, new Money(0.02), strategyProxy.getOpenPositionOrder().getQuantity() / 2, true);
        strategyProxy.createStopAndTargetOrder(strategyProxy.getOpenPositionOrder(), 1, new Money(0.01),
                7, new Money(0.02), strategyProxy.getOpenPositionOrder().getQuantity() / 2, true);
        for (TradeOrder order : strategyProxy.getTradestrategy().getTradeOrders()) {
            _log.info("Key: {} Qty: {} Aux Price: {} Lmt Price: {} Stop Price: {}", order.getOrderKey(), order.getQuantity(), order.getAuxPrice(), order.getLimitPrice(), order.getStopPrice());
        }
        strategyProxy.isPositionCovered();
        entryLimit.setPercentOfMargin(new BigDecimal(0));
        tradeService.saveAspect(entryLimit);
    }

    @Test
    public void cancelOrder() throws Exception {

        Money price = new Money(37.99);
        TradeOrder openOrder = strategyProxy.createRiskOpenPosition(Action.BUY, price,
                price.subtract(new Money(0.2)), true, null, null, null, null);
        reFreshPositionOrders();
        strategyProxy.cancelOrder(openOrder);
        reFreshPositionOrders();
        openOrder = tradeService.findTradeOrderByKey(openOrder.getOrderKey());
        assertEquals(OrderStatus.CANCELLED, openOrder.getStatus());
    }

    @Test
    public void isTradeConvered() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        assertFalse(strategyProxy.isPositionCovered());
    }

    @Test
    public void createStopAndTargetOrder() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        TradeOrder targetOne = strategyProxy.createStopAndTargetOrder(new Money(99.0), new Money(103.99), 100,
                true);
        assertNotNull(targetOne);
        strategyProxy.isPositionCovered();
    }

    @Test
    public void createStopAndTargetOrderPercentQty() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        strategyProxy.createStopAndTargetOrder(strategyProxy.getOpenPositionOrder(), 2, new Money(0.01),
                4, new Money(0.02), strategyProxy.getOpenPositionOrder().getQuantity() / 2, true);
        strategyProxy.isPositionCovered();
    }

    @Test
    public void getStopPriceForPositionRisk() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        Money price = strategyProxy.getStopPriceForPositionRisk(strategyProxy.getOpenPositionOrder(), 2);
        assertNotNull(price);
    }

    @Test
    public void cancelOrdersClosePosition() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        strategyProxy.cancelOrdersClosePosition(true);
        this.reFreshPositionOrders();
        assertTrue(strategyProxy.isPositionCovered());
    }

    @Test
    public void moveStopOCAPrice() throws Exception {

        this.createOpenBuyPosition(new Money(100), Action.BUY, true);
        TradeOrder targetOne = strategyProxy.createStopAndTargetOrder(new Money(99.0), new Money(103.99),
                strategyProxy.getOpenPositionOrder().getQuantity() / 2, true);
        assertNotNull(targetOne);
        reFreshPositionOrders();
        TradeOrder targetTwo = strategyProxy.createStopAndTargetOrder(new Money(99.0), new Money(105.99),
                strategyProxy.getOpenPositionOrder().getQuantity() / 2, true);
        assertNotNull(targetTwo);
        reFreshPositionOrders();
        double avgPrice = strategyProxy.getOpenTradePosition().getTotalBuyValue().doubleValue()
                / strategyProxy.getOpenTradePosition().getTotalBuyQuantity();
        strategyProxy.moveStopOCAPrice(new Money(avgPrice), true);
        reFreshPositionOrders();
        assertTrue(strategyProxy.isPositionCovered());
    }

    @Test
    public void cancelAllOrders() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        strategyProxy.cancelAllOrders();
        assertFalse(strategyProxy.isThereOpenPosition());
    }

    @Test
    public void isTradeOpen() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        assertTrue(strategyProxy.isThereOpenPosition());
    }

    @Test
    public void getCurrentCandleCount() throws ServiceException {


        tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), Side.BOT.equals(tradestrategy.getSide()), 250);
        int count = strategyProxy.getCurrentCandleCount();

        // will close position 1hr and 5min i.e. 13 bars.
        assertEquals(2, count);
    }

    @Test
    public void getCandle() throws Exception {

        tradestrategy.getStrategyData().buildCandle(tradestrategy.getTradingday().getOpen(), 100d, 101d,
                99d, 100d, 100000L, 100d, 100, 1, null);
        CandleItem candleItem = strategyProxy.getCandle(tradestrategy.getTradingday().getOpen());
        assertNotNull(candleItem);
    }

    @Test
    public void updateTradestrategyStatus() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        strategyProxy.updateTradestrategyStatus(TradestrategyStatus.CLOSED);
    }

    @Test
    public void getEntryLimit() {

        DAOEntryLimit result = strategyProxy.getEntryLimit();
        assertNotNull(result);
    }

    @Test
    public void getTradestrategy() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        assertNotNull(strategyProxy.getTradestrategy());
    }

    @Test
    public void getTradeAccount() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        assertNotNull(strategyProxy.getIndividualAccount());
    }

    @Test
    public void getTradePosition() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        assertNotNull(strategyProxy.getOpenTradePosition());
    }

    @Test
    public void getSymbol() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        assertNotNull(strategyProxy.getSymbol());
    }

    @Test
    public void getOpenPositionOrder() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, true);
        assertNotNull(strategyProxy.getOpenPositionOrder());
    }

    @Test
    public void hasActiveOrders() throws Exception {

        createOpenBuyPosition(new Money(100), Action.BUY, false);
        assertTrue(strategyProxy.hasActiveOrders());
    }

    /**
     * Method createOpenPosition.
     *
     * @param price            Money
     * @param fillOpenPosition boolean
     */
    private void createOpenBuyPosition(Money price, String action, boolean fillOpenPosition)
            throws Exception {

        if (!strategyProxy.getTradestrategy().getTradeOrders().isEmpty()) {

            tradeService.deleteTradestrategyTradeOrders(strategyProxy.getTradestrategy());
        }

        TradeOrder tradeOrder = strategyProxy.createOrder(tradestrategy.getContract(), action, OrderType.STPLMT,
                price, price.subtract(new Money(0.2)), 1000, null, null, TriggerMethod.DEFAULT,
                OverrideConstraints.YES, TimeInForce.DAY, true, true, null, null, null, null, null, null);

        if (fillOpenPosition) {

            String side = (Action.BUY.equals(tradeOrder.getAction()) ? Side.BOT : Side.SLD);
            assertNotNull(tradeOrder);
            TradeOrderfill execution = new TradeOrderfill();
            execution.setTradeOrder(tradeOrder);
            execution.setTime(TradingCalendar.getDateTimeNowMarketTimeZone());
            execution.setExchange(tradestrategy.getContract().getExchange());
            execution.setSide(side);
            execution.setQuantity(tradeOrder.getQuantity());
            execution.setAveragePrice(price.getBigDecimalValue());
            execution.setPrice(price.getBigDecimalValue());
            execution.setCumulativeQuantity(tradeOrder.getQuantity());
            ((BackTestBrokerModel) brokerModel).execDetails(tradeOrder.getOrderKey(),
                    tradestrategy.getContract(), execution);
            this.reFreshPositionOrders();
            assertNotNull(strategyProxy.getOpenPositionOrder());

        } else {

            ((BackTestBrokerModel) brokerModel).orderStatus(tradeOrder.getOrderKey(), OrderStatus.SUBMITTED, 0, 0, 0,
                    0, 0, 0, tradeOrder.getClientId(), tradeOrder.getWhyHeld());
        }
    }

    private void reFreshPositionOrders() throws Exception {

        /*
         * Fire an event on the BaseCandleSeries this will trigger a refresh
         * of the Trade in the IStrategyRule. We need to wait until the
         * IStrategyRule is back in a wait state.
         */
        strategyProxy.reFreshPositionOrders();
    }

    /**
     *
     */
    public static class StrategyRuleTest extends AbstractStrategyRule {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -3345516391123859703L;

        /**
         * Default Constructor
         *
         * @param tradeService       TradeService
         * @param brokerManagerModel IBrokerModel
         * @param strategyData       StrategyData
         * @param tradestrategyId    Long
         */

        public StrategyRuleTest(TradeService tradeService, IBrokerModel brokerManagerModel, StrategyData strategyData, Long tradestrategyId) {
            super(tradeService, brokerManagerModel, strategyData, tradestrategyId);
        }

        /*
         * Note the current candle is just forming Enter a tier 1-3 gap in first
         * 5min bar direction, with a 3R target and stop @ 5min high/low
         *
         * @param candleSeries the series of candels that has been updated.
         *
         * @param newBar has a new bar just started.
         */

        /**
         * Method runStrategy.
         *
         * @param candleSeries CandleSeries
         * @param newBar       boolean
         */
        public void runStrategy(CandleSeries candleSeries, boolean newBar) {

            if (getCurrentCandleCount() > 0) {
                // Get the current candle
                CandleItem currentCandleItem = (CandleItem) candleSeries.getDataItem(getCurrentCandleCount());
                ZonedDateTime startPeriod = currentCandleItem.getPeriod().getStart();

                /*
                 * Trade is open kill this Strategy as its job is done.
                 */
                if (this.isThereOpenPosition()) {
                    _log.info("Strategy complete open position filled symbol: {} startPeriod: {}", getSymbol(), startPeriod);
                    this.cancel();
                    return;
                }
                /*
                 * Only manage trades when the market is open and the candle
                 * is for this Tradestrategies trading day.
                 */
                if (TradingCalendar.isMarketHours(getTradestrategy().getTradingday().getOpen(),
                        getTradestrategy().getTradingday().getClose(), startPeriod)
                        && TradingCalendar.sameDay(getTradestrategy().getTradingday().getOpen(), startPeriod)) {

                    // _log.info(getTradestrategy().getStrategy().getClassName()
                    // + " symbol: " + getSymbol() + " startPeriod: "
                    // + startPeriod);

                    // Is it the the 9:35 candle?
                    if (startPeriod.equals(TradingCalendar.getDateAtTime(startPeriod, 9, 35, 0)) && newBar) {
                        // Do nothing
                        _log.info("Rule after 9:35:00 bar, close the {} Symbol: {}", getTradestrategy().getStrategy().getClassName(), getSymbol());
                    } else if (startPeriod.isAfter(TradingCalendar.getDateAtTime(startPeriod, 10, 30, 0))) {
                        _log.info("Rule after 10:30:00 bar, close the {} Symbol: {}", getTradestrategy().getStrategy().getClassName(), getSymbol());
                        // Kill this process we are done!
                        this.cancel();
                    }
                }
            }
        }
    }
}
