/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade test
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
import org.trade.core.broker.IBrokerModel;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.strategy.AbstractStrategyRule;
import org.trade.core.persistent.dao.strategy.StrategyRuleException;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.Side;

import java.io.Serial;
import java.time.ZonedDateTime;

/**
 *
 */
public class PosMgrAllOrNothingStrategy extends AbstractStrategyRule {

    /**
     * 1/ If the open position is filled create a STP (transmit=false see 2/ )
     * and 1 Target (LMT) OCA order at xR with 100% of the filled quantity. Use
     * the open position fill quantity, price and stop price to determine the
     * target price. The STP order take an initial risk of 1R.
     * <p>
     * 2/ Target/Stop prices should be round over/under whole/half numbers when
     * ever they are calculated..
     * <p>
     * 3/ Close any open positions at 15:58.
     */

    @Serial
    private static final long serialVersionUID = 5998132222691879078L;
    private final static Logger _log = LoggerFactory.getLogger(PosMgrAllOrNothingStrategy.class);

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

    public PosMgrAllOrNothingStrategy(IBrokerModel brokerManagerModel, StrategyData strategyData,
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
            CandleItem currentCandleItem = (CandleItem) candleSeries.getDataItem(getCurrentCandleCount());
            ZonedDateTime startPeriod = currentCandleItem.getPeriod().getStart();

            // AbstractStrategyRule.logCandle(this,
            // currentCandleItem.getCandle());

            /*
             * Get the current open trade. If no trade is open this Strategy
             * will be closed down.
             */

            if (!this.isThereOpenPosition()) {
                _log.info("No open position so Cancel Strategy Mgr Symbol: {} Time:{}", getSymbol(), startPeriod);
                this.cancel();
                return;
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

            if (this.isThereOpenPosition() && !this.isPositionCovered()) {

                /*
                 * Position has been opened and not covered submit the target
                 * and stop orders for the open quantity. One target at xR.
                 */

                _log.info("Open position submit Stop/Tgt orders Symbol: {} Time:{}", getSymbol(), startPeriod);

                /*
                 * Risk amount is based of the average filled price and actual
                 * stop price not the rounded quantity. But if the stop price is
                 * not set use Risk Amount/Quantity.
                 */
                double riskAmount;
                if (null == this.getOpenPositionOrder().getStopPrice()) {
                    riskAmount = Math.abs(this.getTradestrategy().getRiskAmount().doubleValue()
                            / this.getOpenPositionOrder().getFilledQuantity().doubleValue());
                } else {
                    riskAmount = Math.abs(this.getOpenPositionOrder().getAverageFilledPrice().doubleValue()
                            - this.getOpenPositionOrder().getStopPrice().doubleValue());
                }

                String action = Action.BUY;
                int buySellMultipliter = 1;
                if (Side.BOT.equals(getOpenTradePosition().getSide())) {
                    action = Action.SELL;
                    buySellMultipliter = -1;
                }

                // Add a penny to the stop and target
                double stop = this.getOpenPositionOrder().getAverageFilledPrice().doubleValue()
                        + (riskAmount * 1 * buySellMultipliter);

                Money auxPrice = addPennyAndRoundStop(stop, this.getOpenTradePosition().getSide(), action, -0.01);

                this.createOrder(this.getTradestrategy().getContract(), action, OrderType.STP, null, auxPrice,
                        this.getOpenPositionOrder().getFilledQuantity(), null, false, true);
            }

            /*
             * Close any opened positions with a market order at the end of the
             * day.
             */
            if (!currentCandleItem.getLastUpdateDate()
                    .isBefore(this.getTradestrategy().getTradingday().getClose().minusMinutes(2))) {
                cancelOrdersClosePosition(true);
                _log.info("PositionManagerStrategy 15:58:00 done: {} Time: {}", getSymbol(), startPeriod);
                this.cancel();
            }
        } catch (StrategyRuleException ex) {
            _log.error("Error Position User exception: {}", ex.getMessage(), ex);
            error(1, 30, "Error  Position User exception: " + ex.getMessage());
        }
    }
}
