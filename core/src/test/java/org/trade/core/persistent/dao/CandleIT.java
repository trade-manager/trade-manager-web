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
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CandleIT {

    private final static Logger _log = LoggerFactory.getLogger(CandleIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradestrategyRepository tradestrategyRepository;

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
        TradestrategyBase.setTradeService(tradeService);
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
    public void testAddCandle() throws Exception {

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

        transientInstance = tradeService.saveCandle(transientInstance);
        assertNotNull(transientInstance.getId());
        _log.info("testAddCandle IdCandle: {}", transientInstance.getId());
    }

    @Test
    public void testAddCandleSeries() throws Exception {


        for (Tradestrategy tradestrategy : tradestrategyRepository.findAll()) {

            tradestrategy = tradestrategyRepository.findById(tradestrategy.getId()).get();
            tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
            ZonedDateTime prevTradingday = TradingCalendar.addTradingDays(tradestrategy.getTradingday().getOpen(),
                    (-1 * (tradestrategy.getChartDays() - 1)));
            StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(),
                    Tradingday.newInstance(prevTradingday), 2, BarSize.FIVE_MIN, true, 0);
            assertFalse(tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
            tradeService.saveCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());

            _log.info("testAddCandle IdTradeStrategy: {}", tradestrategy.getId());
            assertNotNull(((CandleItem) tradestrategy.getStrategyData().getBaseCandleSeries().getDataItem(0))
                    .getCandle().getId());
        }
    }
}
