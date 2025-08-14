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
    function runStrategy(candleSeriesJSON, newBar) {

        try {

            let candleSeries = JSON.parse(candleSeriesJSON);
            gs.log('Info: StrategyRuleJS::runStrategy key: ' + candleSeries.key + ' newBar: ' + newBar + ' symbol: ' + gs.getSymbol());

            let currentCandleItem = JSON.parse(gs.getCurrentCandleJSON());
            let tradestrategy = JSON.parse(gs.getTradestrategyJSON());
            gs.log('Info: StrategyRuleJS::runStrategy currentCandleItem: ' + JSON.stringify(currentCandleItem));

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

                    if ("PARTIALFILLED" == tradeOrder.data.status) {

                        if (gs.isRiskViolated(currentCandleItem.data.close, tradestrategy.data.risk_amount,
                                tradeOrder.data.quantity, tradeOrder.data.average_filled_price)) {

                            gs.cancelOrderJSON(JSON.stringify(tradeOrder));
                        }
                    }

                    gs.cancel();
                    return;
                }
            }

            return candleSeries.key
        } catch (ex) {

            gs.error(1, 100, 'Error: StrategyRuleJS::runStrategy process javascript msg: ' + ex);
        }
    }

