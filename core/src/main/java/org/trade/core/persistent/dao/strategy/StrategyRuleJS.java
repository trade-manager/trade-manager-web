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
package org.trade.core.persistent.dao.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;

import java.io.Serial;

/**
 *
 */
@Controller
public abstract class StrategyRuleJS extends AbstractStrategyRule {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4876874276185644936L;

    private final static Logger _log = LoggerFactory.getLogger(StrategyRuleJS.class);


    /**
     * Constructor for AbstractStrategyRule. An abstract class that implements
     * the base functionality for a trading strategies this class monitors the
     * candle data set for changes. This class runs in its own thread. there
     * will be one Strategy running per tradestrategy.
     *
     * @param tradeService    TradeService
     * @param brokerModel     IBrokerModel
     * @param strategyData    StrategyData
     * @param tradestrategyId Integer
     */
    public StrategyRuleJS(TradeService tradeService, IBrokerModel brokerModel, StrategyData strategyData, Long tradestrategyId) {

        super(tradeService, brokerModel, strategyData, tradestrategyId);
    }

    /**
     * Method runStrategy. This method is called every time the candleSeries is
     * either updated or a candleItem is added.
     * <p>
     * <p>
     * If market data is selected this will fire every time the last price falls
     * outside the H/L of the current candle. Note also if market data is
     * selected the current Bid/Ask/Last can be accessed via the
     * candleSeries.getContract().
     * <p>
     * If market data is not selected this method fires every 5sec as real time
     * bars update the current candle.
     *
     * @param candleSeries CandleSeries
     * @param newBar       boolean when ever a new bar is added to the candleSeries.
     */
    public void runStrategy(CandleSeries candleSeries, boolean newBar) {

    }
}
