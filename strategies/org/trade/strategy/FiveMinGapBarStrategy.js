function runStrategy(candleSeriesJSON, newBar) {

    try {
        let candleSeries = JSON.parse(candleSeriesJSON);
        gs.log('Info: StrategyRuleJS::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar);

        // Get the current candle
        let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());
        gs.log('Info: StrategyRuleJS::runStrategy currentCandleItem: ' + currentCandleItem.candle.side);
        return candleSeries.key
    } catch (ex) {
        gs.error(1, 100, 'Error: StrategyRuleJS::runStrategy process javascript msg: ' + ex.getMessage());
    }
}

