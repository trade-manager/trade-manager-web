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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.dao.AspectHome;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.TradestrategyStatus;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
public class TradestrategyTest {

    private final static Logger _log = LoggerFactory.getLogger(TradestrategyTest.class);

    private final String symbol = "TEST";
    private TradestrategyHome tradestrategyHome = null;

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
        tradestrategyHome = new TradestrategyHome();
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
    public void testFindVersionById() throws Exception {

        Tradestrategy tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(tradestrategy);

        Integer version = tradestrategyHome.findVersionById(tradestrategy.getId());
        assertNotNull(version);
        _log.info("testFindVersionById IdTradeStrategy:{} version: {}", tradestrategy.getId(), version);
    }

    @Test
    public void testFindPositionOrdersById() throws Exception {

        Tradestrategy tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("testTradingdaysSave IdTradeStrategy:{}", tradestrategy.getId());

        TradestrategyOrders positionOrders = tradestrategyHome
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());
        assertNotNull(positionOrders);
        _log.info("testTradingdaysSave PositionOrders IdTradeStrategy:{}found.", positionOrders.getId());
        positionOrders.setStatus(TradestrategyStatus.CANCELLED);
        AspectHome aspectHome = new AspectHome();
        positionOrders = aspectHome.persist(positionOrders);
        assertNotNull(positionOrders);
        positionOrders = tradestrategyHome.findPositionOrdersByTradestrategyId(tradestrategy.getId());
        _log.info("testTradingdaysSave PositionOrders IdTradeStrategy:{}found Status: {}", positionOrders.getId(), positionOrders.getStatus());
        assertEquals(TradestrategyStatus.CANCELLED, positionOrders.getStatus());
    }

    @Test
    public void testAddTradestrategy() throws Exception {

        Tradestrategy tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("testTradingdaysSave IdTradeStrategy:{}", tradestrategy.getId());
        tradestrategy = tradestrategyHome.findById(tradestrategy.getId());
        assertNotNull(tradestrategy);
        _log.info("testTradingdaysSave IdTradeStrategy:{}found.", tradestrategy.getId());
    }

    @Test
    public void testUpdateTradeStrategy() throws Exception {

        ZonedDateTime open = TradingCalendar.getTradingDayStart(
                TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        TradingdayHome tradingdayHome = new TradingdayHome();
        Tradingdays tradingdays = tradingdayHome.findTradingdaysByDateRange(open, open);
        for (Tradingday tradingday : tradingdays.getTradingdays()) {
            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {
                tradestrategy.setStatus(TradestrategyStatus.OPEN);
            }
            tradingdayHome.persist(tradingday);

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                _log.info("testTradingdaysUpdate IdTradeStrategy:{}  Status: {}", tradestrategy.getId(), tradestrategy.getStatus());
                assertEquals("1", TradestrategyStatus.OPEN, tradestrategy.getStatus());
            }
        }
    }

    @Test
    public void testReadAndSavefileMultipleDayTradestrategy() throws Exception {

        AspectHome aspectHome = new AspectHome();
        TradingdayHome tradingdayHome = new TradingdayHome();
        Tradingdays tradingdays = new Tradingdays();
        Tradingday instance = Tradingday
                .newInstance(TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        tradingdays.add(instance);

        String TEST_FILE = "../db/LoadFile10Stocks.csv";
        tradingdays.populateDataFromFile(TEST_FILE, instance);
        assertFalse(tradingdays.getTradingdays().isEmpty());
        for (Tradingday tradingday : tradingdays.getTradingdays()) {
            tradingdayHome.persist(tradingday);
            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {
                _log.info("testTradingdaysUpdate IdTradeStrategy:{}", tradestrategy.getId());
                aspectHome.remove(tradestrategy);
                aspectHome.remove(tradestrategy.getContract());

            }
            aspectHome.remove(tradingday);
        }
    }

    @Test
    public void testReadAndSavefileOneDayTradestrategy() throws Exception {

        AspectHome aspectHome = new AspectHome();
        TradingdayHome tradingdayHome = new TradingdayHome();
        Tradingdays tradingdays = new Tradingdays();
        Tradingday instance = Tradingday
                .newInstance(TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        tradingdays.add(instance);

        String TEST_FILE = "../db/LoadFile1Stock.csv";
        tradingdays.populateDataFromFile(TEST_FILE, instance);
        assertFalse(tradingdays.getTradingdays().isEmpty());
        for (Tradingday tradingday : tradingdays.getTradingdays()) {
            tradingdayHome.persist(tradingday);
            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                _log.info("testTradingdaysUpdate IdTradeStrategy:{}", tradestrategy.getId());
                aspectHome.remove(tradestrategy);
                aspectHome.remove(tradestrategy.getContract());
            }
            aspectHome.remove(tradingday);
        }
    }

    @Test
    public void testFindTradestrategyDistinctByDateRange() throws Exception {

        Tradestrategy tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("testTradingdaysSave IdTradeStrategy:{}", tradestrategy.getId());
        List<Tradestrategy> results = tradestrategyHome.findTradestrategyDistinctByDateRange(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());
        for (Tradestrategy value : results) {
            _log.info("BarSize: {} ChartDays: {} Strategy: {}", value.getBarSize(), value.getChartDays(), value.getStrategy().getName());
        }
        assertNotNull(results);
    }

    @Test
    public void testFindTradestrategyContractDistinctByDateRange() throws Exception {

        Tradestrategy tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("testTradingdaysSave IdTradeStrategy:{}", tradestrategy.getId());
        List<Tradestrategy> results = tradestrategyHome.findTradestrategyContractDistinctByDateRange(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());
        for (Tradestrategy value : results) {
            _log.info("Contract: {}", value.getContract().getSymbol());
        }
        assertNotNull(results);
    }
}
