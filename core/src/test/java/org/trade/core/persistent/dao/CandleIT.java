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
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Some tests for the CandleIT class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class CandleIT {

    private final static Logger _log = LoggerFactory.getLogger(CandleIT.class);

    @Autowired
    private TradeService tradeService;

    private static List<Tradestrategy> tradestrategies = new ArrayList<>();
    private static final String[] symbols = {"TEST-" + TradestrategyBase.getRandomNumber(4), "TEST-" + TradestrategyBase.getRandomNumber(4)};

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

        for (String symbol : symbols) {

            Tradestrategy tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
            assertNotNull(tradestrategy);
        }
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        for (Tradestrategy tradestrategy : tradestrategies) {

            TradestrategyBase.clearDBData(tradeService, tradestrategy);
        }
        tradestrategies.clear();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {

    }

    @Test
    public void addCandle() {

        RegularTimePeriod period = new CandlePeriod(
                TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), 300);

        for (Tradestrategy tradestrategy : tradeService.findAllTradestrategies()) {

            tradestrategies.add(tradestrategy);
            Candle candle = new Candle(tradestrategy.getContract(), period, period.getStart());
            candle.setHigh(new BigDecimal("20.33"));
            candle.setLow(new BigDecimal("20.11"));
            candle.setOpen(new BigDecimal("20.23"));
            candle.setClose(new BigDecimal("20.28"));
            candle.setVolume(1500L);
            candle.setVwap(new BigDecimal("20.1"));
            candle.setTradeCount(10);

            candle = tradeService.saveAspect(candle);
            assertNotNull(candle.getId());
            _log.info("addCandle IdCandle: {}", candle.getId());
        }
    }

    @Test
    public void addCandleSeries() throws Exception {

        for (Tradestrategy tradestrategy : tradeService.findAllTradestrategies()) {

            tradestrategies.add(tradestrategy);
            tradestrategy = tradeService.findTradestrategyById(tradestrategy.getId());
            tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
            ZonedDateTime prevTradingday = TradingCalendar.addTradingDays(tradestrategy.getTradingday().getOpen(),
                    (-1 * (tradestrategy.getChartDays() - 1)));
            StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(),
                    Tradingday.newInstance(prevTradingday), 2, BarSize.FIVE_MIN, true, 0);
            _log.info("addCandleSeries symbol: {} open: {}. close: {}", tradestrategy.getContract().getSymbol(), tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getClose());

            assertFalse(tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
            tradeService.saveCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());

            // Should do nothing
            tradeService.saveCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());

            _log.info("addCandleSeries tradestrategyId: {}", tradestrategy.getId());
            CandleItem candleItem = (CandleItem) tradestrategy.getStrategyData().getBaseCandleSeries().getDataItem(0);
            assertNotNull(candleItem.getCandle().getId());

            List<Candle> candles = tradeService.findCandlesByContractDateRangeBarSize(tradestrategy.getContract(), candleItem.getCandle().getStartPeriod(), candleItem.getCandle().getEndPeriod(), tradestrategy.getBarSize());
            assertFalse(candles.isEmpty());

            candles = tradeService.findCandlesByContractAndBarSize(tradestrategy.getContract(), BarSize.FIVE_MIN);
            assertFalse(candles.isEmpty());
        }
    }
}
