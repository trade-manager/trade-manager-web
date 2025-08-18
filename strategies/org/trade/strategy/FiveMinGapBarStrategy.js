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
            gs.log('Info: StrategyRuleJS::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar + ' symbol: ' + gs.getSymbol());

            let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());

            if (currentCandleItem.error) {

                gs.cancel();
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
             * Trade is open kill this Strategy as its job is done.
             */
            if (gs.isThereOpenPosition()) {

                gs.log("Strategy complete open position filled symbol: {} startPeriod: {}", gs.getSymbol(), startPeriod);

                /*
                 * If the order is partial filled chaeck and if the risk goes
                 * beyond 1 risk unit cancel the openPositionOrder this will
                 * cause it to be marked as filled.
                 */
                 let tradeOrder = JSON.parse(gs.getOpenPositionOrderJSON());

                 gs.log('Info: StrategyRuleJS::runStrategy tradeOrder: ' + JSON.stringify(tradeOrder));

                if (!tradeOrder.error) {

                    tradeOrder = tradeOrder.data;

                    if (CONSTANTS.ORDER_STATUS.PARTIALFILLED == tradeOrder.status && gs.isRiskViolated(currentCandleItem.data.close, tradestrategy.risk_amount,
                            tradeOrder.quantity, tradeOrder.average_filled_price)) {

                        gs.cancelOrder(JSON.stringify(tradeOrder));
                    }
                }

                gs.cancel();
                return candleSeries.key;
            }

            /*
             * Open position order was cancelled kill this Strategy as its job
             * is done.
             */
            let tradeOrder = JSON.parse(gs.getTradeOrderJSON(openPositionOrderKey));
            gs.log('Info: StrategyRuleJS::runStrategy tradeOrder: ' + JSON.stringify(tradeOrder));

            if (!tradeOrder.error) {

                tradeOrder = tradeOrder.data;

                if (null != openPositionOrderKey && !tradeOrder.active) {

                    gs.info("Strategy complete open position cancelled symbol: +  gs.getSymbol() +  startPeriod: " + startPeriod);
                    gs.updateTradestrategyStatus(CONSTANTS.ORDER_STATUS.PARTIALFILLED);
                    gs.cancel();
                    return candleSeries.key;
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
            gs.log('Info: StrategyRuleJS::runStrategy startCandleDate: ' + startCandleDate + " timeOutDate: " + timeOutDate);

            if (startPeriod.getTime() === startCandleDate.getTime() && newBar) {

                /*
                 * Add the tails as a % of the body. 10% and vwap must be
                 * between O/C.
                 */
                let prevCandleItem = null;

                if (gs.getCurrentCandleCount() > 0) {


                    prevCandleItem =  candleSeries.data[(gs.getCurrentCandleCount() - 1)];
                    gs.log('Info: StrategyRuleJS::runStrategy prevCandleItem: ' + JSON.stringify(prevCandleItem));
                }

                 if (null != prevCandleItem && (gs.between(prevCandleItem.candle.open, prevCandleItem.candle.close, prevCandleItem.candle.vwap))) {

                    let barBodyPercent = (Math.abs(prevCandleItem.candle.open - prevCandleItem.candle.close)/ Math.abs(prevCandleItem.candle.high - prevCandleItem.candle.low)) * 100;
                    gs.log('Info: StrategyRuleJS::runStrategy barBodyPercent: ' + barBodyPercent);

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
                let entrylimit = gs.getEntryLimit(priceClose);

                if(entrylimit.error){

                    gs.cancel();
                }

                entrylimit = JSON.parse(entrylimit);
                entrylimit = entrylimit.data;
                gs.log('Info: StrategyRuleJS::runStrategy entrylimit: ' + JSON.stringify(entrylimit));

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
                    let tradeOrder = gs.createRiskOpenPosition(action, price, priceStop, true, null, null, null, null);

                    if(tradeOrder.error){

                        gs.cancel();
                    }

                    tradeOrder = JSON.parse(tradeOrder);
                    tradeOrder = tradeOrder.data;
                    openPositionOrderKey = tradeOrder.orderKey;
                    gs.log("We have a trade!!  trade order key: " + openPositionOrderKey);

                    // } else {
                    // gs.log("Rule 9:35 5min bar less than 2 * stop limits. Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                    // gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.NBB);
                    // // Kill this process we are done!
                    // gs.cancel();
                    // }
                } else {

                    gs.log("Rule 9:35 5min bar outside % limits. Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                    gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.PERCENT);
                    // Kill this process we are done!
                    gs.cancel();
                }
            } else {

                if (startPeriod.getTime() < timeOutDate.getTime()
                        && startPeriod.getTime() > startCandleDate.getTime()) {

                    let firstCandle = gs.getCandle((new Date(tradestrategy.tradingday.open)).toISOString());
                    /*
                     * Check for 5 min H/L being broken in the opposite
                     * direction to the trade before position is opened.
                     */

                    if (firstCandle.candle.side) {

                        if (currentCandleItem.candle.vwap < firstCandle.candle.low) {

                            // gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.FIVE_MIN_LOW_BROKEN);
                            // gs.cancelAllOrders();
                            // No trade we timed out
                            // gs.log("Rule 5min low broker Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                            // gs.cancel();
                        }
                    } else {

                        if (currentCandleItem.candle.vwap > firstCandle.candle.high) {

                            // gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.FIVE_MIN_HIGH_BROKEN);
                            // gs.cancelAllOrders();
                            // No trade we timed out
                            // gs.log("Rule 5min high broker Symbol: " + gs.getSymbol() + " Time: " + startPeriod);
                            // gs.cancel();
                        }
                    }
                }
            }

            if (!startPeriod.getTime() < timeOutDate.getTime()) {

                gs.log("Rule 11:30:00 bar, time out unfilled open position symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod+  " timeOutDate: " + timeOutDate);

                if (!gs.isThereOpenPosition()
                        && !CONSTANTS.TRADESTRATEGY_STATUS.CANCELLED === tradestrategy.status) {

                    gs.updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.TO);
                    gs.cancelAllOrders();
                    // No trade we timed out
                    gs.log("Rule 11:30:00 bar, time out unfilled open position symbol: " +  gs.getSymbol() +  " startPeriod: " + startPeriod+  " timeOutDate: " + timeOutDate);
                }
                gs.cancel();
            }

            return candleSeries.key;
        } catch (ex) {

            gs.error(1, 100, 'Error: StrategyRuleJS::runStrategy process javascript msg: ' + ex);
        }
    }





