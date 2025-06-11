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
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.trade.core.dao.AspectRepository;
import org.trade.core.persistent.TradeService;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Side;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradePositionIT {

    private final static Logger _log = LoggerFactory.getLogger(TradePositionIT.class);

    @Autowired
    private AspectRepository aspectRepository;
    @Autowired
    private TradeService tradeService;

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
        String symbol = "TEST";
        TradestrategyBase.setTradestrategyBase(aspectRepository, tradeService);
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

        TradePosition tradePosition = aspectRepository.save(instance);

        assertNotNull(tradePosition.getId());
        _log.info("testAddTradePosition IdTradeStrategy: {}IdTradePosition: {}", this.tradestrategy.getId(), tradePosition.getId());

        tradeService.delete(tradePosition);
        _log.info("testDeleteTradePosition IdTradeStrategy: {}", tradestrategy.getId());
        tradePosition = tradeService.findTradePositionById(tradePosition.getId());
        assertNull(tradePosition);
    }
}
