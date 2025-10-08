package org.trade.core.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.candle.Candle;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.CandleDataset;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class CandlePeriodIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(CandlePeriodIT.class);

    private static Tradestrategy tradestrategy;
    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);

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

        tradestrategy = createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void getCandleBar() throws Exception {


        ZonedDateTime startPeriod = tradestrategy.getTradingday().getOpen();
        ZonedDateTime prevTradingday = tradestrategy.getTradingday().getOpen()
                .minusDays((tradestrategy.getChartDays() - 1));
        prevTradingday = TradingCalendar.getPrevTradingDay(prevTradingday);
        List<Candle> candles = tradeService.getCandleService().findByContractDateRangeBarSize(
                tradestrategy.getContract(), prevTradingday,
                tradestrategy.getTradingday().getOpen(), tradestrategy.getBarSize());

        if (candles.isEmpty()) {

            tradestrategy.getStrategyData().populateCandleSeries(Tradingday.newInstance(prevTradingday), 2, tradestrategy.getBarSize(), true, 0);
        } else {

            CandleDataset.populateSeries(tradestrategy.getStrategyData(), candles);
        }

        assertFalse(tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
        Candle candle = tradestrategy.getStrategyData().getBaseCandleSeries()
                .getBar(TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                                tradestrategy.getTradingday().getOpen()),
                        TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                                tradestrategy.getTradingday().getClose()));

        _log.info("Bar for Contract: {} Start Period: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {}", candle.getContract().getSymbol(), candle.getPeriod(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVwap(), candle.getVolume());
    }

    @Test
    public void getAvgCandleBar() throws Exception {


        ZonedDateTime startPeriod = tradestrategy.getTradingday().getOpen();
        ZonedDateTime prevTradingday = tradestrategy.getTradingday().getOpen()
                .minusDays((tradestrategy.getChartDays() - 1));
        prevTradingday = TradingCalendar.getPrevTradingDay(prevTradingday);
        List<Candle> candles = tradeService.getCandleService().findByContractDateRangeBarSize(
                tradestrategy.getContract(), prevTradingday,
                tradestrategy.getTradingday().getOpen(), tradestrategy.getBarSize());

        if (candles.isEmpty()) {

            tradestrategy.getStrategyData().populateCandleSeries(Tradingday.newInstance(prevTradingday), 2, tradestrategy.getBarSize(), true, 0);

        } else {

            CandleDataset.populateSeries(tradestrategy.getStrategyData(), candles);
        }
        assertFalse(tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
        Candle candle = tradestrategy.getStrategyData().getBaseCandleSeries().getAverageBar(
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        tradestrategy.getTradingday().getOpen()),
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        tradestrategy.getTradingday().getClose()),
                false);
        _log.info("Non wieghted avg bar for Contract: {} Start Period: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {}", candle.getContract().getSymbol(), candle.getPeriod(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVwap(), candle.getVolume());

        candle = tradestrategy.getStrategyData().getBaseCandleSeries().getAverageBar(
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        tradestrategy.getTradingday().getOpen()),
                TradingCalendar.getDateAtTime(TradingCalendar.getPrevTradingDay(startPeriod),
                        tradestrategy.getTradingday().getClose()),
                true);
        _log.info("Weighted avg bar for Contract: {} Start Period: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {}", candle.getContract().getSymbol(), candle.getPeriod(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVwap(), candle.getVolume());
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
