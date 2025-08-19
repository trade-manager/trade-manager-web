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
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderDto;
import org.trade.core.persistent.dao.TradestrategyDto;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.JSONMapper;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.AllocationMethod;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.CalculationType;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.ContentType;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.IndicatorSeries;
import org.trade.core.valuetype.MarketBar;
import org.trade.core.valuetype.MarketBias;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.Percent;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.Tier;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TradestrategyStatus;
import org.trade.core.valuetype.TriggerMethod;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

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

    private Rule rule = null;
    private volatile Context context;
    private static Scriptable localScope;
    private static Function functionRunStrategy = null;

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
     * @param rule            Rule
     */
    public StrategyRuleJS(TradeService tradeService, IBrokerModel brokerModel, StrategyData strategyData, Long tradestrategyId, Rule rule) {

        this(tradeService, brokerModel, strategyData, tradestrategyId);
        this.rule = rule;
    }

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
     * Method call once to initialize the strategy in the worker thread.
     */
    public void initialize() {

        try {
            context = Context.enter();
            // Set the JavaScript language version (ECMAScript 6)
            context.setLanguageVersion(Context.VERSION_ES6);

            //Scriptable globalScope = context.initSafeStandardObjects();
            Scriptable globalScope = context.initStandardObjects();

            // Wrap the Java object for use in the JavaScript environment
            Object gsJsObject = Context.javaToJS(this, globalScope);

            // Make the Java object available in JavaScript as the global variable 'gs'
            ScriptableObject.putProperty(globalScope, "gs", gsJsObject);

            // Create  a local scope
            localScope = context.newObject(globalScope);
            String strategyName = "";
            String codeJS = null;

            if (null != this.getTradestrategy()) {

                strategyName = this.getTradestrategy().getStrategy().getName();
                Rule rule = this.getTradeService().findRuleByMaxVersion(this.getTradestrategy().getStrategy(), ContentType.JAVASCRIPT);
                codeJS = new String(rule.getRule());
            } else {

                // This is a test
                if (null != this.rule) {

                    codeJS = new String(this.rule.getRule());
                }
            }

            if (null != codeJS) {

                context.evaluateString(localScope, codeJS, strategyName, 1, null);

                Object initialize = localScope.get("initialize", localScope);

                if (initialize instanceof Function) {

                    Function functionInitStrategy = (Function) initialize;
                    Object jsResult = functionInitStrategy.call(context, localScope, localScope, null);
                    _log.info("Info: StrategyRuleJS::initialize initialize isCancelled: {}", jsResult);
                } else {

                    _log.error("Error: StrategyRuleJS::initialize initialize is not a function");
                }

                Object runStrategy = localScope.get("runStrategy", localScope);

                if (!(runStrategy instanceof Function)) {

                    _log.error("Error: StrategyRuleJS::initialize runStrategy is not a function");
                }

                functionRunStrategy = (Function) runStrategy;
            }
        } catch (Exception ex) {

            _log.error("Error: StrategyRuleJS::initialize msg: {}", ex.getMessage());
            this.cancel();
        }
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

        try {

            String candleSeriesJSON = JSONMapper.getJSONString(candleSeries);
            Object[] functionParams = new Object[]{candleSeriesJSON, true};
            Object jsResult = functionRunStrategy.call(context, localScope, localScope, functionParams);
            _log.info("Info: StrategyRuleJS::runStrategy runStrategy isCancelled: {}", jsResult);
        } catch (Exception ex) {

            _log.error("Error: StrategyRuleJS::runStrategy msg: {}", ex.getMessage());
            this.cancel();
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
     * Method log.
     */
    public boolean nil(Object value) {

        return null == value || value.equals("undefined");
    }

    /**
     * Method get current candle.
     *
     * @return JSONObject
     */
    public JSONObject getCurrentCandleJSON() {

        JSONObject result = getResult();

        try {

            CandleItem candle = this.getCurrentCandle();

            if (null != candle) {

                result.put("data", new JSONObject(JSONMapper.getJSONString(candle)));
            } else {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJS::getCurrentCandleJSON candle not found.");
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getCurrentCandleJSON msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    public JSONObject getOpenPositionOrderJSON() {

        JSONObject result = getResult();

        try {

            TradeOrderDto tradeOrder = JSONMapper.convertToDto(this.getOpenPositionOrder(), TradeOrderDto.class);

            if (null != tradeOrder) {

                result.put("data", new JSONObject(JSONMapper.getJSONString(tradeOrder)));
            } else {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJS::getOpenPositionOrderJSON tradeOrder not found.");
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getOpenPositionOrderJSON msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    /**
     * Method get the trade strategy.
     *
     * @return JSONObject
     */
    public JSONObject getTradestrategyJSON() {

        JSONObject result = getResult();

        try {

            TradestrategyDto tradestrategyDto = JSONMapper.convertToDto(this.getTradestrategy(), TradestrategyDto.class);

            if (null != tradestrategyDto) {

                result.put("data", new JSONObject(JSONMapper.getJSONString(tradestrategyDto)));
            } else {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJS::getTradestrategyJSON tradestrategy not found");
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getTradestrategyJSON msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    /**
     * Method get the trade order.
     *
     * @return JSONObject
     */
    public JSONObject getTradeOrderJSON(Integer orderKey) {

        JSONObject result = getResult();

        try {

            TradeOrder tradeOrder = super.getTradeOrder(orderKey);

            if (null != tradeOrder) {

                TradeOrderDto tradeOrderDto = JSONMapper.convertToDto(tradeOrder, TradeOrderDto.class);
                result.put("data", new JSONObject(JSONMapper.getJSONString(tradeOrderDto)));
            } else {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJS::getTradeOrderJSON tradeOrder not found orderKey: " + orderKey);
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getTradeOrderJSON msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }


    /**
     * Method cancel a tradeOrder
     */
    public void cancelOrder(String tradeOrderJSON) {

        JSONObject result = getResult();

        try {

            TradeOrderDto tradeOrderDto = JSONMapper.getDTO(tradeOrderJSON, TradeOrderDto.class);
            TradeOrder tradeOrder = JSONMapper.convertToEntity(tradeOrderDto, TradeOrder.class);

            if (null != tradeOrder) {

                this.cancelOrder(tradeOrder);
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getCurrentCandleJSON msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }
    }

    /**
     * @param high  double
     * @param low   double
     * @param value double
     * @return true/false
     */
    public boolean between(double high, double low, double value) {

        return CoreUtils.isBetween(high, low, value);
    }

    /**
     * @param dateString String
     * @return JSONObject
     */
    public JSONObject getCandle(String dateString) {

        JSONObject result = getResult();

        try {

            ZonedDateTime date = ZonedDateTime.parse(dateString);
            date = TradingCalendar.getDateAtTime(date,
                    this.getTradestrategy().getTradingday().getOpen());
            CandleItem candle = super.getCandle(date);

            if (null != candle) {

                result.put("data", new JSONObject(JSONMapper.getJSONString(candle)));
            } else {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJS::getCandle candle not found.");
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getCandle msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    /**
     * @param value double
     * @return JSONObject
     */
    public JSONObject getEntryLimit(double value) {

        JSONObject result = getResult();

        try {

            result.put("data", new JSONObject(JSONMapper.getJSONString(this.getEntryLimit().getValue(new Money(value)))));
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getEntryLimit msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    /**
     * @param action     String
     * @param entryPrice double
     * @param stopPrice  double
     * @param transmit   boolean
     * @param FAProfile  String
     * @param FAGroup    String
     * @param FAMethod   String
     * @param FAPercent  String
     * @return TradeOrder
     */
    public JSONObject createRiskOpenPosition(String action, double entryPrice, double stopPrice, boolean transmit,
                                             String FAProfile, String FAGroup, String FAMethod, double FAPercent) {
        JSONObject result = getResult();

        try {

            TradeOrder tradeOrder = this.createRiskOpenPosition(action, new Money(entryPrice), new Money(stopPrice), transmit,
                    FAProfile, FAGroup, FAMethod, new Percent(FAPercent));

            if (null != tradeOrder) {

                TradeOrderDto tradeOrderDto = JSONMapper.convertToDto(tradeOrder, TradeOrderDto.class);
                result.put("data", new JSONObject(JSONMapper.getJSONString(tradeOrderDto)));
            } else {

                result.put("error", true);
                result.put("message", "Error: StrategyRuleJS::createRiskOpenPosition tradeOrder not created.");
            }
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::createRiskOpenPosition msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    /**
     * @return JSONObject
     */
    public JSONObject getInitParams() {

        JSONObject result = getResult();

        try {

            JSONObject constantsJSON = new JSONObject();

            List<Decode> chartDaysCodes = ChartDays.newInstance().getCodesDecodes();
            JSONObject chartDaysValuesJSON = new JSONObject();
            for (Decode code : chartDaysCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    chartDaysValuesJSON.put(code.getCode(), Integer.valueOf(code.getValue()));
                }
            }

            constantsJSON.put("CHART_DAYS", chartDaysValuesJSON);

            List<Decode> timeInForceCodes = TimeInForce.newInstance().getCodesDecodes();
            JSONObject timeInForceValuesJSON = new JSONObject();
            for (Decode code : timeInForceCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    timeInForceValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("TIME_IN_FORCE", timeInForceValuesJSON);

            List<Decode> tierCodes = Tier.newInstance().getCodesDecodes();
            JSONObject tierValuesJSON = new JSONObject();
            for (Decode code : tierCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    tierValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("TIER", tierValuesJSON);

            List<Decode> exchangeCodes = Exchange.newInstance().getCodesDecodes();
            JSONObject exchangeValuesJSON = new JSONObject();
            for (Decode code : exchangeCodes) {

                if (!code.getCode().isEmpty()) {

                    exchangeValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("EXCHANGE", exchangeValuesJSON);

            List<Decode> marketBiasCodes = MarketBias.newInstance().getCodesDecodes();
            JSONObject marketBiasValuesJSON = new JSONObject();
            for (Decode code : marketBiasCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    marketBiasValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("MARKET_BIAS", marketBiasValuesJSON);

            List<Decode> marketBarCodes = MarketBar.newInstance().getCodesDecodes();
            JSONObject marketBarValuesJSON = new JSONObject();
            for (Decode code : marketBarCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    marketBarValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("MARKET_BAR", marketBarValuesJSON);

            List<Decode> indicatorSeriesCodes = IndicatorSeries.newInstance().getCodesDecodes();
            JSONObject indicatorSeriesValuesJSON = new JSONObject();
            for (Decode code : indicatorSeriesCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    indicatorSeriesValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("INDICATOR_SERIES", indicatorSeriesValuesJSON);

            List<Decode> allocationMethodCodes = AllocationMethod.newInstance().getCodesDecodes();
            JSONObject allocationMethodValuesJSON = new JSONObject();
            for (Decode code : allocationMethodCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    allocationMethodValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("ALLOCATION_METHOD", allocationMethodValuesJSON);

            List<Decode> calculationTypeCodes = CalculationType.newInstance().getCodesDecodes();
            JSONObject calculationTypeValuesJSON = new JSONObject();
            for (Decode code : calculationTypeCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    calculationTypeValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("CALCULATION_TYPE", calculationTypeValuesJSON);

            List<Decode> barSizeCodes = BarSize.newInstance().getCodesDecodes();
            JSONObject barSizeValuesJSON = new JSONObject();
            for (Decode code : barSizeCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    barSizeValuesJSON.put(code.getCode(), Integer.valueOf(code.getValue()));
                }
            }

            constantsJSON.put("BAR_SIZE", barSizeValuesJSON);

            List<Decode> triggerMethodCodes = TriggerMethod.newInstance().getCodesDecodes();
            JSONObject triggerMethodValuesJSON = new JSONObject();
            for (Decode code : triggerMethodCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    triggerMethodValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("TRIGGER_METHOD", triggerMethodValuesJSON);

            List<Decode> orderTypeCodes = OrderType.newInstance().getCodesDecodes();
            JSONObject orderTypeValuesJSON = new JSONObject();
            for (Decode code : orderTypeCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    orderTypeValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("ORDER_TYPE", orderTypeValuesJSON);

            List<Decode> tradestrategyStatusCodes = TradestrategyStatus.newInstance().getCodesDecodes();
            JSONObject tradestrategyStatusValuesJSON = new JSONObject();
            for (Decode code : tradestrategyStatusCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    tradestrategyStatusValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("TRADESTRATEGY_STATUS", tradestrategyStatusValuesJSON);

            List<Decode> actionCodes = Action.newInstance().getCodesDecodes();
            JSONObject actionValuesJSON = new JSONObject();
            for (Decode code : actionCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    actionValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("ACTION", actionValuesJSON);

            List<Decode> orderStatusCodes = OrderStatus.newInstance().getCodesDecodes();
            JSONObject orderStatusValuesJSON = new JSONObject();
            for (Decode code : orderStatusCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    orderStatusValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("ORDER_STATUS", orderStatusValuesJSON);

            List<Decode> sideCodes = Side.newInstance().getCodesDecodes();
            JSONObject sideValuesJSON = new JSONObject();
            for (Decode code : sideCodes) {

                if (!code.getCode().trim().isEmpty()) {

                    sideValuesJSON.put(code.getCode(), code.getValue());
                }
            }

            constantsJSON.put("SIDE", sideValuesJSON);
            result.put("data", constantsJSON);
        } catch (Exception ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJS::getInitParams msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result;
    }

    protected void done() {

        try {

            super.done();

            if (null != context) {

                context.exit();
                _log.info("Info: StrategyRuleJS::done closed context successfully.");
            }
        } catch (Exception ex) {

            _log.error("Error: StrategyRuleJS::done msg: {}, context: {}", ex.getMessage(), context);
        } finally {

            context = null;
        }
    }

    private JSONObject getResult() {

        return new JSONObject("{'error': false, 'message': ''}");
    }
}
