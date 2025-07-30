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
package org.trade.core.broker;

import com.ib.client.ContractDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.broker.client.Broker;
import org.trade.core.broker.client.ClientSocket;
import org.trade.core.broker.client.IClientWrapper;
import org.trade.core.broker.client.OrderState;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */

public class BackTestBrokerModel extends AbstractBrokerModel implements IClientWrapper {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3191422640254347940L;

    private final static Logger _log = LoggerFactory.getLogger(BackTestBrokerModel.class);

    private final TradeService tradeService;

    // Use getId as key
    private static final ConcurrentHashMap<Integer, Tradestrategy> historyDataRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> realTimeBarsRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> contractRequests = new ConcurrentHashMap<>();

    private final ClientSocket client;

    private static final int SCALE = 5;
    private static final int minOrderId = 100000;

    private AtomicInteger orderKey;

    private static final Integer backfillDateFormat = 2;
    private static final String backfillWhatToShow;
    private static final Integer backfillOffsetDays = 0;
    private static final Integer backfillUseRTH;

    static {

        try {
            backfillWhatToShow = ConfigProperties.getPropAsString("trade.backfill.whatToShow");
            backfillUseRTH = ConfigProperties.getPropAsInt("trade.backfill.useRTH");

        } catch (Exception ex) {
            throw new IllegalArgumentException("Error initializing IBrokerModel Msg: " + ex.getMessage());
        }
    }

    public BackTestBrokerModel(TradeService tradeService) {

        try {

            this.tradeService = tradeService;
            client = new ClientSocket(this, tradeService);
            int maxKey = this.tradeService.findTradeOrderByMaxKey();

            if (maxKey < 100000) {

                maxKey = 100000;
            }
            orderKey = new AtomicInteger(maxKey + 1);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error initializing IBrokerModel Msg: " + ex.getMessage());
        }
    }

    /**
     * Method getHistoricalData.
     *
     * @return ConcurrentHashMap<Integer, Tradestrategy>
     * @see IBrokerModel#getHistoryDataRequests()
     */
    public ConcurrentHashMap<Integer, Tradestrategy> getHistoryDataRequests() {

        return historyDataRequests;
    }

    /**
     * Method isConnected.
     *
     * @return boolean
     * @see IBrokerModel#isConnected()
     */
    public boolean isConnected() {
        return false;
    }

    /**
     * Method onConnect.
     *
     * @param host     String
     * @param port     Integer
     * @param clientId Integer
     * @see IBrokerModel#onConnect(String, Integer, Integer)
     */
    public void onConnect(String host, Integer port, Integer clientId) {

    }

    /**
     * Method connectionClosed.
     */
    public void connectionClosed() {

        onCancelAllRealtimeData();
        this.fireConnectionClosed(true);
        error(0, 1101, "Error Connection was closed! ");
    }

    /**
     * Method disconnect.
     */
    public void onDisconnect() {
        if (isConnected()) {
            onCancelAllRealtimeData();
        }
        this.fireConnectionClosed(false);
    }

    /**
     * Method getBackTestBroker.
     *
     * @param reqId Integer
     * @see IBrokerModel#getBackTestBroker(Integer)
     */
    public Broker getBackTestBroker(Integer reqId) {

        return client.getBackTestBroker(reqId);
    }

    /**
     * Method getNextRequestId.
     *
     * @return Integer
     * @see IBrokerModel#getNextRequestId()
     */
    public Integer getNextRequestId() {

        return orderKey.incrementAndGet();
    }

    /**
     * Method nextValidId.
     *
     * @param orderId int
     * @see com.ib.client.EWrapper#nextValidId(int)
     */
    public void nextValidId(int orderId) {

        try {

            int maxKey = tradeService.findTradeOrderByMaxKey();

            if (maxKey < minOrderId) {
                maxKey = minOrderId;
            }

            if (maxKey < orderId) {

                orderKey = new AtomicInteger(orderId);
            } else {

                orderKey = new AtomicInteger(maxKey + 1);
            }
            this.fireConnectionOpened();

        } catch (Exception ex) {
            error(orderId, 3210, ex.getMessage());
        }
    }

    /**
     * Method onSubscribeAccountUpdates.
     *
     * @param subscribe     boolean
     * @param accountNumber Account
     */
    public void onSubscribeAccountUpdates(boolean subscribe, String accountNumber) {
    }

    /**
     * Method onCancelAccountUpdates.
     *
     * @param accountNumber String
     * @see IBrokerModel#onCancelAccountUpdates(String)
     */
    public void onCancelAccountUpdates(String accountNumber) {
    }

    /**
     * Method onReqFinancialAccount.
     */
    public void onReqFinancialAccount() {
    }

    /**
     * Method onReqReplaceFinancialAccount.
     *
     * @param xml        String
     * @param faDataType int
     */
    public void onReqReplaceFinancialAccount(int faDataType, String xml) {
    }

    /**
     * Method onReqManagedAccount.
     *
     * @see IBrokerModel#onReqManagedAccount()
     */
    public void onReqManagedAccount() {
    }

    /**
     * Method onReqAllOpenOrders.
     *
     * @see IBrokerModel#onReqAllOpenOrders()
     */
    public void onReqAllOpenOrders() {
        // request list of all open orders
        // m_client.reqAllOpenOrders();
    }

    /**
     * Method onReqOpenOrders.
     *
     * @see IBrokerModel#onReqOpenOrders()
     */
    public void onReqOpenOrders() {
        // request list of all open orders
        // m_client.reqOpenOrders();
    }

    /**
     * Method onReqExecutions.
     *
     * @param tradestrategy Tradestrategy
     * @param addOrders     boolean
     */
    public void onReqExecutions(Tradestrategy tradestrategy, boolean addOrders) {

    }

    /**
     * Method onReqAllExecutions.
     *
     * @param mktOpenDate ZonedDateTime
     */
    public void onReqAllExecutions(ZonedDateTime mktOpenDate) {
    }

    /**
     * Method onReqRealTimeBars.
     *
     * @param contract Contract
     * @param mktData  boolean
     */
    public void onReqRealTimeBars(Contract contract, boolean mktData) {

    }

