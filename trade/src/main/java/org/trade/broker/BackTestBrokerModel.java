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
package org.trade.broker;

import com.ib.client.ContractDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.broker.client.Broker;
import org.trade.broker.client.ClientSocket;
import org.trade.broker.client.IClientWrapper;
import org.trade.broker.client.OrderState;
import org.trade.core.factory.ClassFactory;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.TradingCalendar;
import org.trade.core.valuetype.Money;
import org.trade.dictionary.valuetype.BarSize;
import org.trade.dictionary.valuetype.ChartDays;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.persistent.IPersistentModel;
import org.trade.persistent.dao.Contract;
import org.trade.persistent.dao.TradeOrder;
import org.trade.persistent.dao.TradeOrderfill;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.strategy.data.CandleSeries;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class BackTestBrokerModel extends AbstractBrokerModel implements IClientWrapper {

    /**
     *
     */
    private static final long serialVersionUID = 3191422640254347940L;

    private final static Logger _log = LoggerFactory.getLogger(BackTestBrokerModel.class);

    // Use getId as key
    private static final ConcurrentHashMap<Integer, Tradestrategy> m_historyDataRequests = new ConcurrentHashMap<Integer, Tradestrategy>();
    private static final ConcurrentHashMap<Integer, Contract> m_realTimeBarsRequests = new ConcurrentHashMap<Integer, Contract>();
    private static final ConcurrentHashMap<Integer, Contract> m_contractRequests = new ConcurrentHashMap<Integer, Contract>();
    private IPersistentModel m_tradePersistentModel = null;

    private ClientSocket m_client = null;

    private static final int SCALE = 5;
    private static final int minOrderId = 100000;

    private AtomicInteger orderKey = null;

    private static Integer backfillDateFormat = 2;
    private static String backfillWhatToShow;
    private static Integer backfillOffsetDays = 0;
    private static Integer backfillUseRTH = 1;

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
     * @throws BrokerModelException
     * @see IBrokerModel#onConnect(String, Integer, Integer)
     */
    public void onConnect(String host, Integer port, Integer clientId) {

    }

    /**
     * Method connectionClosed.
     *
     * @see com.ib.client.AnyWrapper#connectionClosed()
     */
    public void connectionClosed() {

        onCancelAllRealtimeData();
        this.fireConnectionClosed(true);
        error(0, 1101, "Error Connection was closed! ");
    }

    /**
     * Method disconnect.
     *
     * @throws BrokerModelException
     * @see IBrokerModel#disconnect()
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
        return new Integer(orderKey.incrementAndGet());
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
     * @param subscribe boolean
     * @param account   Account
     * @throws BrokerModelException
     * @see IBrokerModel#onSubscribeAccountUpdates(boolean,
     * account)
     */
    public void onSubscribeAccountUpdates(boolean subscribe, String accountNumber) throws BrokerModelException {
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
     *
     * @see org.trade.broker.onReqFinancialAccount()
     */
    public void onReqFinancialAccount() {
    }

    /**
     * Method onReqReplaceFinancialAccount.
     *
     * @param xml        String
     * @param faDataType int
     * @see org.trade.broker.onReqReplaceFinancialAccount()
     */
    public void onReqReplaceFinancialAccount(int faDataType, String xml) throws BrokerModelException {
    }

    /**
     * Method onReqManagedAccount.
     *
     * @throws BrokerModelException
     * @see IBrokerModel#onReqManagedAccount()
     */
    public void onReqManagedAccount() throws BrokerModelException {
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
     * @throws BrokerModelException
     * @see IBrokerModel#onReqExecutions(Tradestrategy)
     */
    public void onReqExecutions(Tradestrategy tradestrategy, boolean addOrders) throws BrokerModelException {

    }

    /**
     * Method onReqAllExecutions.
     *
     * @param mktOpenDate ZonedDateTime
     * @throws BrokerModelException
     * @see IBrokerModel#onReqAllExecutions(Date)
     */
    public void onReqAllExecutions(ZonedDateTime mktOpenDate) throws BrokerModelException {
    }

    /**
     * Method onReqRealTimeBars.
     *
     * @param contract Contract
     * @param mktData  boolean
     * @throws BrokerModelException
     */
    public void onReqRealTimeBars(Contract contract, boolean mktData) throws BrokerModelException {
    }

    /**
     * Method onReqMarketData.
     *
     * @param contract        Contract
     * @param genericTicklist String
     * @param snapshot        boolean
     * @throws BrokerModelException
     */
    public void onReqMarketData(Contract contract, String genericTicklist, boolean snapshot)
            throws BrokerModelException {

    }

    /**
     * Method onBrokerData.
     *
     * @param tradestrategy Tradestrategy
     * @param ZonedDateTime startDate
     * @param ZonedDateTime endDate
     * @param Integer       barSize
     * @param Integer       chartDays
     * @throws BrokerModelException
     * @see IBrokerModel#onBrokerData(Contract, String, String
     *)
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

                _log.debug("onBrokerData ReqId: " + tradestrategy.getId() + " Symbol: "
                        + tradestrategy.getContract().getSymbol() + " end Time: " + endDateTime + " Period length: "
                        + tradestrategy.getChartDays() + " Bar size: " + tradestrategy.getBarSize() + " WhatToShow: "
                        + backfillWhatToShow + " Regular Trading Hrs: " + backfillUseRTH + " Date format: "
                        + backfillDateFormat);

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
        if (m_historyDataRequests.containsKey(tradestrategy.getId())) {
            return true;
        }
        return false;
    }

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param contract Contract
     * @return boolean
     * @see IBrokerModel#isRealtimeBarsRunning(Contract)
     */
    public boolean isRealtimeBarsRunning(Contract contract) {
        if (m_realTimeBarsRequests.containsKey(contract.getId())) {
            return true;
        }
        return false;
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
     * @throws BrokerModelException
     * @see IBrokerModel#onContractDetails(Contract)
     */
    public void onContractDetails(final Contract contract) throws BrokerModelException {
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
            if (contract.getTradestrategies().isEmpty())
                onCancelRealtimeBars(contract);
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
     * @throws BrokerModelException
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
                _log.debug("Order Placed Key: " + transientInstance.getOrderKey());
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
     * @throws BrokerModelException
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
     * When orders are filled the the exceDetails is fired followed by
     * openOrder() and orderStatus() the order methods fire twice. openOrder
     * gives us the commission amount on the second fire and order status from
     * both. Apart from that I have no idea why they fire twice. I assume its to
     * do with the margin and account updates.
     *
     * @param reqId      int
     * @param contractIB com.ib.client.Contract
     * @param execution  Execution
     * @see http://www.interactivebrokers.com/php/apiUsersGuide/apiguide.htm
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
     * @param contractIB com.ib.client.Contract
     * @param order      com.ib.client.Order
     * @param orderState OrderState
     * @see http://www.interactivebrokers.com/php/apiUsersGuide/apiguide.htm
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

                    _log.debug("Order Key: " + transientInstance.getOrderKey() + " filled.");
                    BackTestBrokerModel.logOrderState(orderState);
                    BackTestBrokerModel.logTradeOrder(tradeOrder);

                    TradeOrder updatedOrder = m_tradePersistentModel.persistTradeOrder(transientInstance);

                    if (updatedOrder.hasTradePosition() && !updatedOrder.getTradePosition().isOpen()) {
                        // Let the controller know a position was closed
                        this.firePositionClosed(updatedOrder.getTradePosition());
                    }
                } else {
                    _log.debug("Order key: " + transientInstance.getOrderKey() + " state changed. Status:"
                            + orderState.m_status);
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
     * @see http://www.interactivebrokers.com/php/apiUsersGuide/apiguide.htm
     */
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId,
                            int parentId, double lastFillPrice, int clientId, String whyHeld) {
        try {
            TradeOrder transientInstance = m_tradePersistentModel.findTradeOrderByKey(new Integer(orderId));
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
            if (CoreUtils.nullSafeComparator(new Integer(filled), transientInstance.getFilledQuantity()) == 1) {
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
                _log.debug("Order Status changed. Status: " + status);
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
     * @param e Exception
     */
    public void error(Exception ex) {
        _log.error("IBrokerModel error msg: " + ex.getMessage());
        // this.fireBrokerError(new BrokerManagerModelException(e));
    }

    /**
     * Method error.
     *
     * @param msg String
     */
    public void error(String msg) {
        _log.error("IBrokerModel error str: " + msg);
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
        BrokerModelException brokerModelException = null;
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
                } else {
                    error(reqId, 3220, "Contract details not found for reqId: " + reqId + " Symbol: "
                            + contractDetails.getSymbol());
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
     * @param volume     int
     * @param tradeCount int
     * @param vwap       double
     * @param hasGaps    boolean
     */
    public void historicalData(int reqId, String dateString, double open, double high, double low, double close,
                               int volume, int tradeCount, double vwap, boolean hasGaps) {
        try {
            volume = volume * 100;
            /*
             * Check to see if the trading day is today and this strategy is
             * selected to trade and that the market is open
             */
            if (m_historyDataRequests.containsKey(reqId)) {
                Tradestrategy tradestrategy = m_historyDataRequests.get(reqId);

                if (dateString.contains("finished-")) {

                    /*
                     * The last one has arrived the reqId is the
                     * tradeStrategyId. Remove this from the processing vector.
                     */
                    CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();
                    m_tradePersistentModel.persistCandleSeries(candleSeries);

                    _log.debug("HistoricalData complete Req Id: " + reqId + " Symbol: "
                            + tradestrategy.getContract().getSymbol() + " Tradingday: "
                            + tradestrategy.getTradingday().getOpen() + " candles to saved: "
                            + candleSeries.getItemCount() + " Contract Tradestrategies size:: "
                            + tradestrategy.getContract().getTradestrategies().size());

                    /*
                     * Check to see if the trading day is today and this
                     * strategy is selected to trade and that the market is open
                     */

                    if (tradestrategy.getTrade() && !this.isBrokerDataOnly()) {
                        this.fireHistoricalDataComplete(tradestrategy);
                    } else {
                        synchronized (m_historyDataRequests) {
                            m_historyDataRequests.remove(reqId);
                            m_historyDataRequests.notify();
                        }
                    }
                } else {

                    ZonedDateTime date = null;
                    /*
                     * There is a bug in the TWS interface format for dates
                     * should always be milli sec but when 1 day is selected as
                     * the period the dates come through as yyyyMMdd.
                     */
                    if (dateString.length() == 8) {
                        date = TradingCalendar.getZonedDateTimeFromDateString(dateString, "yyyyMMdd",
                                TradingCalendar.MKT_TIMEZONE);
                    } else {
                        date = TradingCalendar.getZonedDateTimeFromMilli((Long.parseLong(dateString) * 1000));
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
                                tradestrategy.getTradingday().getClose(), date))
                            return;
                        BigDecimal price = (new BigDecimal(close)).setScale(SCALE, RoundingMode.HALF_EVEN);
                        tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastAskPrice(price);
                        tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastBidPrice(price);
                        tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastPrice(price);
                        tradestrategy.getStrategyData().buildCandle(date, open, high, low, close, volume, vwap,
                                tradeCount, 1, null);
                    }
                }
            } else {
                _log.error("HistoricalData request not found for Req Id: " + reqId + " Date: " + dateString);
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
     * @throws BrokerModelException
     */

    public boolean validateBrokerData(Tradestrategy tradestrategy) throws BrokerModelException {

        boolean valid = true;
        String errorMsg = "Symbol: " + tradestrategy.getContract().getSymbol()
                + " Bar Size/Chart Days combination was not valid for Yahoo API, these values have been updated." + "\n"
                + "Please validate and save." + "\n" + "Note Chart Days/BarSize combinations for Yahoo: " + "\n"
                + "Chart Hist = 1 D, Bar Size >= 1min" + "\n" + "Chart Hist > 1 D to 1 M, Bar Size >= 5min" + "\n"
                + "Chart Hist > 1 M to 3 M, Bar Size = 1 day";
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
        _log.debug("Status: " + orderState.m_status + " Comms Amt: " + orderState.m_commission + " Comms Currency: "
                + orderState.m_commissionCurrency + " Warning txt: " + orderState.m_warningText + " Init Margin: "
                + orderState.m_initMargin + " Maint Margin: " + orderState.m_maintMargin + " Min Comms: "
                + orderState.m_minCommission + " Max Comms: " + orderState.m_maxCommission);
    }

    /**
     * Method updateTradeOrder.
     *
     * @param ibOrder      com.ib.client.Order
     * @param ibOrderState OrderState
     * @param order        TradeOrder
     * @return boolean
     * @throws ParseException
     */
    public static boolean updateTradeOrder(TradeOrder clientOrder, OrderState clientOrderState, TradeOrder order)
            throws ParseException {

        boolean changed = false;

        if (CoreUtils.nullSafeComparator(order.getOrderKey(), clientOrder.getOrderKey()) == 0) {
            if (CoreUtils.nullSafeComparator(order.getStatus(), clientOrderState.m_status.toUpperCase()) != 0) {
                order.setStatus(clientOrderState.m_status.toUpperCase());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getWarningMessage(), clientOrderState.m_warningText) != 0) {
                order.setWarningMessage(clientOrderState.m_warningText);
                changed = true;
            }
            Money comms = new Money(clientOrderState.m_commission);
            if (CoreUtils.nullSafeComparator(comms, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getCommission(), comms.getBigDecimalValue()) != 0) {
                order.setCommission(comms.getBigDecimalValue());
                changed = true;

            }
            if (CoreUtils.nullSafeComparator(order.getClientId(), clientOrder.getClientId()) != 0) {
                order.setClientId(clientOrder.getClientId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getAction(), clientOrder.getAction()) != 0) {
                order.setAction(clientOrder.getAction());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getQuantity(), clientOrder.getQuantity()) != 0) {
                order.setQuantity(clientOrder.getQuantity());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOrderType(), clientOrder.getOrderType()) != 0) {
                order.setOrderType(clientOrder.getOrderType());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(new Money(clientOrder.getLimitPrice()), new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getLimitPrice(), clientOrder.getLimitPrice()) != 0) {
                order.setLimitPrice(clientOrder.getLimitPrice());
                changed = true;
            }

            if (CoreUtils.nullSafeComparator(new Money(clientOrder.getAuxPrice()), new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getAuxPrice(), clientOrder.getAuxPrice()) != 0) {
                order.setAuxPrice(clientOrder.getAuxPrice());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getTimeInForce(), clientOrder.getTimeInForce()) != 0) {
                order.setTimeInForce(clientOrder.getTimeInForce());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOcaGroupName(), clientOrder.getOcaGroupName()) != 0) {
                order.setOcaGroupName(clientOrder.getOcaGroupName());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOcaType(), clientOrder.getOcaType()) != 0) {
                order.setOcaType(clientOrder.getOcaType());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOrderReference(), clientOrder.getOrderReference()) != 0) {
                order.setOrderReference(clientOrder.getOrderReference());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getPermId(), clientOrder.getPermId()) != 0) {
                order.setPermId(clientOrder.getPermId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getParentId(), clientOrder.getParentId()) != 0) {
                order.setParentId(clientOrder.getParentId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getTransmit(), clientOrder.getTransmit()) != 0) {
                order.setTransmit(clientOrder.getTransmit());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getDisplayQuantity(), clientOrder.getDisplayQuantity()) != 0) {
                order.setDisplayQuantity(clientOrder.getDisplayQuantity());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getTriggerMethod(), clientOrder.getTriggerMethod()) != 0) {
                order.setTriggerMethod(clientOrder.getTriggerMethod());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getHidden(), clientOrder.getHidden()) != 0) {
                order.setHidden(clientOrder.getHidden());
                changed = true;
            }
            if (null != clientOrder.getGoodAfterTime()) {
                if (CoreUtils.nullSafeComparator(order.getGoodAfterTime(), clientOrder.getGoodAfterTime()) != 0) {
                    order.setGoodAfterTime(clientOrder.getGoodAfterTime());
                    changed = true;
                }
            }

            if (null != clientOrder.getGoodTillTime()) {
                if (CoreUtils.nullSafeComparator(order.getGoodTillTime(), clientOrder.getGoodTillTime()) != 0) {
                    order.setGoodTillTime(clientOrder.getGoodTillTime());
                    changed = true;
                }
            }

            if (CoreUtils.nullSafeComparator(order.getOverrideConstraints(),
                    clientOrder.getOverrideConstraints()) != 0) {
                order.setOverrideConstraints(clientOrder.getOverrideConstraints());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getAllOrNothing(), clientOrder.getAllOrNothing()) != 0) {
                order.setAllOrNothing(clientOrder.getAllOrNothing());
                changed = true;
            }
            if (changed)
                order.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
        }
        return changed;
    }

    /**
     * Method populateContract.
     *
     * @param contractDetails   com.ib.client.ContractDetails
     * @param transientContract Contract
     * @throws ParseException
     */
    public static boolean populateContract(Contract contractDetails, Contract transientContract) throws ParseException {

        boolean changed = false;
        if (CoreUtils.nullSafeComparator(transientContract.getSymbol(), contractDetails.getSymbol()) == 0) {
            if (CoreUtils.nullSafeComparator(transientContract.getLocalSymbol(),
                    contractDetails.getLocalSymbol()) != 0) {
                transientContract.setLocalSymbol(contractDetails.getLocalSymbol());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getIdContractIB(),
                    contractDetails.getIdContractIB()) != 0) {
                transientContract.setIdContractIB(contractDetails.getIdContractIB());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getPrimaryExchange(),
                    contractDetails.getPrimaryExchange()) != 0) {
                transientContract.setPrimaryExchange(contractDetails.getPrimaryExchange());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getExchange(), contractDetails.getExchange()) != 0) {
                transientContract.setExchange(contractDetails.getExchange());
                changed = true;
            }
            if (null != contractDetails.getExpiry()) {
                if (CoreUtils.nullSafeComparator(transientContract.getExpiry(), contractDetails.getExpiry()) != 0) {
                    transientContract.setExpiry(contractDetails.getExpiry());
                    changed = true;
                }
            }
            if (CoreUtils.nullSafeComparator(transientContract.getSecIdType(), contractDetails.getSecIdType()) != 0) {
                transientContract.setSecIdType(contractDetails.getSecIdType());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getLongName(), contractDetails.getLongName()) != 0) {
                transientContract.setLongName(contractDetails.getLongName());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getCurrency(), contractDetails.getCurrency()) != 0) {
                transientContract.setCurrency(contractDetails.getCurrency());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getCategory(), contractDetails.getCategory()) != 0) {
                transientContract.setCategory(contractDetails.getCategory());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getIndustry(), contractDetails.getIndustry()) != 0) {
                transientContract.setIndustry(contractDetails.getIndustry());
                changed = true;
            }
            Money minTick = new Money(contractDetails.getMinTick());
            if (CoreUtils.nullSafeComparator(minTick, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getMinTick(), minTick.getBigDecimalValue()) != 0) {
                transientContract.setMinTick(minTick.getBigDecimalValue());
                changed = true;
            }
            Money priceMagnifier = new Money(contractDetails.getPriceMagnifier());
            if (CoreUtils.nullSafeComparator(priceMagnifier, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(transientContract.getPriceMagnifier(),
                    priceMagnifier.getBigDecimalValue()) != 0) {
                transientContract.setPriceMagnifier(priceMagnifier.getBigDecimalValue());
                changed = true;
            }

            Money multiplier = new Money(contractDetails.getPriceMultiplier());
            if (CoreUtils.nullSafeComparator(multiplier, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getPriceMultiplier(), multiplier.getBigDecimalValue()) != 0) {
                transientContract.setPriceMultiplier(multiplier.getBigDecimalValue());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getSubCategory(),
                    contractDetails.getSubCategory()) != 0) {
                transientContract.setSubCategory(contractDetails.getSubCategory());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getTradingClass(),
                    contractDetails.getTradingClass()) != 0) {
                transientContract.setTradingClass(contractDetails.getTradingClass());
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Method populateTradeOrderfill.
     *
     * @param execution      com.ib.client.Execution
     * @param tradeOrderfill TradeOrderfill
     * @throws ParseException
     * @throws IOException
     */
    public static void populateTradeOrderfill(TradeOrderfill execution, TradeOrderfill tradeOrderfill)
            throws ParseException, IOException {

        tradeOrderfill.setTime(execution.getTime());
        tradeOrderfill.setExchange(execution.getExchange());
        tradeOrderfill.setSide(execution.getSide());
        tradeOrderfill.setQuantity(execution.getQuantity());
        tradeOrderfill.setPrice(execution.getPrice());
        tradeOrderfill.setAccountNumber(execution.getAccountNumber());
        tradeOrderfill.setAveragePrice(execution.getAveragePrice());
        tradeOrderfill.setCumulativeQuantity(execution.getCumulativeQuantity());
        tradeOrderfill.setExecId(execution.getExecId());
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

        _log.debug("orderId: " + orderId + " status: " + status + " filled: " + filled + " remaining: " + remaining
                + " avgFillPrice: " + avgFillPrice + " permId: " + permId + " parentId: " + parentId
                + " lastFillPrice: " + lastFillPrice + " clientId: " + clientId + " whyHeld: " + whyHeld);
    }

    /**
     * Method logTradeOrder.
     *
     * @param order TradeOrder
     */
    public static void logTradeOrder(TradeOrder order) {

        _log.debug("OrderKey: " + +order.getOrderKey() + " ClientId: " + order.getClientId() + " PermId: "
                + order.getPermId() + " Action: " + order.getAction() + " TotalQuantity: " + order.getQuantity()
                + " OrderType: " + order.getOrderType() + " LmtPrice: " + order.getLimitPrice() + " AuxPrice: "
                + order.getAuxPrice() + " Tif: " + order.getTimeInForce() + " OcaGroup: " + order.getOcaGroupName()
                + " OcaType: " + order.getOcaType() + " OrderRef: " + order.getOrderReference() + " Transmit: "
                + order.getTransmit() + " DisplaySize: " + order.getDisplayQuantity() + " TriggerMethod: "
                + order.getTriggerMethod() + " Hidden: " + order.getHidden() + " ParentId: " + order.getParentId()
                + " GoodAfterTime: " + order.getGoodAfterTime() + " GoodTillDate: " + order.getGoodTillTime()
                + " OverridePercentageConstraints: " + order.getOverrideConstraints() + " AllOrNone: "
                + order.getAllOrNothing());
    }

    /**
     * Method logContract.
     *
     * @param contect com.ib.client.Contract
     */
    public static void logContract(Contract contract) {
        _log.debug("Symbol: " + contract.getSymbol() + " Sec Type: " + contract.getSecType() + " Exchange: "
                + contract.getExchange() + " Con Id: " + contract.getIdContractIB() + " Currency: "
                + contract.getCurrency() + " SecIdType: " + contract.getSecIdType() + " Primary Exch: "
                + contract.getPrimaryExchange() + " Local Symbol: " + contract.getLocalSymbol() + " Multiplier: "
                + contract.getPriceMultiplier() + " Expiry: " + contract.getExpiry() + " Category: "
                + contract.getCategory() + " Industry: " + contract.getIndustry() + " LongName: "
                + contract.getLongName());
    }

    /**
     * Method logExecution.
     *
     * @param execution com.ib.client.Execution
     */
    public static void logExecution(TradeOrderfill execution) {
        _log.debug("execDetails OrderId: " + execution.getTradeOrder().getId() + " Exchange: "
                + execution.getExchange() + " Side: " + execution.getSide() + " ExecId: " + execution.getExecId()
                + " Time: " + execution.getTime() + " Qty: " + execution.getQuantity() + " AveragePrice: "
                + execution.getAveragePrice() + " Price: " + execution.getPrice() + " CumulativeQuantity: "
                + execution.getCumulativeQuantity());
    }
}
