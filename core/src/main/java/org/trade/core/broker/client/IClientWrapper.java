package org.trade.core.broker.client;

import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.Tradestrategy;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IClientWrapper {

    void error(Exception e);

    void error(String str);

    void error(int id, int errorCode, String errorMsg);

    void connectionClosed();

    void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId,
                     int parentId, double lastFillPrice, int clientId, String whyHeld);

    void openOrder(int orderId, Contract contract, TradeOrder order, OrderState orderState);

    void nextValidId(int orderId);

    void contractDetails(int reqId, Contract contract);

    void contractDetailsEnd(int reqId);

    void execDetails(int reqId, Contract contract, TradeOrderfill execution);

    void execDetailsEnd(int reqId);

    void historicalData(int reqId, String date, double open, double high, double low, double close, long volume,
                        int tradeCount, int barSize, double WAP, boolean hasGaps);

    void historicalDataComplete(int reqId);

    void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap,
                     int count);

    void onCancelRealtimeBars(Tradestrategy tradestrategy);

    void onCancelBrokerData(Tradestrategy tradestrategy);

    void onCancelBrokerData(Contract contract);

    void onCancelRealtimeBars(Contract contract);
}
