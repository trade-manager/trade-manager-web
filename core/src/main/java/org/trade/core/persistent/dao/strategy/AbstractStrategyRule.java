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
import org.springframework.beans.factory.annotation.Autowired;
import org.trade.core.broker.BrokerModelException;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Entrylimit;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.series.SeriesChangeEvent;
import org.trade.core.persistent.dao.series.SeriesChangeListener;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.Worker;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.DAOEntryLimit;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.OverrideConstraints;
import org.trade.core.valuetype.Percent;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TriggerMethod;

import javax.swing.event.EventListenerList;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 *
 */
public abstract class AbstractStrategyRule extends Worker implements SeriesChangeListener, IStrategyRule, Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4876874276185644936L;

    private final static Logger _log = LoggerFactory.getLogger(AbstractStrategyRule.class);

    @Autowired
    private TradeService tradeService;

    /*
     * Message handler that allows the main controller to listen for errors.
     * Storage for registered change listeners.
     */
    private final transient EventListenerList listenerList;

    private final IBrokerModel brokerModel;
    private final DAOEntryLimit entryLimits = new DAOEntryLimit();
    private final StrategyData strategyData;
    private Tradestrategy tradestrategy = null;
    private TradestrategyOrders tradestrategyOrders = null;
    private final Integer idTradestrategy;
    private String symbol = null;
    private boolean seriesChanged = false;
    private final Object lockStrategyWorker = new Object();
    private boolean listeningCandles = false;
    private int currentCandleCount = -1;
    private ZonedDateTime strategyLastFired = TradingCalendar.getDateTimeNowMarketTimeZone();

    /**
     * Constructor for AbstractStrategyRule. An abstract class that implements
     * the base functionality for a trading strategies this class monitors the
     * candle data set for changes. This class runs in its own thread. there
     * will be one Strategy running per tradestrategy.
     *
     * @param brokerManagerModel IBrokerModel
     * @param strategyData       StrategyData
     * @param idTradestrategy    Integer
     */
    public AbstractStrategyRule(IBrokerModel brokerManagerModel, StrategyData strategyData, Integer idTradestrategy) {
        this.listenerList = new EventListenerList();
        this.brokerModel = brokerManagerModel;
        this.strategyData = strategyData;
        this.idTradestrategy = idTradestrategy;
    }

    /**
     * Method error. All errors are sent via this method to any class that is
     * listening to this strategy. Usually this is the main controller.
     *
     * @param id        int
     * @param errorCode int
     * @param errorMsg  String
     * @see IStrategyRule#error(int, int, String)
     */
    public void error(int id, int errorCode, String errorMsg) {

        if (id > 0) {
            _log.warn("Error symbol: {} Error Id: {} Error Code: {} Error Msg: {}", symbol, id, errorCode, errorMsg);
        }
        this.fireStrategyError(new StrategyRuleException(id, errorCode, "Symbol: " + symbol + " " + errorMsg));
        /*
         * For Errors close the strategy down.
         */
        if (id == 1) {
            this.cancel();
        }
    }

    /**
     * Registers an object to receive notification of changes to the
     * strategyRule.
     *
     * @param listener the object to register.
     */
    public void addMessageListener(IStrategyChangeListener listener) {
        this.listenerList.add(IStrategyChangeListener.class, listener);
    }

    /**
     * Deregisters an object so that it no longer receives notification of
     * changes to the strategyRule.
     *
     * @param listener the object to deregister.
     */
    public void removeMessageListener(IStrategyChangeListener listener) {
        this.listenerList.remove(IStrategyChangeListener.class, listener);
    }

    public void removeAllMessageListener() {
        IStrategyChangeListener[] listeners = this.listenerList.getListeners(IStrategyChangeListener.class);
        for (IStrategyChangeListener listener : listeners) {
            removeMessageListener(listener);
        }
    }

    /**
     * Notifies all registered listeners that the strategyRule has an error.
     *
     * @param strategyError StrategyRuleException
     */
    protected void fireStrategyError(StrategyRuleException strategyError) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IStrategyChangeListener.class) {
                ((IStrategyChangeListener) listeners[i + 1]).strategyError(strategyError);
            }
        }
    }

    /**
     * Notifies all registered listeners that the strategyRule has completed.
     *
     * @param tradestrategy Tradestrategy
     */
    protected void fireStrategyComplete(String strategyClassName, final Tradestrategy tradestrategy) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IStrategyChangeListener.class) {
                ((IStrategyChangeListener) listeners[i + 1]).strategyComplete(strategyClassName, tradestrategy);
            }
        }
    }

    /**
     * Notifies all registered listeners that the strategyRule has started.
     *
     * @param tradestrategy Tradestrategy
     */
    protected void fireStrategyStarted(String strategyClassName, final Tradestrategy tradestrategy) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IStrategyChangeListener.class) {
                ((IStrategyChangeListener) listeners[i + 1]).strategyStarted(strategyClassName, tradestrategy);
            }
        }
    }

    /**
     * Notifies all registered listeners that the strategyRule rule has
     * completed.
     *
     * @param tradestrategy Tradestrategy
     */
    protected void fireRuleComplete(final Tradestrategy tradestrategy) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IStrategyChangeListener.class) {
                ((IStrategyChangeListener) listeners[i + 1]).ruleComplete(tradestrategy);
            }
        }
    }

    /**
     * The main process thread. This will run until it is either canceled or is
     * done.
     * <p>
     * (non-Javadoc)
     */

    protected Void doInBackground() {

        /*
         * We initialize here to keep this instances as part of this worker
         * thread
         */
        try {

            // Get an instances for this thread.
            this.tradestrategy = tradeService.findTradestrategyById(this.idTradestrategy);
            this.tradestrategy.setStrategyData(this.strategyData);
            this.symbol = this.tradestrategy.getContract().getSymbol();

            _log.info("Starting strategyClass: {} engine doInBackground Symbol: {} idTradestrategy: {} Tradingday Date: {}", this.getClass().getName(), this.symbol, this.idTradestrategy, this.tradestrategy.getTradingday().getOpen());

            /*
             * Process the current candle if there is one on startup.
             */

            currentCandleCount = this.strategyData.getBaseCandleSeries().getItemCount() - 1;

            seriesChanged = true;

            reFreshPositionOrders();

            do {
                /*
                 * Lock until a candle arrives. First time in we process the
                 * current candle.
                 */
                synchronized (lockStrategyWorker) {
                    while (!seriesChanged) {
                        lockStrategyWorker.wait();
                    }
                    seriesChanged = false;
                }

                if (!this.isCancelled()) {

                    /*
                     * If candle count > than current we have a new candle
                     *
                     * If equal then we have an updated candle.
                     *
                     * The currentCandleCount is greater than the candle series.
                     * Then another thread must have cleared the candle series
                     * so shut down the strategy.
                     */
                    CandleSeries candleSeries = this.tradestrategy.getStrategyData().getBaseCandleSeries();

                    boolean newCandle = false;
                    if ((candleSeries.getItemCount() - 1) > currentCandleCount) {
                        /*
                         * Add one to the currentCandleCount until we catch up
                         * to the candleSeries candle count. As it is possible
                         * the candle count in another thread gets ahead of this
                         * thread and so this thread is playing catch up.
                         */
                        currentCandleCount++;
                        newCandle = true;

                    } else if (currentCandleCount > (candleSeries.getItemCount() - 1)) {

                        _log.info("Cancelled as candleSeries have been cleared Symbol: {} class: {}", getSymbol(), this.getClass().getName());
                        this.cancel();
                        break;
                    } else if (currentCandleCount == (candleSeries.getItemCount() - 1)) {
                        /*
                         * We have an updated candle. If we are listening for
                         * candles and none are arriving then close the
                         * strategy.
                         */
                        if (currentCandleCount == -1 && listeningCandles) {
                            this.cancel();
                            break;
                        }
                    }

                    if (currentCandleCount > -1) {
                        /*
                         * Check the candle is during the trading range and fire
                         * the rules.
                         */
                        if (!getCurrentCandle().getPeriod().getStart()
                                .isBefore(this.tradestrategy.getTradingday().getOpen())) {
                            /*
                             * Refresh the orders in the positionOrders as these
                             * may have been filled via another thread. This
                             * gets the Orders/OpenPosition and Contract
                             */
                            reFreshPositionOrders();
                            this.tradestrategy.getContract()
                                    .setLastAskPrice(candleSeries.getContract().getLastAskPrice());
                            this.tradestrategy.getContract()
                                    .setLastBidPrice(candleSeries.getContract().getLastBidPrice());
                            this.tradestrategy.getContract().setLastPrice(candleSeries.getContract().getLastPrice());
                            runStrategy(candleSeries, newCandle);
                            strategyLastFired = TradingCalendar.getDateTimeNowMarketTimeZone();
                        }
                    }
                    /*
                     * First time in add a listener for new candle.
                     */
                    if (!listeningCandles) {

                        /*
                         * Start listening for new candles and candle changes.
                         */
                        this.strategyData.getBaseCandleSeries().addChangeListener(this);
                        /*
                         * Tell the worker if listening. Note only for back
                         * testing that the strategy is running.
                         */
                        this.fireStrategyStarted(this.getClass().getSimpleName(), this.tradestrategy);
                        listeningCandles = true;

                        _log.info("Started strategyClass: {} engine doInBackground Symbol: {} idTradestrategy: {}", this.getClass().getName(), this.symbol, this.idTradestrategy);
                    } else {
                        this.fireRuleComplete(this.tradestrategy);
                    }
                }

            } while (!this.isDone() && !this.isCancelled());
        } catch (InterruptedException interExp) {
            // Do nothing.
        } catch (Exception ex) {
            _log.error("Error StrategyWorker exception: {} class: {} Msg: {}", getSymbol(), this.getClass().getName(), ex.getMessage(), ex);
            error(1, 100, "Error StrategyWorker exception: " + ex.getMessage());
        } finally {
            /*
             * Ok we are complete clean up.
             */
        }
        return null;
    }

    /**
     * Method cancel.
     *
     * @see IStrategyRule#cancel()
     */
    public void cancel() {
        this.setIsCancelled(true);
        /*
         * Unlock the doInBackground that may be waiting for a candle. This will
         * cause a clean finish to the process.
         */
        _log.info("Started strategyClass: {} canceled.", this.getClass().getName());
        synchronized (lockStrategyWorker) {
            seriesChanged = true;
            lockStrategyWorker.notify();
        }
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
    public abstract void runStrategy(CandleSeries candleSeries, boolean newBar);

    protected void done() {
        this.fireStrategyComplete(this.getClass().getSimpleName(), this.tradestrategy);
        removeAllMessageListener();
        this.strategyData.getBaseCandleSeries().removeChangeListener(this);
        _log.info("Rule engine done: {} class: {} idTradestrategy: {} Tradingday Date: {}", getSymbol(), this.getClass().getSimpleName(), this.tradestrategy.getId(), this.tradestrategy.getTradingday().getOpen());
    }

    /**
     * Method seriesChanged. The series change event for the candle series this
     * receives all changes to the candle data set. these changes happen in the
     * Broker interface when new data is received by the market.
     *
     * @param event SeriesChangeEvent
     */
    public void seriesChanged(SeriesChangeEvent event) {
        synchronized (lockStrategyWorker) {
            seriesChanged = true;
            lockStrategyWorker.notify();
        }
    }

    /**
     * Method closePosition. This method creates a market order to close the
     * Trade. The order is persisted and transmitted via the broker interface to
     * the market.
     *
     * @param transmit boolean
     */
    public TradeOrder closePosition(boolean transmit) throws StrategyRuleException {

        if (this.isThereOpenPosition()) {

            int openQuantity = Math.abs(this.getOpenTradePosition().getOpenQuantity());

            if (openQuantity > 0) {
                String action = Action.BUY;
                if (Side.BOT.equals(this.getOpenTradePosition().getSide())) {
                    action = Action.SELL;
                }

                return this.createOrder(this.getTradestrategy().getContract(), action, OrderType.MKT, null, null,
                        openQuantity, null, null, TriggerMethod.DEFAULT, OverrideConstraints.YES, TimeInForce.DAY,
                        false, transmit, null, null, this.getOpenPositionOrder().getFAProfile(),
                        this.getOpenPositionOrder().getFAGroup(), this.getOpenPositionOrder().getFAMethod(),
                        this.getOpenPositionOrder().getFAPercent());
            }
        }
        return null;
    }

    /**
     * This method creates an open position order for the Trade. The order is
     * persisted and transmitted via the broker interface to the market.
     */

    public TradeOrder submitOrder(final Contract contract, final TradeOrder tradeOrder) throws StrategyRuleException {

        try {
            tradeOrder.validate();
            TradeOrder instance = getBrokerManager().onPlaceOrder(contract, tradeOrder);
            this.getTradestrategyOrders().addTradeOrder(instance);
            return instance;
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 500, "Error submitting new tradeOrder to broker : " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 300, "Error create tradeOrder : " + ex.getMessage());
        }
    }

    /**
     * Method createOrder.
     * <p>
     * This method creates an open position order for the Trade. The order is
     * persisted and transmitted via the broker interface to the market.
     *
     * @param contract   Contract
     * @param action     String
     * @param orderType  String
     * @param limitPrice Money
     * @param auxPrice   Money
     * @param quantity   Integer
     * @param roundPrice Boolean
     * @param transmit   Boolean
     */
    public TradeOrder createOrder(Contract contract, String action, String orderType, Money limitPrice, Money auxPrice,
                                  Integer quantity, Boolean roundPrice, Boolean transmit) throws StrategyRuleException {
        return createOrder(contract, action, orderType, limitPrice, auxPrice, quantity, null, null,
                TriggerMethod.DEFAULT, OverrideConstraints.YES, TimeInForce.DAY, roundPrice, transmit, null, null, null,
                null, null, null);
    }

    /**
     * Method createOrder.
     * <p>
     * This method creates an open position order for the Trade. The order is
     * persisted and transmitted via the broker interface to the market.
     */
    public TradeOrder createOrder(Contract contract, String action, String orderType, Money limitPrice, Money auxPrice,
                                  int quantity, String ocaGroupName, Boolean roundPrice, Boolean transmit) throws StrategyRuleException {
        return createOrder(contract, action, orderType, limitPrice, auxPrice, quantity, ocaGroupName, null,
                TriggerMethod.DEFAULT, OverrideConstraints.YES, TimeInForce.DAY, roundPrice, transmit, null, null, null,
                null, null, null);
    }

    /**
     * Method createOrder.
     * <p>
     * This method creates an open position order for the Trade. The order is
     * persisted and transmitted via the broker interface to the market.
     */
    public TradeOrder createOrder(Contract contract, String action, String orderType, Money limitPrice, Money auxPrice,
                                  Integer quantity, String ocaGroupName, Integer triggerMethod, Integer overrideConstraints,
                                  String timeInForce, Boolean roundPrice, Boolean transmit) throws StrategyRuleException {
        return createOrder(contract, action, orderType, limitPrice, auxPrice, quantity, ocaGroupName, null,
                triggerMethod, overrideConstraints, timeInForce, roundPrice, transmit, null, null, null, null, null,
                null);
    }

    /**
     * Method createOrder.
     * <p>
     * This method creates an open position order for the Trade. The order is
     * persisted and transmitted via the broker interface to the market.
     */
    public TradeOrder createOrder(Contract contract, String action, String orderType, Money limitPrice, Money auxPrice,
                                  Integer quantity, String ocaGroupName, Integer parentId, Integer triggerMethod, Integer overrideConstraints,
                                  String timeInForce, Boolean roundPrice, Boolean transmit, Money trailStopPrice, Percent trailingPercent,
                                  String FAProfile, String FAGroup, String FAMethod, BigDecimal FAPercent) throws StrategyRuleException {

        try {

            TradeOrder tradeOrder = new TradeOrder(this.getTradestrategy(), action, this.getOrderCreateDate(),
                    orderType, limitPrice, auxPrice, quantity, ocaGroupName, parentId, triggerMethod,
                    overrideConstraints, timeInForce, transmit, trailStopPrice, trailingPercent, FAProfile, FAGroup,
                    FAMethod, FAPercent);

            if (roundPrice) {
                tradeOrder = this.roundTradeOrderPrice(tradeOrder);
            }
            tradeOrder.validate();
            tradeOrder = getBrokerManager().onPlaceOrder(contract, tradeOrder);
            this.getTradestrategyOrders().addTradeOrder(tradeOrder);
            return tradeOrder;

        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 500, "Error submitting new tradeOrder to broker : " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 300, "Error create tradeOrder : " + ex.getMessage());
        }
    }

    /**
     * Method updateOrder.
     * <p>
     * This method creates an open position order for the Trade. The order is
     * persisted and transmitted via the broker interface to the market.
     */
    public TradeOrder updateOrder(Integer orderKey, String action, String orderType, Money limitPrice, Money auxPrice,
                                  Integer quantity, Boolean roundPrice, Boolean transmit) throws StrategyRuleException {
        try {

            if (null == orderKey)
                throw new StrategyRuleException(1, 200, "Order Key cannot be null");

            TradeOrder tradeOrder = tradeService.findTradeOrderByKey(orderKey);

            if (null == action)
                throw new StrategyRuleException(1, 201, "Action cannot be null");

            if (OrderType.LMT.equals(orderType) && null == limitPrice)
                throw new StrategyRuleException(1, 204, "Limit price cannot be null");

            if (OrderType.STPLMT.equals(orderType) && (null == limitPrice || null == auxPrice))
                throw new StrategyRuleException(1, 205, "Limit/Aux price cannot be null");

            if (roundPrice) {
                String side = (Action.BUY.equals(action) ? Side.BOT : Side.SLD);
                if (OrderType.LMT.equals(orderType)) {
                    if (roundPrice) {
                        limitPrice = addPennyAndRoundStop(limitPrice.doubleValue(), side, action, 0.01);
                    }
                } else if (OrderType.STPLMT.equals(orderType)) {
                    Money diffPrice = limitPrice.subtract(auxPrice);
                    auxPrice = addPennyAndRoundStop(auxPrice.doubleValue(), side, action, 0.01);
                    limitPrice = auxPrice.add(diffPrice);
                } else {
                    auxPrice = addPennyAndRoundStop(auxPrice.doubleValue(), side, action, 0.01);
                }
            }
            tradeOrder.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            tradeOrder.setLimitPrice((null == limitPrice ? null : limitPrice.getBigDecimalValue()));
            tradeOrder.setAuxPrice((null == auxPrice ? null : auxPrice.getBigDecimalValue()));

            if (quantity > 0)
                tradeOrder.setQuantity(quantity);

            if (null != orderType)
                tradeOrder.setOrderType(orderType);

            tradeOrder.setTransmit(transmit);
            TradeOrder tradeOrderUpdate = getBrokerManager().onPlaceOrder(getTradestrategy().getContract(), tradeOrder);

            tradeOrder.setVersion(tradeOrderUpdate.getVersion());
            return tradeOrder;
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 510,
                    "Error submitting updated tradeOrder to broker: " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 310, "Error update tradeOrder : " + ex.getMessage());
        }
    }

    /**
     * Method createRiskOpenPosition. This method creates an open position order
     * for the Trade. The order is persisted and transmitted via the broker
     * interface to the market.
     */
    public TradeOrder createRiskOpenPosition(String action, Money entryPrice, Money stopPrice, boolean transmit,
                                             String FAProfile, String FAGroup, String FAMethod, Percent FAPercent) throws StrategyRuleException {

        if (this.isThereOpenPosition())
            throw new StrategyRuleException(1, 205, "Cannot create position for TradePosition Id: "
                    + this.getOpenTradePosition().getId() + " as position is already open.");

        if (null == action)
            throw new StrategyRuleException(1, 206, "Action cannot be null");

        try {

            Entrylimit entrylimit = getEntryLimit().getValue(entryPrice);
            String side = (Action.BUY.equals(action) ? Side.BOT : Side.SLD);

            /*
             * Add/Subtract 1 cent to the entry round the price for 1, 0.5
             * numbers
             */
            entryPrice = addPennyAndRoundStop(entryPrice.doubleValue(), side, action, 0.01);
            double risk = getTradestrategy().getRiskAmount().doubleValue();

            double stop = entryPrice.doubleValue() - stopPrice.doubleValue();

            // Round to round value
            int quantity = (int) ((int) risk / Math.abs(stop));
            /*
             * Check to see if we are in the limits of the amount of margin we
             * can use. If percentOfMargin is null or zero ignore this calc.
             */
            if (null != entrylimit.getPercentOfMargin() && entrylimit.getPercentOfMargin().doubleValue() > 0) {
                if ((quantity * entryPrice.doubleValue()) > this.getIndividualAccount().getBuyingPower()
                        .multiply(entrylimit.getPercentOfMargin()).doubleValue()) {
                    quantity = (int) ((int) this.getIndividualAccount().getBuyingPower().doubleValue()
                            * entrylimit.getPercentOfMargin().doubleValue()
                            / entryPrice.getBigDecimalValue().doubleValue());
                }
            }

            quantity = (int) ((Math.rint(quantity / entrylimit.getShareRound().doubleValue()))
                    * entrylimit.getShareRound().doubleValue());
            if (quantity == 0) {
                quantity = 10;
            }

            Money limitPrice = new Money(
                    (Side.BOT.equals(side) ? (entryPrice.doubleValue() + entrylimit.getLimitAmount().doubleValue())
                            : (entryPrice.doubleValue() - entrylimit.getLimitAmount().doubleValue())));
            TradeOrder tradeOrder = new TradeOrder(this.getTradestrategy(), action, OrderType.STPLMT, quantity,
                    entryPrice.getBigDecimalValue(), limitPrice.getBigDecimalValue(), this.getOrderCreateDate());

            tradeOrder.setStopPrice(stopPrice.getBigDecimalValue());
            tradeOrder.setTransmit(transmit);
            if (FAProfile != null) {
                tradeOrder.setFAProfile(FAProfile);
            } else {
                if (FAGroup != null) {
                    tradeOrder.setFAGroup(FAGroup);
                    tradeOrder.setFAMethod(FAMethod);
                    tradeOrder.setFAPercent(FAPercent.getBigDecimalValue());
                } else {
                    if (null != getTradestrategy().getPortfolio().getIndividualAccount()) {
                        tradeOrder.setAccountNumber(
                                getTradestrategy().getPortfolio().getIndividualAccount().getAccountNumber());
                    }
                }
            }
            tradeOrder = getBrokerManager().onPlaceOrder(getTradestrategy().getContract(), tradeOrder);
            this.getTradestrategyOrders().addTradeOrder(tradeOrder);
            return tradeOrder;
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 520, "Error submitting new tradeOrder to broker: " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 320, "Error create risk open tradeOrder : " + ex.getMessage());
        }
    }

    /**
     * Method cancelOrder. This method cancels the open order position for the
     * Trade. The order is persisted and transmitted via the broker interface to
     * the market.
     */
    public void cancelOrder(final TradeOrder order) throws StrategyRuleException {
        try {
            if (null != order) {
                if (order.isActive()) {
                    getBrokerManager().onCancelOrder(order);
                }
            }
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 530, "Error cancelling tradeOrder to broker: " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 330, "Error create risk open tradeOrder : " + ex.getMessage());
        }
    }

    /**
     * Method isPositionConvered. This method checks to see if the open order
     * position for the Trade has a order to cover the position i.e. a
     * target/stop that covers the total open quantity.
     */
    public boolean isPositionCovered() throws StrategyRuleException {

        try {
            int openQuantity = 0;
            if (this.isThereOpenPosition()) {
                // Find the open position orders
                for (TradeOrder order : this.getTradestrategyOrders().getTradeOrders()) {
                    if (order.isActive()) {
                        /*
                         * Note that this will give 2X the open amount. But when
                         * an OCA order is filled the cancel tends to happen
                         * before the other side is completely filled.
                         */
                        openQuantity = openQuantity + order.getQuantity();
                    }
                }
                return openQuantity >= Math.abs(this.getOpenTradePosition().getOpenQuantity());
            }
            return false;

        } catch (Exception ex) {
            throw new StrategyRuleException(1, 340,
                    "Error StrategyWorker isPositionCovered exception: " + ex.getMessage());
        }
    }

    /**
     * Method createStopAndTargetOrder. This method creates orders to cover an
     * open position order for the Trade. The order is persisted and transmitted
     * via the broker interface to the market. This will create two order OCA
     * one as a LMT target order and one as a STP stop order.
     * <p>
     * i.e. If we are in a position IBM 1000 shares at $120 then the following
     * will create two orders. stopPrice = $118 quantity = 1000 numberRiskUnits
     * = 3 percentQty = 100%
     * <p>
     * IBM LMT $125.99 IBM STP $117.99
     * <p>
     * Note all orders are rounded up/down (around whole/half numbers.) based on
     * the EntryLimit table.
     */
    public TradeOrder createStopAndTargetOrder(final Money stopPrice, final Money targetPrice, final Integer quantity,
                                               final Boolean stopTransmit) throws StrategyRuleException {

        if (quantity == 0)
            throw new StrategyRuleException(1, 207, "Quantity cannot be zero");

        if (!this.isThereOpenPosition()) {
            throw new StrategyRuleException(1, 208, "Error position is not open");
        }
        try {
            String action = Action.BUY;
            if (Side.BOT.equals(getOpenTradePosition().getSide())) {
                action = Action.SELL;
            }

            String ocaID = Integer.toString((BigDecimal.valueOf(Math.random() * 1000000)).intValue());

            TradeOrder orderTarget = new TradeOrder(this.getTradestrategy(), action, OrderType.LMT, quantity, null,
                    targetPrice.getBigDecimalValue(), this.getOrderCreateDate());

            orderTarget.setOcaType(2);
            orderTarget.setTransmit(true);
            orderTarget.setOcaGroupName(ocaID);

            orderTarget = getBrokerManager().onPlaceOrder(getTradestrategy().getContract(), orderTarget);
            this.getTradestrategyOrders().addTradeOrder(orderTarget);
            /*
             * Note the last order submitted in TWS on OCA order is the only one
             * that can be updated
             */

            TradeOrder orderStop = new TradeOrder(this.getTradestrategy(), action, OrderType.STP, quantity,
                    stopPrice.getBigDecimalValue(), null, this.getOrderCreateDate());
            orderStop.setOcaType(2);
            orderStop.setTransmit(stopTransmit);
            orderStop.setOcaGroupName(ocaID);
            if (null != getTradestrategy().getPortfolio().getIndividualAccount()) {
                orderStop.setAccountNumber(getTradestrategy().getPortfolio().getIndividualAccount().getAccountNumber());

            }
            orderStop = getBrokerManager().onPlaceOrder(getTradestrategy().getContract(), orderStop);
            this.getTradestrategyOrders().addTradeOrder(orderStop);
            return orderTarget;

        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 540, "Error submitting new tradeOrder to broker: " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 350, "Error create stop/target tradeOrder: " + ex.getMessage());
        }
    }

    /**
     * Method createStopAndTargetOrder. This method creates orders to cover an
     * open position order for the Trade. The order is persisted and transmitted
     * via the broker interface to the market. This will create two order OCA
     * one as a LMT target order and one as a STP stop order.
     * <p>
     * i.e. If we are in a position IBM 1000 shares at $120 then the following
     * will create two orders. stopPrice = $118 quantity = 1000 numberRiskUnits
     * = 3 percentQty = 100%
     * <p>
     * IBM LMT $125.99 IBM STP $117.99
     * <p>
     * Note all orders are rounded up/down (around whole/half numbers.) based on
     * the EntryLimit table.
     */
    public TradeOrder createStopAndTargetOrder(final TradeOrder openPosition, final Integer stopRiskUnits,
                                               final Money stopAddAmount, final Integer targetRiskUnits, final Money targetAddAmount,
                                               final Integer quantity, final Boolean stopTransmit) throws StrategyRuleException {

        if (!this.isThereOpenPosition()) {
            throw new StrategyRuleException(1, 209, "Error position is not open");
        }
        try {
            /*
             * Risk amount is based of the average filled price and actual stop
             * price not the rounded quantity. But if the stop price is not set
             * use Risk Amount/Quantity.
             */
            double riskAmount;
            if (null == openPosition.getStopPrice()) {
                riskAmount = Math.abs(this.getTradestrategy().getRiskAmount().doubleValue()
                        / openPosition.getFilledQuantity().doubleValue());
            } else {
                riskAmount = Math.abs(
                        openPosition.getAverageFilledPrice().doubleValue() - openPosition.getStopPrice().doubleValue());
            }

            String action = Action.BUY;
            int buySellMultipliter = 1;
            if (Side.BOT.equals(getOpenTradePosition().getSide())) {
                action = Action.SELL;
                buySellMultipliter = -1;
            }

            // Add a penny to the stop and target
            double stop = openPosition.getAverageFilledPrice().doubleValue()
                    + (riskAmount * stopRiskUnits * buySellMultipliter);
            if (stop < 0)
                stop = 0.02;
            Money stopPrice = addPennyAndRoundStop(stop, this.getOpenTradePosition().getSide(), action,
                    stopAddAmount.doubleValue());

            double target = openPosition.getAverageFilledPrice().doubleValue()
                    + (riskAmount * targetRiskUnits * buySellMultipliter * -1);
            if (target < 0)
                target = 0.02;
            Money targetPrice = addPennyAndRoundStop(target, this.getOpenTradePosition().getSide(), action,
                    targetAddAmount.doubleValue());

            String ocaID = Integer.toString((BigDecimal.valueOf(Math.random() * 1000000)).intValue());

            TradeOrder orderTarget = new TradeOrder(this.getTradestrategy(), action, OrderType.LMT, quantity, null,
                    targetPrice.getBigDecimalValue(), this.getOrderCreateDate());

            orderTarget.setOcaType(2);
            orderTarget.setTransmit(true);
            orderTarget.setOcaGroupName(ocaID);
            orderTarget.setAccountNumber(openPosition.getAccountNumber());
            orderTarget.setFAGroup(openPosition.getFAGroup());
            orderTarget.setFAProfile(openPosition.getFAProfile());
            orderTarget.setFAMethod(openPosition.getFAMethod());
            orderTarget.setFAPercent(openPosition.getFAPercent());
            orderTarget = getBrokerManager().onPlaceOrder(getTradestrategy().getContract(), orderTarget);
            this.getTradestrategyOrders().addTradeOrder(orderTarget);
            /*
             * Note the last order submitted in TWS on OCA order is the only one
             * that can be updated
             */

            TradeOrder orderStop = new TradeOrder(this.getTradestrategy(), action, OrderType.STP, quantity,
                    stopPrice.getBigDecimalValue(), null, this.getOrderCreateDate());
            orderStop.setOcaType(2);
            orderStop.setTransmit(stopTransmit);
            orderStop.setOcaGroupName(ocaID);
            orderStop.setAccountNumber(openPosition.getAccountNumber());
            orderStop.setFAGroup(openPosition.getFAGroup());
            orderStop.setFAProfile(openPosition.getFAProfile());
            orderStop.setFAMethod(openPosition.getFAMethod());
            orderStop.setFAPercent(openPosition.getFAPercent());
            orderStop = getBrokerManager().onPlaceOrder(getTradestrategy().getContract(), orderStop);
            this.getTradestrategyOrders().addTradeOrder(orderStop);
            return orderTarget;
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 550, "Error submitting new tradeOrder to broker: " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 360, "Error create stop/target tradeOrder: " + ex.getMessage());
        }
    }

    /**
     * Method getStopPriceForPositionRisk. This method will calculate the Stop
     * price based on the number of risk units and the risk amount.
     */
    public Money getStopPriceForPositionRisk(final TradeOrder openPosition, int numberRiskUnits)
            throws StrategyRuleException {
        try {
            double riskAmount = (this.getTradestrategy().getRiskAmount().doubleValue()
                    / this.getOpenTradePosition().getOpenQuantity()) * numberRiskUnits;

            if (Side.BOT.equals(this.getOpenTradePosition().getSide())) {
                riskAmount = riskAmount * -1;
            }

            // Add a penny to the stop
            return new Money(openPosition.getAverageFilledPrice().doubleValue() + riskAmount);

        } catch (Exception ex) {
            throw new StrategyRuleException(1, 370, "Error getting stop price for risk position: " + ex.getMessage());
        }
    }

    /**
     * Method cancelOrdersClosePosition. This method will close a position by
     * canceling all unfilled orders and creating a market order to close the
     * position.
     */
    public TradeOrder cancelOrdersClosePosition(boolean transmit) throws StrategyRuleException {

        _log.debug("Strategy  closeOpenPosition symbol: {}", symbol);
        try {
            cancelAllOrders();
            if (this.isThereOpenPosition()) {
                return closePosition(transmit);
            }
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 380, "Error StrategyWorker exception: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Method moveStopOCAPrice. This method will the stop order for a trade to
     * the new values..
     */
    public void moveStopOCAPrice(Money stopPrice, Boolean transmit) throws StrategyRuleException {

        try {
            if (this.isThereOpenPosition()) {
                // If the STP order has changed send the update and refresh the
                // version of the order.
                for (TradeOrder tradeOrder : this.getTradestrategyOrders().getTradeOrders()) {
                    if (!tradeOrder.getIsOpenPosition() && tradeOrder.isActive()) {
                        if (OrderType.STP.equals(tradeOrder.getOrderType()) && null != tradeOrder.getOcaGroupName()) {
                            if (!tradeOrder.getAuxPrice().equals(stopPrice.getBigDecimalValue())
                                    || !tradeOrder.getTransmit().equals(transmit)) {
                                tradeOrder.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                                tradeOrder.setAuxPrice(stopPrice.getBigDecimalValue());
                                tradeOrder.setTransmit(transmit);
                                TradeOrder tradeOrderBE = this.getBrokerManager()
                                        .onPlaceOrder(getTradestrategy().getContract(), tradeOrder);
                                tradeOrder.setVersion(tradeOrderBE.getVersion());
                            }
                        }
                    }
                }
            }
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 560, "Error updating tradeOrder to broker: " + ex.getMessage());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 390, "Error StrategyWorker moveStopOCAPrice exception Symbol: "
                    + this.symbol + " Stop Price: " + stopPrice + " Msg: " + ex.getMessage());
        }
    }

    /**
     * Method cancelAllOrders. This method will all orders for a trade position.
     */
    public void cancelAllOrders() throws StrategyRuleException {
        _log.debug("Strategy  cancelAllOrders symbol: {}", symbol);
        for (TradeOrder order : this.getTradestrategyOrders().getTradeOrders()) {
            cancelOrder(order);
        }
    }

    /**
     * Method addPennyAndRoundStop. This method takes a price and adds/subtracts
     * pennies to that prices and rounds the results based on whole/half number.
     */
    public Money addPennyAndRoundStop(double price, String side, String action, double dollars)
            throws StrategyRuleException {
        if (price < 0) {
            throw new StrategyRuleException(1, 223, "Error rounding price cannot be less than zero price: " + price);
        }
        double roundPrice;
        if (Side.BOT.equals(side)) {
            roundPrice = roundPrice(price + dollars, action);
        } else {
            roundPrice = roundPrice(price - dollars, action);
        }
        return new Money(roundPrice);
    }

    /**
     * Method requestOrderExecutions.
     */
    public void requestOrderExecutions(final Tradestrategy tradestrategy) throws StrategyRuleException {

        try {
            this.getBrokerManager().onReqExecutions(tradestrategy, true);
        } catch (BrokerModelException ex) {
            throw new StrategyRuleException(1, 224, "Error requesting execulted orders. Meg: " + ex.getMessage());
        }

    }

    /**
     * Method getCurrentCandleCount.
     *
     * @return int
     */
    public int getCurrentCandleCount() {
        return currentCandleCount;
    }

    /**
     * Method getCurrentCandle.
     *
     * @return CandleItem
     */
    public CandleItem getCurrentCandle() {
        if (getCurrentCandleCount() > -1) {
            CandleSeries candleSeries = this.getTradestrategy().getStrategyData().getBaseCandleSeries();
            return (CandleItem) candleSeries.getDataItem(getCurrentCandleCount());
        }
        return null;
    }

    /**
     * Method getCandle.
     *
     * @param startPeriod ZonedDateTime
     */
    public CandleItem getCandle(ZonedDateTime startPeriod) throws StrategyRuleException {
        CandleItem candle;
        CandleSeries baseCandleSeries = getTradestrategy().getStrategyData().getBaseCandleSeries();
        CandlePeriod period = new CandlePeriod(startPeriod, baseCandleSeries.getBarSize());
        int index = baseCandleSeries.indexOf(period);
        if (index > -1) {
            candle = (CandleItem) baseCandleSeries.getDataItem(index);
        } else {
            throw new StrategyRuleException(1, 210,
                    "Error Candle not found for period: " + period + " in baseCandleSeries barSize: "
                            + baseCandleSeries.getBarSize() + " series count: " + baseCandleSeries.getItemCount()
                            + " StartPeriod: " + startPeriod);
        }
        return candle;
    }

    /**
     * Method updateTradestrategyStatus.
     */
    public void updateTradestrategyStatus(String status) throws StrategyRuleException {
        try {
            this.getTradestrategyOrders().setStatus(status);
            this.getTradestrategyOrders().setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            this.tradestrategyOrders = tradeService.saveAspect(this.getTradestrategyOrders());
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 400, "Error updating tradestrategy status: " + ex.getMessage());
        }
    }

    /**
     * Method getBrokerManager.
     *
     * @return IBrokerModel
     */
    private IBrokerModel getBrokerManager() {
        return this.brokerModel;
    }

    /**
     * Method getEntryLimit.
     *
     * @return DAOEntryLimit
     */
    public DAOEntryLimit getEntryLimit() {
        return this.entryLimits;
    }

    /**
     * Method getTradestrategy.
     *
     * @return Tradestrategy
     */
    public Tradestrategy getTradestrategy() {
        return this.tradestrategy;
    }

    /**
     * Method getTradestrategyOrders.
     *
     * @return TradestrategyOrders
     */
    public TradestrategyOrders getTradestrategyOrders() {
        return this.tradestrategyOrders;
    }

    /**
     * Method getStrategyLastFired.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getStrategyLastFired() {
        return strategyLastFired;
    }

    /**
     * Method reFreshPositionOrders.
     */

    public void reFreshPositionOrders() throws StrategyRuleException {
        try {
            this.tradestrategyOrders = tradeService
                    .findPositionOrdersByTradestrategyId(this.idTradestrategy);
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 410, "Error position orders: " + ex.getMessage());
        }
    }

    /**
     * Method getIndividualAccount. Return a refreshed trade account note this
     * is updated when connected to TWS every time the account values change.
     *
     * @return Account
     */
    public Account getIndividualAccount() throws StrategyRuleException {
        try {
            if (null != getTradestrategy().getPortfolio().getIndividualAccount()) {
                return tradeService.findAccountByAccountNumber(
                        getTradestrategy().getPortfolio().getIndividualAccount().getAccountNumber());
            }
            return null;
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 420, "Error finding individual accounts: " + ex.getMessage());
        }
    }

    /**
     * Method hasActiveOrders.
     *
     * @return boolean
     */

    public boolean hasActiveOrders() {
        for (TradeOrder tradeOrder : getTradestrategyOrders().getTradeOrders()) {
            if (tradeOrder.isActive())
                return true;
        }
        return false;
    }

    /**
     * Method isThereOpenPosition.
     *
     * @return boolean
     */
    public boolean isThereOpenPosition() {
        return null != getOpenTradePosition();
    }

    /**
     * Method getOpenTradePosition.
     *
     * @return TradePosition
     */
    public TradePosition getOpenTradePosition() {
        return getTradestrategyOrders().getOpenTradePosition();
    }

    /**
     * Method getSymbol.
     *
     * @return String
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Method getOpenPositionOrder. The open position order is the first order
     * that opened the trade position.
     * <p>
     *
     * @return TradeOrder
     */
    public TradeOrder getOpenPositionOrder() {

        /*
         * First try the TradePosition orders to see if we have a filled
         * Ordered. Note this could have been created by a different
         * tradestrategy.
         */
        if (getTradestrategyOrders().hasOpenTradePosition()) {
            for (TradeOrder tradeOrder : this.getOpenTradePosition().getTradeOrders()) {
                if (tradeOrder.getIsOpenPosition())
                    return tradeOrder;
            }
        }

        return null;
    }

    /**
     * Method getStopPriceMinUnfilled.
     *
     * @return Money
     */
    public Money getStopPriceMinUnfilled() {
        double stopPrice = Double.MAX_VALUE;
        for (TradeOrder tradeOrder : this.getTradestrategyOrders().getTradeOrders()) {
            if (tradeOrder.isActive() && OrderType.STP.equals(tradeOrder.getOrderType())) {
                stopPrice = Math.min(stopPrice, tradeOrder.getAuxPrice().doubleValue());
            }
        }
        if (stopPrice < Double.MAX_VALUE) {
            return new Money(stopPrice);
        }

        return null;
    }

    /**
     * Method getStopPriceMinUnfilled.
     *
     * @return Money
     */
    public Money getTargetPriceMinUnfilled() {
        double stopPrice = Double.MAX_VALUE;
        for (TradeOrder tradeOrder : this.getTradestrategyOrders().getTradeOrders()) {
            if (tradeOrder.isActive() && OrderType.STP.equals(tradeOrder.getOrderType())) {
                stopPrice = Math.min(stopPrice, tradeOrder.getAuxPrice().doubleValue());
            }
        }
        if (stopPrice < Double.MAX_VALUE)
            return new Money(stopPrice);

        return null;
    }

    /**
     * Method getTradeOrder.
     */
    public TradeOrder getTradeOrder(Integer orderKey) {
        for (TradeOrder order : this.getTradestrategyOrders().getTradeOrders()) {
            if (order.getOrderKey().equals(orderKey)) {
                return order;
            }
        }
        return null;
    }

    /**
     * Method isDuringTradingday.
     */
    public boolean isDuringTradingday(ZonedDateTime dateTime) {
        return TradingCalendar.isMarketHours(getTradestrategy().getTradingday().getOpen(),
                getTradestrategy().getTradingday().getClose(), dateTime)
                && TradingCalendar.sameDay(getTradestrategy().getTradingday().getOpen(), dateTime);
    }

    /**
     * Method isRiskViolated.
     */
    public boolean isRiskViolated(Double currentPrice, BigDecimal riskamount, Integer quantity,
                                  BigDecimal averageFilledPrice) {

        BigDecimal currentRiskAmount = new BigDecimal(
                Math.abs(currentPrice - averageFilledPrice.doubleValue()) * quantity);
        return CoreUtils.nullSafeComparator(currentRiskAmount, riskamount) == 1;
    }

    /**
     * Method tradeOrderFilled. Over-ride this in your strategy if you want to
     * add this action.
     *
     * @param tradeOrder TradeOrder
     */

    public void tradeOrderFilled(final TradeOrder tradeOrder) {
    }

    /**
     * Method logCandle.
     */

    public static void logCandle(AbstractStrategyRule context, Candle candle) {
        _log.debug("{} Symbol: {} startPeriod: {} endPeriod: {} Open: {} High: {} Low: {} Close: {} Volume: {} Vwap: {} TradeCount: {} LastUpdate: {}", context.getClass().getSimpleName(), candle.getContract().getSymbol(), candle.getStartPeriod(), candle.getEndPeriod(), new Money(candle.getOpen()), new Money(candle.getHigh()), new Money(candle.getLow()), new Money(candle.getClose()), new Money(candle.getVolume()), new Money(candle.getVwap()), new Money(candle.getTradeCount()), candle.getLastUpdateDate());
    }

    /**
     * Method roundPrice.
     */
    private double roundPrice(double price, String action) throws StrategyRuleException {
        try {
            // Round at whole and half numbers add to this if you
            // need others.
            Entrylimit entrylimit = getEntryLimit().getValue(new Money(price));
            if (null == entrylimit) {
                throw new StrategyRuleException(1, 211, "No EntryLimits found for price: " + price);
            }

            double[] rounding = {1, 0.5};
            int buySellMultiplier = 1;

            if (action.equals(Action.SELL)) {
                buySellMultiplier = -1;
            }

            for (double element : rounding) {
                // Round the price to over under half numbers
                double wholePrice = price + (1 - element);
                double remainder = ((Math.rint(wholePrice) - wholePrice) * buySellMultiplier);
                if ((remainder < entrylimit.getPriceRound().doubleValue()) && (remainder >= 0)) {
                    price = (Math.rint(wholePrice) + (0.01d * buySellMultiplier)) - (1 - element);
                    return price;
                }
            }
            return price;
        } catch (Exception ex) {
            throw new StrategyRuleException(1, 420, "Error rounding price: " + ex.getMessage());
        }
    }

    /**
     * Method getOrderCreateDate. If realtimebars running use current time
     * otherwise we are testing use candle lastUpdateDate.
     *
     * @return LocalDatetime
     */
    private ZonedDateTime getOrderCreateDate() {
        ZonedDateTime createDate = TradingCalendar.getDateTimeNowMarketTimeZone();
        if (!brokerModel.isRealtimeBarsRunning(this.tradestrategy)) {
            if (null != this.getCurrentCandle())
                createDate = this.getCurrentCandle().getPeriod().getStart();
        }
        return createDate;
    }

    /**
     * Method roundTradeOrderPrice.
     */
    private TradeOrder roundTradeOrderPrice(final TradeOrder tradeOrder) throws StrategyRuleException {

        String side = (Action.BUY.equals(tradeOrder.getAction()) ? Side.BOT : Side.SLD);
        if (OrderType.LMT.equals(tradeOrder.getOrderType())) {
            Money limitPrice = addPennyAndRoundStop(tradeOrder.getLimitPrice().doubleValue(), side,
                    tradeOrder.getAction(), 0.01);
            tradeOrder.setLimitPrice(limitPrice.getBigDecimalValue());
        } else if (OrderType.STPLMT.equals(tradeOrder.getOrderType())) {
            BigDecimal diffPrice = tradeOrder.getLimitPrice().subtract(tradeOrder.getAuxPrice());
            BigDecimal auxPrice = addPennyAndRoundStop(tradeOrder.getAuxPrice().doubleValue(), side,
                    tradeOrder.getAction(), 0.01).getBigDecimalValue();
            BigDecimal limitPrice = auxPrice.add(diffPrice);
            tradeOrder.setLimitPrice(limitPrice);
            tradeOrder.setAuxPrice(auxPrice);
        } else {
            BigDecimal auxPrice = addPennyAndRoundStop(tradeOrder.getAuxPrice().doubleValue(), side,
                    tradeOrder.getAction(), 0.01).getBigDecimalValue();
            tradeOrder.setAuxPrice(auxPrice);
        }
        return tradeOrder;
    }
}
