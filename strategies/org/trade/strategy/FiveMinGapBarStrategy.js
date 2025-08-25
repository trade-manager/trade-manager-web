/**
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

        gs.error(1, 100, 'Error: FiveMinGapBarStrategy::initialize failed to get constants  msg: ' + JSON.stringify(CONSTANTS.message));
    }

    CONSTANTS = CONSTANTS.data;
    tradestrategy = JSON.parse(gs.getTradestrategyJSON());

    if (tradestrategy.error) {

        gs.error(1, 100, 'Error: FiveMinGapBarStrategy::initialize failed to get tradestrategy  msg: ' + JSON.stringify(tradestrategy.message));
    }
    tradestrategy = tradestrategy.data;
    gs.log('Info: FiveMinGapBarStrategy::initialize CONSTANTS: ' + JSON.stringify(CONSTANTS));
    gs.log('Info: FiveMinGapBarStrategy::initialize tradestrategy: ' + JSON.stringify(tradestrategy));

    return gs.isCancelled();
}

/**
 * 1/ Enter in the direction of the first 5min bar with a stop at the first
 * 5min bars open. Use a STPLMT order with a range from the Entry Limit
 * table.
 * <p>
 * 2/ Bar must be within the Entry Limit table % Range Bar. i.e in this case
 * 2%
 * <p>
 * 3/ If the position is not filled by 10:30 cancel the order.
 * <p>
 * 4/ Add 1c to the entry price and round over/under whole/half numbers in
 * the direction of the trade, same for stop price.
 * <p>
 * E.g. If first 5min bar is H=21.50 L=21.15 Open= 21.2 Close=21.40 then
 * position order will be Buy STPLMT=21.51-21.55 (STPLMT range 0.04 from
 * EntryLimit table and as we are at 21.5 we also buy over/under whole/half
 * numbers), Quantity=Risk/(21.51-21.2) rounded to +/-100 shares (see
 * EntryLimit table).
 */
let openPositionOrderKey = null;

