let CONSTANTS = null;

/**
 * Method call once to initialize the strategy in the worker thread.
 */
function initialize() {

    let result  = JSON.parse(gs.getInitParams());

    if (!result.error) {

        gs.log('Info: StrategyRuleJS::initialize result.data: ' + JSON.stringify(result.data));
        CONSTANTS = result.data;
    }
}

function runStrategy(candleSeriesJSON, newBar) {

    try {
        let candleSeries = JSON.parse(candleSeriesJSON);
        gs.log('Info: StrategyRuleJS::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar);

        // Get the current candle
        let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());
        gs.log('Info: StrategyRuleJS::runStrategy currentCandleItem: ' + JSON.stringify(currentCandleItem.candle.period.start));
        return candleSeries.key
    } catch (ex) {
    
        gs.error(1, 100, 'Error: StrategyRuleJS::runStrategy process javascript msg: ' + ex.getMessage());
    }
}

