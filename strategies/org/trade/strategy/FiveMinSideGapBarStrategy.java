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
import org.trade.core.valuetype.Money;
import org.trade.dictionary.valuetype.Action;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.dictionary.valuetype.Side;
import org.trade.dictionary.valuetype.TradestrategyStatus;
import org.trade.persistent.dao.Entrylimit;
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

public class FiveMinSideGapBarStrategy extends AbstractStrategyRule {

    /**
     * 1/ Enter in the direction of the Tradingday Tab Side field when the first
     * 5min bar direction co-inside.
     * <p>
     * 2/ Size with a stop at the first 5min bars Low (so for Long position
     * Quantity= Risk/(High-Low) include rounding). Use a STPLMT order with a
     * range from the Entry Limit table.
     * <p>
     * 3/ Bar must be within the Entry Limit table % Range Bar. i.e in this case
     * 2%
     * <p>
     * 4/ If the position is not filled by 10:30 cancel the order.
     * <p>
     * 5/ If the first 5min bar High/Low is broken (so for Side=Long a break of
     * the Low) before entry, cancel the open order position.
     * <p>
     * 6/ Add 1c to the entry price and round over/under whole/half numbers in
     * the direction of the trade, same for stop price.
     * <p>
     * E.g. If first 5min bar is H=21.50 L=21.15 Open= 21.2 Close=21.40 and the
     * Side is Long, then position order will be Buy STPLMT=21.51-21.55 (STPLMT
     * range 0.04 from EntryLimit table and as we are at 21.5 we also buy
     * over/under whole/half numbers), Quantity=Risk/(21.51-21.2) rounded to
     * +/-100 shares (see EntryLimit table).
     */

    @Serial
    private static final long serialVersionUID = -1629661431267666769L;
    private final static Logger _log = LoggerFactory.getLogger(FiveMinSideGapBarStrategy.class);

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

    public FiveMinSideGapBarStrategy(IBrokerModel brokerManagerModel, StrategyData strategyData,
                                     Integer idTradestrategy) {
        super(brokerManagerModel, strategyData, idTradestrategy);
    }

    /**
     * Method runStrategy.
     *
     * @param candleSeries CandleSeries
     * @param newBar       boolean
     */
    public void runStrategy(CandleSeries candleSeries, boolean newBar) {

        try {
            // Get the current candle
            CandleItem currentCandleItem = this.getCurrentCandle();
            // AbstractStrategyRule.logCandle(this,
            // currentCandleItem.getCandle());
            ZonedDateTime startPeriod = currentCandleItem.getPeriod().getStart();
            CandleItem prevCandleItem = null;
            if (getCurrentCandleCount() > 0) {
                prevCandleItem = (CandleItem) candleSeries.getDataItem(getCurrentCandleCount() - 1);
                // AbstractStrategyRule
                // .logCandle(this, prevCandleItem.getCandle());
            }
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
             * If the 5min low is broken cancel orders as trade no longer valid.
             */

            CandleItem openCandle = this.getCandle(this.getTradestrategy().getTradingday().getOpen());

            if (null == getTradestrategy().getSide()) {
                error(1, 101, "Error  tradestrategy side has not been set.");
            }
            if (!this.isThereOpenPosition()) {
                if (Side.BOT.equals(getTradestrategy().getSide())) {
                    if (openCandle.getLow() > Objects.requireNonNull(prevCandleItem).getLow()) {
                        _log.info("Rule 5min low broken. Symbol: {} Time: {}", getSymbol(), startPeriod);
                        this.cancelAllOrders();
                        updateTradestrategyStatus(TradestrategyStatus.FIVE_MIN_LOW_BROKEN);
                        this.cancel();
                        return;

                    }
                } else {
                    if (openCandle.getHigh() < Objects.requireNonNull(prevCandleItem).getHigh()) {
                        _log.info("Rule 5min high broken. Symbol: {} Time: {}", getSymbol(), startPeriod);
                        this.cancelAllOrders();
                        updateTradestrategyStatus(TradestrategyStatus.FIVE_MIN_HIGH_BROKEN);
                        this.cancel();
                        return;
                    }
                }
            }

            /*
             * Is it the the 9:35 candle? and we have not created an open
             * position trade.
             */
            if (startPeriod.equals(this.getTradestrategy().getTradingday().getOpen()
                    .plusMinutes(this.getTradestrategy().getBarSize() / 60)) && newBar) {

                Money price = new Money(Objects.requireNonNull(prevCandleItem).getHigh());
                Money priceStop = new Money(prevCandleItem.getLow());
                String action = Action.BUY;
                if (Side.SLD.equals(getTradestrategy().getSide())) {
                    price = new Money(prevCandleItem.getLow());
                    priceStop = new Money(prevCandleItem.getHigh());
                    action = Action.SELL;
                }
                // Is the candle in the direction of the trade i.e.
                // a long play should have a green 5min candle
                if (prevCandleItem.isSide(getTradestrategy().getSide())) {
                    Money priceClose = new Money(prevCandleItem.getClose());
                    Entrylimit entrylimit = getEntryLimit().getValue(priceClose);

                    double percentChange = Math.abs(prevCandleItem.getHigh() - prevCandleItem.getLow())
                            / prevCandleItem.getClose();
                    // If the candle less than the entry limit %
                    if (percentChange < entrylimit.getPercentOfPrice().doubleValue()) {
                        // TODO add the tails as a % of the body.
                        _log.info(" We have a trade!!  Symbol: {} Time: {}", getSymbol(), startPeriod);

                        /*
                         * Create an open position.
                         */
                        TradeOrder tradeOrder = createRiskOpenPosition(action, price, priceStop, true, null, null, null,
                                null);
                        openPositionOrderKey = tradeOrder.getOrderKey();

                    } else {
                        _log.info("Rule 9:35 5min bar outside % limits. Symbol: {} Time: {}", getSymbol(), startPeriod);
                        updateTradestrategyStatus(TradestrategyStatus.PERCENT);
                        // Kill this process we are done!
                        this.cancel();
                    }

                } else {
                    _log.info("Rule 5 min Red/Green bar opposite to trade direction. Symbol: {} Time: {}", getSymbol(), startPeriod);

                    if (Side.SLD.equals(getTradestrategy().getSide())) {
                        this.updateTradestrategyStatus(TradestrategyStatus.GB);
                    } else {
                        this.updateTradestrategyStatus(TradestrategyStatus.RB);
                    }

                    // Kill this process we are done!
                    this.cancel();
                }
            } else if (!startPeriod.isBefore(this.getTradestrategy().getTradingday().getOpen().plusMinutes(60))) {

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
