package org.trade.core.series;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.trade.core.dao.AspectRepository;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyBase;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.properties.TradeAppLoadConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CandleSeriesIT {

    private final static Logger _log = LoggerFactory.getLogger(CandleSeriesIT.class);

    @Autowired
    private AspectRepository aspectRepository;

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
        TradestrategyBase.setTradestrategyBase(aspectRepository, tradeService);
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
