
package org.trade.core.broker;

import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;

import java.util.EventListener;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The interface that must be supported by classes that wish to receive
 * notification of changes to a dataset.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IBrokerChangeListener extends EventListener {

    /**
     * Receives notification of an brokerManagerModel change event.
     */

    void connectionOpened();

    void connectionClosed(boolean forced);

    /**
     * Method executionDetailsEnd.
     *
     * @param execDetails ConcurrentHashMap<Integer,TradeOrder>
     */
    void executionDetailsEnd(ConcurrentHashMap<Integer, TradeOrder> execDetails);

    /**
     * Method historicalDataComplete.
     *
     * @param tradestrategy Tradestrategy
     */
    void historicalDataComplete(Tradestrategy tradestrategy);

    /**
     * Method managedAccountsUpdated.
     *
     * @param accountNumber String
     */
    void managedAccountsUpdated(String accountNumber);

    /**
     * Method fAAccountsCompleted. Notifies all registered listeners that the
     * brokerManagerModel has received all FA Accounts information.
     */
    void fAAccountsCompleted();

    /**
     * Method updateAccountTime.
     *
     * @param accountNumber String
     */
    void updateAccountTime(String accountNumber);

    /**
     * Method brokerError.
     *
     * @param brokerError BrokerModelException
     */
    void brokerError(BrokerModelException brokerError);

    /**
     * Method tradeOrderFilled.
     *
     * @param tradeOrder TradeOrder
     */
    void tradeOrderFilled(TradeOrder tradeOrder);

    /**
     * Method tradeOrderCancelled.
     *
     * @param tradeOrder TradeOrder
     */
    void tradeOrderCancelled(TradeOrder tradeOrder);

    /**
     * Method tradeOrderStatusChanged.
     *
     * @param tradeOrder TradeOrder
     */
    void tradeOrderStatusChanged(TradeOrder tradeOrder);

    /**
     * Method positionClosed.
     *
     * @param tradePosition TradePosition
     */
    void positionClosed(TradePosition tradePosition);

    /**
     * Method openOrderEnd.
     *
     * @param openOrders ConcurrentHashMap<Integer,TradeOrder>
     */
    void openOrderEnd(ConcurrentHashMap<Integer, TradeOrder> openOrders);

}
