let CONSTANTS = null;
let tradestrategy = null;
/**
 * Method call once to initialize the strategy in the worker thread.
 */
function initialize() {

    CONSTANTS = JSON.parse(gs.getInitParams());

    if (CONSTANTS.error) {

        gs.error(1, 100, 'Error: PosMgrFHXRBHYRStrategy::initialize failed to get constants  msg: ' + JSON.stringify(CONSTANTS.message));
    }

    CONSTANTS = CONSTANTS.data;
    tradestrategy = JSON.parse(gs.getTradestrategyJSON());

    if (tradestrategy.error) {

        gs.error(1, 100, 'Error: PosMgrFHXRBHYRStrategy::initialize failed to get tradestrategy  msg: ' + JSON.stringify(tradestrategy.message));
    }
    tradestrategy = tradestrategy.data;
    gs.log('Info: PosMgrFHXRBHYRStrategy::initialize CONSTANTS: ' + JSON.stringify(CONSTANTS));
    gs.log('Info: PosMgrFHXRBHYRStrategy::initialize tradestrategy: ' + JSON.stringify(tradestrategy));

    return gs.isCancelled();
}

let openPositionOrderKey = null;

function runStrategy(candleSeriesJSON, newBar) {

    try {

        let candleSeries = JSON.parse(candleSeriesJSON);
        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar + ' symbol: ' + gs.getSymbol());

        let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());

        if (currentCandleItem.error) {

            gs.cancel();
            return gs.isCancelled();
        }

        currentCandleItem = currentCandleItem.data;

        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy currentCandleItem: ' + JSON.stringify(currentCandleItem));
        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy openPositionOrderKey: ' + openPositionOrderKey);
        // Get the current candle start date
        let startPeriod = new Date(currentCandleItem.period.start);
        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy startPeriod: ' + startPeriod);

        /*
         * Get the current open trade. If no trade is open this Strategy
         * will be closed down.
         */
        if (!gs.isThereOpenPosition()) {

            gs.log("No open position so Cancel Strategy Mgr symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);
            gs.cancel();
            return gs.isCancelled();
        }

        let tradePosition = JSON.parse(gs.getOpenTradePositionJSON());

        if (tradePosition.error) {

            gs.cancel();
            return gs.isCancelled();
        }

        tradePosition = tradePosition.data;
        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy tradePosition: ' + JSON.stringify(tradePosition));

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
            // Integer tgt3Qty = quantity - (tgt1Qty + tgt2Qty);
            let tradeOrder = JSON.parse(gs.getOpenPositionOrderJSON());

            if (tradeOrder.error) {

                gs.cancel();
                return gs.isCancelled();
            }

            tradeOrder = tradeOrder.data;
            let quantity = tradeOrder.filledQuantity;
            let tgt1Qty = quantity / 2;
            let tgt2Qty = quantity - tgt1Qty;

            gs.createStopAndTargetOrder(tradeOrder, 2, 0.01, 2, 0.01, tgt1Qty, true);
            gs.createStopAndTargetOrder(tradeOrder, 2, 0.01, 2, 0.01, tgt2Qty, true);
            // createStopAndTargetOrder(getOpenPositionOrder(), 2,0.01,4,0.01, tgt3Qty, true);
            gs.log("Open position submit Stop/Tgt orders created symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);
        }

        /*
         * Manage the stop orders if the current bars Vwap crosses the Vwap
         * of the first 5min bar then move the stop price ( currently -2R)
         * to the average fill price i.e. break even. This allows for tails
         * that break the 5min high/low between 9:40 thru 15:30.
         */
        let startCandleDate = new Date(tradestrategy.tradingday.open);
        startCandleDate.setMinutes((new Date(tradestrategy.tradingday.open)).getMinutes() + 5);
        let timeOutDate = new Date(tradestrategy.tradingday.close);
        timeOutDate.setMinutes((new Date(tradestrategy.tradingday.close)).getMinutes() - 30);
        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy startPeriod: ' + startPeriod + " startCandleDate: " + startCandleDate + " timeOutDate: " + timeOutDate);

        if (startPeriod.getTime() < timeOutDate.getTime() && startPeriod.getTime() > startCandleDate.getTime()) {

            let firstCandle = JSON.parse(gs.getCandle(startPeriod.toISOString()));

            if (firstCandle.error) {

                gs.cancel();
                return gs.isCancelled();
            }

            firstCandle = firstCandle.data;

            let tradeOrder = JSON.parse(gs.getOpenPositionOrderJSON());

            if (tradeOrder.error) {

                throw tradeOrder.message;
                gs.cancel();
                return gs.isCancelled();
            }

            tradeOrder = tradeOrder.data;
            gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy firstCandle: ' + JSON.stringify(firstCandle));
            gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy tradeOrder: ' + JSON.stringify(tradeOrder));
            gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy current.vwap: ' + currentCandleItem.vwap + "firstCandle vwap: " + firstCandle.vwap);

            if (CONSTANTS.SIDE.BOT === tradePosition.side) {

                if (currentCandleItem.vwap < firstCandle.vwap) {

                    let stopPrice = gs.addPennyAndRoundStop(tradeOrder.averageFilledPrice, tradeOrder.side, CONSTANTS.ACTION.SELL, 0.01);
                    gs.moveStopOCAPrice(stopPrice, true);
                    gs.log("Move Stop to b.e. Strategy Mgr Symbol: " + gs.getSymbol() + " Time:" + startPeriod + " Price: " + stopPrice + " first bar Vwap: " + firstCandle.getVwap() + " Curr Vwap: " + currentCandleItem.vwap);
                }
            } else {

                if (currentCandleItem.vwap > firstCandle.vwap) {

                    let stopPrice = gs.addPennyAndRoundStop(tradeOrder.averageFilledPrice, tradeOrder.side, CONSTANTS.ACTION.BUY, 0.01);
                    gs.moveStopOCAPrice(stopPrice, true);
                    gs.log("Move Stop to b.e. Strategy Mgr Symbol: " + gs.getSymbol() + " Time:" + startPeriod + " Price: " + stopPrice + " first bar Vwap: " + firstCandle.getVwap() + " Curr Vwap: " + currentCandleItem.vwap);
                }
            }
        }

        /*
         * At 15:30 Move stop order to b.e. i.e. the average fill price of
         * the open order.
         */
        if (startPeriod.getTime() === timeOutDate.getTime() && newBar) {

            gs.log("Rule move stop to b.e.. symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);
            let action = CONSTANTS.ACTION.SELL;
            let avgPrice = tradePosition.totalBuyValue / tradePosition.totalBuyQuantity;
            let prevCandleItem = null;

            if (gs.getCurrentCandleCount() > 0) {

                prevCandleItem =  candleSeries.data[(gs.getCurrentCandleCount() - 1)];
                gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy prevCandleItem: ' + JSON.stringify(prevCandleItem));
            }

            if (null != prevCandleItem && avgPrice < prevCandleItem.candle.low) {

                avgPrice = prevCandleItem.candle.low;
            }

            if (CONSTANTS.SIDE.SLD === tradePosition.side) {

                action = CONSTANTS.ACTION.BUY;
                avgPrice = tradePosition.totalSellValue / tradePosition.totalSellQuantity;

                if (avgPrice > prevCandleItem.candle.high) {

                    avgPrice = prevCandleItem.candle.high;
                }
            }

            let stopPrice = gs.addPennyAndRoundStop(avgPrice, tradePosition.side, action, 0.01);
            gs.moveStopOCAPrice(stopPrice, true);
        }

        /*
         * Move stock to b.e. when target one hit.
         */
        let targetOneOrder = JSON.parse(gs.getTargetOneOrderJSON());

        if (targetOneOrder.error) {

            gs.cancel();
            return gs.isCancelled();
        }

        targetOneOrder = targetOneOrder.data;
        gs.log('Info: PosMgrFHXRBHYRStrategy::runStrategy targetOneOrder: ' + JSON.stringify(targetOneOrder));

        if (null != targetOneOrder) {

            if (targetOneOrder.isFilled && newBar) {

                gs.log("Rule move stop to b.e. after target one hit symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);
                let action = CONSTANTS.ACTION.SELL;

                if (CONSTANTS.SIDE.SLD === tradePosition.side) {

                    action = CONSTANTS.ACTION.BUY;
                }

                let newStop = gs.addPennyAndRoundStop(targetOneOrder.averageFilledPrice, tradePosition.side, action, 0.01);

                if (!(newStop === gs.getStopPriceMinUnfilled())) {
                    // moveStopOCAPrice(newStop, true);
                }
            }
        }

        /*
         * We have sold the first half of the position try to trail BH on
         * one minute bars.
         */
        if (null != targetOneOrder) {

            if (targetOneOrder.isFilled) {

                let newStop = gs.getOneMinuteTrailStop(gs.getStopPriceMinUnfilled());

                if (!newStop === gs.getStopPriceMinUnfilled()) {

                    gs.log("PositionManagerStrategy OneMinuteTrail, symbol: " + gs.getSymbol() + " Trail Price: " + newStop + " Time: " + startPeriod + " Side: " + tradePosition.side);
                    // moveStopOCAPrice(newStop, true);
                }
           }
       }

        /*
         * Close any opened positions with a market order at the end of the
         * day.
         */
        let lastTimeOutDate = new Date(tradestrategy.tradingday.close);
        lastTimeOutDate.setMinutes((new Date(tradestrategy.tradingday.close)).getMinutes() - 2);
        let endPeriod = new Date(currentCandleItem.period.end);

        if (endPeriod.getTime() > lastTimeOutDate.getTime()) {

            gs.cancelOrdersClosePosition(true);
            gs.log("PositionManagerStrategy 15:58:00 done, symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);
            gs.cancel();
        }

        return gs.isCancelled();
    } catch (ex) {

        gs.error(1, 100, 'Error: PosMgrFHXRBHYRStrategy::runStrategy process javascript msg: ' + ex.getMessage());
    }
}
