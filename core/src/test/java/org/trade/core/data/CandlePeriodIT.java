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
package org.trade.core.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyBase;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.series.indicator.CandleDataset;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@SpringBootTest
public class CandlePeriodIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(CandlePeriodIT.class);

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
        this.tradestrategy = this.getTestTradestrategy(tradeService, symbol);
        assertNotNull(this.tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {
        this.clearDBData(tradeService);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void getCandleBar() throws Exception {


        ZonedDateTime startPeriod = this.tradestrategy.getTradingday().getOpen();
        ZonedDateTime prevTradingday = tradestrategy.getTradingday().getOpen()
                .minusDays((tradestrategy.getChartDays() - 1));
        prevTradingday = TradingCalendar.getPrevTradingDay(prevTradingday);
        List<Candle> candles = tradeService.findCandlesByContractDateRangeBarSize(
                this.tradestrategy.getContract().getId(), prevTradingday,
                this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getBarSize());
        if (candles.isEmpty()) {
            StrategyData.doDummyData(this.tradestrategy.getStrategyData().getBaseCandleSeries(),
                    Tradingday.newInstance(prevTradingday), 2, BarSize.FIVE_MIN, true, 0);
        } else {
            CandleDataset.populateSeries(this.tradestrategy.getStrategyData(), candles);
        }
        assertFalse(this.tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
        Candle candle = this.tradestrategy.getStrategyData().getBaseCandleSeries()
                .getBar(TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                                this.tradestrategy.getTradingday().getOpen()),
                        TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                                this.tradestrategy.getTradingday().getClose()));
        _log.info("Bar for Contract: {} Start Period: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {}", candle.getContract().getSymbol(), candle.getPeriod(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVwap(), candle.getVolume());


    }

    @Test
    public void getAvgCandleBar() throws Exception {


        ZonedDateTime startPeriod = this.tradestrategy.getTradingday().getOpen();
        ZonedDateTime prevTradingday = this.tradestrategy.getTradingday().getOpen()
                .minusDays((this.tradestrategy.getChartDays() - 1));
        prevTradingday = TradingCalendar.getPrevTradingDay(prevTradingday);
        List<Candle> candles = tradeService.findCandlesByContractDateRangeBarSize(
                this.tradestrategy.getContract().getId(), prevTradingday,
                this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getBarSize());
        if (candles.isEmpty()) {
            StrategyData.doDummyData(this.tradestrategy.getStrategyData().getBaseCandleSeries(),
                    Tradingday.newInstance(prevTradingday), 2, BarSize.FIVE_MIN, true, 0);
        } else {
            CandleDataset.populateSeries(this.tradestrategy.getStrategyData(), candles);
        }
        assertFalse(this.tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
        Candle candle = this.tradestrategy.getStrategyData().getBaseCandleSeries().getAverageBar(
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        this.tradestrategy.getTradingday().getOpen()),
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        this.tradestrategy.getTradingday().getClose()),
                false);
        _log.info("Non wieghted avg bar for Contract: {} Start Period: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {}", candle.getContract().getSymbol(), candle.getPeriod(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVwap(), candle.getVolume());

        candle = this.tradestrategy.getStrategyData().getBaseCandleSeries().getAverageBar(
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        this.tradestrategy.getTradingday().getOpen()),
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        this.tradestrategy.getTradingday().getClose()),
                true);
        _log.info("Wieghted avg bar for Contract: {} Start Period: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {}", candle.getContract().getSymbol(), candle.getPeriod(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVwap(), candle.getVolume());


    }

    @Test
    public void dateConversion() {

        String dateString = "20151129 09:35:11";

        LocalDateTime formattedDate = TradingCalendar.getLocalDateTimeFromDateTimeString(dateString,
                "yyyyMMdd HH:mm:ss");

        _log.info("Date  time: {}", formattedDate);
        ZonedDateTime date = ZonedDateTime.of(formattedDate, TradingCalendar.MKT_TIMEZONE);
        _log.info("Date EST time: {}", date);

        ZoneId defaultZone = TimeZone.getDefault().toZoneId();
        ZonedDateTime newLocal = date.withZoneSameInstant(defaultZone);
        _log.info("Date PST time: {}", newLocal);

        ZonedDateTime newInstant = date.withZoneSameLocal(defaultZone);
        _log.info("Date PST time: {}", newInstant);

        assertNotNull(date);

    }

    @Test
    public void secondsNext() {

        int size = 100;
        int secondsLength = 3600;

        RegularTimePeriod period = new CandlePeriod(
                TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), secondsLength);

        for (int i = 0; i < size; i++) {
            _log.info("Time is : {} Start: {} End: {}", period, period.getStart(), period.getEnd());
            period = period.next();
            assertNotNull(period);
        }
    }

    @Test
    public void secondsPrev() {

        int size = 100;
        int secondsLength = 3600;

        RegularTimePeriod period = new CandlePeriod(
                TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), secondsLength);

        for (int i = 0; i < size; i++) {
            _log.info("Time is : {} Start: {} End: {}", period, period.getStart(), period.getEnd());
            period = period.previous();
            assertNotNull(period);
        }
    }

    @Test
    public void findCurrentTimePeriod() {

        int secondsLength = 300;
        ZonedDateTime now = TradingCalendar.getDateTimeNowMarketTimeZone();
        ZonedDateTime startBusDate = TradingCalendar.getTradingDayStart(now);
        long periods = TradingCalendar.getDurationInSeconds(startBusDate, now) / secondsLength;
        startBusDate = startBusDate.plusSeconds(periods);

        RegularTimePeriod period = new CandlePeriod(startBusDate, secondsLength);
        _log.info("\n Bus Day Start : {}\n Start: {}\n End: {}\n Periods: {}", startBusDate, period.getStart(), period.getEnd(), periods);
        assertNotNull(period);
    }
}
