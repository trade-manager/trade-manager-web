package org.trade.core.persistent.strategy.series;

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
import org.trade.core.persistent.strategy.series.indicator.CandleSeries;
import org.trade.core.persistent.tradestrategy.Tradestrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class CandleSeriesIT extends TradestrategyBase {

    private static final Logger _log = LoggerFactory.getLogger(CandleSeriesIT.class);

    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static Tradestrategy tradestrategy;

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
    public void candleSeriessClone() throws Exception {

        CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();
        CandleSeries series = (CandleSeries) tradestrategy.getStrategyData().getBaseCandleSeries().clone();

        if (candleSeries.equals(series)) {

            _log.info("CandleSeries: {}", series);
        }
        assertEquals(series, candleSeries);
    }
}
