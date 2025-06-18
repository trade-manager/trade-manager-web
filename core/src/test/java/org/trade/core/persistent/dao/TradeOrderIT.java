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
package org.trade.core.persistent.dao;

import com.ib.client.Execution;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.trade.core.broker.TWSBrokerModel;
import org.trade.core.persistent.TradeService;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.Side;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
public class TradeOrderIT {

    private final static Logger _log = LoggerFactory.getLogger(TradeOrderIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    private Tradestrategy tradestrategy = null;
    private Integer clientId = null;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        TradeAppLoadConfig.loadAppProperties();
        clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");

        String symbol = "TEST";
        this.tradestrategy = TradestrategyBase.getTestTradestrategy(tradeService, symbol);
        assertNotNull(this.tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        TradestrategyBase.clearDBData(tradeService);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void addTradeOrder() throws Exception {

        String side = this.tradestrategy.getSide();
        String action = Action.BUY;
        if (Side.SLD.equals(side)) {
            action = Action.SELL;
        }

        double risk = this.tradestrategy.getRiskAmount().doubleValue();

        double stop = 0.20;
        BigDecimal price = new BigDecimal(20);
        int quantity = (int) ((int) risk / stop);
        ZonedDateTime createDate = this.tradestrategy.getTradingday().getOpen().plusMinutes(5);

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, action, OrderType.STPLMT, quantity, price,
                price.add(new BigDecimal("0.004")), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder.setClientId(clientId);
        tradeOrder.setTransmit(true);
        tradeOrder.setStatus("SUBMITTED");
        tradeOrder.validate();
        tradeOrder = tradeService.saveTradeOrder(tradeOrder);
        assertNotNull(tradeOrder);
        _log.info("IdOrder: {}", tradeOrder.getId());

        TradeOrder tradeOrder1 = new TradeOrder(this.tradestrategy, Action.SELL, OrderType.STP, quantity,
                price.subtract(new BigDecimal(1)), null, createDate);

        tradeOrder1.setAuxPrice(price.subtract(new BigDecimal(1)));
        tradeOrder1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder1.setClientId(clientId);
        tradeOrder1.setTransmit(true);
        tradeOrder1.setStatus("SUBMITTED");
        tradeOrder1.validate();
        tradeOrder1 = tradeService.saveTradeOrder(tradeOrder1);
        assertNotNull(tradeOrder1);
    }

    @Test
    public void addOpenStopTargetTradeOrder() throws Exception {

        String side = this.tradestrategy.getSide();
        String action = Action.BUY;
        if (Side.SLD.equals(side)) {
            action = Action.SELL;
        }

        double risk = this.tradestrategy.getRiskAmount().doubleValue();

        double stop = 0.20;
        BigDecimal price = new BigDecimal(20);
        int quantity = (int) ((int) risk / stop);
        ZonedDateTime createDate = this.tradestrategy.getTradingday().getOpen().plusMinutes(5);

        TradeOrder tradeOrder1 = new TradeOrder(this.tradestrategy, action, OrderType.STPLMT, quantity, price,
                price, createDate);
        tradeOrder1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder1.setClientId(clientId);
        tradeOrder1.setOcaGroupName("");
        tradeOrder1.setTransmit(true);
        tradeOrder1.setStatus("SUBMITTED");
        tradeOrder1.validate();
        tradeOrder1 = tradeService.saveTradeOrder(tradeOrder1);
        int buySellMultiplier = 1;
        if (action.equals(Action.BUY)) {
            action = Action.SELL;

        } else {
            action = Action.BUY;
            buySellMultiplier = -1;
        }

        TradeOrder tradeOrder2 = new TradeOrder(this.tradestrategy, action, OrderType.LMT, quantity / 2, null,
                price.add(new BigDecimal((stop * 3) * buySellMultiplier)), createDate);

        tradeOrder2.setClientId(clientId);
        tradeOrder2.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder2.setOcaType(2);
        tradeOrder2.setOcaGroupName(this.tradestrategy.getId() + "q1w2e3");
        tradeOrder2.setTransmit(true);
        tradeOrder2.setStatus("SUBMITTED");
        tradeOrder2.validate();
        tradeOrder2 = tradeService.saveTradeOrder(tradeOrder2);
        assertNotNull(tradeOrder2);

        TradeOrder tradeOrder3 = new TradeOrder(this.tradestrategy, action, OrderType.LMT, quantity / 2, null,
                price.add(new BigDecimal((stop * 4) * buySellMultiplier)), createDate);

        tradeOrder3.setClientId(clientId);
        tradeOrder3.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder3.setOcaType(2);
        tradeOrder3.setOcaGroupName(this.tradestrategy.getId() + "q1w2e3");
        tradeOrder3.setTransmit(true);
        tradeOrder3.setStatus("SUBMITTED");
        tradeOrder3.validate();
        tradeOrder3 = tradeService.saveTradeOrder(tradeOrder3);
        assertNotNull(tradeOrder3);

        TradeOrder tradeOrder4 = new TradeOrder(this.tradestrategy, action, OrderType.STP, quantity,
                price.add(new BigDecimal(stop * buySellMultiplier * -1)), null, createDate);
        tradeOrder4.setLimitPrice(new BigDecimal(0));
        tradeOrder4.setAuxPrice(price.add(new BigDecimal(stop * buySellMultiplier * -1)));
        tradeOrder4.setClientId(clientId);
        tradeOrder4.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder4.setOcaType(2);
        tradeOrder4.setOcaGroupName(this.tradestrategy.getId() + "q1w2e3");
        tradeOrder4.setTransmit(true);
        tradeOrder4.setStatus("SUBMITTED");
        tradeOrder4.validate();
        tradeOrder4 = tradeService.saveTradeOrder(tradeOrder4);
        assertNotNull(tradeOrder4);

        _log.info("IdOrder: {}", tradeOrder1.getId());
    }

    @Test
    public void addTradeOrderFill() throws Exception {

        addTradeOrder();
        int minute = 25;

        for (TradeOrder tradeOrder : this.tradestrategy.getTradeOrders()) {

            tradeOrder = tradeOrderRepository.findByOrderKey(tradeOrder.getOrderKey());
            minute = minute + 3;
            ZonedDateTime filledDate = this.tradestrategy.getTradingday().getOpen().plusMinutes(minute);
            if (tradeOrder.getIsOpenPosition()) {

                TradeOrderfill orderfill = new TradeOrderfill(tradeOrder, "Paper", tradeOrder.getLimitPrice(),
                        tradeOrder.getQuantity() / 2, "ISLAND", "1234", tradeOrder.getLimitPrice(),
                        tradeOrder.getQuantity() / 2, this.tradestrategy.getSide(), filledDate);

                tradeOrder.addTradeOrderfill(orderfill);

                TradeOrderfill orderfill1 = new TradeOrderfill(tradeOrder, "Paper", tradeOrder.getLimitPrice(),
                        tradeOrder.getQuantity() / 2, "ISLAND", "12345", tradeOrder.getLimitPrice(),
                        tradeOrder.getQuantity() / 2, this.tradestrategy.getSide(), filledDate.plusMinutes(3));
                tradeOrder.addTradeOrderfill(orderfill1);
                tradeOrder.setIsFilled(true);
                tradeOrder.setFilledQuantity(tradeOrder.getQuantity());
                tradeOrder.setStatus("FILLED");
                tradeOrder.setFilledDate(filledDate.plusMinutes(3));
                tradeOrder.setAverageFilledPrice(tradeOrder.getLimitPrice());
                tradeOrder.setCommission(BigDecimal.valueOf(tradeOrder.getQuantity() * 0.005));

            } else {
                if (OrderType.STP.equals(tradeOrder.getOrderType())) {

                    TradeOrderfill orderfill = new TradeOrderfill(tradeOrder, "Paper", tradeOrder.getAuxPrice(),
                            tradeOrder.getQuantity(), "ISLAND", "12345", tradeOrder.getAuxPrice(),
                            tradeOrder.getQuantity(), this.tradestrategy.getSide(), filledDate.plusMinutes(5));
                    tradeOrder.addTradeOrderfill(orderfill);
                    tradeOrder.setIsFilled(true);
                    tradeOrder.setStatus(OrderStatus.FILLED);
                    tradeOrder.setAverageFilledPrice(tradeOrder.getAuxPrice());
                    tradeOrder.setFilledDate(filledDate.plusMinutes(5));
                    tradeOrder.setCommission(BigDecimal.valueOf(tradeOrder.getQuantity() * 0.005));
                    tradeOrder.setFilledQuantity(tradeOrder.getQuantity());

                } else {
                    tradeOrder.setStatus(OrderStatus.CANCELLED);
                }
            }

            tradeOrder = tradeService.saveTradeOrder(tradeOrder);
            _log.info("IdOrder: {} Action:{} OrderType:{} Status:{} filledDate:{}", tradeOrder.getId(), tradeOrder.getAction(), tradeOrder.getOrderType(), tradeOrder.getStatus(), filledDate);
        }
    }

    @Test
    public void addDetachedTradeOrder() {

        String side = this.tradestrategy.getSide();
        String action = Action.BUY;
        if (Side.SLD.equals(side)) {
            action = Action.SELL;
        }

        TradeOrder tradeOrder = new TradeOrder(this.tradestrategy, action, OrderType.STPLMT, 100,
                new BigDecimal("20.20"), new BigDecimal("20.23"), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        // Save new order with detached trade
        tradeOrder = tradeService.saveTradeOrder(tradeOrder);
        Execution execution = new Execution();
        execution.side(side);
        execution.time(TradingCalendar.getFormattedDate(TradingCalendar.getDateTimeNowMarketTimeZone(),
                "yyyyMMdd HH:mm:ss"));
        execution.exchange("ISLAND");
        execution.shares(tradeOrder.getQuantity());
        execution.price(tradeOrder.getLimitPrice().doubleValue());
        execution.avgPrice(tradeOrder.getLimitPrice().doubleValue());
        execution.cumQty(tradeOrder.getQuantity());
        execution.execId("1234");
        TradeOrderfill orderfill = new TradeOrderfill();
        TWSBrokerModel.populateTradeOrderfill(execution, orderfill);
        orderfill.setTradeOrder(tradeOrder);
        tradeOrder.addTradeOrderfill(orderfill);
        // Save a detached order with a new order fill
        tradeOrder = tradeService.saveTradeOrder(tradeOrder);
        assertNotNull(tradeOrder);

        if (action.equals(Action.BUY)) {
            action = Action.SELL;

        } else {
            action = Action.BUY;
        }
        TradeOrder tradeOrder1 = new TradeOrder(this.tradestrategy, action, OrderType.LMT, 300, null,
                new BigDecimal("23.41"), TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder1.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder1 = tradeService.saveTradeOrder(tradeOrder1);

        Execution execution1 = new Execution();
        execution1.side(side);
        execution1.time(TradingCalendar.getFormattedDate(TradingCalendar.getDateTimeNowMarketTimeZone(),
                "yyyyMMdd HH:mm:ss"));
        execution1.exchange("ISLAND");
        execution1.shares(tradeOrder1.getQuantity());
        execution1.price(tradeOrder1.getLimitPrice().doubleValue());
        execution1.avgPrice(tradeOrder1.getLimitPrice().doubleValue());
        execution1.cumQty(tradeOrder1.getQuantity());
        execution1.execId("1234");
        TradeOrderfill orderfill1 = new TradeOrderfill();
        TWSBrokerModel.populateTradeOrderfill(execution1, orderfill1);
        orderfill1.setTradeOrder(tradeOrder1);
        tradeOrder1.addTradeOrderfill(orderfill1);
        tradeOrder1 = tradeService.saveTradeOrder(tradeOrder1);
        assertNotNull(tradeOrder1);
    }

    @Test
    public void findTradeOrderByMaxKey() throws Exception {

        Integer orderKey = tradeService.findTradeOrderByMaxKey();
        _log.info("Max Order key: {}", orderKey);
    }
}
