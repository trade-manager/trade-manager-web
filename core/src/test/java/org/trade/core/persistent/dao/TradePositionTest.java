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
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.dao.AspectHome;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Side;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
public class TradePositionTest {

    private final static Logger _log = LoggerFactory.getLogger(TradePositionTest.class);

    private TradePositionHome tradePositionHome = null;
    private AspectHome aspectHome = null;
    private Tradestrategy tradestrategy = null;

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
        tradePositionHome = new TradePositionHome();
        aspectHome = new AspectHome();
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
    public void testAddRemoveTradePosition() throws Exception {


        TradePosition instance = new TradePosition(this.tradestrategy.getContract(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

        TradePosition tradePosition = aspectHome.persist(instance);

        assertNotNull(tradePosition.getId());
        _log.info("testAddTradePosition IdTradeStrategy: {}IdTradePosition: {}", this.tradestrategy.getId(), tradePosition.getId());

        tradePositionHome.remove(tradePosition);
        _log.info("testDeleteTradePosition IdTradeStrategy: {}", tradestrategy.getId());
        tradePosition = tradePositionHome.findById(tradePosition.getId());
        assertNull(tradePosition);

    }
}
