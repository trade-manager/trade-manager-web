package org.trade.core.series;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.TradeAppLoadConfig;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyBase;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class CandleSeriesTest {

    private final static Logger _log = LoggerFactory.getLogger(CandleSeriesTest.class);


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
    public void testCandleSeriessClone() throws Exception {

        CandleSeries candleSeries = this.tradestrategy.getStrategyData().getBaseCandleSeries();
        CandleSeries series = (CandleSeries) this.tradestrategy.getStrategyData().getBaseCandleSeries().clone();
        if (candleSeries.equals(series)) {
            _log.info("CandleSeries: {}", series);
        }
        assertEquals(series, candleSeries);

    }
}
