package org.trade.strategy.data;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.persistent.dao.TradestrategyBase;
import org.trade.ui.TradeAppLoadConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CandleSeriesTest {

    private final static Logger _log = LoggerFactory.getLogger(CandleSeriesTest.class);
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
    public void testCandleSeriessClone() throws Exception {

        CandleSeries candleSeries = this.tradestrategy.getStrategyData().getBaseCandleSeries();
        CandleSeries series = (CandleSeries) this.tradestrategy.getStrategyData().getBaseCandleSeries().clone();
        if (candleSeries.equals(series)) {
            _log.info("CandleSeries: {}", series);
        }
        assertEquals("1", series, candleSeries);

    }
}
