/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
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
package org.trade.broker.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.factory.ClassFactory;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.TradingCalendar;
import org.trade.dictionary.valuetype.Action;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.dictionary.valuetype.OrderType;
import org.trade.dictionary.valuetype.Side;
import org.trade.persistent.IPersistentModel;
import org.trade.persistent.PersistentModelException;
import org.trade.persistent.dao.Candle;
import org.trade.persistent.dao.Contract;
import org.trade.persistent.dao.Strategy;
import org.trade.persistent.dao.TradeOrder;
import org.trade.persistent.dao.TradeOrderfill;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.persistent.dao.TradestrategyOrders;
import org.trade.strategy.data.CandleDataset;
import org.trade.strategy.data.CandleSeries;
import org.trade.strategy.data.IndicatorSeries;
import org.trade.strategy.data.StrategyData;
import org.trade.strategy.data.candle.CandleItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DBBroker extends Broker {

    private final static Logger _log = LoggerFactory.getLogger(DBBroker.class);

    private IPersistentModel tradePersistentModel = null;
    private StrategyData strategyData = null;
    private Tradestrategy tradestrategy = null;
    private Integer idTradestrategy = null;
    private IClientWrapper brokerModel = null;
    private BigDecimal trailAmount = null;
    private BigDecimal trailLimitOffsetAmount = null;

    private long execId = TradingCalendar.geMillisFromZonedDateTime(TradingCalendar.getDateTimeNowMarketTimeZone());

    private static Integer _backTestBarSize = 0;

    static {
        try {
            _backTestBarSize = ConfigProperties.getPropAsInt("trade.backtest.barSize");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error initializing BackTestBroker Msg: " + ex.getMessage());
        }
    }

    /**
     * Constructor for BackTestBroker.
     *
     * @param strategyData    StrategyData
     * @param idTradestrategy Integer
     * @param brokerModel     IBrokerModel
     */
    public DBBroker(StrategyData strategyData, Integer idTradestrategy, IClientWrapper brokerModel) {
        this.idTradestrategy = idTradestrategy;
        this.brokerModel = brokerModel;
        this.strategyData = strategyData;
    }

    /**
     * Method doInBackground.
     *
     * @return Void
     */
    public Void doInBackground() {

        try {

            this.tradePersistentModel = (IPersistentModel) ClassFactory
                    .getServiceForInterface(IPersistentModel._persistentModel, this);
            this.tradestrategy = this.tradePersistentModel.findTradestrategyById(this.idTradestrategy);
            this.strategyData.clearBaseCandleDataset();
            this.tradestrategy.setStrategyData(this.strategyData);

            ZonedDateTime endDate = TradingCalendar.getDateAtTime(
                    TradingCalendar.getPrevTradingDay(tradestrategy.getTradingday().getClose()),
                    tradestrategy.getTradingday().getClose());
            ZonedDateTime startDate = TradingCalendar.addTradingDays(tradestrategy.getTradingday().getClose(),
                    (-1 * (tradestrategy.getChartDays() - 1)));
            startDate = TradingCalendar.getDateAtTime(startDate, tradestrategy.getTradingday().getOpen());
            endDate = TradingCalendar.addTradingDays(endDate, -1);

            List<Candle> candles = new ArrayList<>();
            List<Candle> candlesTradingday = new ArrayList<>();
            candles = this.getCandles(this.tradestrategy, startDate, endDate, this.tradestrategy.getBarSize());

            if (_backTestBarSize > 0) {

                /*
                 * Try and find the candles in the database with the matching
                 * barSize or the next lowest.
                 */
                candlesTradingday = this.getCandles(this.tradestrategy, this.tradestrategy.getTradingday().getOpen(),
                        this.tradestrategy.getTradingday().getOpen(), _backTestBarSize);

                if (candlesTradingday.isEmpty()) {
                    _log.warn("No backTestBarSize = " + _backTestBarSize + " data available for "
                            + this.tradestrategy.getContract().getSymbol() + " and Tradingday: "
                            + this.tradestrategy.getTradingday().getOpen() + " will use barSize = "
                            + this.tradestrategy.getBarSize() + " data if avaialble.");
                    candlesTradingday = this.getCandles(this.tradestrategy,
                            this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getTradingday().getOpen(),
                            this.tradestrategy.getBarSize());
                }

            } else {
                candlesTradingday = this.getCandles(this.tradestrategy, this.tradestrategy.getTradingday().getOpen(),
                        this.tradestrategy.getTradingday().getOpen(), this.tradestrategy.getBarSize());
            }

            /*
             * Wait for the strategy to start.
             */
            synchronized (lockBackTestWorker) {
                while (strategiesRunning.get() < 1) {
                    lockBackTestWorker.wait();
                }
            }
            if (candlesTradingday.isEmpty()) {
                _log.warn("No data available to run a backtest for Symbol: "
                        + this.tradestrategy.getContract().getSymbol() + " and Tradingday: "
                        + this.tradestrategy.getTradingday().getOpen());
                /*
                 * Poke the strategy this will kill it as there is no data.
                 */
                this.tradestrategy.getStrategyData().getBaseCandleSeries().fireSeriesChanged();
            } else {
                for (Candle candle : candlesTradingday) {
                    candles.add(candle);
                }
                candlesTradingday.clear();
                /*
                 * Populate any child datasets.
                 */
                populateIndicatorCandleSeries(tradestrategy, this.tradestrategy.getTradingday().getOpen(),
                        this.tradestrategy.getTradingday().getOpen());

            }

            TradestrategyOrders positionOrders = null;

            for (Candle candle : candles) {
                /*
                 * We use the direct add to BaseCandle data-set rather than
                 * going via the IBrokerModel because the IBrokerModel is in
                 * another thread and so this thread tends to be blocked by
                 * other activities.
                 */

                ruleComplete.set(0);

                this.tradestrategy.getStrategyData().getBaseCandleSeries().getContract()
                        .setLastAskPrice(candle.getClose());
                this.tradestrategy.getStrategyData().getBaseCandleSeries().getContract()
                        .setLastBidPrice(candle.getClose());
                this.tradestrategy.getStrategyData().getBaseCandleSeries().getContract()
                        .setLastPrice(candle.getClose());

                this.tradestrategy.getStrategyData().buildCandle(candle.getStartPeriod(),
                        candle.getOpen().doubleValue(), candle.getHigh().doubleValue(), candle.getLow().doubleValue(),
                        candle.getClose().doubleValue(), candle.getVolume(), candle.getVwap().doubleValue(),
                        candle.getTradeCount(), this.tradestrategy.getBarSize() / candle.getBarSize(),
                        candle.getLastUpdateDate());

                /*
                 * Wait for the candle to be processed by the strategy.
                 */
                synchronized (lockBackTestWorker) {
                    /*
                     * Wait for the rule to be completed by the strategy. note
                     * this worker is listening to the strategy worker.
                     */
                    while ((strategiesRunning.get() > 0) && (ruleComplete.get() < 1)) {
                        lockBackTestWorker.wait();
                    }
                }
                if (candle.getStartPeriod().isBefore(this.tradestrategy.getTradingday().getOpen()))
                    continue;

                positionOrders = this.tradePersistentModel.findPositionOrdersByTradestrategyId(this.idTradestrategy);

                /*
                 * The new candle may create an order so this call fills it and
                 * return whether this is opening a position.
                 */
                if (filledOrders(this.tradestrategy.getContract(), positionOrders, candle)) {

                    /*
                     * Need to recall fillOrders as this is a new open position
                     * and an OCA order may now be ready to be filled this
                     * happens when we have an engulfing bar. Only need to wait
                     * if this is a new open position. This gives time for the
                     * PositionManagerStrategy to start and create the OCA
                     * order. so we wait to see if those new orders need to be
                     * filled.
                     */

                    /*
                     * Check to see if the strategy needs to update the OCA
                     * orders if the current bar requires the stop to be moved.
                     * This check is only done if the current candle is against
                     * the bar. i.e. we assume if we had a candle that
                     * encompassed the entry and stop and is in the direction of
                     * the trade that we weren't stopped out on the entry
                     * candle.
                     */
                    positionOrders = this.tradePersistentModel
                            .findPositionOrdersByTradestrategyId(this.idTradestrategy);

                    if (this.tradestrategy.getStrategy().hasStrategyManager()) {
                        synchronized (lockBackTestWorker) {
                            while (strategiesRunning.get() < 1 && positionOrders.hasOpenTradePosition()) {
                                lockBackTestWorker.wait();
                            }
                        }
                    }
                    if (positionOrders.hasOpenTradePosition()) {
                        if (!this.tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty()) {
                            CandleItem candleItem = (CandleItem) this.tradestrategy.getStrategyData()
                                    .getBaseCandleSeries().getDataItem(
                                            this.tradestrategy.getStrategyData().getBaseCandleSeries().getItemCount()
                                                    - 1);
                            if (!candleItem.isSide(positionOrders.getOpenTradePosition().getSide())) {
                                /*
                                 * Refresh the orders as the other thread may
                                 * have added orders that need to be filled.
                                 */
                                positionOrders = this.tradePersistentModel
                                        .findPositionOrdersByTradestrategyId(this.idTradestrategy);
                                filledOrders(this.tradestrategy.getContract(), positionOrders, candle);
                            }
                        }
                    }

                    /*
                     * We now have an open position so we wait for the strategy
                     * that got us into this position to close.
                     */
                    synchronized (lockBackTestWorker) {
                        while (strategiesRunning.get() > 1) {
                            lockBackTestWorker.wait();
                        }
                    }
                }
                if (strategiesRunning.get() == 0 && !positionOrders.hasOpenTradePosition())
                    break;
            }
            candles.clear();

        } catch (InterruptedException interExp) {
            // Do nothing.
        } catch (Exception ex) {
            _log.error("Error BackTestBroker Symbol: " + this.tradestrategy.getContract().getSymbol() + " Msg: "
                    + ex.getMessage(), ex);
        }
        return null;
    }

    public void done() {
        brokerModel.onCancelRealtimeBars(this.tradestrategy);
        brokerModel.onCancelBrokerData(this.tradestrategy);
        // Free some memory!!
        this.tradestrategy.setStrategyData(null);
        _log.debug("BackTestBroker done for: " + tradestrategy.getContract().getSymbol() + " idTradestrategy: "
                + this.tradestrategy.getId());
    }

    /**
     * Method filledOrders.
     *
     * @param contract Contract
     * @param trade    Trade
     * @param candle   Candle
     * @return boolean
     * @throws Exception
     */
    private boolean filledOrders(Contract contract, TradestrategyOrders positionOrders, Candle candle)
            throws Exception {

        boolean orderfilled = false;
        for (TradeOrder order : positionOrders.getTradeOrders()) {
            if (OrderStatus.UNSUBMIT.equals(order.getStatus())) {
                /*
                 * Can't use the com.ib.client.OrderState as constructor is no
                 * visible.
                 */
                OrderState orderState = new OrderState();
                orderState.m_status = OrderStatus.SUBMITTED;
                this.brokerModel.openOrder(order.getOrderKey(), contract, order, orderState);
                /*
                 * TODO we should read the orders back after any call to the
                 * broker interface.
                 */
                order.setStatus(OrderStatus.SUBMITTED);
            }
        }
        for (TradeOrder order : positionOrders.getTradeOrders()) {
            if (OrderStatus.SUBMITTED.equals(order.getStatus()) && order.getTransmit()) {

                BigDecimal filledPrice = getFilledPrice(order, candle);
                if (null != filledPrice) {
                    if (!orderfilled)
                        orderfilled = true;

                    if (null == order.getOcaGroupName()) {
                        createOrderExecution(contract, order, filledPrice, candle.getStartPeriod());
                    } else {
                        // If OCA cancel other side
                        for (TradeOrder orderOCA : positionOrders.getTradeOrders()) {
                            if (orderOCA.isDirty())
                                continue;

                            if (order.getOcaGroupName().equals(orderOCA.getOcaGroupName())
                                    && !order.getOrderKey().equals(orderOCA.getOrderKey()) && !orderOCA.getIsFilled()) {
                                BigDecimal orderOCAFilledPrice = getFilledPrice(orderOCA, candle);

                                if (null != orderOCAFilledPrice) {
                                    /*
                                     * Other side of order could have been
                                     * filled on this bar also. So assume the if
                                     * a green bar we went open/low/high/close.
                                     */
                                    if (candle.getClose().compareTo(candle.getOpen()) > 0) {
                                        // Green bar
                                        if (filledPrice.compareTo(orderOCAFilledPrice) > 0) {
                                            cancelOrder(contract, order);
                                            order.setDirty(true);
                                            createOrderExecution(contract, orderOCA, orderOCAFilledPrice,
                                                    candle.getStartPeriod());
                                            orderOCA.setDirty(true);
                                            break;
                                        }
                                    } else {
                                        if (filledPrice.compareTo(orderOCAFilledPrice) < 0) {
                                            cancelOrder(contract, order);
                                            order.setDirty(true);
                                            createOrderExecution(contract, orderOCA, orderOCAFilledPrice,
                                                    candle.getStartPeriod());
                                            orderOCA.setDirty(true);
                                            break;
                                        }
                                    }
                                }
                                cancelOrder(contract, orderOCA);
                                orderOCA.setDirty(true);
                                createOrderExecution(contract, order, filledPrice, candle.getStartPeriod());
                                order.setDirty(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return orderfilled;
    }

    /**
     * Method getFilledPrice.
     *
     * @param order  TradeOrder
     * @param candle Candle
     * @return BigDecimal
     */
    private BigDecimal getFilledPrice(TradeOrder order, Candle candle) {

        if (order.getCreateDate().isAfter(candle.getLastUpdateDate())) {
            return null;
        }

        /*
         * Use the close price for market orders as the candle has been
         * processed by the Strategy at this point. Note assume mkt orders are
         * always filled.
         */
        if (OrderType.MKT.equals(order.getOrderType()))
            return candle.getClose();

        /*
         * There must be enough volume to fill the unfilled quantity. For none
         * market orders.
         *
         * TODO add logic to handle partial fills.
         */
        if (candle.getVolume() < order.getQuantity())
            return null;

        /*
         * Set the AuxPrice to the trailing amount.
         */
        if (OrderType.TRAIL.equals(order.getOrderType()) || OrderType.TRAILLIMIT.equals(order.getOrderType())) {
            /*
             * First time in set the trailAmount which will be the trailing
             * amount note AuxPrice doubles as a holder for this values until
             * the price triggers this value to be set to the trailing price.
             * i.e first time in it is set to the current price -0.12c then once
             * the price moves up the trail is reset to current - 0.12c for a
             * long position.
             *
             * Note first time in the AuxPrice is trail amount i.e 0.12c behind
             * close price. The Limit price is the LimitOffSet i.e 0.04c so
             * SPTLMT order when current price is 63.22 this sets up a STPLMT
             * order with stop price at 63.10 and Limit at 63.06.
             *
             * The Trailing Stop Price holds the starting point for the Stop
             * price if this field is specified and if is less than the current
             * market price for a Long position.
             */

            if (null != order.getAuxPrice()) {
                if (null == trailAmount) {
                    trailAmount = order.getAuxPrice();
                    trailLimitOffsetAmount = order.getLimitPrice();
                    order.setAuxPrice((Action.SELL.equals(order.getAction()) ? candle.getClose().subtract(trailAmount)
                            : candle.getClose().add(trailAmount)));
                }

            } else {
                if (null != order.getTrailingPercent()) {
                    if (null == trailAmount) {
                        trailAmount = (candle.getClose().multiply(order.getTrailingPercent()))
                                .divide(new BigDecimal(100));
                        trailLimitOffsetAmount = order.getLimitPrice();
                        if (null != order.getTrailStopPrice()) {
                            if (Action.SELL.equals(order.getAction())
                                    && -1 == order.getTrailStopPrice().compareTo(candle.getClose())) {
                                order.setAuxPrice(order.getTrailStopPrice());
                            } else if (Action.BUY.equals(order.getAction())
                                    && 1 == order.getTrailStopPrice().compareTo(candle.getClose())) {
                                order.setAuxPrice(order.getTrailStopPrice());
                            }

                        } else {
                            order.setAuxPrice((Action.SELL.equals(order.getAction())
                                    ? candle.getClose().subtract(trailAmount) : candle.getClose().add(trailAmount)));
                            order.setLimitPrice((Action.SELL.equals(order.getAction())
                                    ? candle.getClose().subtract(trailAmount.subtract(trailLimitOffsetAmount))
                                    : candle.getClose().add(trailAmount.add(trailLimitOffsetAmount))));
                        }
                    }
                }
            }
            BigDecimal avgFillPrice = null;
            if (order.hasTradePosition()) {
                avgFillPrice = order.getTradePosition().getTotalNetValue()
                        .divide(new BigDecimal(order.getTradePosition().getOpenQuantity()));
            } else {
                avgFillPrice = candle.getClose();
            }

            /*
             * Only trigger the trailing when we are aover/under the average
             * fill price of the position. Otherwise the stop price will be
             * either that which was set in TrailStopPrice or the market price
             * when the order was first submitted. i.e candle.close - trail
             * amt/percent
             */
            if (Action.SELL.equals(order.getAction()) && (1 == candle.getClose().compareTo(avgFillPrice))) {
                if (1 == candle.getClose().subtract(trailAmount).compareTo(order.getAuxPrice())) {
                    order.setAuxPrice(candle.getClose().subtract(trailAmount));
                    if (OrderType.TRAILLIMIT.equals(order.getOrderType())) {
                        order.setLimitPrice(candle.getClose().subtract(trailAmount.subtract(trailLimitOffsetAmount)));
                    }
                }
            }
            if (Action.BUY.equals(order.getAction()) && (-1 == candle.getClose().compareTo(avgFillPrice))) {
                if (-1 == candle.getClose().add(trailAmount).compareTo(order.getAuxPrice())) {
                    order.setAuxPrice(candle.getClose().add(trailAmount));
                    if (OrderType.TRAILLIMIT.equals(order.getOrderType())) {
                        order.setLimitPrice(candle.getClose().add(trailAmount.add(trailLimitOffsetAmount)));
                    }
                }
            }
        }

        if (Action.SELL.equals(order.getAction())) {
            if (OrderType.STP.equals(order.getOrderType()) || OrderType.TRAIL.equals(order.getOrderType())) {
                if (candle.getLow().compareTo(order.getAuxPrice()) < 1) {
                    if (candle.getOpen().compareTo(order.getAuxPrice()) < 1) {
                        return candle.getOpen();
                    }
                    return order.getAuxPrice();
                }
            } else if (OrderType.STPLMT.equals(order.getOrderType())
                    || OrderType.TRAILLIMIT.equals(order.getOrderType())) {
                if (candle.getLow().compareTo(order.getAuxPrice()) < 1
                        && candle.getHigh().compareTo(order.getLimitPrice()) > -1) {
                    if (candle.getOpen().compareTo(order.getAuxPrice()) > -1) {
                        return order.getAuxPrice();
                    } else {
                        if (CoreUtils.isBetween(order.getAuxPrice(), order.getLimitPrice(), candle.getOpen())) {
                            return candle.getOpen();
                        } else {
                            if (candle.getOpen().compareTo(order.getLimitPrice()) < 1) {
                                return order.getLimitPrice();
                            }
                        }
                    }
                }
            } else if (OrderType.LMT.equals(order.getOrderType())) {
                if (candle.getHigh().compareTo(order.getLimitPrice()) > -1) {
                    if (candle.getOpen().compareTo(order.getLimitPrice()) > -1) {
                        return candle.getOpen();
                    }
                    return order.getLimitPrice();
                }
            }

        } else {
            if (OrderType.STP.equals(order.getOrderType()) || OrderType.TRAIL.equals(order.getOrderType())) {
                if (candle.getHigh().compareTo(order.getAuxPrice()) > -1) {
                    if (candle.getOpen().compareTo(order.getAuxPrice()) > -1) {
                        return candle.getOpen();
                    }
                    return order.getAuxPrice();
                }
            } else if (OrderType.STPLMT.equals(order.getOrderType())
                    || OrderType.TRAILLIMIT.equals(order.getOrderType())) {
                if (candle.getHigh().compareTo(order.getAuxPrice()) > -1
                        && candle.getLow().compareTo(order.getLimitPrice()) < 1) {
                    if (candle.getOpen().compareTo(order.getAuxPrice()) < 1) {
                        return order.getAuxPrice();
                    } else {
                        if (CoreUtils.isBetween(order.getAuxPrice(), order.getLimitPrice(), candle.getOpen())) {
                            return candle.getOpen();
                        } else {
                            if (candle.getOpen().compareTo(order.getLimitPrice()) > -1) {
                                return order.getLimitPrice();
                            }
                        }
                    }
                }

            } else if (OrderType.LMT.equals(order.getOrderType())) {
                if (candle.getLow().compareTo(order.getLimitPrice()) < 1) {
                    if (candle.getOpen().compareTo(order.getLimitPrice()) < 1) {
                        return candle.getOpen();
                    }
                    return order.getLimitPrice();
                }
            }
        }
        return null;
    }

    /**
     * Method createOrderExecution.
     *
     * @param contract    Contract
     * @param order       TradeOrder
     * @param filledPrice BigDecimal
     * @param date        Date
     * @throws IOException
     */
    private void createOrderExecution(Contract contract, TradeOrder order, BigDecimal filledPrice, ZonedDateTime date)
            throws IOException {

        double commission = order.getQuantity() * 0.005d;
        if (commission < 1) {
            commission = 1;
        }

        TradeOrderfill execution = new TradeOrderfill();
        execution.setTradeOrder(order);
        execution.setAveragePrice(filledPrice);
        execution.setCommission(new BigDecimal(commission));
        execution.setCumulativeQuantity(order.getQuantity());
        execution.setExchange("BATS");
        execution.setPrice(filledPrice);
        execution.setTime(date);
        if (Action.BUY.equals(order.getAction())) {
            execution.setSide(Side.BOT);
        } else {
            execution.setSide(Side.SLD);
        }
        execution.setQuantity(order.getQuantity());
        execution.setExecId(String.valueOf(execId++));
        this.brokerModel.execDetails(execution.getTradeOrder().getOrderKey(), contract, execution);
        OrderState orderState = new OrderState();
        orderState.m_status = OrderStatus.FILLED;
        orderState.m_commission = commission;
        this.brokerModel.openOrder(order.getOrderKey(), contract, order, orderState);
    }

    /**
     * Method cancelOrder.
     *
     * @param contract Contract
     * @param order    TradeOrder
     * @throws IOException
     */
    private void cancelOrder(Contract contract, TradeOrder order) throws IOException {
        OrderState orderState = new OrderState();
        orderState.m_status = OrderStatus.CANCELLED;
        this.brokerModel.openOrder(order.getOrderKey(), contract, order, orderState);
        order.setStatus(OrderStatus.CANCELLED);
    }

    /**
     * Method populateIndicatorCandleSeries. For any child indicators that are
     * candle based create a Tradestrategy that will get the data. If this
     * tradestrategy already exist share this with any other tradestrategy that
     * requires this.
     *
     * @param tradestrategy Tradestrategy
     * @param startDate     ZonedDateTime
     * @param endDate       ZonedDateTime
     * @throws PersistentModelException
     */
    private void populateIndicatorCandleSeries(Tradestrategy tradestrategy, ZonedDateTime startDate,
                                               ZonedDateTime endDate) throws PersistentModelException {

        CandleDataset candleDataset = (CandleDataset) tradestrategy.getStrategyData()
                .getIndicatorByType(IndicatorSeries.CandleSeries);
        if (null != candleDataset) {
            for (int seriesIndex = 0; seriesIndex < candleDataset.getSeriesCount(); seriesIndex++) {

                CandleSeries series = candleDataset.getSeries(seriesIndex);

                Contract contract = this.tradePersistentModel.findContractByUniqueKey(series.getSecType(),
                        series.getSymbol(), series.getExchange(), series.getCurrency(), null);
                if (null == contract)
                    continue;

                Tradestrategy childTradestrategy = new Tradestrategy(contract, tradestrategy.getTradingday(),
                        new Strategy(), tradestrategy.getPortfolio(), new BigDecimal(0), null, null, false,
                        tradestrategy.getChartDays(), tradestrategy.getBarSize());
                childTradestrategy.setDirty(false);

                List<Candle> indicatorCandles = this.tradePersistentModel.findCandlesByContractDateRangeBarSize(
                        childTradestrategy.getContract().getId(), startDate, endDate,
                        childTradestrategy.getBarSize());
                if (indicatorCandles.isEmpty()) {
                    _log.warn("No data available for " + childTradestrategy.getContract().getSymbol()
                            + " and Tradingday: " + startDate + " to " + endDate + " and barSize: "
                            + childTradestrategy.getBarSize());
                } else {

                    StrategyData strategyData = StrategyData.create(childTradestrategy);
                    CandleDataset.populateSeries(strategyData, indicatorCandles);
                    indicatorCandles.clear();

                    CandleSeries childSeries = strategyData.getBaseCandleSeries();
                    childSeries.setDisplaySeries(series.getDisplaySeries());
                    childSeries.setSeriesRGBColor(series.getSeriesRGBColor());
                    childSeries.setSymbol(series.getSymbol());
                    childSeries.setSecType(series.getSecType());
                    childSeries.setCurrency(series.getCurrency());
                    childSeries.setExchange(series.getExchange());
                    candleDataset.setSeries(seriesIndex, childSeries);
                }
            }
        }
    }

    /**
     * Method getCandles. Try to get the candles based the current barSize or
     * less. barSizes must be integer divisible.
     *
     * @param tradestrategy Tradestrategy
     * @param startDate     ZonedDateTime
     * @param endDate       ZonedDateTime
     * @param barSize       int
     * @return List<Candle>
     * @throws PersistentModelException
     */

    private List<Candle> getCandles(Tradestrategy tradestrategy, ZonedDateTime startDate, ZonedDateTime endDate,
                                    int barSize) throws PersistentModelException {
        List<Candle> candles = new ArrayList<Candle>(0);
        int[] barSizes = {3600, 1800, 900, 300, 120, 60, 30};
        for (int size : barSizes) {
            if (size <= barSize) {
                /*
                 * Only go for barSize that are whole integer divisible.
                 */
                if ((Math.floor(
                        tradestrategy.getBarSize() / (double) size) == (tradestrategy.getBarSize() / (double) size))) {
                    candles = tradePersistentModel.findCandlesByContractDateRangeBarSize(
                            tradestrategy.getContract().getId(), startDate, endDate, size);
                    if (!candles.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return candles;
    }
}
