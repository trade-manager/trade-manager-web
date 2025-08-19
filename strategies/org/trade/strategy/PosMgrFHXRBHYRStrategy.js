    let CONSTANTS = null;

    /**
     * Method call once to initialize the strategy in the worker thread.
     */
    function initialize() {

        let result  = JSON.parse(gs.getInitParams());

        if(!result.error){

            gs.log('Info: StrategyRuleJS::initialize result.data: ' + JSON.stringify(result.data));
            CONSTANTS = result.data;
        }
    }

    function runStrategy(candleSeriesJSON, newBar) {

        try {

            let candleSeries = JSON.parse(candleSeriesJSON);
            gs.log('Info: StrategyRuleJS::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar + ' symbol: ' + gs.getSymbol());

            let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());

            if (currentCandleItem.error) {

                gs.cancel();
                return gs.isCancelled();
            }

            let tradestrategy = JSON.parse(gs.getTradestrategyJSON());
            tradestrategy = tradestrategy.data;
            gs.log('Info: StrategyRuleJS::runStrategy currentCandleItem: ' + JSON.stringify(currentCandleItem));
            gs.log('Info: StrategyRuleJS::runStrategy tradestrategy: ' + JSON.stringify(tradestrategy));
            gs.log('Info: StrategyRuleJS::runStrategy openPositionOrderKey: ' + openPositionOrderKey);
            // Get the current candle start date
            let startPeriod = new Date(currentCandleItem.data.period.start);
            gs.log('Info: StrategyRuleJS::runStrategy startPeriod: ' + startPeriod);

            /*
             * Get the current open trade. If no trade is open this Strategy
             * will be closed down.
             */
            if (!gs.isThereOpenPosition()) {

                gs.log("No open position so Cancel Strategy Mgr symbol: {} startPeriod: {}", gs.getSymbol(), startPeriod);
                gs.cancel();
                return gs.isCancelled();
            }

             /*
              * If all trades are closed shut down the position user
              *
              * Note this strategy is run as soon as we enter a position.
              *
              * Check to see if the open position is filled and the open quantity
              * is > 0 also check to see if we already have this position
              * covered.
              */
             if (gs.isThereOpenPosition() && !gs.isPositionCovered()) {

                 /*
                  * Position has been opened and not covered submit the target
                  * and stop orders for the open quantity. Two targets at 2R and
                  * 2R Stop and 2X actual stop this will be managed to 1R below
                  *
                  * Make the stop -2R and manage to the Vwap MA of the opening
                  * bar.
                  */
                 let quantity = gs.getOpenPositionOrder().getFilledQuantity();
                 let tgt1Qty = quantity / 2;
                 let tgt2Qty = quantity - tgt1Qty;

                 // Integer tgt3Qty = quantity - (tgt1Qty + tgt2Qty);
                 let tradeOrder = JSON.parse(gs.getOpenPositionOrderJSON());

                 if(tradeOrder.error){

                     gs.cancel();
                     return gs.isCancelled();
                 }

                 tradeOrder = tradeOrder.data;
                 gs.createStopAndTargetOrder(tradeOrder, 2, 0.01, 2, 0.01, tgt1Qty, true);
                 gs.createStopAndTargetOrder(tradeOrder, 2, 0.01, 2, 0.01, tgt2Qty, true);
                 // createStopAndTargetOrder(getOpenPositionOrder(),
                 // 2,0.01,4,0.01, tgt3Qty, true);
                 gs.log("Open position submit Stop/Tgt orders created symbol: {} startPeriod: {}", gs.getSymbol(), startPeriod);
             }
            return gs.isCancelled();
        } catch (ex) {
            gs.error(1, 100, 'Error: StrategyRuleJS::runStrategy process javascript msg: ' + ex.getMessage());
        }
    }

