    let CONSTANTS = null;

    function initStrategy() {

        let result  = JSON.parse(gs.getInitParams());

        if(!result.error){

            gs.log('Info: StrategyRuleJS::initStrategy result.data: ' + JSON.stringify(result.data));
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
            let tradestrategy = JSON.parse(gs.getTradestrategyJSON());
            gs.log('Info: StrategyRuleJS::runStrategy currentCandleItem: ' + JSON.stringify(currentCandleItem));
            gs.log('Info: StrategyRuleJS::runStrategy tradestrategy: ' + JSON.stringify(tradestrategy));
            gs.log('Info: StrategyRuleJS::runStrategy openPositionOrderKey: ' + openPositionOrderKey);

            if (!currentCandleItem.error) {

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

                    if (!tradeOrder.error && CONSTANTS.ORDER_STATUS.PARTIALFILLED == tradeOrder.data.status) {

                        if (gs.isRiskViolated(currentCandleItem.data.close, tradestrategy.data.risk_amount,
                                tradeOrder.data.quantity, tradeOrder.data.average_filled_price)) {

                            gs.cancelOrder(JSON.stringify(tradeOrder));
                        }
                    }

                    gs.cancel();
                    return;
                }
            }

            /*
             * Open position order was cancelled kill this Strategy as its job
             * is done.
             */
            let tradeOrder = JSON.parse(gs.getTradeOrderJSON(openPositionOrderKey));
            gs.log('Info: StrategyRuleJS::runStrategy tradeOrder: ' + JSON.stringify(tradeOrder));

            if (!tradeOrder.error && null != openPositionOrderKey && !tradeOrder.data.active) {

                gs.info("Strategy complete open position cancelled symbol: +  gs.getSymbol() +  startPeriod: " + startPeriod);
                gs.updateTradestrategyStatus(CONSTANTS.ORDER_STATUS.PARTIALFILLED);
                gs.cancel();
                return;
            }

            /*
             * Is it the the 9:35 candle? and we have not created an open
             * position trade.
             */
            if (startPeriod.equals(this.getTradestrategy().getTradingday().getOpen()
                    .plusMinutes(this.getTradestrategy().getBarSize() / 60)) && newBar) {

                /*
                 * Add the tails as a % of the body. 10% and vwap must be
                 * between O/C.
                 */
                let prevCandleItem = null;

                if (getCurrentCandleCount() > 0) {

                    prevCandleItem = (CandleItem) candleSeries.getDataItem(getCurrentCandleCount() - 1);
                    // AbstractStrategyRule
                    // .logCandle(this, prevCandleItem.getCandle());
                }

                if (CoreUtils.isBetween(Objects.requireNonNull(prevCandleItem).getOpen(), prevCandleItem.getClose(),
                        prevCandleItem.getVwap())) {

                    let barBodyPercent = (Math.abs(prevCandleItem.getOpen() - prevCandleItem.getClose())
                            / Math.abs(prevCandleItem.getHigh() - prevCandleItem.getLow())) * 100;

                    if (barBodyPercent < 10) {

                        gs.info("Bar Body outside % range  Symbol: {} Time: {}", getSymbol(), startPeriod);
                        updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.NBB);
                        gs.cancel();
                        return;
                    }
                }

                let side = CONSTANTS.SIDE.SLD;

                if (prevCandleItem.side == CONSTANTS.SIDE.BOT) {

                    side = CONSTANTS.SIDE.BOT;
                }

                let price = new Money(prevCandleItem.getHigh());
                let priceStop = new Money(prevCandleItem.getLow());
                let action = CONSTANTS.ACTION.BUY;

                if (CONSTANTS.SIDE.SLD == side) {

                    price = new Money(prevCandleItem.getLow());
                    priceStop = new Money(prevCandleItem.getHigh());
                    action = CONSTANTS.ACTION.SELL;
                }

                let priceClose = new Money(prevCandleItem.getClose());
                let entrylimit = getEntryLimit().getValue(priceClose);
                let highLowRange = Math.abs(prevCandleItem.getHigh() - prevCandleItem.getLow());
                priceStop = new Money(prevCandleItem.getOpen());

                // If the candle less than the entry limit %
                if (((highLowRange) / prevCandleItem.getClose()) < entrylimit.getPercentOfPrice().doubleValue()) {

                    /*
                     * Check that the entry - stop is greater than 2* the STPLMT
                     * amount.
                     */
                    // if (Math.abs(price.subtract(priceStop).doubleValue())
                    // > (entrylimit
                    // .getLimitAmount().doubleValue() * 2)) {

                    /*
                     * Create an open position.
                     */
                    gs.info("We have a trade!!  Symbol: {} Time: {}", getSymbol(), startPeriod);
                    let tradeOrder = createRiskOpenPosition(action, price, priceStop, true, null, null, null,
                            null);
                    openPositionOrderKey = tradeOrder.getOrderKey();
                    // } else {
                    // _log.info("Rule 9:35 5min bar less than 2 * stop limits.
                    // Symbol: "
                    // + getSymbol() + " Time: " + startPeriod);
                    // updateTradestrategyStatus(TradestrategyStatus.NBB);
                    // // Kill this process we are done!
                    // this.cancel();
                    // }

                } else {

                    gs.info("Rule 9:35 5min bar outside % limits. Symbol: {} Time: {}", getSymbol(), startPeriod);
                    updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.PERCENT);
                    // Kill this process we are done!
                    gs.cancel();
                }
            } else {

                if (startPeriod.isBefore(this.getTradestrategy().getTradingday().getOpen().plusMinutes(120))
                        && startPeriod.isAfter(this.getTradestrategy().getTradingday().getOpen().plusMinutes(5))) {
                    CandleItem firstCandle = this.getCandle(TradingCalendar.getDateAtTime(startPeriod,
                            this.getTradestrategy().getTradingday().getOpen()));
                    /*
                     * Check for 5 min H/L being broken in the opposite
                     * direction to the trade before position is opened.
                     */

                    if (firstCandle.getSide()) {

                        if (currentCandleItem.getVwap() < firstCandle.getLow()) {

                            // updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.FIVE_MIN_LOW_BROKEN);
                            // this.cancelAllOrders();
                            // // No trade we timed out
                            // _log.info("Rule 5min low broker Symbol: "
                            // + getSymbol() + " Time: " + startPeriod);
                            // this.cancel();
                        }
                    } else {

                        if (currentCandleItem.getVwap() > firstCandle.getHigh()) {

                            // updateTradestrategyStatus(CONSTANTS.TRADESTRATEGY_STATUS.FIVE_MIN_HIGH_BROKEN);
                            // this.cancelAllOrders();
                            // // No trade we timed out
                            // _log.info("Rule 5min high broker Symbol: "
                            // + getSymbol() + " Time: " + startPeriod);
                            // this.cancel();
                        }
                    }
                }
            }

            return candleSeries.key
        } catch (ex) {

            gs.error(1, 100, 'Error: StrategyRuleJS::runStrategy process javascript msg: ' + ex);
        }
    }

