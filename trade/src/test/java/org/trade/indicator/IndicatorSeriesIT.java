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
package org.trade.indicator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.indicator.candle.CandleItem;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class IndicatorSeriesIT {

    private final static Logger _log = LoggerFactory.getLogger(IndicatorSeriesIT.class);

    @Autowired
    private TradeService tradeService;

    private static Tradestrategy tradestrategy;
    private static TradestrategyBase TradestrategyBase;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);

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
    public void saveCandle() {

        ZonedDateTime date = TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone());
        CandleItem candleItem = new CandleItem(tradestrategy.getContract(), tradestrategy.getTradingday(),
                new CandlePeriod(date, 300), 100.23, 100.23, 100.23, 100.23, 10000000L, 100.23, 100, date);
        Candle candle = this.tradeService.saveCandle(candleItem.getCandle());
        assertNotNull(candle.getId());
    }

    @Test
    public void findCodeTypeByNameType() {

        String indicatorName = IndicatorSeries.MovingAverageSeries.substring(0,
                IndicatorSeries.MovingAverageSeries.indexOf("Series"));
        CodeType result = this.tradeService.findCodeTypeByNameType(indicatorName,
                CodeType.IndicatorParameters);
        assertNotNull(result);
    }
}