function runStrategy(candleSeriesJSON, newBar) {

    try {

        let candleSeries = JSON.parse(candleSeriesJSON);
        gs.log('Info: FiveMinGapBarStrategy::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar + ' symbol: ' + gs.getSymbol());

        let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());

        if (currentCandleItem.error) {

            gs.cancel();
            return gs.isCancelled();
        }

        currentCandleItem = currentCandleItem.data;
        gs.log('Info: FiveMinGapBarStrategy::runStrategy currentCandleItem: ' + JSON.stringify(currentCandleItem));
        gs.log('Info: FiveMinGapBarStrategy::runStrategy openPositionOrderKey: ' + openPositionOrderKey);
        // Get the current candle start date
        let startPeriod = new Date(currentCandleItem.period.start);
        gs.log('Info: FiveMinGapBarStrategy::runStrategy startPeriod: ' + startPeriod);

        /*
         * Trade is open kill this Strategy as its job is done.
         */
        if (gs.isThereOpenPosition()) {

            gs.log("Strategy complete open position filled symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);

            /*
             * If the order is partial filled chaeck and if the risk goes
             * beyond 1 risk unit cancel the openPositionOrder this will
             * cause it to be marked as filled.
             */
             let tradeOrder = JSON.parse(gs.getOpenPositionOrderJSON());

             gs.log('Info: FiveMinGapBarStrategy::runStrategy tradeOrder: ' + JSON.stringify(tradeOrder));

            if (!tradeOrder.error) {

                tradeOrder = tradeOrder.data;

                if (CONSTANTS.ORDER_STATUS.PARTIALFILLED == tradeOrder.status && gs.isRiskViolated(currentCandleItem.close, tradestrategy.risk_amount,
                        tradeOrder.quantity, tradeOrder.average_filled_price)) {

                    gs.cancelOrder(tradeOrder);
                }
            }

            gs.cancel();
            return gs.isCancelled();
        }

        /*
         * Open position order was cancelled kill this Strategy as its job
         * is done.
         */
        let tradeOrder = JSON.parse(gs.getTradeOrderJSON(openPositionOrderKey));
        gs.log('Info: FiveMinGapBarStrategy::runStrategy tradeOrder: ' + JSON.stringify(tradeOrder));

        if (!tradeOrder.error) {

            tradeOrder = tradeOrder.data;

            if (null != openPositionOrderKey && !tradeOrder.active) {

                gs.info("Strategy complete open position cancelled symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod);
                gs.updateTradestrategyStatus(CONSTANTS.ORDER_STATUS.PARTIALFILLED);
                gs.cancel();
                return gs.isCancelled();
            }
        }

        /*
         * Is it the the 9:35 candle? and we have not created an open
         * position trade.
         */
        let startCandleDate = new Date(tradestrategy.tradingday.open);
        startCandleDate.setMinutes((new Date(tradestrategy.tradingday.open)).getMinutes() + (tradestrategy.barSize / 60));
        let timeOutDate = new Date(tradestrategy.tradingday.open);
        timeOutDate.setMinutes((new Date(tradestrategy.tradingday.open)).getMinutes() + 120);
        gs.log('Info: FiveMinGapBarStrategy::runStrategy startPeriod: ' + startPeriod + " startCandleDate: " + startCandleDate + " timeOutDate: " + timeOutDate);

        if (startPeriod.getTime() === startCandleDate.getTime() && newBar) {

            /*
             * Add the tails as a % of the body. 10% and vwap must be
             * between O/C.
             */
            let prevCandleItem = null;

            if (gs.getCurrentCandleCount() > 0) {

                prevCandleItem =  candleSeries.data[(gs.getCurrentCandleCount() - 1)];
                gs.log('Info: FiveMinGapBarStrategy::runStrategy prevCandleItem: ' + JSON.stringify(prevCandleItem));
            }

            if (null != prevCandleItem && (gs.between(prevCandleItem.candle.open, prevCandleItem.candle.close, prevCandleItem.candle.vwap))) {

               let barBodyPercent = (Math.abs(prevCandleItem.candle.open - prevCandleItem.candle.close)/ Math.abs(prevCandleItem.candle.high - prevCandleItem.candle.low)) * 100;
               gs.log('Info: FiveMinGapBarStrategy::runStrategy barBodyPercent: ' + barBodyPercent);

               if (barBodyPercent < 10) {

                   gs.log("Bar Body outside % range  Symbol: " + gs.getSymbol() + " Time: " + startPeriod + " barBodyPercent: " + barBodyPercent);
                   gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.NBB);
               }
            }

            let side = CONSTANTS.SIDE.SLD;

            if (prevCandleItem.candle.side) {

                side = CONSTANTS.SIDE.BOT;
            }

            let price = prevCandleItem.candle.high;
            let priceStop = prevCandleItem.candle.low;
            let action = CONSTANTS.ACTION.BUY;

            if (side === CONSTANTS.SIDE.SLD) {

                price = prevCandleItem.candle.low;
                priceStop = prevCandleItem.candle.high;
                action = CONSTANTS.ACTION.SELL;
            }

            let priceClose = prevCandleItem.candle.close;
            let highLowRange = Math.abs(prevCandleItem.candle.high - prevCandleItem.candle.low);
            priceStop = prevCandleItem.candle.open
            let entrylimit = JSON.parse(gs.getEntryLimit(priceClose));

            if (entrylimit.error) {

                gs.cancel();
                return gs.isCancelled();
            }

            entrylimit = entrylimit.data;
            gs.log('Info: FiveMinGapBarStrategy::runStrategy entrylimit: ' + JSON.stringify(entrylimit));

            // If the candle less than the entry limit %
            if ((highLowRange / prevCandleItem.candle.close) < entrylimit.percentOfPrice) {

                /*
                 * Check that the entry - stop is greater than 2* the STPLMT
                 * amount.
                 */
                // if (Math.abs(price.subtract(priceStop).doubleValue()) > (entrylimit.limitAmount * 2)) {

                /*
                 * Create an open position.
                 */
                gs.log("We have a trade!!  Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                let tradeOrder = JSON.parse(gs.createRiskOpenPosition(action, price, priceStop, true, null, null, null, 0));

                if (tradeOrder.error) {

                    gs.cancel();
                    return gs.isCancelled();
                }

                tradeOrder = tradeOrder.data;
                openPositionOrderKey = tradeOrder.orderKey;
                gs.log("We have a trade!!  trade order key: " + openPositionOrderKey);

                // } else {
                // gs.log("Rule 9:35 5min bar less than 2 * stop limits. Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                // gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.NBB);
                // // Kill this process we are done!
                // gs.cancel();
                // return;
                // }
            } else {

                gs.log("Rule 9:35 5min bar outside % limits. Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.PERCENT);
                // Kill this process we are done!
                gs.cancel();
                return gs.isCancelled();
            }
        } else {

            if (startPeriod.getTime() < timeOutDate.getTime()
                    && startPeriod.getTime() > startCandleDate.getTime()) {

                let firstCandle = JSON.parse(gs.getCandle((new Date(tradestrategy.tradingday.open)).toISOString()));
                gs.log("Between trading day start and time out period, firstCandle: " + JSON.stringify(firstCandle));

                if (firstCandle.error) {

                    gs.cancel();
                    return gs.isCancelled();
                }

                firstCandle = firstCandle.data;
                /*
                 * Check for 5 min H/L being broken in the opposite
                 * direction to the trade before position is opened.
                 */

                if (firstCandle.side) {

                    if (currentCandleItem.candle.vwap < firstCandle.low) {

                        // gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.FIVE_MIN_LOW_BROKEN);
                        // gs.cancelAllOrders();
                        // No trade we timed out
                        // gs.log("Rule 5min low broker Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                        // gs.cancel();
                        // return gs.isCancelled();
                    }
                } else {

                    if (currentCandleItem.candle.vwap > firstCandle.high) {

                        // gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.FIVE_MIN_HIGH_BROKEN);
                        // gs.cancelAllOrders();
                        // No trade we timed out
                        // gs.log("Rule 5min high broker Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                        // gs.cancel();
                        // return gs.isCancelled();
                    }
                }
            }
        }

        if (startPeriod.getTime() > timeOutDate.getTime()) {

            if (!gs.isThereOpenPosition()
                    && !(CONSTANTS.TRADESTRATEGY_STATUS.CANCELLED === tradestrategy.status)) {

                // No trade we timed out
                gs.log("Rule 11:30:00 bar, time out unfilled open position symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod+  " timeOutDate: " + timeOutDate + " openPosition: " + gs.isThereOpenPosition() + " status: " + tradestrategy.status);
                gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.TO);
                gs.cancelAllOrders();
                gs.cancel();
                return gs.isCancelled();
            }
        }

        return gs.isCancelled();
    } catch (ex) {

        gs.error(1, 100, 'Error: FiveMinGapBarStrategy::runStrategy process javascript msg: ' + ex);
    }
}
