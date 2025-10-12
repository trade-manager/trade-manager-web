package org.trade.core.util;

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
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.series.indicator.CandleSeries;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Side;

import java.io.Serial;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class WorkerIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(WorkerIT.class);

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

        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        tradestrategy = this.createTestTradestrategy(strategy, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.HOUR_MIN);
        assertNotNull(tradestrategy);
        _log.info(" Test Initialized");
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
    public void entryRuleNoEntryByRT() {

        WorkerTest strategyProxy = new WorkerIT.WorkerTest();
        strategyProxy.execute();
    }

    /**
     *
     */
    public static class WorkerTest extends Worker {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -3345516391123859703L;

        /**
         * Default Constructor
         */

        public WorkerTest() {


        }

        /**
         * Method call once to initialize the strategy in the worker thread.
         */
        public void initStrategy() {
            _log.info("Info: WorkerTest::initStrategy");
        }

        public void runStrategy(CandleSeries candleSeries, boolean newBar) {
            _log.info("Info: WorkerTest::runStrategy");
        }

        protected Void doInBackground() {
            initStrategy();
            CandleSeries candleSeries = new CandleSeries(tradestrategy.getContract().getSymbol(),
                    tradestrategy.getContract(), tradestrategy.getBarSize(), tradestrategy.getTradingday().getOpen(),
                    tradestrategy.getTradingday().getClose());
            runStrategy(candleSeries, true);
            _log.info("Info: WorkerTest::doInBackground");
            return null;
        }

        protected void done() {
            _log.info("Info: WorkerTest::done");
        }
    }
}
