package org.trade.indicator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
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
public class IndicatorSeriesIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(IndicatorSeriesIT.class);

    private static Tradestrategy tradestrategy;
    private static TradestrategyBase TradestrategyBase;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
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
    public static void tearDownAfterClass() {
    }

    @Test
    public void saveCandle() {

        ZonedDateTime date = TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone());
        CandleItem candleItem = new CandleItem(tradestrategy.getContract(),
                new CandlePeriod(date, 300), 100.23, 100.23, 100.23, 100.23, 10000000L, 100.23, 100, date);
        Candle candle = this.tradeService.saveAspect(candleItem.getCandle());
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
