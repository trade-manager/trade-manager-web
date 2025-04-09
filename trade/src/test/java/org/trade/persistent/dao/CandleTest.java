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
package org.trade.persistent.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.dao.AspectHome;
import org.trade.core.util.TradingCalendar;
import org.trade.dictionary.valuetype.BarSize;
import org.trade.strategy.data.StrategyData;
import org.trade.strategy.data.base.RegularTimePeriod;
import org.trade.strategy.data.candle.CandleItem;
import org.trade.strategy.data.candle.CandlePeriod;
import org.trade.ui.TradeAppLoadConfig;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class CandleTest {

    private final static Logger _log = LoggerFactory.getLogger(CandleTest.class);
    @Rule
    public TestName name = new TestName();

    private Tradestrategy tradestrategy = null;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * Method setUp.
     */
    @Before
    public void setUp() throws Exception {

        TradeAppLoadConfig.loadAppProperties();
        String symbol = "TEST";
        this.tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull("1", this.tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @After
    public void tearDown() throws Exception {
        TradestrategyBase.clearDBData();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testAddCandle() {

        try {

            AspectHome aspectHome = new AspectHome();

            RegularTimePeriod period = new CandlePeriod(
                    TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), 300);

            Candle transientInstance = new Candle(this.tradestrategy.getContract(), this.tradestrategy.getTradingday(),
                    period, period.getStart());
            transientInstance.setHigh(new BigDecimal("20.33"));
            transientInstance.setLow(new BigDecimal("20.11"));
            transientInstance.setOpen(new BigDecimal("20.23"));
            transientInstance.setClose(new BigDecimal("20.28"));
            transientInstance.setVolume(1500L);
            transientInstance.setVwap(new BigDecimal("20.1"));
            transientInstance.setTradeCount(10);

            transientInstance = aspectHome.persist(transientInstance);
            assertNotNull("1", transientInstance.getId());
            _log.info("testAddCandle IdCandle: {}", transientInstance.getId());

        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testAddCandleSeries() {

        try {

            TradestrategyHome tradestrategyHome = new TradestrategyHome();
            CandleHome candleHome = new CandleHome();
            for (Tradestrategy tradestrategy : tradestrategyHome.findAll()) {
                tradestrategy = tradestrategyHome.findById(tradestrategy.getId());
                tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
                ZonedDateTime prevTradingday = TradingCalendar.addTradingDays(tradestrategy.getTradingday().getOpen(),
                        (-1 * (tradestrategy.getChartDays() - 1)));
                StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(),
                        Tradingday.newInstance(prevTradingday), 2, BarSize.FIVE_MIN, true, 0);
                assertFalse("1", tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
                candleHome.persistCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());

                _log.info("testAddCandle IdTradeStrategy: {}", tradestrategy.getId());
                assertNotNull("2", ((CandleItem) tradestrategy.getStrategyData().getBaseCandleSeries().getDataItem(0))
                        .getCandle().getId());

            }

        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }
}
