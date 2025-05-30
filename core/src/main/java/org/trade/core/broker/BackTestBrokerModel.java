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
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.IPersistentModel;
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

    // Use getId as key
    private static final ConcurrentHashMap<Integer, Tradestrategy> m_historyDataRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> m_realTimeBarsRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> m_contractRequests = new ConcurrentHashMap<>();
    private final IPersistentModel m_tradePersistentModel;

    private final ClientSocket m_client;

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

    public BackTestBrokerModel() {

        try {

            m_client = new ClientSocket(this);
            m_tradePersistentModel = (IPersistentModel) ClassFactory
                    .getServiceForInterface(IPersistentModel._persistentModel, this);
            int maxKey = m_tradePersistentModel.findTradeOrderByMaxKey();

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
     * @see IBrokerModel#getHistoricalData()
     */
    public ConcurrentHashMap<Integer, Tradestrategy> getHistoricalData() {
        return m_historyDataRequests;
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
     * @param idTradestrategy Integer
     * @see IBrokerModel#getBackTestBroker(Integer)
     */
    public Broker getBackTestBroker(Integer idTradestrategy) {

        return m_client.getBackTestBroker(idTradestrategy);
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

            int maxKey = m_tradePersistentModel.findTradeOrderByMaxKey();

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

            if (this.isHistoricalDataRunning(tradestrategy)) {

                throw new BrokerModelException(tradestrategy.getId(), 3010, "Data request is already in progress for: "
                        + tradestrategy.getContract().getSymbol() + " Please wait or cancel.");
            }

            m_historyDataRequests.put(tradestrategy.getId(), tradestrategy);

            if (this.isBrokerDataOnly()) {

                ZonedDateTime endDay = TradingCalendar
                        .getDateAtTime(TradingCalendar.addTradingDays(endDate, backfillOffsetDays), endDate);
                String endDateTime = TradingCalendar.getFormattedDate(endDay, "yyyyMMdd HH:mm:ss");

                m_contractRequests.put(tradestrategy.getContract().getId(), tradestrategy.getContract());

                _log.debug("onBrokerData ReqId: {} Symbol: {} end Time: {} Period length: {} Bar size: {} WhatToShow: {} Regular Trading Hrs: {} Date format: " + backfillDateFormat, tradestrategy.getId(), tradestrategy.getContract().getSymbol(), endDateTime, tradestrategy.getChartDays(), tradestrategy.getBarSize(), backfillWhatToShow, backfillUseRTH);

                m_client.reqHistoricalData(tradestrategy.getId(), tradestrategy, endDateTime,
                        ChartDays.newInstance(tradestrategy.getChartDays()).getDisplayName(),
                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
                        backfillUseRTH, backfillDateFormat);
            } else {
                m_client.reqHistoricalData(tradestrategy.getId(), tradestrategy, null,
                        ChartDays.newInstance(tradestrategy.getChartDays()).getDisplayName(),
                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
                        backfillUseRTH, backfillDateFormat);
            }

        } catch (Throwable ex) {

            throw new BrokerModelException(tradestrategy.getId(), 3020, "Error broker data Symbol: "
                    + tradestrategy.getContract().getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    /**
     * Method isHistoricalDataRunning.
     *
     * @param contract Contract
     * @return boolean
     * @see IBrokerModel#isHistoricalDataRunning(Contract)
     */
    public boolean isHistoricalDataRunning(Contract contract) {

        for (Tradestrategy item : m_historyDataRequests.values()) {

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
    public boolean isHistoricalDataRunning(Tradestrategy tradestrategy) {

        return m_historyDataRequests.containsKey(tradestrategy.getId());
    }

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param contract Contract
     * @return boolean
     * @see IBrokerModel#isRealtimeBarsRunning(Contract)
     */
    public boolean isRealtimeBarsRunning(Contract contract) {

        return m_realTimeBarsRequests.containsKey(contract.getId());
    }

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    public boolean isRealtimeBarsRunning(Tradestrategy tradestrategy) {

        if (m_realTimeBarsRequests.containsKey(tradestrategy.getContract().getId())) {

            Contract contract = m_realTimeBarsRequests.get(tradestrategy.getContract().getId());

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
     * @see IBrokerModel#isRealtimeBarsRunning(Contract)
     */
    public boolean isMarketDataRunning(Contract contract) {
        return false;
    }

    /**
     * Method isMarketDataRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    public boolean isMarketDataRunning(Tradestrategy tradestrategy) {
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

        m_historyDataRequests.clear();
        m_realTimeBarsRequests.clear();
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
        m_contractRequests.put(contract.getId(), contract);
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

        if (m_historyDataRequests.containsKey(tradestrategy.getId())) {

            tradestrategy.getContract().removeTradestrategy(tradestrategy);

            synchronized (m_historyDataRequests) {

                m_historyDataRequests.remove(tradestrategy.getId());
                m_historyDataRequests.notify();
            }
        }
        m_client.removeBackTestBroker(tradestrategy.getId());
    }

    /**
     * Method onCancelBrokerData.
     *
     * @param contract Contract
     * @see IBrokerModel#onCancelRealtimeBars(Contract)
     */
    public void onCancelBrokerData(Contract contract) {

        for (Tradestrategy tradestrategy : m_historyDataRequests.values()) {

            if (contract.equals(tradestrategy.getContract())) {

                contract.removeTradestrategy(tradestrategy);
                m_client.removeBackTestBroker(tradestrategy.getId());
                synchronized (m_historyDataRequests) {
                    m_historyDataRequests.remove(tradestrategy.getId());
                    m_historyDataRequests.notify();
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

        if (m_realTimeBarsRequests.containsKey(contract.getId())) {

            synchronized (m_realTimeBarsRequests) {
                m_realTimeBarsRequests.remove(contract.getId());
            }
        }
    }

    /**
     * Method onCancelRealtimeBars.
     *
     * @param tradestrategy Tradestrategy
     */
    public void onCancelRealtimeBars(Tradestrategy tradestrategy) {

        if (m_realTimeBarsRequests.containsKey(tradestrategy.getContract().getId())) {

            Contract contract = m_realTimeBarsRequests.get(tradestrategy.getContract().getId());

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
     * Method onPlaceOrd
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
                TradeOrder transientInstance = m_tradePersistentModel.persistTradeOrder(tradeOrder);
                // Debug logging
                _log.debug("Order Placed Key: {}", transientInstance.getOrderKey());
                TWSBrokerModel.logContract(TWSBrokerModel.getIBContract(contract));
                TWSBrokerModel.logTradeOrder(TWSBrokerModel.getIBOrder(transientInstance));
                return transientInstance;
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
            orderState.m_status = OrderStatus.CANCELLED;
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

            TradeOrder transientInstance = m_tradePersistentModel
                    .findTradeOrderByKey(execution.getTradeOrder().getOrderKey());

            if (null == transientInstance) {

                error(execution.getTradeOrder().getOrderKey(), 3170,
                        "Warning Order not found for Order Key: " + execution.getTradeOrder().getOrderKey()
                                + " make sure Client ID: " + 0 + " is not the master in TWS. On execDetails update.");
                return;
            }

            /*
             * We already have this order fill.
             */
            if (transientInstance.existTradeOrderfill(execution.getExecId()))
                return;

            TradeOrderfill tradeOrderfill = new TradeOrderfill();
            BackTestBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);
            tradeOrderfill.setTradeOrder(transientInstance);
            transientInstance.addTradeOrderfill(tradeOrderfill);
            transientInstance.setAverageFilledPrice(tradeOrderfill.getAveragePrice());
            transientInstance.setFilledQuantity(tradeOrderfill.getCumulativeQuantity());
            transientInstance.setFilledDate(tradeOrderfill.getTime());
            boolean isFilled = transientInstance.getIsFilled();
            TradeOrder updatedOrder = m_tradePersistentModel.persistTradeOrderfill(transientInstance);

            // Let the controller know an order was filled
            if (updatedOrder.getIsFilled() && !isFilled)
                this.fireTradeOrderFilled(updatedOrder);

        } catch (Exception ex) {
            error(reqId, 3160, "Errors saving execution: " + ex.getMessage());
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

            TradeOrder transientInstance = m_tradePersistentModel.findTradeOrderByKey(tradeOrder.getOrderKey());
            if (null == transientInstance) {
                error(orderId, 3170, "Warning Order not found for Order Key: " + orderId + " make sure Client ID: " + 0
                        + " is not the master in TWS. On openOrder update.");
                return;
            }

            /*
             * Check to see if anything has changed as this method gets fired
             * twice on order fills.
             */

            if (BackTestBrokerModel.updateTradeOrder(tradeOrder, orderState, transientInstance)) {

                if (OrderStatus.FILLED.equals(transientInstance.getStatus())) {

                    _log.debug("Order Key: {} filled.", transientInstance.getOrderKey());
                    BackTestBrokerModel.logOrderState(orderState);
                    BackTestBrokerModel.logTradeOrder(tradeOrder);

                    TradeOrder updatedOrder = m_tradePersistentModel.persistTradeOrder(transientInstance);

                    if (updatedOrder.hasTradePosition() && !updatedOrder.getTradePosition().isOpen()) {
                        // Let the controller know a position was closed
                        this.firePositionClosed(updatedOrder.getTradePosition());
                    }
                } else {
                    _log.debug("Order key: {} state changed. Status:{}", transientInstance.getOrderKey(), orderState.m_status);
                    BackTestBrokerModel.logOrderState(orderState);
                    BackTestBrokerModel.logTradeOrder(tradeOrder);
                    TradeOrder updatedOrder = m_tradePersistentModel.persistTradeOrder(transientInstance);
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

            TradeOrder transientInstance = m_tradePersistentModel.findTradeOrderByKey(orderId);

            if (null == transientInstance) {
                error(orderId, 3170, "Warning Order not found for Order Key: " + orderId + " make sure Client ID: " + 0
                        + " is not the master in TWS. On orderStatus update.");
                return;
            }
            /*
             * Check to see if anything has changed as this method gets fired
             * twice on order fills.
             */
            boolean changed = false;

            if (CoreUtils.nullSafeComparator(transientInstance.getStatus(), status.toUpperCase()) != 0) {

                transientInstance.setStatus(status.toUpperCase());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientInstance.getWhyHeld(), whyHeld) != 0) {

                transientInstance.setWhyHeld(whyHeld);
                changed = true;
            }
            /*
             * If filled qty is greater than current filled qty set the new
             * value.
             */
            if (CoreUtils.nullSafeComparator(filled, transientInstance.getFilledQuantity()) == 1) {

                if (filled > 0) {

                    transientInstance.setAverageFilledPrice(new BigDecimal(avgFillPrice));
                    transientInstance.setFilledQuantity(filled);
                    changed = true;
                }
            }

            if (changed) {

                transientInstance.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                transientInstance.setStatus(status.toUpperCase());
                transientInstance.setWhyHeld(whyHeld);
                _log.debug("Order Status changed. Status: {}", status);
                TWSBrokerModel.logOrderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId,
                        lastFillPrice, clientId, whyHeld);

                boolean isFilled = transientInstance.getIsFilled();
                TradeOrder updatedOrder = m_tradePersistentModel.persistTradeOrder(transientInstance);

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
        if (m_contractRequests.containsKey(id)) {

            symbol = m_contractRequests.get(id).getSymbol();
            synchronized (m_contractRequests) {
                m_contractRequests.remove(id);
            }
        }
        if (m_historyDataRequests.containsKey(id)) {

            symbol = m_historyDataRequests.get(id).getContract().getSymbol();
            synchronized (m_historyDataRequests) {
                m_historyDataRequests.remove(id);
                m_historyDataRequests.notify();
            }
        }
        if (m_realTimeBarsRequests.containsKey(id)) {

            symbol = m_realTimeBarsRequests.get(id).getSymbol();
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
            if (m_realTimeBarsRequests.containsKey(id)) {
                synchronized (m_realTimeBarsRequests) {
                    m_realTimeBarsRequests.remove(id);
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

            if (m_contractRequests.containsKey(reqId)) {

                Contract contract = m_contractRequests.get(reqId);
                BackTestBrokerModel.logContract(contractDetails);

                if (BackTestBrokerModel.populateContract(contractDetails, contract)) {

                    m_tradePersistentModel.persistContract(contract);
                    synchronized (m_contractRequests) {
                        m_contractRequests.remove(reqId);
                    }
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

        if (m_contractRequests.containsKey(reqId)) {
            synchronized (m_contractRequests) {
                m_contractRequests.remove(reqId);
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
            if (m_historyDataRequests.containsKey(reqId)) {

                /*
                 * There is a bug in the TWS interface format for dates
                 * should always be milli sec but when 1 day is selected as
                 * the period the dates come through as yyyyMMdd.
                 */
                Tradestrategy tradestrategy = m_historyDataRequests.get(reqId);
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
            error(reqId, 3260, ex.getMessage());
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
            if (m_historyDataRequests.containsKey(reqId)) {

                Tradestrategy tradestrategy = m_historyDataRequests.get(reqId);

                CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();
                m_tradePersistentModel.persistCandleSeries(candleSeries);

                _log.debug("HistoricalData complete Req Id: {}, Symbol: {}, Tradingday: {}, candles to saved: {}, Contract Tradestrategies size:: {}", reqId, tradestrategy.getContract().getSymbol(), tradestrategy.getTradingday().getOpen(), candleSeries.getItemCount(), tradestrategy.getContract().getTradestrategies().size());

                /*
                 * The last one has arrived the reqId is the
                 * tradeStrategyId. Remove this from the processing vector.
                 */

                synchronized (m_historyDataRequests) {

                    m_historyDataRequests.remove(reqId);
                    m_historyDataRequests.notify();
                }

                /*
                 * Check to see if the trading day is today and this
                 * strategy is selected to trade and that the market is open
                 */
                synchronized (tradestrategy.getContract().getTradestrategies()) {

                    this.fireHistoricalDataComplete(tradestrategy);

                    if (tradestrategy.getTradingday().getClose()
                            .isAfter(TradingCalendar.getDateTimeNowMarketTimeZone())) {

                        if (!this.isRealtimeBarsRunning(tradestrategy.getContract())) {

                            tradestrategy.getContract().addTradestrategy(tradestrategy);
                            this.onReqRealTimeBars(tradestrategy.getContract(),
                                    tradestrategy.getStrategy().getMarketData());
                        } else {
                            Contract contract = m_realTimeBarsRequests.get(tradestrategy.getContract().getId());
                            contract.addTradestrategy(tradestrategy);
                        }
                    }
                }
            } else {
                _log.error("HistoricalDataComplete request not found for Req Id: {}", reqId);
            }
        } catch (Exception ex) {
            error(reqId, 3260, ex.getMessage());
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
        _log.debug("Status: {} Comms Amt: {} Comms Currency: {} Warning txt: {} Init Margin: {} Maint Margin: {} Min Comms: {} Max Comms: {}", orderState.m_status, orderState.m_commission, orderState.m_commissionCurrency, orderState.m_warningText, orderState.m_initMargin, orderState.m_maintMargin, orderState.m_minCommission, orderState.m_maxCommission);
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

            if (CoreUtils.nullSafeComparator(order.getStatus(), clientOrderState.m_status.toUpperCase()) != 0) {
                order.setStatus(clientOrderState.m_status.toUpperCase());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getWarningMessage(), clientOrderState.m_warningText) != 0) {
                order.setWarningMessage(clientOrderState.m_warningText);
                order.setDirty(true);
            }
            Money comms = new Money(clientOrderState.m_commission);

            if (CoreUtils.nullSafeComparator(comms, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getCommission(), comms.getBigDecimalValue()) != 0) {
                order.setCommission(comms.getBigDecimalValue());
                order.setDirty(true);

            }

            if (CoreUtils.nullSafeComparator(order.getClientId(), clientOrder.getClientId()) != 0) {
                order.setClientId(clientOrder.getClientId());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getAction(), clientOrder.getAction()) != 0) {
                order.setAction(clientOrder.getAction());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getQuantity(), clientOrder.getQuantity()) != 0) {
                order.setQuantity(clientOrder.getQuantity());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getOrderType(), clientOrder.getOrderType()) != 0) {
                order.setOrderType(clientOrder.getOrderType());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(new Money(clientOrder.getLimitPrice()), new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getLimitPrice(), clientOrder.getLimitPrice()) != 0) {
                order.setLimitPrice(clientOrder.getLimitPrice());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(new Money(clientOrder.getAuxPrice()), new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getAuxPrice(), clientOrder.getAuxPrice()) != 0) {
                order.setAuxPrice(clientOrder.getAuxPrice());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getTimeInForce(), clientOrder.getTimeInForce()) != 0) {
                order.setTimeInForce(clientOrder.getTimeInForce());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getOcaGroupName(), clientOrder.getOcaGroupName()) != 0) {
                order.setOcaGroupName(clientOrder.getOcaGroupName());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getOcaType(), clientOrder.getOcaType()) != 0) {
                order.setOcaType(clientOrder.getOcaType());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getOrderReference(), clientOrder.getOrderReference()) != 0) {
                order.setOrderReference(clientOrder.getOrderReference());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getPermId(), clientOrder.getPermId()) != 0) {
                order.setPermId(clientOrder.getPermId());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getParentId(), clientOrder.getParentId()) != 0) {
                order.setParentId(clientOrder.getParentId());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getTransmit(), clientOrder.getTransmit()) != 0) {
                order.setTransmit(clientOrder.getTransmit());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getDisplayQuantity(), clientOrder.getDisplayQuantity()) != 0) {
                order.setDisplayQuantity(clientOrder.getDisplayQuantity());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getTriggerMethod(), clientOrder.getTriggerMethod()) != 0) {
                order.setTriggerMethod(clientOrder.getTriggerMethod());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getHidden(), clientOrder.getHidden()) != 0) {
                order.setHidden(clientOrder.getHidden());
                order.setDirty(true);
            }

            if (null != clientOrder.getGoodAfterTime()) {

                if (CoreUtils.nullSafeComparator(order.getGoodAfterTime(), clientOrder.getGoodAfterTime()) != 0) {
                    order.setGoodAfterTime(clientOrder.getGoodAfterTime());
                    order.setDirty(true);
                }
            }

            if (null != clientOrder.getGoodTillTime()) {
                if (CoreUtils.nullSafeComparator(order.getGoodTillTime(), clientOrder.getGoodTillTime()) != 0) {
                    order.setGoodTillTime(clientOrder.getGoodTillTime());
                    order.setDirty(true);
                }
            }

            if (CoreUtils.nullSafeComparator(order.getOverrideConstraints(),
                    clientOrder.getOverrideConstraints()) != 0) {
                order.setOverrideConstraints(clientOrder.getOverrideConstraints());
                order.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(order.getAllOrNothing(), clientOrder.getAllOrNothing()) != 0) {
                order.setAllOrNothing(clientOrder.getAllOrNothing());
                order.setDirty(true);
            }

            if (order.isDirty()) {

                order.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            }
        }
        return order.isDirty();
    }

    /**
     * Method populateContract.
     *
     * @param contractDetails   com.ib.client.ContractDetails
     * @param transientContract Contract
     */
    public static boolean populateContract(Contract contractDetails, Contract transientContract) {

        if (CoreUtils.nullSafeComparator(transientContract.getSymbol(), contractDetails.getSymbol()) == 0) {

            if (CoreUtils.nullSafeComparator(transientContract.getLocalSymbol(),
                    contractDetails.getLocalSymbol()) != 0) {

                transientContract.setLocalSymbol(contractDetails.getLocalSymbol());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getIdContractIB(),
                    contractDetails.getIdContractIB()) != 0) {

                transientContract.setIdContractIB(contractDetails.getIdContractIB());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getPrimaryExchange(),
                    contractDetails.getPrimaryExchange()) != 0) {

                transientContract.setPrimaryExchange(contractDetails.getPrimaryExchange());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getExchange(), contractDetails.getExchange()) != 0) {

                transientContract.setExchange(contractDetails.getExchange());
                transientContract.setDirty(true);
            }

            if (null != contractDetails.getExpiry()) {

                if (CoreUtils.nullSafeComparator(transientContract.getExpiry(), contractDetails.getExpiry()) != 0) {

                    transientContract.setExpiry(contractDetails.getExpiry());
                    transientContract.setDirty(true);
                }
            }

            if (CoreUtils.nullSafeComparator(transientContract.getSecIdType(), contractDetails.getSecIdType()) != 0) {

                transientContract.setSecIdType(contractDetails.getSecIdType());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getLongName(), contractDetails.getLongName()) != 0) {

                transientContract.setLongName(contractDetails.getLongName());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getCurrency(), contractDetails.getCurrency()) != 0) {

                transientContract.setCurrency(contractDetails.getCurrency());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getCategory(), contractDetails.getCategory()) != 0) {

                transientContract.setCategory(contractDetails.getCategory());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getIndustry(), contractDetails.getIndustry()) != 0) {

                transientContract.setIndustry(contractDetails.getIndustry());
                transientContract.setDirty(true);
            }

            Money minTick = new Money(contractDetails.getMinTick());

            if (CoreUtils.nullSafeComparator(minTick, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getMinTick(), minTick.getBigDecimalValue()) != 0) {

                transientContract.setMinTick(minTick.getBigDecimalValue());
                transientContract.setDirty(true);
            }

            Money priceMagnifier = new Money(contractDetails.getPriceMagnifier());

            if (CoreUtils.nullSafeComparator(priceMagnifier, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(transientContract.getPriceMagnifier(),
                    priceMagnifier.getBigDecimalValue()) != 0) {

                transientContract.setPriceMagnifier(priceMagnifier.getBigDecimalValue());
                transientContract.setDirty(true);
            }

            Money multiplier = new Money(contractDetails.getPriceMultiplier());

            if (CoreUtils.nullSafeComparator(multiplier, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getPriceMultiplier(), multiplier.getBigDecimalValue()) != 0) {

                transientContract.setPriceMultiplier(multiplier.getBigDecimalValue());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getSubCategory(),
                    contractDetails.getSubCategory()) != 0) {

                transientContract.setSubCategory(contractDetails.getSubCategory());
                transientContract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(transientContract.getTradingClass(),
                    contractDetails.getTradingClass()) != 0) {

                transientContract.setTradingClass(contractDetails.getTradingClass());
                transientContract.setDirty(true);
            }
        }

        return transientContract.isDirty();
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
        _log.debug("Symbol: {} Sec Type: {} Exchange: {} Con Id: {} Currency: {} SecIdType: {} Primary Exch: {} Local Symbol: {} Multiplier: {} Expiry: {} Category: {} Industry: {} LongName: {}", contract.getSymbol(), contract.getSecType(), contract.getExchange(), contract.getIdContractIB(), contract.getCurrency(), contract.getSecIdType(), contract.getPrimaryExchange(), contract.getLocalSymbol(), contract.getPriceMultiplier(), contract.getExpiry(), contract.getCategory(), contract.getIndustry(), contract.getLongName());
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
