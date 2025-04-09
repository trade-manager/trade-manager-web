/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.broker.IBrokerModel;
import org.trade.core.util.TradingCalendar;
import org.trade.core.valuetype.Money;
import org.trade.dictionary.valuetype.Action;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.dictionary.valuetype.Side;
import org.trade.dictionary.valuetype.TradestrategyStatus;
import org.trade.persistent.dao.TradeOrder;
import org.trade.strategy.data.CandleSeries;
import org.trade.strategy.data.StrategyData;
import org.trade.strategy.data.candle.CandleItem;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class Vwap5MinSideGapBarStrategy extends AbstractStrategyRule {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2138009534354123773L;
    private final static Logger _log = LoggerFactory.getLogger(Vwap5MinSideGapBarStrategy.class);

    private Integer openPositionOrderKey = null;

    /**
     * Default Constructor Note if you use class variables remember these will
     * need to be initialized if the strategy is restarted i.e. if they are
     * created on startup under a constraint you must find a way to populate
     * that value if the strategy were to be restarted and the constraint is not
     * met.
     *
     * @param brokerManagerModel BrokerModel
     * @param strategyData       StrategyData
     * @param idTradestrategy    Integer
     */

    public Vwap5MinSideGapBarStrategy(IBrokerModel brokerManagerModel, StrategyData strategyData,
                                      Integer idTradestrategy) {
        super(brokerManagerModel, strategyData, idTradestrategy);
    }

    /*
     * Enter over/under the 9:35am 5min bar if the bar is in the direction of
     * the Tradestrategy side and the Vwap is also in that direction. Note the
     * strategy will run until either: 1/ The above conditions are not met,
     * cancel this strategy. 2/ The open position is filled, cancel this
     * strategy. 3/ 10:30 comes cancel open position and cancel this strategy.
     * 4/ Open position is filled, cancel this strategy.
     *
     * @param candleSeries the series of candles that has been updated.
     *
     * @param newBar has a new bar just started.
     */

    /**
     * Method runStrategy.
     *
     * @param candleSeries CandleSeries
     * @param newBar       boolean
     */
    public void runStrategy(CandleSeries candleSeries, boolean newBar) {

        try {
            /*
             * Get the current candle
             */
            CandleItem currentCandleItem = this.getCurrentCandle();
            // AbstractStrategyRule.logCandle(this,
            // currentCandleItem.getCandle());
            ZonedDateTime startPeriod = currentCandleItem.getPeriod().getStart();

            /*
             * Trade is open kill this Strategy as its job is done.
             */
            if (this.isThereOpenPosition()) {
                _log.info("Strategy complete open position filled symbol: {} startPeriod: {}", getSymbol(), startPeriod);
                /*
                 * If the order is partial filled chaeck and if the risk goes
                 * beyond 1 risk unit cancel the openPositionOrder this will
                 * cause it to be marked as filled.
                 */
                if (OrderStatus.PARTIALFILLED.equals(this.getOpenPositionOrder().getStatus())) {
                    if (isRiskViolated(currentCandleItem.getClose(), this.getTradestrategy().getRiskAmount(),
                            this.getOpenPositionOrder().getQuantity(),
                            this.getOpenPositionOrder().getAverageFilledPrice())) {
                        this.cancelOrder(this.getOpenPositionOrder());
                    }
                }
                this.cancel();
                return;
            }

            /*
             * Open position order was cancelled kill this Strategy as its job
             * is done.
             */
            if (null != openPositionOrderKey && !this.getTradeOrder(openPositionOrderKey).isActive()) {
                _log.info("Strategy complete open position cancelled symbol: {} startPeriod: {}", getSymbol(), startPeriod);
                updateTradestrategyStatus(TradestrategyStatus.CANCELLED);
                this.cancel();
                return;
            }

            /*
             * Is it the the 9:35 candle? and we have not created an open
             * position trade.
             */
            if (startPeriod.equals(this.getTradestrategy().getTradingday().getOpen()
                    .plusMinutes(this.getTradestrategy().getBarSize() / 60)) && newBar) {

                /*
                 * Is the candle in the direction of the Tradestrategy side i.e.
                 * a long play should have a green 5min candle
                 */
                CandleItem prevCandleItem = null;
                if (getCurrentCandleCount() > 0) {
                    prevCandleItem = (CandleItem) candleSeries.getDataItem(getCurrentCandleCount() - 1);
                    // AbstractStrategyRule
                    // .logCandle(this, prevCandleItem.getCandle());
                }
                if (Objects.requireNonNull(prevCandleItem).isSide(getTradestrategy().getSide())) {

                    if ((Side.BOT.equals(getTradestrategy().getSide())
                            && prevCandleItem.getVwap() < currentCandleItem.getVwap())
                            || (Side.SLD.equals(getTradestrategy().getSide())
                            && prevCandleItem.getVwap() > currentCandleItem.getVwap())) {
                        /*
                         * Based on the prev bar create the stop, entry price
                         * using the 9:35 5min bar high/low.
                         */
                        Money price = new Money(prevCandleItem.getHigh());
                        Money priceStop = new Money(prevCandleItem.getLow());
                        String action = Action.BUY;
                        if (Side.SLD.equals(getTradestrategy().getSide())) {
                            price = new Money(prevCandleItem.getLow());
                            priceStop = new Money(prevCandleItem.getHigh());
                            action = Action.SELL;
                        }

                        /*
                         * Create an open position order.
                         */
                        TradeOrder tradeOrder = createRiskOpenPosition(action, price, priceStop, true, null, null, null,
                                null);
                        openPositionOrderKey = tradeOrder.getOrderKey();

                    } else {
                        _log.info("Rule Vwap 5 min Side Gap bar. Vwap not in direction of side. Symbol: {} Time: {}", getSymbol(), startPeriod);

                        if (Side.SLD.equals(getTradestrategy().getSide())) {
                            this.updateTradestrategyStatus(TradestrategyStatus.GB);
                        } else {
                            this.updateTradestrategyStatus(TradestrategyStatus.RB);
                        }

                        // Cancel this process we are done!
                        this.cancel();
                    }

                } else {
                    _log.info("Rule 5 min Red/Green bar opposite to trade direction. Symbol: {} Time: {}", getSymbol(), startPeriod);

                    if (Side.SLD.equals(getTradestrategy().getSide())) {
                        this.updateTradestrategyStatus(TradestrategyStatus.GB);
                    } else {
                        this.updateTradestrategyStatus(TradestrategyStatus.RB);
                    }

                    // Cancel this process we are done!
                    this.cancel();
                }
            } else if (!startPeriod.isBefore(TradingCalendar.getDateAtTime(startPeriod, 10, 30, 0))) {

                if (!this.isThereOpenPosition()
                        && !TradestrategyStatus.CANCELLED.equals(getTradestrategy().getStatus())) {
                    this.updateTradestrategyStatus(TradestrategyStatus.TO);
                    this.cancelAllOrders();
                    // No trade we timed out
                    _log.info("Rule 10:30:00 bar, time out unfilled open position Symbol: {} Time: {}", getSymbol(), startPeriod);
                }
                this.cancel();
            }
        } catch (StrategyRuleException ex) {
            _log.error("Error  runRule exception: {}", ex.getMessage(), ex);
            error(1, 20, "Error  runRule exception: " + ex.getMessage());
        }
    }
}
