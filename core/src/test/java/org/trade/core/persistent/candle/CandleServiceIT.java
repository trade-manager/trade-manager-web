package org.trade.core.persistent.candle;

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
import org.trade.core.persistent.strategy.series.indicator.StrategyData;
import org.trade.core.persistent.strategy.series.indicator.candle.CandleItem;
import org.trade.core.persistent.strategy.series.indicator.candle.CandlePeriod;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;

import java.math.BigDecimal;
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
public class CandleServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(CandleServiceIT.class);

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

            Tradestrategy tradestrategy = this.createTestTradestrategy(symbol);
            assertNotNull(tradestrategy);
        }
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
    public void addCandle() {

        RegularTimePeriod period = new CandlePeriod(
                TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), 300);

        for (Tradestrategy tradestrategy : tradeService.getTradestrategyService().findAll()) {

            tradestrategies.add(tradestrategy);
            Candle candle = new Candle(tradestrategy.getContract(), period, period.getStart());
            candle.setHigh(new BigDecimal("20.33"));
            candle.setLow(new BigDecimal("20.11"));
            candle.setOpen(new BigDecimal("20.23"));
            candle.setClose(new BigDecimal("20.28"));
            candle.setVolume(1500L);
            candle.setVwap(new BigDecimal("20.1"));
            candle.setTradeCount(10);

            candle = tradeService.getAspectService().save(candle);
            assertNotNull(candle.getId());
            _log.info("addCandle IdCandle: {}", candle.getId());
        }
    }

    @Test
    public void addCandleSeries() throws Exception {

        for (Tradestrategy tradestrategy : tradeService.getTradestrategyService().findAll()) {

            tradestrategies.add(tradestrategy);
            tradestrategy = tradeService.getTradestrategyService().findById(tradestrategy.getId());
            tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
            tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), true, 0);

            _log.info("addCandleSeries symbol: {} open: {}. close: {}", tradestrategy.getContract().getSymbol(), tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getClose());

            assertFalse(tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
            tradeService.saveCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());

            // Should do nothing
            tradeService.saveCandleSeries(tradestrategy.getStrategyData().getBaseCandleSeries());

            _log.info("addCandleSeries tradestrategyId: {}", tradestrategy.getId());
            CandleItem candleItem = (CandleItem) tradestrategy.getStrategyData().getBaseCandleSeries().getDataItem(0);
            assertNotNull(candleItem.getCandle().getId());

            List<Candle> candles = tradeService.getCandleService().findByContractDateRangeBarSize(tradestrategy.getContract(), candleItem.getCandle().getStartPeriod(), candleItem.getCandle().getEndPeriod(), tradestrategy.getBarSize());
            assertFalse(candles.isEmpty());

            candles = tradeService.getCandleService().findByContractAndBarSize(tradestrategy.getContract(), BarSize.FIVE_MIN);
            assertFalse(candles.isEmpty());
        }
    }
}
