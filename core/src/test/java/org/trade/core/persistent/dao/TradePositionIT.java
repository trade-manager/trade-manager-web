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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.persistent.TradeService;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Side;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradePositionIT {

    private final static Logger _log = LoggerFactory.getLogger(TradePositionIT.class);

    @Autowired
    private TradeService tradeService;

    private static Tradestrategy tradestrategy;
    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);

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

        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void addRemoveTradePosition() {


        TradePosition instance = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

        instance = tradeService.saveAspect(instance);

        assertNotNull(instance.getId());
        _log.info("testAddTradePosition IdTradeStrategy: {}IdTradePosition: {}", tradestrategy.getId(), instance.getId());

        tradeService.deleteAspect(instance);
        _log.info("testDeleteTradePosition IdTradeStrategy: {}", tradestrategy.getId());
        instance = tradeService.findTradePositionById(instance.getId());
        assertNull(instance);
    }
}