    /**
     * Method onReqMarketData.
     *
     * @param contract        Contract
     * @param genericTicklist String
     * @param snapshot        boolean
     */
    public void onReqMarketData(Contract contract, String genericTicklist, boolean snapshot) {

    }

    /**
     * Method onBrokerData.
     *
     * @param tradestrategy Tradestrategy
     * @param endDate       endDate
     */
    public void onBrokerData(final Tradestrategy tradestrategy, final ZonedDateTime endDate)
            throws BrokerModelException {

        try {

            if (this.isHistoricalDataRequestRunning(tradestrategy)) {

                throw new BrokerModelException(tradestrategy.getRequestId(), 3010, "Data request is already in progress for: "
                        + tradestrategy.getContract().getSymbol() + " Please wait or cancel.");
            }

            historyDataRequests.put(tradestrategy.getRequestId(), tradestrategy);

            if (this.isBrokerDataOnly()) {

                ZonedDateTime endDay = TradingCalendar
                        .getDateAtTime(TradingCalendar.addTradingDays(endDate, backfillOffsetDays), endDate);
                String endDateTime = TradingCalendar.getFormattedDate(endDay, "yyyyMMdd HH:mm:ss");

                contractRequests.put(tradestrategy.getContract().getRequestId(), tradestrategy.getContract());

                _log.debug("onBrokerData ReqId: {} Symbol: {} end Time: {} Period length: {} Bar size: {} WhatToShow: {} Regular Trading Hrs: {} Date format: " + backfillDateFormat, tradestrategy.getId(), tradestrategy.getContract().getSymbol(), endDateTime, tradestrategy.getChartDays(), tradestrategy.getBarSize(), backfillWhatToShow, backfillUseRTH);

                client.reqHistoricalData(tradestrategy.getRequestId(), tradestrategy, endDateTime,
                        ChartDays.newInstance(tradestrategy.getChartDays()).getDisplayName(),
                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
                        backfillUseRTH, backfillDateFormat);
            } else {

                // Running a strategy and getting the historical data. realTimeBarsRequests is used to
                // Stop the same contract for different days running at the same time.
                //realTimeBarsRequests.put(tradestrategy.getContract().getRequestId(), tradestrategy.getContract());
                client.reqHistoricalData(tradestrategy.getRequestId(), tradestrategy, null,
                        ChartDays.newInstance(tradestrategy.getChartDays()).getDisplayName(),
                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
                        backfillUseRTH, backfillDateFormat);
            }
        } catch (Throwable ex) {

            throw new BrokerModelException(tradestrategy.getRequestId(), 3020, "Error broker data Symbol: "
                    + tradestrategy.getContract().getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    /**
     * Method isHistoricalDataRunning.
     *
     * @param contract Contract
     * @return boolean
     * @see IBrokerModel#isHistoricalDataRequestRunning(Contract)
     */
    public boolean isHistoricalDataRequestRunning(Contract contract) {

        for (Tradestrategy item : historyDataRequests.values()) {

            if (contract.equals(item.getContract())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method isHistoricalDataRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    public boolean isHistoricalDataRequestRunning(Tradestrategy tradestrategy) {

        return historyDataRequests.containsKey(tradestrategy.getRequestId());
    }

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param contract Contract
     * @return boolean
     * @see IBrokerModel#isRealtimeBarsRequestRunning(Contract)
     */
    public boolean isRealtimeBarsRequestRunning(Contract contract) {

        return realTimeBarsRequests.containsKey(contract.getRequestId());
    }

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    public boolean isRealtimeBarsRequestRunning(Tradestrategy tradestrategy) {

        if (realTimeBarsRequests.containsKey(tradestrategy.getContract().getRequestId())) {

            Contract contract = realTimeBarsRequests.get(tradestrategy.getContract().getRequestId());

            for (Tradestrategy item : contract.getTradestrategies()) {

                if (item.equals(tradestrategy)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method isMarketDataRunning.
     *
     * @param contract Contract
     * @return boolean
     * @see IBrokerModel#isRealtimeBarsRequestRunning(Contract)
     */
    public boolean isMarketDataRequestRunning(Contract contract) {
        return false;
    }

    /**
     * Method isMarketDataRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    public boolean isMarketDataRequestRunning(Tradestrategy tradestrategy) {
        return false;
    }

    /**
     * Method isAccountUpdatesRunning.
     *
     * @param accountNumber String
     * @return boolean
     * @see IBrokerModel#isAccountUpdatesRunning(String)
     */
    public boolean isAccountUpdatesRunning(String accountNumber) {
        return false;
    }

    /**
     * Method onCancelAllRealtimeData.
     *
     * @see IBrokerModel#onCancelAllRealtimeData()
     */
    public void onCancelAllRealtimeData() {

        historyDataRequests.clear();
        realTimeBarsRequests.clear();
    }

    /**
     * Method onContractDetails.
     *
     * @param contract Contract
     * @see IBrokerModel#onContractDetails(Contract)
     */
    public void onContractDetails(final Contract contract) {
        /*
         * This will use the Yahoo API to get the data.
         */
        contractRequests.put(contract.getRequestId(), contract);
    }

    /**
     * Method onCancelContractDetails.
     *
     * @param contract Contract
     * @see IBrokerModel#onCancelContractDetails(Contract)
     */
    public void onCancelContractDetails(Contract contract) {
    }

    /**
     * Method onCancelBrokerData.
     *
     * @param tradestrategy Tradestrategy
     */
    public void onCancelBrokerData(Tradestrategy tradestrategy) {

        if (historyDataRequests.containsKey(tradestrategy.getRequestId())) {

            tradestrategy.getContract().removeTradestrategy(tradestrategy);

            synchronized (historyDataRequests) {

                historyDataRequests.remove(tradestrategy.getRequestId());
                historyDataRequests.notify();
            }
        }
        client.removeBackTestBroker(tradestrategy.getRequestId());
    }

    /**
     * Method onCancelBrokerData.
     *
     * @param contract Contract
     * @see IBrokerModel#onCancelBrokerData(Contract)
     */
    public void onCancelBrokerData(Contract contract) {

        for (Tradestrategy tradestrategy : historyDataRequests.values()) {

            if (contract.equals(tradestrategy.getContract())) {

                contract.removeTradestrategy(tradestrategy);
                client.removeBackTestBroker(tradestrategy.getRequestId());

                synchronized (historyDataRequests) {

                    historyDataRequests.remove(tradestrategy.getRequestId());
                    historyDataRequests.notify();
                }
            }
        }
    }

    /**
     * Method onCancelRealtimeBars.
     *
     * @param contract Contract
     * @see IBrokerModel#onCancelRealtimeBars(Contract)
     */
    public void onCancelRealtimeBars(Contract contract) {

        if (realTimeBarsRequests.containsKey(contract.getRequestId())) {

            synchronized (realTimeBarsRequests) {

                realTimeBarsRequests.remove(contract.getRequestId());
                realTimeBarsRequests.notify();
            }
        }
    }

    /**
     * Method onCancelRealtimeBars.
     *
     * @param tradestrategy Tradestrategy
     */
    public void onCancelRealtimeBars(Tradestrategy tradestrategy) {

        if (realTimeBarsRequests.containsKey(tradestrategy.getContract().getRequestId())) {

            Contract contract = realTimeBarsRequests.get(tradestrategy.getContract().getRequestId());

            for (Tradestrategy item : contract.getTradestrategies()) {

                if (item.equals(tradestrategy)) {

                    contract.removeTradestrategy(tradestrategy);
                    break;
                }
            }

            if (contract.getTradestrategies().isEmpty()) {

                onCancelRealtimeBars(contract);
            }
        }
    }

    /**
     * Method onCancelMarketData.
     *
     * @param contract Contract
     * @see IBrokerModel#onCancelRealtimeBars(Contract)
     */
    public void onCancelMarketData(Contract contract) {
    }

    /**
     * Method onCancelMarketData.
     *
     * @param tradestrategy Tradestrategy
     */
    public void onCancelMarketData(Tradestrategy tradestrategy) {
    }

    /**
     * Method onPlaceOrder
     *
     * @param contract   Contract
     * @param tradeOrder TradeOrder
     * @return TradeOrder
     * @see IBrokerModel#onPlaceOrder(Contract, TradeOrder)
     */
    public TradeOrder onPlaceOrder(final Contract contract, final TradeOrder tradeOrder) throws BrokerModelException {

        try {

            synchronized (tradeOrder) {

                if (null == tradeOrder.getOrderKey()) {

                    tradeOrder.setOrderKey(getNextRequestId());
                }

                if (null == tradeOrder.getClientId()) {

                    tradeOrder.setClientId(999);
                }

                TradeOrder instance = tradeService.saveTradeOrder(tradeOrder);

                // Debug logging
                _log.debug("Order Placed Key: {}", instance.getOrderKey());
                TWSBrokerModel.logContract(TWSBrokerModel.getIBContract(contract));
                TWSBrokerModel.logTradeOrder(TWSBrokerModel.getIBOrder(instance));
                return instance;
            }
        } catch (Exception ex) {

            throw new BrokerModelException(tradeOrder.getOrderKey(), 3030,
                    "Could not save or place TradeOrder: " + tradeOrder.getOrderKey() + " Msg: " + ex.getMessage());
        }
    }

    /**
     * Method onCancelOrder.
     *
     * @param tradeOrder TradeOrder
     * @see IBrokerModel#onCancelOrder(TradeOrder)
     */
    public void onCancelOrder(TradeOrder tradeOrder) throws BrokerModelException {

        try {

            OrderState orderState = new OrderState();
            orderState.status = OrderStatus.CANCELLED;
            openOrder(tradeOrder.getOrderKey(), null, tradeOrder, orderState);
        } catch (Exception ex) {
            throw new BrokerModelException(tradeOrder.getOrderKey(), 3040, "Could not CancelOrder: " + ex.getMessage());
        }
    }

    /**
     * Method execDetails.
     * <p>
     * When orders are filled the  exccDetails is fired followed by
     * openOrder() and orderStatus() the order methods fire twice. openOrder
     * gives us the commission amount on the second fire and order status from
     * both. Apart from that I have no idea why they fire twice. I assume its to
     * do with the margin and account updates.
     *
     * @param reqId      int
     * @param contractIB com.ib.client.Contract
     * @param execution  Execution
     */
    public void execDetails(int reqId, Contract contractIB, TradeOrderfill execution) {

        try {

            BackTestBrokerModel.logExecution(execution);
            TradeOrder instance = tradeService
                    .findTradeOrderByKey(execution.getTradeOrder().getOrderKey());

            if (null == instance) {

                error(execution.getTradeOrder().getOrderKey(), 3170,
                        "Warning Order not found for Order Key: " + execution.getTradeOrder().getOrderKey()
                                + " make sure Client ID: " + 0 + " is not the master in TWS. On execDetails update.");
                return;
            }

            /*
             * We already have this order fill.
             */
            if (instance.existTradeOrderfill(execution.getExecId())) {

                return;
            }

            TradeOrderfill tradeOrderfill = new TradeOrderfill();
            BackTestBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);
            tradeOrderfill.setTradeOrder(instance);
            instance.addTradeOrderfill(tradeOrderfill);
            instance.setAverageFilledPrice(tradeOrderfill.getAveragePrice());
            instance.setFilledQuantity(tradeOrderfill.getCumulativeQuantity());
            instance.setFilledDate(tradeOrderfill.getTime());
            boolean isFilled = instance.getIsFilled();
            TradeOrder updatedOrder = tradeService.saveTradeOrderfill(instance);

            // Let the controller know an order was filled
            if (updatedOrder.getIsFilled() && !isFilled) {

                this.fireTradeOrderFilled(updatedOrder);
            }
        } catch (Exception ex) {
            error(reqId, 3160, "Errors symbol: " + contractIB.getSymbol() + " saving execution: " + ex.getMessage());
        }
    }

    /**
     * Method execDetailsEnd.
     *
     * @param reqId int
     */
    public void execDetailsEnd(int reqId) {

    }

    /**
     * Method openOrder.
     * <p>
     * This method is called to feed in open orders.
     *
     * @param orderId    int
     * @param contract   com.ib.client.Contract
     * @param tradeOrder com.ib.client.Order
     * @param orderState OrderState
     */
    public void openOrder(int orderId, final Contract contract, final TradeOrder tradeOrder,
                          final OrderState orderState) {

        try {

            TradeOrder instance = tradeService.findTradeOrderByKey(tradeOrder.getOrderKey());

            if (null == instance) {

                error(orderId, 3170, "Warning Order not found for Order Key: " + orderId + " make sure Client ID: " + 0
                        + " is not the master in TWS. On openOrder update.");
                return;
            }

            /*
             * Check to see if anything has changed as this method gets fired
             * twice on order fills.
             */
            if (BackTestBrokerModel.updateTradeOrder(tradeOrder, orderState, instance)) {

                if (OrderStatus.FILLED.equals(instance.getStatus())) {

                    _log.debug("Order Key: {} filled.", instance.getOrderKey());
                    BackTestBrokerModel.logOrderState(orderState);
                    BackTestBrokerModel.logTradeOrder(tradeOrder);

                    TradeOrder updatedOrder = tradeService.saveTradeOrder(instance);

                    if (updatedOrder.hasTradePosition() && !updatedOrder.getTradePosition().isOpen()) {
                        // Let the controller know a position was closed
                        this.firePositionClosed(updatedOrder.getTradePosition());
                    }
                } else {
                    _log.debug("Order key: {} state changed. Status:{}", instance.getOrderKey(), orderState.status);
                    BackTestBrokerModel.logOrderState(orderState);
                    BackTestBrokerModel.logTradeOrder(tradeOrder);
                    TradeOrder updatedOrder = tradeService.saveTradeOrder(instance);

                    if (OrderStatus.CANCELLED.equals(updatedOrder.getStatus())) {
                        // Let the controller know a position was closed
                        this.fireTradeOrderCancelled(updatedOrder);
                    }
                }
            }
        } catch (Exception ex) {
            error(orderId, 3180, "Errors updating open order: " + ex.getMessage());
        }
    }

    public void openOrderEnd() {
    }

    /**
     * Method orderStatus.
     * <p>
     * This method is called whenever the status of an order changes. It is also
     * fired after reconnecting to TWS if the client has any open orders.
     *
     * @param orderId       int
     * @param status        String
     * @param filled        int
     * @param remaining     int
     * @param avgFillPrice  double
     * @param permId        int
     * @param parentId      int
     * @param lastFillPrice double
     * @param clientId      int
     * @param whyHeld       String
     */
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId,
                            int parentId, double lastFillPrice, int clientId, String whyHeld) {

        try {

            TradeOrder instance = tradeService.findTradeOrderByKey(orderId);

            if (null == instance) {
                error(orderId, 3170, "Warning Order not found for Order Key: " + orderId + " make sure Client ID: " + 0
                        + " is not the master in TWS. On orderStatus update.");
                return;
            }
            /*
             * Check to see if anything has changed as this method gets fired
             * twice on order fills.
             */
            boolean changed = false;

            if (CoreUtils.nullSafeComparator(instance.getStatus(), status.toUpperCase()) != 0) {

                instance.setStatus(status.toUpperCase());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(instance.getWhyHeld(), whyHeld) != 0) {

                instance.setWhyHeld(whyHeld);
                changed = true;
            }
            /*
             * If filled qty is greater than current filled qty set the new
             * value.
             */
            if (CoreUtils.nullSafeComparator(filled, instance.getFilledQuantity()) == 1) {

                if (filled > 0) {

                    instance.setAverageFilledPrice(new BigDecimal(avgFillPrice));
                    instance.setFilledQuantity(filled);
                    changed = true;
                }
            }

            if (changed) {

                instance.setOrderUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                instance.setStatus(status.toUpperCase());
                instance.setWhyHeld(whyHeld);
                _log.debug("Order Status changed. Status: {}", status);
                TWSBrokerModel.logOrderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId,
                        lastFillPrice, clientId, whyHeld);

                boolean isFilled = instance.getIsFilled();
                TradeOrder updatedOrder = tradeService.saveTradeOrder(instance);

                if (OrderStatus.CANCELLED.equals(updatedOrder.getStatus())) {

                    // Let the controller know a position was closed
                    this.fireTradeOrderCancelled(updatedOrder);
                } else {

                    this.fireTradeOrderStatusChanged(updatedOrder);
                    // Let the controller know an order was filled
                    if (updatedOrder.getIsFilled() && !isFilled)
                        this.fireTradeOrderFilled(updatedOrder);
                }
            }

        } catch (Exception ex) {
            error(orderId, 3100, "Errors updating open order status: " + ex.getMessage());
        }
    }

    /**
     * Method error.
     *
     * @param ex Exception
     */
    public void error(Exception ex) {
        _log.error("IBrokerModel error msg: {}", ex.getMessage());
        // this.fireBrokerError(new BrokerManagerModelException(e));
    }

    /**
     * Method error.
     *
     * @param msg String
     */
    public void error(String msg) {
        _log.error("IBrokerModel error str: {}", msg);
        // this.fireBrokerError(new BrokerManagerModelException(msg));
    }

    /**
     * 0 - 999 are IB TWS error codes for Orders or data 1000 - 1999 are IB TWS
     * System error 2000 - 2999 are IB TWS Warning 4000 - 4999 are application
     * warnings 5000 - 5999 are application information
     *
     * @param id   int
     * @param code int
     * @param msg  String
     * @see IBrokerModel#error(int, int, String)
     */
    public void error(int id, int code, String msg) {

        String symbol = "N/A";
        BrokerModelException brokerModelException;

        if (contractRequests.containsKey(id)) {

            symbol = contractRequests.get(id).getSymbol();
            synchronized (contractRequests) {
                contractRequests.remove(id);
            }
        }

        if (historyDataRequests.containsKey(id)) {

            symbol = historyDataRequests.get(id).getContract().getSymbol();

            synchronized (historyDataRequests) {

                historyDataRequests.remove(id);
                historyDataRequests.notify();
            }
        }

        if (realTimeBarsRequests.containsKey(id)) {

            symbol = realTimeBarsRequests.get(id).getSymbol();
        }

        /*
         * Error code 162 (Historical data request pacing violation)and 366 (No
         * historical data query found for ticker id) are error code for no
         * market or historical data found.
         *
         * Error code 202, Order cancelled 201, Order rejected
         *
         * Error code 321 Error validating request:-'jd' : cause - FA data
         * operations ignored for non FA customers.
         *
         * Error code 502, Couldn't connect to TWS. Confirm that API is enabled
         * in TWS via the Configure>API menu command.
         */
        String errorMsg = "Req/Order Id: " + id + " Code: " + code + " symbol: " + symbol + " Msg: " + msg;

        if (((code > 1999) && (code < 3000)) || ((code >= 200) && (code < 299)) || (code == 366) || (code == 162)
                || (code == 321) || (code == 3170)) {

            if (((code > 1999) && (code < 3000))) {

                _log.info(errorMsg);
                brokerModelException = new BrokerModelException(3, code, errorMsg);
            } else if (code == 202 || code == 201 || code == 3170) {

                _log.warn(errorMsg);
                brokerModelException = new BrokerModelException(2, code, errorMsg);
            } else if (code == 321) {

                _log.info(errorMsg);
                return;
            } else {

                _log.warn(errorMsg);
                brokerModelException = new BrokerModelException(2, code, errorMsg);
            }

        } else {

            if (realTimeBarsRequests.containsKey(id)) {

                synchronized (realTimeBarsRequests) {
                    realTimeBarsRequests.remove(id);
                    realTimeBarsRequests.notify();
                }
            }

            _log.error(errorMsg);
            brokerModelException = new BrokerModelException(1, code, errorMsg);
        }
        this.fireBrokerError(brokerModelException);
    }

    /**
     * Method contractDetails.
     *
     * @param reqId           int
     * @param contractDetails ContractDetails
     * @see com.ib.client.EWrapper#contractDetails(int, ContractDetails)
     */
    public void contractDetails(int reqId, Contract contractDetails) {

        try {

            if (contractRequests.containsKey(reqId)) {

                Contract contract = contractRequests.get(reqId);

                // Refresh the contract as contractDetails and contract are the same in PolygonBroker
                // If the same contract is being back tested over multiple days it could have been updated
                // by a previous request.
                contract = tradeService.findContractById(contract.getId());
                BackTestBrokerModel.logContract(contractDetails);

                if (BackTestBrokerModel.populateContract(contract, contractDetails)) {

                    contract = tradeService.saveAspect(contract);
                    contractRequests.remove(reqId);
                }
            }
        } catch (Exception ex) {

            error(reqId, 3230, ex.getMessage());
        }
    }

    /**
     * Method contractDetailsEnd.
     *
     * @param reqId int
     */
    public void contractDetailsEnd(int reqId) {

        if (contractRequests.containsKey(reqId)) {

            synchronized (contractRequests) {

                contractRequests.remove(reqId);
            }
        }
    }

    /**
     * Method historicalData.
     *
     * @param reqId      int
     * @param dateString String
     * @param open       double
     * @param high       double
     * @param low        double
     * @param close      double
     * @param volume     long
     * @param tradeCount int
     * @param barSize    int
     * @param vwap       double
     * @param hasGaps    boolean
     */
    public void historicalData(int reqId, String dateString, double open, double high, double low, double close,
                               long volume, int tradeCount, int barSize, double vwap, boolean hasGaps) {
        try {

            /*
             * Check to see if the trading day is today and this strategy is
             * selected to trade and that the market is open
             */
            if (historyDataRequests.containsKey(reqId)) {

                /*
                 * There is a bug in the TWS interface format for dates
                 * should always be milli sec but when 1 day is selected as
                 * the period the dates come through as yyyyMMdd.
                 */
                Tradestrategy tradestrategy = historyDataRequests.get(reqId);
                ZonedDateTime date;

                if (dateString.length() == 8) {

                    date = TradingCalendar.getZonedDateTimeFromDateString(dateString, "yyyyMMdd",
                            TradingCalendar.MKT_TIMEZONE);
                } else {

                    date = TradingCalendar.getZonedDateTimeFromMilli((Long.parseLong(dateString)));
                }

                /*
                 * For daily bars set the time to the open time.
                 */
                if (tradestrategy.getBarSize() > 3600) {

                    date = TradingCalendar.getDateAtTime(date, tradestrategy.getTradingday().getOpen());
                }

                if (tradestrategy.getTradingday().getClose().isAfter(date)) {

                    if (backfillUseRTH == 1
                            && !TradingCalendar.isMarketHours(tradestrategy.getTradingday().getOpen(),
                            tradestrategy.getTradingday().getClose(), date)) {

                        return;
                    }

                    BigDecimal price = (new BigDecimal(close)).setScale(SCALE, RoundingMode.HALF_EVEN);
                    tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastAskPrice(price);
                    tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastBidPrice(price);
                    tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastPrice(price);
                    tradestrategy.getStrategyData().buildCandle(date, open, high, low, close, volume, vwap,
                            tradeCount, tradestrategy.getStrategyData().getCandleDataset().getSeries(0).getBarSize() / barSize, null);
                }
            } else {
                _log.error("HistoricalData request not found for Req Id: {} Date: {}", reqId, dateString);
            }
        } catch (Exception ex) {
            error(reqId, 3270, "Error: historicalData msg: " + ex.getMessage());
        }
    }

    /**
     * Method historicalDataComplete.
     *
     * @param reqId int
     */
    public void historicalDataComplete(int reqId) {

        try {

            /*
             * Check to see if the trading day is today and this strategy is
             * selected to trade and that the market is open
             */
            if (historyDataRequests.containsKey(reqId)) {

                Tradestrategy tradestrategy = historyDataRequests.get(reqId);
                CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();
                tradeService.saveCandleSeries(candleSeries);
                _log.debug("HistoricalData complete Req Id: {}, Symbol: {}, Tradingday: {}, candles to saved: {}, Contract Tradestrategies size:: {}", reqId, tradestrategy.getContract().getSymbol(), tradestrategy.getTradingday().getOpen(), candleSeries.getItemCount(), tradestrategy.getContract().getTradestrategies().size());

                /*
                 * Check to see if the trading day is today and this
                 * strategy is selected to trade and that the market is open
                 */
                synchronized (tradestrategy.getContract().getTradestrategies()) {

                    this.fireHistoricalDataComplete(tradestrategy);

                    if (tradestrategy.getTradingday().getClose()
                            .isAfter(TradingCalendar.getDateTimeNowMarketTimeZone())) {

                        if (!this.isRealtimeBarsRequestRunning(tradestrategy.getContract())) {

                            tradestrategy.getContract().addTradestrategy(tradestrategy);
                            this.onReqRealTimeBars(tradestrategy.getContract(),
                                    tradestrategy.getStrategy().getMarketData());
                        } else {

                            Contract contract = realTimeBarsRequests.get(tradestrategy.getContract().getRequestId());
                            contract.addTradestrategy(tradestrategy);
                        }
                    }
                }
            } else {

                _log.error("HistoricalDataComplete request not found for Req Id: {}", reqId);
            }
        } catch (Exception ex) {

            error(reqId, 3260, "Error: HistoricalDataComplete msg: " + ex.getMessage());
        }
    }

    /**
     * Method realtimeBar.
     *
     * @param reqId      int
     * @param time       long
     * @param open       double
     * @param high       double
     * @param low        double
     * @param close      double
     * @param volume     long
     * @param vwap       double
     * @param tradeCount int
     */
    public synchronized void realtimeBar(int reqId, long time, double open, double high, double low, double close,
                                         long volume, double vwap, int tradeCount) {
    }

    /**
     * Method validateBrokerData.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */

    public boolean validateBrokerData(Tradestrategy tradestrategy) throws BrokerModelException {

        boolean valid = true;
        String errorMsg = "Symbol: " + tradestrategy.getContract().getSymbol()
                + " Bar Size/Chart Days combination was not valid for Polygon API, these values have been updated." + "\n"
                + "Please validate and save.\n Note Polygon only supports 1 min bars these will be rolled up to the desired bar size.";

        if (tradestrategy.getBarSize() < 60) {

            tradestrategy.setBarSize(60);
            valid = false;

        } else if ((tradestrategy.getChartDays() > 1 && tradestrategy.getChartDays() < 7)
                && tradestrategy.getBarSize() < 300) {

            tradestrategy.setBarSize(300);
            valid = false;
        } else if (tradestrategy.getChartDays() > 30 && (tradestrategy.getBarSize() <= 3600)) {

            tradestrategy.setBarSize(1);
            valid = false;
        }

        if ((tradestrategy.getBarSize() < 300) && tradestrategy.getChartDays() > 1) {

            tradestrategy.setChartDays(1);
            valid = false;
        } else if (tradestrategy.getBarSize() <= 3600 && tradestrategy.getChartDays() > 30) {

            tradestrategy.setChartDays(7);
            valid = false;
        }

        if (!valid) {

            tradestrategy.setDirty(true);
            throw new BrokerModelException(1, 3901, errorMsg);
        }

        return true;
    }

    /**
     * Method logOrderState.
     *
     * @param orderState OrderState
     */
    public static void logOrderState(OrderState orderState) {
        _log.debug("Status: {} Comms Amt: {} Comms Currency: {} Warning txt: {} Init Margin: {} Maint Margin: {} Min Comms: {} Max Comms: {}", orderState.status, orderState.commission, orderState.commissionCurrency, orderState.warningText, orderState.initMargin, orderState.maintMargin, orderState.minCommission, orderState.maxCommission);
    }

    /**
     * Method updateTradeOrder.
     *
     * @param clientOrder      com.ib.client.Order
     * @param clientOrderState OrderState
     * @param order            TradeOrder
     * @return boolean
     */
    public static boolean updateTradeOrder(TradeOrder clientOrder, OrderState clientOrderState, TradeOrder order) {

        if (CoreUtils.nullSafeComparator(order.getOrderKey(), clientOrder.getOrderKey()) == 0) {

            if (null != clientOrderState.status && CoreUtils.nullSafeComparator(order.getStatus(), clientOrderState.status.toUpperCase()) != 0) {

                order.setStatus(clientOrderState.status.toUpperCase());
                order.setDirty(true);
            }

            if (null != clientOrderState.warningText && CoreUtils.nullSafeComparator(order.getWarningMessage(), clientOrderState.warningText) != 0) {

                order.setWarningMessage(clientOrderState.warningText);
                order.setDirty(true);
            }
            Money comms = new Money(clientOrderState.commission);

            if (CoreUtils.nullSafeComparator(comms, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getCommission(), comms.getBigDecimalValue()) != 0) {

                order.setCommission(comms.getBigDecimalValue());
                order.setDirty(true);
            }

            if (null != clientOrder.getClientId() && CoreUtils.nullSafeComparator(order.getClientId(), clientOrder.getClientId()) != 0) {

                order.setClientId(clientOrder.getClientId());
                order.setDirty(true);
            }

            if (null != clientOrder.getAction() && CoreUtils.nullSafeComparator(order.getAction(), clientOrder.getAction()) != 0) {

                order.setAction(clientOrder.getAction());
                order.setDirty(true);
            }

            if (null != clientOrder.getQuantity() && CoreUtils.nullSafeComparator(order.getQuantity(), clientOrder.getQuantity()) != 0) {

                order.setQuantity(clientOrder.getQuantity());
                order.setDirty(true);
            }

            if (null != clientOrder.getOrderType() && CoreUtils.nullSafeComparator(order.getOrderType(), clientOrder.getOrderType()) != 0) {

                order.setOrderType(clientOrder.getOrderType());
                order.setDirty(true);
            }

            if (null != clientOrder.getLimitPrice() && CoreUtils.nullSafeComparator(new Money(clientOrder.getLimitPrice()), new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getLimitPrice(), clientOrder.getLimitPrice()) != 0) {

                order.setLimitPrice(clientOrder.getLimitPrice());
                order.setDirty(true);
            }

            if (null != clientOrder.getAuxPrice() && CoreUtils.nullSafeComparator(new Money(clientOrder.getAuxPrice()), new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getAuxPrice(), clientOrder.getAuxPrice()) != 0) {

                order.setAuxPrice(clientOrder.getAuxPrice());
                order.setDirty(true);
            }

            if (null != clientOrder.getTimeInForce() && CoreUtils.nullSafeComparator(order.getTimeInForce(), clientOrder.getTimeInForce()) != 0) {

                order.setTimeInForce(clientOrder.getTimeInForce());
                order.setDirty(true);
            }

            if (null != clientOrder.getOcaGroupName() && CoreUtils.nullSafeComparator(order.getOcaGroupName(), clientOrder.getOcaGroupName()) != 0) {

                order.setOcaGroupName(clientOrder.getOcaGroupName());
                order.setDirty(true);
            }

            if (null != clientOrder.getOcaType() && CoreUtils.nullSafeComparator(order.getOcaType(), clientOrder.getOcaType()) != 0) {

                order.setOcaType(clientOrder.getOcaType());
                order.setDirty(true);
            }

            if (null != clientOrder.getOrderReference() && CoreUtils.nullSafeComparator(order.getOrderReference(), clientOrder.getOrderReference()) != 0) {

                order.setOrderReference(clientOrder.getOrderReference());
                order.setDirty(true);
            }

            if (null != clientOrder.getPermId() && CoreUtils.nullSafeComparator(order.getPermId(), clientOrder.getPermId()) != 0) {

                order.setPermId(clientOrder.getPermId());
                order.setDirty(true);
            }

            if (null != clientOrder.getParentId() && CoreUtils.nullSafeComparator(order.getParentId(), clientOrder.getParentId()) != 0) {

                order.setParentId(clientOrder.getParentId());
                order.setDirty(true);
            }

            if (null != clientOrder.getTransmit() && CoreUtils.nullSafeComparator(order.getTransmit(), clientOrder.getTransmit()) != 0) {

                order.setTransmit(clientOrder.getTransmit());
                order.setDirty(true);
            }

            if (null != clientOrder.getDisplayQuantity() && CoreUtils.nullSafeComparator(order.getDisplayQuantity(), clientOrder.getDisplayQuantity()) != 0) {

                order.setDisplayQuantity(clientOrder.getDisplayQuantity());
                order.setDirty(true);
            }

            if (null != clientOrder.getTriggerMethod() && CoreUtils.nullSafeComparator(order.getTriggerMethod(), clientOrder.getTriggerMethod()) != 0) {

                order.setTriggerMethod(clientOrder.getTriggerMethod());
                order.setDirty(true);
            }

            if (null != clientOrder.getHidden() && CoreUtils.nullSafeComparator(order.getHidden(), clientOrder.getHidden()) != 0) {

                order.setHidden(clientOrder.getHidden());
                order.setDirty(true);
            }

            if (null != clientOrder.getGoodAfterTime() && CoreUtils.nullSafeComparator(order.getGoodAfterTime(), clientOrder.getGoodAfterTime()) != 0) {

                order.setGoodAfterTime(clientOrder.getGoodAfterTime());
                order.setDirty(true);
            }

            if (null != clientOrder.getGoodTillTime() && CoreUtils.nullSafeComparator(order.getGoodTillTime(), clientOrder.getGoodTillTime()) != 0) {

                order.setGoodTillTime(clientOrder.getGoodTillTime());
                order.setDirty(true);
            }

            if (null != clientOrder.getOverrideConstraints() && CoreUtils.nullSafeComparator(order.getOverrideConstraints(),
                    clientOrder.getOverrideConstraints()) != 0) {

                order.setOverrideConstraints(clientOrder.getOverrideConstraints());
                order.setDirty(true);
            }

            if (null != clientOrder.getAllOrNothing() && CoreUtils.nullSafeComparator(order.getAllOrNothing(), clientOrder.getAllOrNothing()) != 0) {

                order.setAllOrNothing(clientOrder.getAllOrNothing());
                order.setDirty(true);
            }

            if (order.isDirty()) {

                order.setOrderUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            }
        }

        return order.isDirty();
    }

    /**
     * Method populateContract.
     *
     * @param contract          Contract
     * @param transientContract Contract
     */
    public static boolean populateContract(Contract contract, Contract transientContract) {

        if (CoreUtils.nullSafeComparator(transientContract.getSymbol(), contract.getSymbol()) == 0) {

            if (null != transientContract.getLocalSymbol() && CoreUtils.nullSafeComparator(transientContract.getLocalSymbol(), contract.getLocalSymbol()) != 0) {

                contract.setLocalSymbol(transientContract.getLocalSymbol());
                contract.setDirty(true);
            }

            if (null != transientContract.getContractIBId() && CoreUtils.nullSafeComparator(transientContract.getContractIBId(), contract.getContractIBId()) > 0) {

                contract.setContractIBId(transientContract.getContractIBId());
                contract.setDirty(true);
            }

            if (null != transientContract.getPrimaryExchange() && CoreUtils.nullSafeComparator(transientContract.getPrimaryExchange(), contract.getPrimaryExchange()) > 0) {

                contract.setPrimaryExchange(transientContract.getPrimaryExchange());
                contract.setDirty(true);
            }

            if (null != transientContract.getExchange() && CoreUtils.nullSafeComparator(transientContract.getExchange(), contract.getExchange()) > 0) {

                contract.setExchange(transientContract.getExchange());
                contract.setDirty(true);
            }

            if (null != transientContract.getExpiry() && CoreUtils.nullSafeComparator(transientContract.getExpiry(), contract.getExpiry()) > 0) {

                contract.setExpiry(transientContract.getExpiry());
                contract.setDirty(true);
            }

            if (null != transientContract.getSecIdType() && CoreUtils.nullSafeComparator(transientContract.getSecIdType(), contract.getSecIdType()) > 0) {

                contract.setSecIdType(transientContract.getSecIdType());
                contract.setDirty(true);
            }

            if (null != transientContract.getLongName() && CoreUtils.nullSafeComparator(transientContract.getLongName(), contract.getLongName()) > 0) {

                contract.setLongName(transientContract.getLongName());
                contract.setDirty(true);
            }

            if (null != transientContract.getCurrency() && CoreUtils.nullSafeComparator(transientContract.getCurrency(), contract.getCurrency()) > 0) {

                contract.setCurrency(transientContract.getCurrency());
                contract.setDirty(true);
            }

            if (null != transientContract.getCategory() && CoreUtils.nullSafeComparator(transientContract.getCategory(), contract.getCategory()) > 0) {

                contract.setCategory(transientContract.getCategory());
                contract.setDirty(true);
            }

            if (null != transientContract.getIndustry() && CoreUtils.nullSafeComparator(transientContract.getIndustry(), contract.getIndustry()) > 0) {

                contract.setIndustry(transientContract.getIndustry());
                contract.setDirty(true);
            }

            if (null != transientContract.getMinTick() && CoreUtils.nullSafeComparator(transientContract.getMinTick(), contract.getMinTick()) > 0) {

                contract.setMinTick(transientContract.getMinTick());
                contract.setDirty(true);
            }

            if (null != transientContract.getPriceMagnifier() && CoreUtils.nullSafeComparator(transientContract.getPriceMagnifier(), contract.getPriceMagnifier()) > 0) {

                contract.setPriceMagnifier(transientContract.getPriceMagnifier());
                contract.setDirty(true);
            }

            if (null != transientContract.getPriceMultiplier() && CoreUtils.nullSafeComparator(transientContract.getPriceMultiplier(), contract.getPriceMultiplier()) > 0) {

                contract.setPriceMultiplier(transientContract.getPriceMultiplier());
                contract.setDirty(true);
            }

            if (null != transientContract.getSubCategory() && CoreUtils.nullSafeComparator(transientContract.getSubCategory(), contract.getSubCategory()) > 0) {

                contract.setSubCategory(transientContract.getSubCategory());
                contract.setDirty(true);
            }

            if (null != transientContract.getTradingClass() && CoreUtils.nullSafeComparator(transientContract.getTradingClass(), contract.getTradingClass()) > 0) {

                contract.setTradingClass(transientContract.getTradingClass());
                contract.setDirty(true);
            }
        }

        return contract.isDirty();
    }

    /**
     * Method populateTradeOrderfill.
     *
     * @param execution      com.ib.client.Execution
     * @param tradeOrderfill TradeOrderfill
     */
    public static void populateTradeOrderfill(TradeOrderfill execution, TradeOrderfill tradeOrderfill) {

        tradeOrderfill.setTime(execution.getTime());
        tradeOrderfill.setExchange(execution.getExchange());
        tradeOrderfill.setSide(execution.getSide());
        tradeOrderfill.setQuantity(execution.getQuantity());
        tradeOrderfill.setPrice(execution.getPrice());
        tradeOrderfill.setAccountNumber(execution.getAccountNumber());
        tradeOrderfill.setAveragePrice(execution.getAveragePrice());
        tradeOrderfill.setCumulativeQuantity(execution.getCumulativeQuantity());
        tradeOrderfill.setExecId(execution.getExecId());
        tradeOrderfill.setDirty(true);
    }

    /**
     * Method logOrderStatus.
     *
     * @param orderId       int
     * @param status        String
     * @param filled        int
     * @param remaining     int
     * @param avgFillPrice  double
     * @param permId        int
     * @param parentId      int
     * @param lastFillPrice double
     * @param clientId      int
     * @param whyHeld       String
     */
    public static void logOrderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice,
                                      int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

        _log.debug("orderId: {} status: {} filled: {} remaining: {} avgFillPrice: {} permId: {} parentId: {} lastFillPrice: {} clientId: {} whyHeld: {}", orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
    }

    /**
     * Method logTradeOrder.
     *
     * @param order TradeOrder
     */
    public static void logTradeOrder(TradeOrder order) {

        _log.debug("OrderKey: {} ClientId: {} PermId: {} Action: {} TotalQuantity: {} OrderType: {} LmtPrice: {} AuxPrice: {} Tif: {} OcaGroup: {} OcaType: {} OrderRef: {} Transmit: {} DisplaySize: {} TriggerMethod: {} Hidden: {} ParentId: {} GoodAfterTime: {} GoodTillDate: {} OverridePercentageConstraints: {} AllOrNone: {}", order.getOrderKey(), order.getClientId(), order.getPermId(), order.getAction(), order.getQuantity(), order.getOrderType(), order.getLimitPrice(), order.getAuxPrice(), order.getTimeInForce(), order.getOcaGroupName(), order.getOcaType(), order.getOrderReference(), order.getTransmit(), order.getDisplayQuantity(), order.getTriggerMethod(), order.getHidden(), order.getParentId(), order.getGoodAfterTime(), order.getGoodTillTime(), order.getOverrideConstraints(), order.getAllOrNothing());
    }

    /**
     * Method logContract.
     *
     * @param contract com.ib.client.Contract
     */
    public static void logContract(Contract contract) {
        _log.debug("Symbol: {} Sec Type: {} Exchange: {} Con Id: {} Currency: {} SecIdType: {} Primary Exch: {} Local Symbol: {} Multiplier: {} Expiry: {} Category: {} Industry: {} LongName: {}", contract.getSymbol(), contract.getSecType(), contract.getExchange(), contract.getContractIBId(), contract.getCurrency(), contract.getSecIdType(), contract.getPrimaryExchange(), contract.getLocalSymbol(), contract.getPriceMultiplier(), contract.getExpiry(), contract.getCategory(), contract.getIndustry(), contract.getLongName());
    }

    /**
     * Method logExecution.
     *
     * @param execution com.ib.client.Execution
     */
    public static void logExecution(TradeOrderfill execution) {
        _log.debug("execDetails OrderId: {} Exchange: {} Side: {} ExecId: {} Time: {} Qty: {} AveragePrice: {} Price: {} CumulativeQuantity: {}", execution.getTradeOrder().getId(), execution.getExchange(), execution.getSide(), execution.getExecId(), execution.getTime(), execution.getQuantity(), execution.getAveragePrice(), execution.getPrice(), execution.getCumulativeQuantity());
    }
}
