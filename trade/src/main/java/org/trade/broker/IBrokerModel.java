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

import org.trade.broker.client.Broker;
import org.trade.persistent.dao.Contract;
import org.trade.persistent.dao.TradeOrder;
import org.trade.persistent.dao.Tradestrategy;

import java.time.ZonedDateTime;
import java.util.EventListener;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public interface IBrokerModel {

    public final static String _brokerTest = "BrokerTest";

    public final static String _broker = "Broker";

    /**
     * Method addMessageListener.
     *
     * @param listener IBrokerChangeListener
     */
    void addMessageListener(IBrokerChangeListener listener);

    /**
     * Method removeMessageListener.
     *
     * @param listener IBrokerChangeListener
     */
    void removeMessageListener(IBrokerChangeListener listener);

    /**
     * Method hasListener.
     *
     * @param listener EventListener
     * @return boolean
     */
    boolean hasListener(EventListener listener);

    /**
     * Method error.
     *
     * @param id   int
     * @param code int
     * @param msg  String
     */
    void error(int id, int code, String msg);

    /**
     * Method onConnect.
     *
     * @param host     String
     * @param port     Integer
     * @param clientId Integer
     * @throws BrokerModelException
     */
    void onConnect(String host, Integer port, Integer clientId);

    /**
     * Method isConnected.
     *
     * @return boolean
     */
    boolean isConnected();

    /**
     * Method disconnect.
     *
     * @throws BrokerModelException
     */
    void onDisconnect();

    /**
     * Method getNextRequestId.
     *
     * @return Integer
     */
    Integer getNextRequestId();

    /**
     * Method getBackTestBroker.
     *
     * @param idTradestrategy Integer
     * @return Broker
     */
    Broker getBackTestBroker(Integer idTradestrategy);

    /**
     * Method onSubscribeAccountUpdates.
     *
     * @param subscribe     boolean
     * @param accountNumber String
     * @throws BrokerModelException
     */
    void onSubscribeAccountUpdates(boolean subscribe, String accountNumber) throws BrokerModelException;

    /**
     * Method onCancelAccountUpdates.
     *
     * @param accountNumber String
     */
    void onCancelAccountUpdates(String accountNumber);

    /**
     * Method onReqFinancialAccount.
     *
     * @throws BrokerModelException
     */
    void onReqFinancialAccount();

    /**
     * Method onReqReplaceFinancialAccount.
     *
     * @param xml        String
     * @param faDataType int
     * @throws BrokerModelException
     */
    void onReqReplaceFinancialAccount(int faDataType, String xml) throws BrokerModelException;

    /**
     * Method onReqManagedAccount.
     *
     * @throws BrokerModelException
     */
    void onReqManagedAccount() throws BrokerModelException;

    /**
     * Method onReqAllOpenOrders.
     *
     * @throws BrokerModelException
     */
    void onReqAllOpenOrders() throws BrokerModelException;

    /**
     * Method onReqOpenOrders.
     *
     * @throws BrokerModelException
     */
    void onReqOpenOrders() throws BrokerModelException;

    /**
     * Method onBrokerData.
     *
     * @param tradestrategy Tradestrategy
     * @param endDate       ZonedDateTime
     * @throws BrokerModelException
     */
    public void onBrokerData(Tradestrategy tradestrategy, ZonedDateTime endDate) throws BrokerModelException;

    /**
     * Method onReqRealTimeBars.
     *
     * @param contract Contract
     * @param mktData  boolean
     * @throws BrokerModelException
     */
    void onReqRealTimeBars(Contract contract, boolean mktData) throws BrokerModelException;

    /**
     * Method onReqMarketData.
     *
     * @param contract        Contract
     * @param genericTicklist String
     * @param snapshot        boolean
     * @throws BrokerModelException
     */
    void onReqMarketData(Contract contract, String genericTicklist, boolean snapshot) throws BrokerModelException;

    /**
     * Method onReqAllExecutions.
     *
     * @param mktOpenDate ZonedDateTime
     * @throws BrokerModelException
     */
    void onReqAllExecutions(ZonedDateTime mktOpenDate) throws BrokerModelException;

    /**
     * Method onReqExecutions.
     *
     * @param tradestrategy Tradestrategy
     * @param addOrders     boolean
     * @throws BrokerModelException
     * @see IBrokerModel#onReqExecutions(Tradestrategy)
     */
    void onReqExecutions(Tradestrategy tradestrategy, boolean addOrders) throws BrokerModelException;

    /**
     * Method isHistoricalDataRunning.
     *
     * @param contract Contract
     * @return boolean
     */
    boolean isHistoricalDataRunning(Contract contract);

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    boolean isRealtimeBarsRunning(Tradestrategy tradestrategy);

    /**
     * Method isRealtimeBarsRunning.
     *
     * @param contract Contract
     * @return boolean
     */
    boolean isRealtimeBarsRunning(Contract contract);

    /**
     * Method isMarketDataRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    boolean isMarketDataRunning(Tradestrategy tradestrategy);

    /**
     * Method isMarketDataRunning.
     *
     * @param contract Contract
     * @return boolean
     */
    boolean isMarketDataRunning(Contract contract);

    /**
     * Method isHistoricalDataRunning.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     */
    boolean isHistoricalDataRunning(Tradestrategy tradestrategy);

    /**
     * Method isAccountUpdatesRunning.
     *
     * @param accountNumber String
     * @return boolean
     */
    boolean isAccountUpdatesRunning(String accountNumber);

    void onCancelAllRealtimeData();

    /**
     * Method onCancelRealtimeBars.
     *
     * @param contract Contract
     */
    void onCancelRealtimeBars(Contract contract);

    /**
     * Method onCancelRealtimeBars.
     *
     * @param tradestrategy Tradestrategy
     */
    void onCancelRealtimeBars(Tradestrategy tradestrategy);

    /**
     * Method onCancelMarketData.
     *
     * @param contract Contract
     */
    void onCancelMarketData(Contract contract);

    /**
     * Method onCancelMarketData.
     *
     * @param tradestrategy Tradestrategy
     */
    void onCancelMarketData(Tradestrategy tradestrategy);

    /**
     * Method onCancelBrokerData.
     *
     * @param tradestrategy Tradestrategy
     */
    void onCancelBrokerData(Tradestrategy tradestrategy);

    /**
     * Method onCancelBrokerData.
     *
     * @param contract Contract
     */
    void onCancelBrokerData(Contract contract);

    /**
     * Method onCancelContractDetails.
     *
     * @param contract Contract
     */
    void onCancelContractDetails(Contract contract);

    /**
     * Method onContractDetails.
     *
     * @param contract Contract
     * @throws BrokerModelException
     */
    void onContractDetails(Contract contract) throws BrokerModelException;

    /**
     * Method getHistoricalData.
     *
     * @return ConcurrentHashMap<Integer, Tradestrategy>
     */
    ConcurrentHashMap<Integer, Tradestrategy> getHistoricalData();

    /**
     * Method onPlaceOrder.
     *
     * @param contract   Contract
     * @param tradeOrder TradeOrder
     * @return TradeOrder
     * @throws BrokerModelException
     */
    TradeOrder onPlaceOrder(Contract contract, TradeOrder tradeOrder) throws BrokerModelException;

    /**
     * Method onCancelOrder.
     *
     * @param tradeOrder TradeOrder
     * @throws BrokerModelException
     */
    void onCancelOrder(TradeOrder tradeOrder) throws BrokerModelException;

    /**
     * Method isBrokerDataOnly.
     *
     * @return boolean
     */
    boolean isBrokerDataOnly();

    /**
     * Method setBrokerDataOnly.
     *
     * @param brokerDataOnly boolean
     */
    void setBrokerDataOnly(boolean brokerDataOnly);

    /**
     * Method validateBrokerData.
     *
     * @param tradestrategy Tradestrategy
     * @return boolean
     * @throws BrokerModelException
     */

    boolean validateBrokerData(Tradestrategy tradestrategy) throws BrokerModelException;

}
