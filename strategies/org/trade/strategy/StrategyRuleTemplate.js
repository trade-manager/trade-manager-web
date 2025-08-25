/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
let CONSTANTS = null;
let tradestrategy = null;
/**
 * Method call once to initialize the strategy in the worker thread.
 */
function initialize() {

    CONSTANTS = JSON.parse(gs.getInitParams());

    if (CONSTANTS.error) {

        gs.error(1, 100, 'Error: StrategyRuleJS::initialize failed to get constants  msg: ' + JSON.stringify(CONSTANTS.message));
    }

    CONSTANTS = CONSTANTS.data;
    tradestrategy = JSON.parse(gs.getTradestrategyJSON());

    if (tradestrategy.error) {

        gs.error(1, 100, 'Error: StrategyRuleJS::initialize failed to get tradestrategy  msg: ' + JSON.stringify(tradestrategy.message));
    }
    tradestrategy = tradestrategy.data;
    gs.log('Info: StrategyRuleJS::initialize CONSTANTS: ' + JSON.stringify(CONSTANTS));
    gs.log('Info: StrategyRuleJS::initialize tradestrategy: ' + JSON.stringify(tradestrategy));

    return gs.isCancelled();
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

