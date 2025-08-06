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
package org.trade.core.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.strategy.AbstractStrategyRule;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.JSONMapper;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.Side;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class AbstractStrategyJSIT {

    private final static Logger _log = LoggerFactory.getLogger(AbstractStrategyJSIT.class);

    @Autowired
    private TradeService tradeService;

    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static Tradestrategy tradestrategy;
    private static IBrokerModel brokerModel;
    private static String templateName;
    private static String strategyDir;
    private static StrategyRuleJSTest strategyProxy;

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

        List<Object> param = new ArrayList<>();
        param.add(tradeService);
        brokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._brokerTest, param, this);
        templateName = ConfigProperties.getPropAsString("trade.strategy.template");
        strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
        Integer clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        Integer port = Integer.valueOf(ConfigProperties.getPropAsString("trade.tws.port"));
        String host = ConfigProperties.getPropAsString("trade.tws.host");
        brokerModel.onConnect(host, port, clientId);

        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
        assertNotNull(tradestrategy);
        StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(), tradestrategy.getTradingday(), 1, BarSize.FIVE_MIN, Side.BOT.equals(tradestrategy.getSide()), 0);

        strategyProxy = new StrategyRuleJSTest(tradeService, brokerModel, tradestrategy.getStrategyData(),
                tradestrategy.getId());
        assertNotNull(strategyProxy);
        strategyProxy.execute();

        do {
            Thread.sleep(1000);
        } while (!strategyProxy.isWaiting());
        _log.info(" Test Initialized");
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        brokerModel.onDisconnect();
        strategyProxy.cancel();
        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void runJavaScript() {

    }

    /**
     *
     */
    public static class StrategyRuleJSWrapper implements Serializable {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -3345516391123859703L;

        private final static Logger _log = LoggerFactory.getLogger(StrategyRuleJSWrapper.class);

        private final StrategyRuleJSTest strategyRuleJSTest;
        private static final JSONObject result = new JSONObject("{'error': false, 'message': ''}");

        /**
         * Default Constructor
         *
         * @param strategyRuleJSTest StrategyRuleJSTest
         */
        public StrategyRuleJSWrapper(StrategyRuleJSTest strategyRuleJSTest) {

            this.strategyRuleJSTest = strategyRuleJSTest;
        }

        /**
         * Method log.
         */
        public void log(String message) {

            strategyRuleJSTest.log(message);
        }

        /**
         * Method log.
         */
        @JSGetter
        public JSONObject getCurrentCandle() {

            try {

                CandleItem candle = strategyRuleJSTest.getCurrentCandle();
                result.put("candle", new JSONObject(JSONMapper.getJSONString(candle)));
            } catch (JsonProcessingException ex) {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJSWrapper::getCurrentCandle msg: " + ex.getMessage());
                _log.error(result.getString("message"));
            }

            return result;
        }
    }

    /**
     *
     */
    public static class StrategyRuleJSTest extends AbstractStrategyRule {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = -3345516391123859703L;

        /**
         * Default Constructor
         *
         * @param tradeService       TradeService
         * @param brokerManagerModel IBrokerModel
         * @param strategyData       StrategyData
         * @param tradestrategyId    Long
         */
        public StrategyRuleJSTest(TradeService tradeService, IBrokerModel brokerManagerModel, StrategyData strategyData, Long tradestrategyId) {
            super(tradeService, brokerManagerModel, strategyData, tradestrategyId);


        }

        /*
         * Note the current candle is just forming Enter a tier 1-3 gap in first
         * 5min bar direction, with a 3R target and stop @ 5min high/low
         *
         * @param candleSeries the series of candels that has been updated.
         *
         * @param newBar has a new bar just started.
         */

        /**
         * Method runStrategy.
         *
         * @param candleSeries CandleSeries
         * @param newBar       boolean
         */
        public void runStrategy(CandleSeries candleSeries, boolean newBar) {

            try (Context context = Context.enter()) {

                // Set the JavaScript language version (ECMAScript 6)
                context.setLanguageVersion(Context.VERSION_ES6);
                String candleSeriesJSON = JSONMapper.getJSONString(candleSeries);

                //Scriptable globalScope = context.initSafeStandardObjects();
                Scriptable globalScope = context.initStandardObjects();
                // ScriptableObject.defineClass(scope, StrategyRuleJS.class);

                // Create an instance of a Java class to expose to JavaScript
                //MyGlideSystem gs = new MyGlideSystem();
                StrategyRuleJSWrapper gs = new StrategyRuleJSWrapper(this);

                // Wrap the Java object for use in the JavaScript environment
                Object gsJsObject = Context.javaToJS(gs, globalScope);

                // Make the Java object available in JavaScript as the global variable 'gs'
                ScriptableObject.putProperty(globalScope, "gs", gsJsObject);

                String strategyName = "";
                String codeJS = this.getStrategyJS(strategyName);
                context.evaluateString(globalScope, codeJS, strategyName, 1, null);
                Object jsFunctionObj = globalScope.get("runStrategy", globalScope);

                if (!(jsFunctionObj instanceof Function)) {

                    _log.error("Error: StrategyRuleJS::runStrategy runStrategy is not a function");
                }
                Function jsFunction = (Function) jsFunctionObj;
                Object[] functionParams = new Object[]{candleSeriesJSON, true};
                Object jsResult = jsFunction.call(context, globalScope, globalScope, functionParams);
                _log.info("result: {}", jsResult);
            } catch (Exception ex) {

                _log.error("Error: StrategyRuleJS::runStrategy msg: {}", ex.getMessage());
            }
        }
    }
}
