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
import org.trade.persistent.dao.TradestrategyTest;
import org.trade.ui.TradeAppLoadConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
        try {
            TradeAppLoadConfig.loadAppProperties();
            String symbol = "TEST";
            this.tradestrategy = TradestrategyTest.getTestTradestrategy(symbol);
            assertNotNull("1", this.tradestrategy);
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    /**
     * Method tearDown.
     */
    @After
    public void tearDown() throws Exception {
        TradestrategyTest.clearDBData();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testCandleSeriessClone() {
        try {

            CandleSeries candleSeries = this.tradestrategy.getStrategyData().getBaseCandleSeries();
            CandleSeries series = (CandleSeries) this.tradestrategy.getStrategyData().getBaseCandleSeries().clone();
            if (candleSeries.equals(series)) {
                _log.info("CandleSeries: {}", series);
            }
            assertEquals("1", series, candleSeries);
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

}
