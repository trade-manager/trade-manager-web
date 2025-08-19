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
package org.trade.core.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Side;

import java.io.Serial;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class WorkerIT {

    private final static Logger _log = LoggerFactory.getLogger(WorkerIT.class);

    @Autowired
    private TradeService tradeService;

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
        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, strategy, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.HOUR_MIN);
        assertNotNull(tradestrategy);
        _log.info(" Test Initialized");
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        TradestrategyBase.clearDBData(tradeService, tradestrategy);
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
