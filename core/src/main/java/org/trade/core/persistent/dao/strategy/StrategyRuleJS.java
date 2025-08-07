/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
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
package org.trade.core.persistent.dao.strategy;

import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.util.JSONMapper;
import org.trade.core.valuetype.ContentType;

import java.io.Serial;

/**
 *
 */
public class StrategyRuleJS extends AbstractStrategyRule {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4876874276185644936L;

    private final static Logger _log = LoggerFactory.getLogger(StrategyRuleJS.class);

    private static final JSONObject result = new JSONObject("{'error': false, 'message': ''}");

    /**
     * Constructor for AbstractStrategyRule. An abstract class that implements
     * the base functionality for a trading strategies this class monitors the
     * candle data set for changes. This class runs in its own thread. there
     * will be one Strategy running per tradestrategy.
     *
     * @param tradeService    TradeService
     * @param brokerModel     IBrokerModel
     * @param strategyData    StrategyData
     * @param tradestrategyId Integer
     */
    public StrategyRuleJS(TradeService tradeService, IBrokerModel brokerModel, StrategyData strategyData, Long tradestrategyId) {

        super(tradeService, brokerModel, strategyData, tradestrategyId);
    }

    /**
     * Method runStrategy. This method is called every time the candleSeries is
     * either updated or a candleItem is added.
     * <p>
     * <p>
     * If market data is selected this will fire every time the last price falls
     * outside the H/L of the current candle. Note also if market data is
     * selected the current Bid/Ask/Last can be accessed via the
     * candleSeries.getContract().
     * <p>
     * If market data is not selected this method fires every 5sec as real time
     * bars update the current candle.
     *
     * @param candleSeries CandleSeries
     * @param newBar       boolean when ever a new bar is added to the candleSeries.
     */
    public void runStrategy(CandleSeries candleSeries, boolean newBar) {

        try (Context context = Context.enter()) {

            // Set the JavaScript language version (ECMAScript 6)
            context.setLanguageVersion(Context.VERSION_ES6);

            //Scriptable globalScope = context.initSafeStandardObjects();
            Scriptable globalScope = context.initStandardObjects();

            // Wrap the Java object for use in the JavaScript environment
            Object gsJsObject = Context.javaToJS(this, globalScope);

            // Make the Java object available in JavaScript as the global variable 'gs'
            ScriptableObject.putProperty(globalScope, "gs", gsJsObject);

            String strategyName = "";
            String codeJS = this.getStrategyJS(strategyName);
            Rule rule = this.getTradeService().findRuleByMaxVersion(this.getTradestrategy().getStrategy(), ContentType.JAVASCRIPT);

            if (null != rule) {

                codeJS = new String(rule.getRule());
            }

            context.evaluateString(globalScope, codeJS, strategyName, 1, null);
            Object jsFunctionObj = globalScope.get("runStrategy", globalScope);

            if (!(jsFunctionObj instanceof Function)) {

                _log.error("Error: StrategyRuleJS::runStrategy runStrategy is not a function");
            }

            Function jsFunction = (Function) jsFunctionObj;
            String candleSeriesJSON = JSONMapper.getJSONString(candleSeries);
            Object[] functionParams = new Object[]{candleSeriesJSON, true};
            Object jsResult = jsFunction.call(context, globalScope, globalScope, functionParams);
            _log.info("result: {}", jsResult);
        } catch (Exception ex) {

            _log.error("Error: StrategyRuleJS::runStrategy msg: {}", ex.getMessage());
        }
    }

    /**
     * Method error.
     */
    public void error(int id, int errorCode, String errorMsg) {

        super.error(id, errorCode, errorMsg);
    }

    /**
     * Method log.
     */
    public void log(String message) {

        _log.info(message);
    }

    /**
     * Method get current candle.
     *
     * @return
     */
    public JSONObject getCurrentCandleJSON() {

        try {

            CandleItem candle = this.getCurrentCandle();
            result.put("candle", new JSONObject(JSONMapper.getJSONString(candle)));
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJSWrapper::getCurrentCandle msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }
}
