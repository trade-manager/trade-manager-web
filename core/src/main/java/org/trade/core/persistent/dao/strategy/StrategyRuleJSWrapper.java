package org.trade.core.persistent.dao.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.util.JSONMapper;

import java.io.Serial;
import java.io.Serializable;

public class StrategyRuleJSWrapper implements Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3345516391123859703L;

    private final static Logger _log = LoggerFactory.getLogger(StrategyRuleJSWrapper.class);

    private final StrategyRuleJS strategyRuleJS;
    private static final JSONObject result = new JSONObject("{'error': false, 'message': ''}");

    /**
     * Default Constructor
     *
     * @param strategyRuleJS StrategyRuleJS
     */
    public StrategyRuleJSWrapper(StrategyRuleJS strategyRuleJS) {

        this.strategyRuleJS = strategyRuleJS;
    }

    /**
     * Method log.
     */
    public void log(String message) {

        this.strategyRuleJS.log(message);
    }

    /**
     * Method get current candle.
     */
    public String getCurrentCandle() {

        try {

            CandleItem candle = strategyRuleJS.getCurrentCandle();
            result.put("candle", new JSONObject(JSONMapper.getJSONString(candle)));
        } catch (JsonProcessingException ex) {

            result.put("error", true);
            result.put("message", "Error: StrategyRuleJSWrapper::getCurrentCandle msg: " + ex.getMessage());
            _log.error(result.getString("message"));
        }

        return result.toString();
    }
}
