package org.trade.broker;

import com.ib.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.broker.client.Broker;
import org.trade.broker.request.TWSAccountAliasRequest;
import org.trade.broker.request.TWSAllocationRequest;
import org.trade.broker.request.TWSGroupRequest;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.factory.ClassFactory;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.TradingCalendar;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.Percent;
import org.trade.dictionary.valuetype.Currency;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.dictionary.valuetype.OrderType;
import org.trade.dictionary.valuetype.*;
import org.trade.persistent.IPersistentModel;
import org.trade.persistent.dao.Contract;
import org.trade.persistent.dao.*;
import org.trade.strategy.data.CandleSeries;
import org.trade.strategy.data.StrategyData;
import org.trade.strategy.data.candle.CandleItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class TWSBrokerModel extends AbstractBrokerModel implements EWrapper, EReaderSignal {

    /**
     *
     */
    private static final long serialVersionUID = 595280836716405557L;

    private final static Logger _log = LoggerFactory.getLogger(TWSBrokerModel.class);

    // Use getId as key
    private static final ConcurrentHashMap<Integer, Tradestrategy> m_historyDataRequests = new ConcurrentHashMap<Integer, Tradestrategy>();
    private static final ConcurrentHashMap<Integer, Contract> m_realTimeBarsRequests = new ConcurrentHashMap<Integer, Contract>();
    private static final ConcurrentHashMap<Integer, Contract> m_marketDataRequests = new ConcurrentHashMap<Integer, Contract>();
    private static final ConcurrentHashMap<Integer, Contract> m_contractRequests = new ConcurrentHashMap<Integer, Contract>();
    // Use account number as key
    private static final ConcurrentHashMap<String, Account> m_accountRequests = new ConcurrentHashMap<String, Account>();

    // All Use orderKey as key
    private static final ConcurrentHashMap<Integer, TradeOrder> openOrders = new ConcurrentHashMap<Integer, TradeOrder>();
    private static final ConcurrentHashMap<Integer, TradeOrder> tradeOrdersExecutions = new ConcurrentHashMap<Integer, TradeOrder>();
    // Use execId as key
    private static final ConcurrentHashMap<String, Execution> executionDetails = new ConcurrentHashMap<String, Execution>();
    // Use commsReport.m_execId as key
    private static final ConcurrentHashMap<String, CommissionReport> commissionDetails = new ConcurrentHashMap<String, CommissionReport>();

    private EClientSocket m_client = null;
    private IPersistentModel m_tradePersistentModel = null;
    private AtomicInteger reqId = null;
    private AtomicInteger orderKey = null;
    private Integer m_clientId = null;

    private static final int SCALE = 5;
    private static final int minOrderId = 100000;

    private static final String AVAILABLE_FUNDS = "AvailableFunds";
    private static final String ACCOUNTTYPE = "AccountType";
    private static final String BUYING_POWER = "BuyingPower";
    private static final String CASH_BALANCE = "CashBalance";
    private static final String CURRENCY = "Currency";
    private static final String GROSS_POSITION_VALUE = "GrossPositionValue";
    private static final String REALIZED_P_L = "RealizedPnL";
    private static final String UNREALIZED_P_L = "UnrealizedPnL";
    private static final String STOCK_MKT_VALUE = "StockMarketValue";

    /*
     * TWS socket values see config.properties
     *
     * Determines the date format applied to returned bars. Valid values
     * include:
     *
     * 1 - dates applying to bars returned in the format:
     * yyyymmdd{space}{space}hh:mm:dd
     *
     * 2 - dates are returned as a long integer specifying the number of seconds
     * since 1/1/1970 GMT.
     */

    private static Integer backfillDateFormat = 2;
    private static Integer backfillUseRTH = 1;
    private static String backfillWhatToShow;
    private static Integer backfillOffsetDays = 0;
    private static String genericTicklist = "233";
    private static boolean marketUpdateOnClose = false;

    static {
        try {
            backfillUseRTH = ConfigProperties.getPropAsInt("trade.backfill.useRTH");
            backfillWhatToShow = ConfigProperties.getPropAsString("trade.backfill.whatToShow");
            backfillOffsetDays = ConfigProperties.getPropAsInt("trade.backfill.offsetDays");
            genericTicklist = ConfigProperties.getPropAsString("trade.marketdata.genericTicklist");
            marketUpdateOnClose = ConfigProperties.getPropAsBoolean("trade.marketdata.realtime.updateClose");

        } catch (Exception ex) {
            throw new IllegalArgumentException("Error initializing IBrokerModel Msg: " + ex.getMessage());
        }
    }

    public TWSBrokerModel() {
        try {
            m_client = new EClientSocket(this, this);
            m_tradePersistentModel = (IPersistentModel) ClassFactory
                    .getServiceForInterface(IPersistentModel._persistentModel, this);
            reqId = new AtomicInteger((int) (System.currentTimeMillis() / 1000d));

        } catch (Exception ex) {
            throw new IllegalArgumentException("Error initializing IBrokerModel Msg: " + ex.getMessage());
        }
    }

    public boolean isConnected() {
        return m_client.isConnected();
    }


    public ConcurrentHashMap<Integer, Tradestrategy> getHistoricalData() {
        return m_historyDataRequests;
    }


    public void onConnect(String host, Integer port, Integer clientId) {
        this.m_clientId = clientId;
        m_client.eConnect(host, port, clientId);
        openOrders.clear();
    }

    public void onDisconnect() {
        onCancelAllRealtimeData();
        if (m_client.isConnected()) {
            for (String accountNumber : m_accountRequests.keySet()) {
                this.onCancelAccountUpdates(accountNumber);
            }
            m_client.eDisconnect();
        }
        this.fireConnectionClosed(false);
    }

    public void connectionClosed() {
        _log.error("TWS Broker Model connectionClosed ");
        onCancelAllRealtimeData();
        this.fireConnectionClosed(true);
    }

    public Broker getBackTestBroker(Integer idTradestrategy) {
        return null;
    }

    public void onReqFinancialAccount() {
        try {
            if (m_client.isConnected()) {
                m_client.requestFA(EClientSocket.ALIASES);
            } else {
                throw new BrokerModelException(0, 3010, "Not conected Financial Account data cannot be retrieved");
            }
        } catch (Exception ex) {
            error(0, 3295, "Error requesting Financial Account Msg: " + ex.getMessage());
        }
    }

    public void onReqReplaceFinancialAccount(int faDataType, String xml) throws BrokerModelException {
        try {
            if (m_client.isConnected()) {
                m_client.replaceFA(faDataType, xml);
            } else {
                throw new BrokerModelException(0, 3010, "Not conected Financial Account data cannot be replaced");
            }
        } catch (Exception ex) {
            error(0, 3295, "Error replacing Financial Account Msg: " + ex.getMessage());
        }
    }

    public void onReqManagedAccount() throws BrokerModelException {
        // request list of all open orders
        if (m_client.isConnected()) {
            m_client.reqManagedAccts();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    public void onSubscribeAccountUpdates(boolean subscribe, String accountNumber) throws BrokerModelException {
        try {
            Account account = m_tradePersistentModel.findAccountByNumber(accountNumber);
            m_accountRequests.put(accountNumber, account);
            if (m_client.isConnected()) {
                m_client.reqAccountUpdates(subscribe, accountNumber);
            } else {
                throw new BrokerModelException(0, 3010,
                        "Not conected to TWS historical account data cannot be retrieved");
            }

        } catch (Exception ex) {
            error(0, 3290, "Error requesting Account: " + accountNumber + " Msg: " + ex.getMessage());
        }

    }

    public void onReqAllOpenOrders() throws BrokerModelException {
        // request list of all open orders
        if (m_client.isConnected()) {
            openOrders.clear();
            m_client.reqAllOpenOrders();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    public void onReqOpenOrders() throws BrokerModelException {
        // request list of all open orders
        if (m_client.isConnected()) {
            openOrders.clear();
            m_client.reqOpenOrders();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    public void onReqAllExecutions(ZonedDateTime mktOpenDate) throws BrokerModelException {
        try {
            /*
             * Request execution reports based on the supplied filter criteria
             */

            if (m_client.isConnected()) {
                tradeOrdersExecutions.clear();
                commissionDetails.clear();
                executionDetails.clear();
                Integer reqId = this.getNextRequestId();
                m_client.reqExecutions(reqId, TWSBrokerModel.getIBExecutionFilter(m_clientId, mktOpenDate, null, null));
            } else {
                throw new BrokerModelException(0, 3020, "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(0, 3020,
                    "Error request executions for Date: " + mktOpenDate + " Msg: " + ex.getMessage());
        }
    }

    public void onReqExecutions(Tradestrategy tradestrategy, boolean addOrders) throws BrokerModelException {
        try {
            /*
             * Request execution reports based on the supplied filter criteria
             */
            Integer clientId = m_clientId;
            if (m_client.isConnected()) {
                tradeOrdersExecutions.clear();
                commissionDetails.clear();
                executionDetails.clear();
                /*
                 * This will get all orders i.e. those created by this client
                 * and those created by other clients in TWS.
                 */
                if (addOrders)
                    clientId = 0;

                Integer reqId = tradestrategy.getId();
                m_client.reqExecutions(reqId,
                        TWSBrokerModel.getIBExecutionFilter(clientId, tradestrategy.getTradingday().getOpen(),
                                tradestrategy.getContract().getSecType(), tradestrategy.getContract().getSymbol()));
            } else {
                throw new BrokerModelException(tradestrategy.getId(), 3020,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(tradestrategy.getId(), 3020,
                    "Error request executions for symbol: " + tradestrategy.getContract().getSymbol() + " Msg: "
                            + ex.getMessage());
        }
    }


    public void onReqRealTimeBars(Contract contract, boolean mktData) throws BrokerModelException {

        try {
            if (m_client.isConnected()) {

                if (this.isRealtimeBarsRunning(contract)) {
                    throw new BrokerModelException(contract.getId(), 3030,
                            "RealtimeBars request is already in progress for: " + contract.getSymbol()
                                    + " Please wait or cancel.");
                }
                m_realTimeBarsRequests.put(contract.getId(), contract);

                /*
                 * Bar interval is set to 5= 5sec this is the only thing
                 * supported by TWS for live data.
                 */
                ArrayList<TagValue> realTimeBarOptions = new ArrayList<>();
                m_client.reqRealTimeBars(contract.getId(), TWSBrokerModel.getIBContract(contract), 5,
                        backfillWhatToShow, (backfillUseRTH > 0), realTimeBarOptions);

                if (mktData) {
                    onReqMarketData(contract, genericTicklist, false);
                }

            } else {
                throw new BrokerModelException(contract.getId(), 3040,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(contract.getId(), 3050,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public void onReqMarketData(Contract contract, String genericTicklist, boolean snapshot)
            throws BrokerModelException {

        try {
            if (m_client.isConnected()) {
                if (this.isMarketDataRunning(contract)) {
                    throw new BrokerModelException(contract.getId(), 3030,
                            "MarketData request is already in progress for: " + contract.getSymbol()
                                    + " Please wait or cancel.");
                }
                List<TagValue> mktDataOptions = new ArrayList<TagValue>();
                m_marketDataRequests.put(contract.getId(), contract);
                m_client.reqMktData(contract.getId(), TWSBrokerModel.getIBContract(contract), genericTicklist, snapshot,
                        mktDataOptions);

            } else {
                throw new BrokerModelException(contract.getId(), 3040,
                        "Not conected to TWS market data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(contract.getId(), 3050,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public void onContractDetails(Contract contract) throws BrokerModelException {

        try {
            if (m_client.isConnected()) {
                if (!m_contractRequests.containsKey(contract.getId())) {
                    /*
                     * Null the IB Contract Id as these sometimes change. This
                     * will force a get of the IB data via the
                     * Exchange/Symbol/Currency.
                     */
                    contract.setIdContractIB(null);
                    m_contractRequests.put(contract.getId(), contract);
                    TWSBrokerModel.logContract(TWSBrokerModel.getIBContract(contract));
                    m_client.reqContractDetails(contract.getId(), TWSBrokerModel.getIBContract(contract));
                }
            } else {
                throw new BrokerModelException(contract.getId(), 3080,
                        "Not conected to TWS contract data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(contract.getId(), 3090,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public void onBrokerData(Tradestrategy tradestrategy, ZonedDateTime endDate) throws BrokerModelException {

        try {

            if (m_client.isConnected()) {
                if (this.isHistoricalDataRunning(tradestrategy)) {
                    throw new BrokerModelException(tradestrategy.getId(), 3010,
                            "HistoricalData request is already in progress for: "
                                    + tradestrategy.getContract().getSymbol() + " Please wait or cancel.");
                }

                /*
                 * When running data via the TWS API we start the
                 * DatasetContainers internal thread to process candle updates
                 * and all indicator updates. That reduces the delay to the
                 * broker interface thread for messages coming in.
                 */
                if (!tradestrategy.getStrategyData().isRunning())
                    tradestrategy.getStrategyData().execute();

                m_historyDataRequests.put(tradestrategy.getId(), tradestrategy);

                endDate = TradingCalendar.getDateAtTime(TradingCalendar.addTradingDays(endDate, backfillOffsetDays),
                        endDate);

                String endDateTime = TradingCalendar.getFormattedDate(endDate, "yyyyMMdd HH:mm:ss");

                /*
                 * TWS API data has a limit of one calendar year of data. So
                 * apply this limit to the chartDays.
                 */
                Integer chartDays = tradestrategy.getChartDays();
                if (TradingCalendar.getDurationInDays(
                        TradingCalendar.addTradingDays(endDate, (-1 * tradestrategy.getChartDays())),
                        TradingCalendar.getDateTimeNowMarketTimeZone()) > 365) {
                    chartDays = 365;
                }

                _log.info("onBrokerData Req Id: " + tradestrategy.getId() + " Symbol: "
                        + tradestrategy.getContract().getSymbol() + " end Time: " + endDateTime + " Period length: "
                        + ChartDays.newInstance(chartDays).getDisplayName() + " Bar size: "
                        + BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName() + " WhatToShow: "
                        + backfillWhatToShow + " Regular Trading Hrs: " + backfillUseRTH + " Date format: "
                        + backfillDateFormat);
                List<TagValue> chartOptions = new ArrayList<TagValue>();

                m_client.reqHistoricalData(tradestrategy.getId(),
                        TWSBrokerModel.getIBContract(tradestrategy.getContract()), endDateTime,
                        ChartDays.newInstance(chartDays).getDisplayName(),
                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
                        backfillUseRTH, backfillDateFormat, chartOptions);

            } else {
                throw new BrokerModelException(tradestrategy.getId(), 3100,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(tradestrategy.getId(), 3110, "Error broker data Symbol: "
                    + tradestrategy.getContract().getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public boolean isAccountUpdatesRunning(String accountNumber) {
        if (m_accountRequests.containsKey(accountNumber)) {
            return true;
        }
        return false;
    }

    public boolean isHistoricalDataRunning(Contract contract) {
        for (Tradestrategy item : m_historyDataRequests.values()) {
            if (contract.equals(item.getContract())) {
                return true;
            }
        }
        return false;
    }

    public boolean isHistoricalDataRunning(Tradestrategy tradestrategy) {
        if (m_historyDataRequests.containsKey(tradestrategy.getId())) {
            return true;
        }
        return false;
    }

    public boolean isRealtimeBarsRunning(Contract contract) {
        if (m_client.isConnected()) {
            if (m_realTimeBarsRequests.containsKey(contract.getId())) {
                return true;
            }
        }
        return false;
    }

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

    public boolean isMarketDataRunning(Contract contract) {
        if (m_client.isConnected()) {
            if (m_marketDataRequests.containsKey(contract.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMarketDataRunning(Tradestrategy tradestrategy) {
        if (m_marketDataRequests.containsKey(tradestrategy.getContract().getId())) {
            Contract contract = m_marketDataRequests.get(tradestrategy.getContract().getId());
            for (Tradestrategy item : contract.getTradestrategies()) {
                if (item.equals(tradestrategy)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onCancelAllRealtimeData() {

        if (m_client.isConnected()) {
            for (Tradestrategy tradestrategy : m_historyDataRequests.values()) {
                this.onCancelBrokerData(tradestrategy);
            }
            for (Contract contract : m_realTimeBarsRequests.values()) {
                this.onCancelRealtimeBars(contract);
            }
            for (Contract contract : m_marketDataRequests.values()) {
                this.onCancelMarketData(contract);
            }
            for (Contract contract : m_contractRequests.values()) {
                this.onCancelContractDetails(contract);
            }
        }
        m_contractRequests.clear();
        m_historyDataRequests.clear();
        m_realTimeBarsRequests.clear();
        m_marketDataRequests.clear();

    }

    public void onCancelAccountUpdates(String accountNumber) {
        synchronized (m_accountRequests) {
            if (m_accountRequests.containsKey(accountNumber)) {
                if (m_client.isConnected()) {
                    m_client.reqAccountUpdates(false, accountNumber);
                }
                m_accountRequests.remove(accountNumber);
            }
        }
    }

    public void onCancelContractDetails(Contract contract) {
        if (m_client.isConnected()) {
            if (m_contractRequests.contains(contract.getId()))
                synchronized (m_contractRequests) {
                    m_contractRequests.remove(contract.getId());
                }
        }
    }

    public void onCancelBrokerData(Tradestrategy tradestrategy) {

        if (m_historyDataRequests.containsKey(tradestrategy.getId())) {
            if (m_client.isConnected())
                m_client.cancelHistoricalData(tradestrategy.getId());
            synchronized (m_historyDataRequests) {
                m_historyDataRequests.remove(tradestrategy.getId());
                m_historyDataRequests.notify();
            }
        }
    }

    public void onCancelBrokerData(Contract contract) {
        for (Tradestrategy tradestrategy : m_historyDataRequests.values()) {
            if (contract.equals(tradestrategy.getContract())) {
                if (m_client.isConnected())
                    m_client.cancelHistoricalData(tradestrategy.getId());
                synchronized (m_historyDataRequests) {
                    m_historyDataRequests.remove(tradestrategy.getId());
                    m_historyDataRequests.notify();
                }
            }
        }
    }

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
                onCancelMarketData(contract);
            }
        }
    }

    public void onCancelRealtimeBars(Contract contract) {

        if (m_realTimeBarsRequests.containsKey(contract.getId())) {
            if (m_client.isConnected())
                m_client.cancelRealTimeBars(contract.getId());
            synchronized (m_realTimeBarsRequests) {
                m_realTimeBarsRequests.remove(contract.getId());
            }
        }
    }

    public void onCancelMarketData(Tradestrategy tradestrategy) {
        if (m_marketDataRequests.containsKey(tradestrategy.getContract().getId())) {
            Contract contract = m_marketDataRequests.get(tradestrategy.getContract().getId());
            for (Tradestrategy item : contract.getTradestrategies()) {
                if (item.equals(tradestrategy)) {
                    contract.removeTradestrategy(tradestrategy);
                    break;
                }
            }
            if (contract.getTradestrategies().isEmpty()) {
                onCancelMarketData(contract);
            }
        }
    }

    public void onCancelMarketData(Contract contract) {

        if (m_marketDataRequests.containsKey(contract.getId())) {
            if (m_client.isConnected())
                m_client.cancelMktData(contract.getId());
            synchronized (m_marketDataRequests) {
                m_marketDataRequests.remove(contract.getId());
            }
        }
    }

    public TradeOrder onPlaceOrder(Contract contract, TradeOrder tradeOrder) throws BrokerModelException {
        try {
            if (m_client.isConnected()) {
                synchronized (tradeOrder) {
                    if (null == tradeOrder.getOrderKey()) {
                        tradeOrder.setOrderKey(orderKey.getAndIncrement());
                    }
                    if (null == tradeOrder.getClientId()) {
                        tradeOrder.setClientId(this.m_clientId);
                    }
                    tradeOrder = m_tradePersistentModel.persistTradeOrder(tradeOrder);

                    _log.debug("Order Placed Key: " + tradeOrder.getOrderKey());
                    com.ib.client.Contract IBContract = TWSBrokerModel.getIBContract(contract);
                    com.ib.client.Order IBOrder = TWSBrokerModel.getIBOrder(tradeOrder);

                    // Log to debug comment out for performance.
                    logContract(IBContract);
                    logTradeOrder(IBOrder);

                    m_client.placeOrder(tradeOrder.getOrderKey(), IBContract, IBOrder);
                    return tradeOrder;
                }

            } else {
                throw new BrokerModelException(tradeOrder.getOrderKey(), 3120,
                        "Client not conected to TWS order cannot be placed");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(tradeOrder.getOrderKey(), 3130,
                    "Could not save or place TradeOrder: " + tradeOrder.getOrderKey() + " Msg: " + ex.getMessage());
        }
    }

    public void onCancelOrder(TradeOrder tradeOrder) throws BrokerModelException {
        if (m_client.isConnected()) {
            if (null != tradeOrder.getOrderKey()) {
                m_client.cancelOrder(tradeOrder.getOrderKey());
            }
        } else {
            throw new BrokerModelException(tradeOrder.getOrderKey(), 3140,
                    "Not conected to TWS order cannot be placed");
        }
    }

    public void execDetails(int reqId, com.ib.client.Contract contractIB, Execution execution) {
        try {
            TWSBrokerModel.logExecution(execution);

            TradeOrder transientInstance = m_tradePersistentModel
                    .findTradeOrderByKey(Math.abs(execution.orderId()));
            if (null == transientInstance) {
                /*
                 * If the executionDetails is null and the order does not exist
                 * then we have made a request for order executions with a
                 * different clientId than the one which created this order.
                 */
                if (null == m_tradePersistentModel.findTradeOrderfillByExecId(execution.execId())) {
                    executionDetails.put(execution.execId(), execution);
                }
                return;
            }

            /*
             * We already have this order fill.
             */

            if (transientInstance.existTradeOrderfill(execution.execId()))
                return;

            TradeOrderfill tradeOrderfill = new TradeOrderfill();
            TWSBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);
            tradeOrderfill.setTradeOrder(transientInstance);
            transientInstance.addTradeOrderfill(tradeOrderfill);
            transientInstance.setAverageFilledPrice(tradeOrderfill.getAveragePrice());
            transientInstance.setFilledQuantity(tradeOrderfill.getCumulativeQuantity());
            transientInstance.setFilledDate(tradeOrderfill.getTime());
            boolean isFilled = transientInstance.getIsFilled();
            transientInstance = m_tradePersistentModel.persistTradeOrderfill(transientInstance);
            // Let the controller know an order was filled
            if (transientInstance.getIsFilled() && !isFilled)
                this.fireTradeOrderFilled(transientInstance);

            tradeOrdersExecutions.put(transientInstance.getOrderKey(), transientInstance);
            _log.error("execDetails tradeOrdersExecutions reqId: " + reqId);

        } catch (Exception ex) {
            error(reqId, 3160, "Errors saving execution: " + ex.getMessage());
        }
    }

    public void execDetailsEnd(int reqId) {

        try {

            for (Integer key : tradeOrdersExecutions.keySet()) {
                TradeOrder tradeorder = tradeOrdersExecutions.get(key);
                if (tradeorder.getIsFilled()) {
                    if (tradeorder.hasTradePosition() && !tradeorder.getTradePosition().isOpen()) {
                        // Let the controller know a position was closed
                        this.firePositionClosed(tradeorder.getTradePosition());
                    }
                }
            }
            if (!executionDetails.isEmpty()) {

                /*
                 * If the tradestrategy exists for this request then we must
                 * create the traderOrders and tradeOrderfills that have been
                 * request and that do not already exist. Note executionDetails
                 * only contains executions for tradeOrders that do not exist.
                 */

                if (m_tradePersistentModel.existTradestrategyById(reqId)) {

                    Tradestrategy tradestrategy = m_tradePersistentModel.findTradestrategyById(reqId);
                    /*
                     * Internal created order have Integer.MAX_VALUE or are
                     * negative as their value, so change the m_orderId to
                     * nextOrderKey.
                     */
                    int nextOrderKey = orderKey.getAndIncrement();
                    for (String key : executionDetails.keySet()) {
                        Execution execution = executionDetails.get(key);
                        if (execution.orderId() == Integer.MAX_VALUE || execution.orderId() < 0) {
                            execution.orderId(nextOrderKey);
                        } else {
                            continue;
                        }
                        // Multiple executions for the same order.
                        for (String key1 : executionDetails.keySet()) {
                            Execution execution1 = executionDetails.get(key1);
                            if (execution1.permId() == execution.permId()) {
                                execution1.orderId(nextOrderKey);
                            }
                        }
                        nextOrderKey = orderKey.getAndIncrement();
                    }

                    /*
                     * Create the tradeOrder for these executions.
                     */
                    ConcurrentHashMap<Integer, TradeOrder> tradeOrders = new ConcurrentHashMap<Integer, TradeOrder>();
                    for (String key : executionDetails.keySet()) {
                        Execution execution = executionDetails.get(key);

                        if (tradeOrders.containsKey(execution.orderId())) {
                            continue;
                        }
                        TradeOrderfill tradeOrderfill = new TradeOrderfill();
                        TWSBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);

                        String action = Action.SELL;
                        if (Side.BOT.equals(execution.side())) {
                            action = Action.BUY;
                        }

                        Integer quantity = tradeOrderfill.getQuantity();
                        TradeOrder tradeOrder = new TradeOrder(tradestrategy, action, tradeOrderfill.getTime(),
                                OrderType.MKT, quantity, null, null, OverrideConstraints.YES, TimeInForce.DAY,
                                TriggerMethod.DEFAULT);
                        tradeOrder.setClientId(execution.clientId());
                        tradeOrder.setPermId(execution.permId());
                        tradeOrder.setOrderKey(execution.orderId());
                        for (String key1 : executionDetails.keySet()) {
                            Execution execution1 = executionDetails.get(key1);
                            if (execution1.permId() == execution.permId()
                                    && !execution1.execId().equals(execution.execId())) {
                                TradeOrderfill tradeOrderfill1 = new TradeOrderfill();
                                TWSBrokerModel.populateTradeOrderfill(execution1, tradeOrderfill1);
                                quantity = quantity + tradeOrderfill1.getQuantity();
                                /*
                                 * Make sure the create date for the order is
                                 * the earliest time.
                                 */
                                if (tradeOrder.getCreateDate().isAfter(tradeOrderfill1.getTime())) {
                                    tradeOrder.setCreateDate(tradeOrderfill1.getTime());
                                }
                            }
                        }
                        tradeOrder.setQuantity(quantity);
                        tradeOrders.put(tradeOrder.getOrderKey(), tradeOrder);
                    }

                    List<TradeOrder> orders = new ArrayList<TradeOrder>();
                    for (Integer orderKey : tradeOrders.keySet()) {
                        TradeOrder tradeOrder = tradeOrders.get(orderKey);
                        orders.add(tradeOrder);
                    }
                    Collections.sort(orders, TradeOrder.CREATE_ORDER);

                    for (TradeOrder tradeOrder : orders) {
                        // tradeOrder =
                        // m_tradePersistentModel.persistTradeOrder(tradeOrder);
                        double totalComms = 0;
                        for (String key : executionDetails.keySet()) {
                            Execution execution = executionDetails.get(key);
                            if (tradeOrder.getPermId().equals(execution.permId())) {
                                TradeOrderfill tradeOrderfill = new TradeOrderfill();
                                TWSBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);
                                /*
                                 * Commissions are sent through via the
                                 * commissionReport call. This happens when an
                                 * order is executed or a call to
                                 * OnReqExecutions.
                                 */
                                CommissionReport comms = commissionDetails.get(key);

                                if (null != comms) {
                                    totalComms = totalComms + comms.m_commission;
                                    tradeOrderfill.setCommission(new BigDecimal(comms.m_commission));
                                }
                                tradeOrderfill.setTradeOrder(tradeOrder);
                                tradeOrder.addTradeOrderfill(tradeOrderfill);
                            }
                        }
                        tradeOrder.setCommission(new BigDecimal(totalComms));
                        tradeOrder = m_tradePersistentModel.persistTradeOrderfill(tradeOrder);
                        TradeOrder transientInstance = m_tradePersistentModel
                                .findTradeOrderByKey(tradeOrder.getOrderKey());
                        // Let the controller know an order was filled
                        if (tradeOrder.getIsFilled()) {
                            this.fireTradeOrderFilled(transientInstance);
                        }
                    }
                }
            }
            /*
             * Let the controller know there are execution details.
             */
            this.fireExecutionDetailsEnd(tradeOrdersExecutions);
        } catch (Exception ex) {
            error(reqId, 3330, "Error adding new open orders: " + ex.getMessage());
        }
    }

    public void openOrder(int orderId, com.ib.client.Contract contractIB, com.ib.client.Order order,
                          OrderState orderState) {
        try {

            TWSBrokerModel.logOrderState(orderState);
            TWSBrokerModel.logTradeOrder(order);

            TradeOrder transientInstance = m_tradePersistentModel.findTradeOrderByKey(new Integer(order.orderId()));

            if (null == transientInstance) {
                error(orderId, 3170,
                        "Warning Order not found for Order Key: " + order.orderId() + " make sure Client ID: "
                                + this.m_clientId + " is not the master in TWS. On openOrder update.");
                transientInstance = new TradeOrder();
                transientInstance.setOrderKey(order.orderId());
                transientInstance.setCreateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                TWSBrokerModel.updateTradeOrder(order, orderState, transientInstance);
                openOrders.put(transientInstance.getOrderKey(), transientInstance);
                return;
            }

            /*
             * Check to see if anything has changed as this method gets fired
             * twice on order fills.
             */

            if (TWSBrokerModel.updateTradeOrder(order, orderState, transientInstance)) {

                if (OrderStatus.FILLED.equals(transientInstance.getStatus())) {
                    _log.debug("Open order filled Order Key:" + transientInstance.getOrderKey());
                    transientInstance = m_tradePersistentModel.persistTradeOrder(transientInstance);

                    if (transientInstance.hasTradePosition() && !transientInstance.getTradePosition().isOpen()) {
                        // Let the controller know a position was closed
                        this.firePositionClosed(transientInstance.getTradePosition());
                    }
                } else {
                    _log.debug("Open order state changed. Status:" + orderState.status());
                    transientInstance = m_tradePersistentModel.persistTradeOrder(transientInstance);
                    if (OrderStatus.CANCELLED.equals(transientInstance.getStatus())) {
                        // Let the controller know a position was closed
                        this.fireTradeOrderCancelled(transientInstance);
                    } else {
                        this.fireTradeOrderStatusChanged(transientInstance);
                    }
                }
            }
            openOrders.put(transientInstance.getOrderKey(), transientInstance);
        } catch (Exception ex) {
            error(orderId, 3180, "Errors updating open order: " + ex.getMessage());
        }
    }

    public void openOrderEnd() {
        _log.debug("openOrderEnd");
        // Let the controller know there are open orders
        for (TradeOrder openOrder : openOrders.values()) {
            _log.debug("openOrderEnd Open Order Key: " + openOrder.getOrderKey() + " Order status: "
                    + openOrder.getStatus());
        }
        this.fireOpenOrderEnd(openOrders);
    }

    public void orderStatus(int orderId, String status, double filled, double remaining,
                            double avgFillPrice, int permId, int parentId, double lastFillPrice,
                            int clientId, String whyHeld) {

        try {
            TradeOrder transientInstance = m_tradePersistentModel.findTradeOrderByKey(orderId);
            if (null == transientInstance) {
                error(orderId, 3170, "Warning Order not found for Order Key: " + orderId + " make sure Client ID: "
                        + this.m_clientId + " is not the master in TWS. On orderStatus update.");
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
            if (CoreUtils.nullSafeComparator((int) filled, transientInstance.getFilledQuantity()) == 1) {
                if (filled > 0) {
                    transientInstance.setAverageFilledPrice(new BigDecimal(avgFillPrice));
                    transientInstance.setFilledQuantity((int) filled);
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
                transientInstance = m_tradePersistentModel.persistTradeOrder(transientInstance);

                if (OrderStatus.CANCELLED.equals(transientInstance.getStatus())) {
                    // Let the controller know a position was closed
                    this.fireTradeOrderCancelled(transientInstance);
                } else {
                    this.fireTradeOrderStatusChanged(transientInstance);
                    // Let the controller know an order was filled
                    if (transientInstance.getIsFilled() && !isFilled)
                        this.fireTradeOrderFilled(transientInstance);
                }
            }
        } catch (Exception ex) {
            error(orderId, 3200, "Errors updating open order status: " + ex.getMessage());
        }
    }

    public void error(Exception ex) {
        _log.error("IBrokerModel error msg: " + ex.getMessage());
        // this.fireBrokerError(new BrokerManagerModelException(ex));
    }

    public void error(String msg) {
        _log.error("IBrokerModel error str: " + msg);
        // this.fireBrokerError(new BrokerManagerModelException(str));
    }

    public void error(int id, int code, String msg) {

        String symbol = "N/A";
        BrokerModelException brokerModelException = null;
        if (m_contractRequests.containsKey(id)) {
            symbol = m_contractRequests.get(id).getSymbol();
        }
        if (m_historyDataRequests.containsKey(id)) {
            Tradestrategy tradestrategy = m_historyDataRequests.get(id);
            symbol = tradestrategy.getContract().getSymbol();
            if (code == 162) {
                symbol = tradestrategy.getContract().getSymbol() + " pacing violation Tradingday: "
                        + tradestrategy.getTradingday().getOpen() + " BarSize: " + tradestrategy.getBarSize()
                        + " ChartDays: " + tradestrategy.getChartDays() + "  \n"
                        + "The following conditions can cause a pacing violation: \n"
                        + "1/ Making identical historical data requests within 15 seconds. \n"
                        + "2/ Making six or more historical data requests for the same Contract, Exchange and Tick Type within two seconds. \n"
                        + "3/ Making more than 60 historical data requests in any ten-minute period.  \n";
            }
            synchronized (m_historyDataRequests) {
                m_historyDataRequests.remove(id);
                m_historyDataRequests.notify();
            }
        }
        if (m_realTimeBarsRequests.containsKey(id)) {
            symbol = m_realTimeBarsRequests.get(id).getSymbol();
        }
        if (m_marketDataRequests.containsKey(id)) {
            symbol = m_marketDataRequests.get(id).getSymbol();
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
        String errorMsg = "Req/Order Id: " + id + " Code: " + code + " Symbol: " + symbol + " Msg: " + msg;

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
            if (m_marketDataRequests.containsKey(id)) {
                synchronized (m_marketDataRequests) {
                    m_marketDataRequests.remove(id);
                }
            }

            _log.error(errorMsg);
            brokerModelException = new BrokerModelException(1, code, errorMsg);

        }
        this.fireBrokerError(brokerModelException);

        /*
         * If onConnect() fails error 502 will be fired. This needs to tell the
         * main controller that we could not connect and so return the app to
         * test mode.
         */
        if (502 == code) {
            this.fireConnectionClosed(false);
        }
    }

    public void tickPrice(int reqId, int field, double value, int canAutoExecute) {

        try {

            BigDecimal price = (new BigDecimal(value)).setScale(SCALE, RoundingMode.HALF_EVEN);
            synchronized (price) {
                // _log.warn("tickPrice Field: " + field + " value :" + value
                // + " time: " + System.currentTimeMillis());
                if (!m_marketDataRequests.containsKey(reqId))
                    return;
                Contract contract = m_marketDataRequests.get(reqId);

                /*
                 * Make sure the lastPrice is between the current Bid/Ask as
                 * prints can come in late in T/S i.e. bad ticks that are
                 * outside the current Bid/Ask.
                 */

                for (Tradestrategy tradestrategy : contract.getTradestrategies()) {
                    Contract seriesContract = tradestrategy.getStrategyData().getBaseCandleSeries().getContract();

                    switch (TickType.get(field)) {
                        case ASK: {
                            seriesContract.setLastAskPrice(price);
                            break;
                        }
                        case BID: {
                            seriesContract.setLastBidPrice(price);
                            break;
                        }
                        case LAST: {
                            seriesContract.setLastPrice(price);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            error(reqId, 3210, ex.getMessage());
        }
    }

    public synchronized void tickSize(int reqId, int field, int value) {
        try {
            switch (TickType.get(field)) {
                case VOLUME: {

                    if (m_realTimeBarsRequests.containsKey(reqId)) {
                        Contract contract = m_realTimeBarsRequests.get(reqId);

                        for (Tradestrategy tradestrategy : contract
                                .getTradestrategies()) {
                            StrategyData datasetContainer = tradestrategy
                                    .getStrategyData();
                            synchronized (datasetContainer) {
                                if (datasetContainer.getBaseCandleSeries()
                                        .getItemCount() > 0) {
                                    CandleItem candle = (CandleItem) datasetContainer
                                            .getBaseCandleSeries().getDataItem(
                                                    datasetContainer
                                                            .getBaseCandleSeries()
                                                            .getItemCount() - 1);
                                    candle.setVolume(value * 100);
                                    candle.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                                    datasetContainer.getBaseCandleSeries()
                                            .fireSeriesChanged();
                                    _log.info("TickSize Symbol: "
                                            + tradestrategy.getContract()
                                            .getSymbol() + " "
                                            + TickType.getField(field) + " : "
                                            + (value * 100));
                                }
                            }
                        }
                    }

                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception ex) {
            error(reqId, 3210, ex.getMessage());
        }
    }

    public void tickString(int reqId, int field, String value) {

        try {

            /*
             * 48 = RTVolume String = last trade price;last trade size;last
             * trade time;total volume;vwap;single trade flag
             */

            // _log.info("tickString reqId: " + reqId + " field: " + field
            // + " value: " + value);

            synchronized (value) {

                if (!m_marketDataRequests.containsKey(new Integer(reqId)))
                    return;

                switch (TickType.get(field)) {
                    case RT_VOLUME: {
                        /*
                         * If there is no price ignore this value.
                         */
                        if (value.startsWith(";"))
                            return;

                        StringTokenizer st = new StringTokenizer(value, ";");
                        int tokenNumber = 0;
                        BigDecimal price = new BigDecimal(0);
                        ZonedDateTime time = null;
                        while (st.hasMoreTokens()) {
                            tokenNumber++;
                            String token = st.nextToken();
                            switch (tokenNumber) {
                                case 1: {
                                    price = (new BigDecimal(Double.parseDouble(token))).setScale(SCALE,
                                            RoundingMode.HALF_EVEN);
                                    break;
                                }
                                case 2: {
                                    _log.debug("TickString Trade Size: " + Integer.parseInt(token));
                                    break;
                                }
                                case 3: {
                                    time = TradingCalendar.getZonedDateTimeFromMilli(Long.parseLong(token));
                                    break;
                                }
                                case 4: {
                                    _log.debug("TickString Total Volume: " + Integer.parseInt(token) * 100);
                                    break;
                                }
                                case 5: {
                                    _log.debug("TickString Total Vwap: " + token);
                                    break;
                                }
                                case 6: {
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                        }

                        if (price.doubleValue() > 0) {

                            Contract contract = m_marketDataRequests.get(reqId);
                            // _log.warn("TickString ReqId: " + reqId + " Field: "
                            // + field + " String: " + value);
                            for (Tradestrategy tradestrategy : contract.getTradestrategies()) {

                                Contract seriesContract = tradestrategy.getStrategyData().getBaseCandleSeries()
                                        .getContract();
                                int index = tradestrategy.getStrategyData().getBaseCandleSeries().indexOf(time);
                                if (index < 0)
                                    return;

                                CandleItem candleItem = (CandleItem) tradestrategy.getStrategyData().getBaseCandleSeries()
                                        .getDataItem(index);
                                if (seriesContract.getLastAskPrice().doubleValue() > 0
                                        && seriesContract.getLastBidPrice().doubleValue() > 0
                                        && (price.doubleValue() <= seriesContract.getLastAskPrice().doubleValue()
                                        && price.doubleValue() >= seriesContract.getLastBidPrice().doubleValue())) {

                                    if (marketUpdateOnClose && (price.doubleValue() != candleItem.getClose())) {

                                        candleItem.setClose(price.doubleValue());
                                        candleItem.setLastUpdateDate(time);
                                        /*
                                         * Note if you want you can fire the series
                                         * change here this will fire runStrategy.
                                         * Could cause problems if the method is not
                                         * synchronized in the strategy when the
                                         * stock is fast running.
                                         */
                                        tradestrategy.getStrategyData().getBaseCandleSeries().fireSeriesChanged();
                                        /*
                                         * This can be used to update the charts.
                                         * NOTE not recommended for performance
                                         * reasons chart events are slow to update..
                                         */
                                        // tradestrategy.getStrategyData()
                                        // .getCandleDataset().getSeries(0)
                                        // .fireSeriesChanged();
                                        // _log.info("TickString Symbol: "
                                        // + seriesContract.getSymbol()
                                        // + " Trade Time: " + time
                                        // + " Price: " + price + " Bid: "
                                        // + seriesContract.getLastBidPrice()
                                        // + " Ask: "
                                        // + seriesContract.getLastAskPrice());
                                    } else {
                                        if (price.doubleValue() > candleItem.getHigh()
                                                || price.doubleValue() < candleItem.getLow()) {
                                            candleItem.setClose(price.doubleValue());
                                            candleItem.setLastUpdateDate(time);
                                            /*
                                             * Note if you want you can fire the
                                             * series change here this will fire
                                             * runStrategy. Could cause problems if
                                             * the method is not synchronized in the
                                             * strategy when the stock is fast
                                             * running.
                                             */
                                            tradestrategy.getStrategyData().getBaseCandleSeries().fireSeriesChanged();
                                            /*
                                             * This can be used to update the
                                             * charts. NOTE not recommended for
                                             * performance reasons chart events are
                                             * slow to update..
                                             */
                                            // tradestrategy.getStrategyData()
                                            // .getCandleDataset().getSeries(0)
                                            // .fireSeriesChanged();
                                            //
                                            // _log.info("TickString Symbol: "
                                            // + seriesContract.getSymbol()
                                            // + " Trade Time: " + time
                                            // + " Price: " + price + " Bid: "
                                            // + seriesContract.getLastBidPrice()
                                            // + " Ask: "
                                            // + seriesContract.getLastAskPrice());
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            error(reqId, 3210, ex.getMessage());
        }

    }

    public void tickOptionComputation(int reqId, int field, double impliedVol, double delta, double optPrice,
                                      double pvDividend, double gamma, double vega, double theta, double undPrice) {
        _log.debug("tickOptionComputation:" + reqId);
    }

    public void tickGeneric(int reqId, int tickType, double value) {
        _log.debug("tickGeneric: " + reqId + " tickType: " + tickType + " tickValue: " + value);
    }

    public void tickEFP(int reqId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture,
                        int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {
        _log.debug("tickEFP:" + reqId);
    }

    public void updatePortfolio(com.ib.client.Contract contract, int position, double marketPrice, double marketValue,
                                double averageCost, double unrealizedPNL, double realizedPNL, String accountNumber) {
        _log.debug("updatePortfolio Account#: " + accountNumber + " contract:" + contract.symbol() + " position:"
                + position + " marketPrice:" + marketPrice + " marketValue:" + marketValue + " averageCost:"
                + averageCost + " unrealizedPNL:" + unrealizedPNL + " realizedPNL:" + realizedPNL);
    }

    public void updateAccountValue(String key, String value, String currency, String accountNumber) {

        synchronized (key) {

            _log.debug("updateAccountValue Account#: " + accountNumber + " Key:" + key + " Value:" + value
                    + " Currency:" + currency);
            if (m_accountRequests.containsKey(accountNumber)) {
                Account account = m_accountRequests.get(accountNumber);
                if (key.equals(TWSBrokerModel.ACCOUNTTYPE)) {
                    account.setAccountType(value);
                    account.setDirty(true);
                }
                if (account.getCurrency().equals(currency)) {
                    if (key.equals(TWSBrokerModel.AVAILABLE_FUNDS)) {
                        account.setAvailableFunds(new BigDecimal(value));
                        account.setDirty(true);
                    }
                    if (key.equals(TWSBrokerModel.BUYING_POWER)) {
                        account.setBuyingPower(new BigDecimal(value));
                        account.setDirty(true);
                    }
                    if (key.equals(TWSBrokerModel.CASH_BALANCE)) {
                        account.setCashBalance(new BigDecimal(value));
                        account.setDirty(true);
                    }
                    if (key.equals(TWSBrokerModel.CURRENCY)) {
                        account.setCurrency(value);
                        account.setDirty(true);
                    }
                    if (key.equals(TWSBrokerModel.GROSS_POSITION_VALUE) || key.equals(TWSBrokerModel.STOCK_MKT_VALUE)) {
                        account.setGrossPositionValue(new BigDecimal(value));
                        account.setDirty(true);
                    }
                    if (key.equals(TWSBrokerModel.REALIZED_P_L)) {
                        account.setRealizedPnL(new BigDecimal(value));
                        account.setDirty(true);
                    }
                    if (key.equals(TWSBrokerModel.UNREALIZED_P_L)) {
                        account.setUnrealizedPnL(new BigDecimal(value));
                        account.setDirty(true);
                    }
                }
            }
        }
    }

    public void updateAccountTime(String timeStamp) {

        try {
            _log.debug("updateAccountTime:" + timeStamp);
            for (String accountNumber : m_accountRequests.keySet()) {
                Account account = m_accountRequests.get(accountNumber);
                synchronized (account) {
                    /*
                     * Don't use the incoming time stamp as this does not show
                     * seconds just HH:mm format.
                     */
                    if (account.isDirty()) {
                        account.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                        account = m_tradePersistentModel.persistAspect(account, true);
                        m_accountRequests.replace(accountNumber, account);
                        this.fireUpdateAccountTime(accountNumber);
                    }
                }
            }
        } catch (Exception ex) {
            error(0, 3310, "Errors updating Trade Account: " + ex.getMessage());
        }
    }

    public void accountDownloadEnd(String accountNumber) {
        _log.debug("accountDownloadEnd: " + accountNumber);
    }

    public Integer getNextRequestId() {
        return new Integer(reqId.incrementAndGet());
    }

    public void nextValidId(int orderId) {
        try {
            _log.debug("nextValidId: " + orderId);
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

    public void contractDetails(int reqId, ContractDetails contractDetails) {
        try {
            if (m_contractRequests.containsKey(reqId)) {
                Contract contract = m_contractRequests.get(reqId);
                TWSBrokerModel.logContractDetails(contractDetails);
                if (TWSBrokerModel.populateContract(contractDetails, contract)) {
                    m_tradePersistentModel.persistContract(contract);
                }
            } else {
                error(reqId, 3220, "Contract details not found for reqId: " + reqId + " Symbol: "
                        + contractDetails.contract().symbol());
            }
        } catch (Exception ex) {
            error(reqId, 3230, ex.getMessage());
        }
    }

    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        _log.debug("bondContractDetails:" + reqId);
    }

    public void contractDetailsEnd(int reqId) {
        if (m_contractRequests.containsKey(reqId)) {
            synchronized (m_contractRequests) {
                m_contractRequests.remove(reqId);
            }
        }
    }

    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {
        _log.debug("updateMktDepth: " + tickerId + " " + position + " " + operation + " " + side + " " + price + " "
                + size);
    }

    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price,
                                 int size) {
        _log.debug("updateMktDepthL2: " + tickerId + " " + position + " " + operation + " " + side + " " + price + " "
                + size);

    }

    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        _log.debug("updateNewsBulletin: " + msgId + " " + msgType + " " + message + " " + origExchange);

    }

    public void managedAccounts(String accountNumbers) {
        try {
            _log.debug("Managed accounts: " + accountNumbers);
            this.fireManagedAccountsUpdated(accountNumbers);

        } catch (Exception ex) {
            error(0, 3315, "Error updating Managed Accounts: " + ex.getMessage());
        } finally {
            /*
             * Call FA Accounts to see if we are Financial Advisor.
             */
            onReqFinancialAccount();
        }
    }

    public void receiveFA(int faDataType, String xml) {
        ByteArrayInputStream inputSource = null;
        try {
            inputSource = new ByteArrayInputStream(xml.getBytes("utf-8"));
            switch (faDataType) {
                case EClientSocket.ALIASES: {
                    _log.debug("Aliases: /n" + xml);
                    final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
                    final Aspects aspects = (Aspects) request.fromXML(inputSource);
                    for (Aspect aspect : aspects.getAspect()) {
                        Account item = (Account) aspect;
                        Account account = m_tradePersistentModel.findAccountByNumber(item.getAccountNumber());
                        if (null == account) {
                            account = new Account(item.getAccountNumber(), item.getAccountNumber(), Currency.USD,
                                    AccountType.INDIVIDUAL);
                        }
                        account.setAlias(item.getAlias());
                        account.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                        m_tradePersistentModel.persistAspect(account);
                    }
                    m_client.requestFA(EClientSocket.GROUPS);
                    break;
                }
                case EClientSocket.PROFILES: {
                    _log.debug("Profiles: /n" + xml);
                    final TWSAllocationRequest request = new TWSAllocationRequest();
                    final Aspects aspects = (Aspects) request.fromXML(inputSource);
                    for (Aspect aspect : aspects.getAspect()) {
                        m_tradePersistentModel.persistPortfolio((Portfolio) aspect);
                    }
                    this.fireFAAccountsCompleted();
                    break;
                }
                case EClientSocket.GROUPS: {
                    _log.debug("Groups: /n" + xml);
                    final TWSGroupRequest request = new TWSGroupRequest();
                    final Aspects aspects = (Aspects) request.fromXML(inputSource);
                    for (Aspect aspect : aspects.getAspect()) {
                        m_tradePersistentModel.persistPortfolio((Portfolio) aspect);
                    }
                    m_client.requestFA(EClientSocket.PROFILES);
                    break;
                }
                default: {
                    _log.debug("receiveFA: /n" + xml);
                }
            }
        } catch (Exception ex) {
            error(faDataType, 3235, ex.getMessage());
        } finally {
            try {
                if (null != inputSource)
                    inputSource.close();
            } catch (IOException ex) {
                error(faDataType, 3236, ex.getMessage());
            }
        }
    }

    public void marketDataType(int reqId, int marketDataType) {
        _log.debug("marketDataType: " + reqId + " " + marketDataType);
    }

    public void historicalData(int reqId, String dateString, double open, double high, double low, double close,
                               int volume, int tradeCount, double vwap, boolean hasGaps) {
        try {
            volume = volume * 100;
            if (m_historyDataRequests.containsKey(reqId)) {
                Tradestrategy tradestrategy = m_historyDataRequests.get(reqId);

                if (dateString.contains("finished-")) {

                    CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();

                    _log.debug("HistoricalData complete Req Id: " + reqId + " Symbol: "
                            + tradestrategy.getContract().getSymbol() + " Tradingday: "
                            + tradestrategy.getTradingday().getOpen() + " candles to saved: "
                            + candleSeries.getItemCount() + " Contract Tradestrategies size:: "
                            + tradestrategy.getContract().getTradestrategies().size());

                    m_tradePersistentModel.persistCandleSeries(candleSeries);

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

                    ZonedDateTime date;
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
            }
        } catch (Exception ex) {
            error(reqId, 3260, ex.getMessage());
        }
    }

    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume,
                            double vwap, int tradeCount) {
        // Called when a candle finishes
        try {

            volume = volume * 100;
            ZonedDateTime date = TradingCalendar.getZonedDateTimeFromMilli(time * 1000);

            // Only store data that is during mkt hours
            if (m_realTimeBarsRequests.containsKey(reqId)) {
                Contract contract = m_realTimeBarsRequests.get(reqId);

                synchronized (contract) {
                    Collections.sort(contract.getTradestrategies(), Tradestrategy.TRADINGDAY_CONTRACT);
                    boolean updateCandleDB = true;
                    for (Tradestrategy tradestrategy : contract.getTradestrategies()) {
                        StrategyData strategyData = tradestrategy.getStrategyData();

                        if (TradingCalendar.isMarketHours(tradestrategy.getTradingday().getOpen(),
                                tradestrategy.getTradingday().getClose(), date)) {

                            if (!this.isMarketDataRunning(contract)) {
                                BigDecimal price = new BigDecimal(close).setScale(SCALE, RoundingMode.HALF_EVEN);
                                strategyData.getBaseCandleSeries().getContract().setLastAskPrice(price);
                                strategyData.getBaseCandleSeries().getContract().setLastBidPrice(price);
                                strategyData.getBaseCandleSeries().getContract().setLastPrice(price);
                            }
                            ZonedDateTime lastUpdateDate = date.plusNanos(4999);

                            strategyData.buildCandle(date, open, high, low, close, volume, vwap, tradeCount,
                                    (tradestrategy.getBarSize() / 5), lastUpdateDate);

                            if (!strategyData.getBaseCandleSeries().isEmpty()) {
                                CandleItem candleItem = (CandleItem) strategyData.getBaseCandleSeries()
                                        .getDataItem(strategyData.getBaseCandleSeries().getItemCount() - 1);
                                if (updateCandleDB) {
                                    m_tradePersistentModel.persistCandle(candleItem.getCandle());
                                    updateCandleDB = false;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            error(reqId, 3270, ex.getMessage());
        }
    }

    public void commissionReport(CommissionReport commsReport) {

        try {
            TWSBrokerModel.logCommissionReport(commsReport);

            TradeOrderfill transientInstance = m_tradePersistentModel.findTradeOrderfillByExecId(commsReport.m_execId);
            if (null != transientInstance) {
                TradeOrder tradeOrder = m_tradePersistentModel
                        .findTradeOrderByKey(transientInstance.getTradeOrder().getOrderKey());
                for (TradeOrderfill tradeOrderfill : tradeOrder.getTradeOrderfills()) {
                    if (tradeOrderfill.getExecId().equals(commsReport.m_execId)) {
                        tradeOrderfill.setCommission(new BigDecimal(commsReport.m_commission));
                        m_tradePersistentModel.persistTradeOrderfill(tradeOrderfill.getTradeOrder());
                        return;
                    }
                }

            } else {
                commissionDetails.put(commsReport.m_execId, commsReport);
            }

        } catch (Exception ex) {
            error(1, 3280, "Errors saving execution: " + ex.getMessage());
        }
    }


    public boolean validateBrokerData(Tradestrategy tradestrategy) throws BrokerModelException {

        boolean valid = true;
        String errorMsg = "Symbol: " + tradestrategy.getContract().getSymbol()
                + " Bar Size/Chart Days combination was not valid for TWS API, these values have been updated.\n Please validate and save.\n "
                + "Note Chart Days/BarSize combinations for IB TWS:\n "
                + "Chart Hist/Bar Size 1 Y/1 day, 6 M/1 day, 3 M/1 day 1 M/(1 day, 1 hour)\n "
                + "Chart Hist 1 W/ Bar Size(1 day, 1 hour, 30 mins, 15 mins 2 D 1 hour, 30 mins, 15 mins, 3 mins, 2 mins, 1 min)\n "
                + "Chart Hist 1 D/ Bar Size(1 hour, 30 mins, 15 mins, 5 mins 3 mins, 2 mins, 1 min, 30 secs)\n ";
        if (tradestrategy.getChartDays() > 1 && (tradestrategy.getBarSize() < 60)) {
            tradestrategy.setBarSize(60);
            valid = false;
        } else if (tradestrategy.getChartDays() > 7 && tradestrategy.getBarSize() < 3600) {
            tradestrategy.setBarSize(3600);
            valid = false;
        }

        if (tradestrategy.getBarSize() == 30 && tradestrategy.getChartDays() > 1) {
            tradestrategy.setChartDays(1);
            valid = false;
        } else if (tradestrategy.getBarSize() <= 1800 && tradestrategy.getChartDays() > 7) {
            tradestrategy.setChartDays(7);
            valid = false;
        } else if (tradestrategy.getBarSize() == 3600 && tradestrategy.getChartDays() > 30) {
            tradestrategy.setChartDays(30);
            valid = false;
        }
        if (!valid) {
            tradestrategy.setDirty(true);
            throw new BrokerModelException(1, 3901, errorMsg);
        }

        return valid;
    }

    public static com.ib.client.Contract getIBContract(Contract contract) {
        com.ib.client.Contract ibContract = new com.ib.client.Contract();
        if (null != contract.getIdContractIB()) {
            // ibContract.m_conId = contract.getIdContractIB();
        }
        if (null != contract.getSymbol()) {
            ibContract.symbol(contract.getSymbol());
        }
        if (null != contract.getSecType()) {
            ibContract.secType(contract.getSecType());
        }
        if (null != contract.getExchange()) {
            ibContract.exchange(contract.getExchange());
        }
        if (null != contract.getPrimaryExchange()) {
            ibContract.primaryExch(contract.getPrimaryExchange());
        }
        if (null != contract.getExpiry()) {
            if (SECType.FUTURE.equals(contract.getSecType())) {
                ibContract.lastTradeDateOrContractMonth(TradingCalendar.getFormattedDate(contract.getExpiry(), "yyyyMMdd").substring(0,
                        6));
            }
        }
        if (null != contract.getCurrency()) {
            ibContract.currency(contract.getCurrency());
        }

        if (null != contract.getLocalSymbol()) {
            ibContract.localSymbol(contract.getLocalSymbol());
        }
        if (null != contract.getSecIdType()) {
            ibContract.secIdType(contract.getSecIdType());
        }

        return ibContract;
    }

    public static com.ib.client.Order getIBOrder(TradeOrder order) {
        com.ib.client.Order ibOrder = new com.ib.client.Order();

        if (null != order.getOrderKey()) {
            ibOrder.orderId(order.getOrderKey());
        }
        if (null != order.getClientId()) {
            ibOrder.clientId(order.getClientId());
        }
        if (null != order.getPermId()) {
            ibOrder.permId(order.getPermId());
        }
        if (null != order.getParentId()) {
            ibOrder.parentId(order.getParentId());
        }
        if (null != order.getAction()) {
            ibOrder.action(order.getAction());
        }
        if (null != order.getQuantity()) {
            ibOrder.totalQuantity(order.getQuantity());
        }
        if (null != order.getOrderType()) {
            ibOrder.orderType(order.getOrderType());
        }
        if (null != order.getLimitPrice()) {
            ibOrder.lmtPrice(order.getLimitPrice().doubleValue());
        }
        if (null != order.getAuxPrice()) {
            ibOrder.auxPrice(order.getAuxPrice().doubleValue());
        }
        if (null != order.getTrailStopPrice()) {
            ibOrder.trailStopPrice(order.getTrailStopPrice().doubleValue());
        }
        if (null != order.getTrailingPercent()) {
            ibOrder.trailingPercent(order.getTrailingPercent().doubleValue());
        }
        if (null != order.getTimeInForce()) {
            ibOrder.tif(order.getTimeInForce());
        }
        if (null != order.getOcaGroupName()) {
            ibOrder.ocaGroup(order.getOcaGroupName()); // one cancels all
        }
        // group
        // name
        if (null != order.getOcaType()) {
            ibOrder.ocaType(order.getOcaType()); // 1 = CANCEL_WITH_BLOCK, 2
        }
        // =
        // REDUCE_WITH_BLOCK, 3 =
        // REDUCE_NON_BLOCK
        if (null != order.getOrderReference()) {
            ibOrder.orderRef(order.getOrderReference());
        }
        if (null != order.getTransmit()) {
            ibOrder.transmit(order.getTransmit()); // if false, order will be
        }
        if (null != order.getDisplayQuantity()) {
            ibOrder.displaySize(order.getDisplayQuantity());
        }
        if (null != order.getTriggerMethod()) {
            ibOrder.triggerMethod(order.getTriggerMethod()); // 0=Default
        }

        if (null != order.getHidden()) {
            ibOrder.hidden(order.getHidden());
        }
        if (null != order.getGoodAfterTime()) {
            ibOrder.goodAfterTime(TradingCalendar.getFormattedDate(order.getGoodAfterTime(), "yyyyMMdd HH:mm:ss"));
        }
        if (null != order.getGoodTillTime()) {
            ibOrder.goodTillDate(TradingCalendar.getFormattedDate(order.getGoodTillTime(), "yyyyMMdd HH:mm:ss"));
        }
        if (null != order.getOverrideConstraints()) {
            ibOrder.overridePercentageConstraints((order.getOverrideConstraints() == 0) ? false : true);
        }
        if (null != order.getAllOrNothing()) {
            ibOrder.allOrNone(order.getAllOrNothing());
        }
        if (null != order.getFAProfile()) {
            ibOrder.faProfile(order.getFAProfile());
        }
        if (null != order.getFAGroup()) {
            ibOrder.faGroup(order.getFAGroup());
        }
        if (null != order.getFAMethod()) {
            ibOrder.faMethod(order.getFAMethod());
        }
        if (null != order.getFAPercent()) {
            Percent faPercent = new Percent(order.getFAPercent());
            ibOrder.faPercentage(faPercent.getBigDecimalValue().toString());
        }
        if (null != order.getAccountNumber()) {
            ibOrder.account(order.getAccountNumber());
        }
        return ibOrder;
    }

    private static boolean updateTradeOrder(com.ib.client.Order ibOrder, com.ib.client.OrderState ibOrderState,
                                            TradeOrder order) {

        boolean changed = false;

        if (CoreUtils.nullSafeComparator(order.getOrderKey(), ibOrder.orderId()) == 0) {
            if (CoreUtils.nullSafeComparator(order.getStatus(), ibOrderState.status().name()) != 0) {
                order.setStatus(ibOrderState.status().name());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getWarningMessage(), ibOrderState.warningText()) != 0) {
                order.setWarningMessage(ibOrderState.warningText());
                changed = true;
            }
            Money comms = new Money(ibOrderState.commission());
            if (CoreUtils.nullSafeComparator(comms, new Money(Double.MAX_VALUE)) != 0) {
                if (CoreUtils.nullSafeComparator(order.getCommission(), comms.getBigDecimalValue()) != 0) {
                    order.setCommission(comms.getBigDecimalValue());
                    changed = true;
                }
            }
            if (CoreUtils.nullSafeComparator(order.getClientId(), ibOrder.clientId()) != 0) {
                order.setClientId(ibOrder.clientId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getAction(), ibOrder.action().getApiString()) != 0) {
                order.setAction(ibOrder.action().getApiString());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getQuantity(), (int) ibOrder.totalQuantity()) != 0) {
                order.setQuantity((int) ibOrder.totalQuantity());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOrderType(), ibOrder.orderType().getApiString()) != 0) {
                order.setOrderType(ibOrder.orderType().getApiString());
                changed = true;
            }

            Money lmtPrice = new Money(ibOrder.lmtPrice());
            if (CoreUtils.nullSafeComparator(lmtPrice, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getLimitPrice(), lmtPrice.getBigDecimalValue()) != 0) {
                order.setLimitPrice(lmtPrice.getBigDecimalValue());
                changed = true;
            }
            Money auxPrice = new Money(ibOrder.auxPrice());
            if (CoreUtils.nullSafeComparator(auxPrice, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(order.getAuxPrice(), auxPrice.getBigDecimalValue()) != 0) {
                order.setAuxPrice(auxPrice.getBigDecimalValue());
                changed = true;
            }

            Money trailStopPrice = new Money(ibOrder.trailStopPrice());
            if (CoreUtils.nullSafeComparator(trailStopPrice, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(order.getTrailStopPrice(), trailStopPrice.getBigDecimalValue()) != 0) {
                order.setTrailStopPrice(trailStopPrice.getBigDecimalValue());
                changed = true;
            }

            Money trailingPercent = new Money(ibOrder.trailingPercent());
            if (CoreUtils.nullSafeComparator(trailingPercent, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(order.getTrailingPercent(), trailingPercent.getBigDecimalValue()) != 0) {
                order.setTrailingPercent(trailingPercent.getBigDecimalValue());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getTimeInForce(), ibOrder.tif().getApiString()) != 0) {
                order.setTimeInForce(ibOrder.tif().getApiString());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOcaGroupName(), ibOrder.ocaGroup()) != 0) {
                order.setOcaGroupName(ibOrder.ocaGroup());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOcaType(), ibOrder.ocaType().ordinal()) != 0) {
                order.setOcaType(ibOrder.ocaType().ordinal());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getOrderReference(), ibOrder.orderRef()) != 0) {
                order.setOrderReference(ibOrder.orderRef());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getAccountNumber(), ibOrder.account()) != 0) {
                order.setAccountNumber(ibOrder.account());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getFAGroup(), ibOrder.faGroup()) != 0) {
                order.setFAGroup(ibOrder.faGroup());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getFAMethod(), ibOrder.faMethod().getApiString()) != 0) {
                order.setFAMethod(ibOrder.faMethod().getApiString());
                changed = true;
            }
            Money faPercent = new Money(ibOrder.faPercentage());
            if (CoreUtils.nullSafeComparator(order.getFAPercent(), faPercent.getBigDecimalValue()) != 0) {
                order.setFAPercent(faPercent.getBigDecimalValue());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getFAProfile(), ibOrder.faProfile()) != 0) {
                order.setFAProfile(ibOrder.faProfile());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getPermId(), (int) ibOrder.permId()) != 0) {
                order.setPermId((int) ibOrder.permId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getParentId(), ibOrder.parentId()) != 0) {
                order.setParentId(ibOrder.parentId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getTransmit(), ibOrder.transmit()) != 0) {
                order.setTransmit(ibOrder.transmit());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getDisplayQuantity(), ibOrder.displaySize()) != 0) {
                order.setDisplayQuantity(ibOrder.displaySize());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getTriggerMethod(), ibOrder.triggerMethod().val()) != 0) {
                order.setTriggerMethod(ibOrder.triggerMethod().val());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getHidden(), ibOrder.hidden()) != 0) {
                order.setHidden(ibOrder.hidden());
                changed = true;
            }
            if (null != ibOrder.goodAfterTime()) {
                ZonedDateTime goodAfterTime = TradingCalendar
                        .getZonedDateTimeFromDateTimeString(ibOrder.goodAfterTime(), "yyyyMMdd HH:mm:ss");
                if (CoreUtils.nullSafeComparator(order.getGoodAfterTime(), goodAfterTime) != 0) {
                    order.setGoodAfterTime(goodAfterTime);
                    changed = true;
                }
            }

            if (null != ibOrder.goodTillDate()) {
                ZonedDateTime goodTillDate = TradingCalendar.getZonedDateTimeFromDateTimeString(ibOrder.goodTillDate(),
                        "yyyyMMdd HH:mm:ss");
                if (CoreUtils.nullSafeComparator(order.getGoodTillTime(), goodTillDate) != 0) {
                    order.setGoodTillTime(goodTillDate);
                    changed = true;
                }
            }
            Integer overridePercentageConstraints = new Integer((ibOrder.overridePercentageConstraints() ? 1 : 0));
            if (CoreUtils.nullSafeComparator(order.getOverrideConstraints(), overridePercentageConstraints) != 0) {
                order.setOverrideConstraints(overridePercentageConstraints);
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(order.getAllOrNothing(), ibOrder.allOrNone()) != 0) {
                order.setAllOrNothing(ibOrder.allOrNone());
                changed = true;
            }
            if (changed)
                order.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
        }
        return changed;
    }

    private static boolean populateContract(com.ib.client.ContractDetails contractDetails, Contract transientContract)
            throws ParseException {

        boolean changed = false;
        /*
         * For stock the localsymbol must match. For futues they will not e.g
         * Symbol ES Local will be ES06. TODO Need to find out how to handle
         * same symbol different local symbols when using exchange SMART.
         */
        if (CoreUtils.nullSafeComparator(transientContract.getSymbol(), contractDetails.contract().localSymbol()) != 0
                && SECType.STOCK.equals(transientContract.getSecType())) {
            return changed;

        }
        if (CoreUtils.nullSafeComparator(transientContract.getSymbol(), contractDetails.contract().symbol()) == 0) {
            if (CoreUtils.nullSafeComparator(transientContract.getLocalSymbol(),
                    contractDetails.contract().localSymbol()) != 0) {
                transientContract.setLocalSymbol(contractDetails.contract().localSymbol());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getIdContractIB(),
                    contractDetails.contract().conid()) != 0) {
                transientContract.setIdContractIB(contractDetails.contract().conid());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getPrimaryExchange(),
                    contractDetails.contract().primaryExch()) != 0) {
                transientContract.setPrimaryExchange(contractDetails.contract().primaryExch());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getExchange(),
                    contractDetails.contract().exchange()) != 0) {
                transientContract.setExchange(contractDetails.contract().exchange());
                changed = true;
            }
            if (null != contractDetails.contract().lastTradeDateOrContractMonth()) {
                ZonedDateTime expiryDateTime = TradingCalendar.getZonedDateTimeFromDateString(
                        contractDetails.contract().lastTradeDateOrContractMonth(), "yyyyMMdd", TradingCalendar.MKT_TIMEZONE);
                if (CoreUtils.nullSafeComparator(transientContract.getExpiry(), expiryDateTime) != 0) {
                    transientContract.setExpiry(expiryDateTime);
                    changed = true;
                }
            }
            if (CoreUtils.nullSafeComparator(transientContract.getSecIdType(),
                    contractDetails.contract().secIdType().getApiString()) != 0) {
                transientContract.setSecIdType(contractDetails.contract().secIdType().getApiString());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getLongName(), contractDetails.longName()) != 0) {
                transientContract.setLongName(contractDetails.longName());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getCurrency(),
                    contractDetails.contract().currency()) != 0) {
                transientContract.setCurrency(contractDetails.contract().currency());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getCategory(), contractDetails.category()) != 0) {
                transientContract.setCategory(contractDetails.category());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getIndustry(), contractDetails.industry()) != 0) {
                transientContract.setIndustry(contractDetails.industry());
                changed = true;
            }
            Money minTick = new Money(contractDetails.minTick());
            if (CoreUtils.nullSafeComparator(minTick, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getMinTick(), minTick.getBigDecimalValue()) != 0) {
                transientContract.setMinTick(minTick.getBigDecimalValue());
                changed = true;
            }
            Money priceMagnifier = new Money(contractDetails.priceMagnifier());
            if (CoreUtils.nullSafeComparator(priceMagnifier, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(transientContract.getPriceMagnifier(),
                    priceMagnifier.getBigDecimalValue()) != 0) {
                transientContract.setPriceMagnifier(priceMagnifier.getBigDecimalValue());
                changed = true;
            }

            Money multiplier = new Money(contractDetails.contract().multiplier());
            if (CoreUtils.nullSafeComparator(multiplier, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getPriceMultiplier(), multiplier.getBigDecimalValue()) != 0) {
                transientContract.setPriceMultiplier(multiplier.getBigDecimalValue());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getSubCategory(), contractDetails.subcategory()) != 0) {
                transientContract.setSubCategory(contractDetails.subcategory());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getTradingClass(),
                    contractDetails.contract().tradingClass()) != 0) {
                transientContract.setTradingClass(contractDetails.contract().tradingClass());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getComboLegDescription(),
                    contractDetails.contract().comboLegsDescrip()) != 0) {
                transientContract.setComboLegDescription(contractDetails.contract().comboLegsDescrip());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getContractMonth(),
                    contractDetails.contractMonth()) != 0) {
                transientContract.setContractMonth(contractDetails.contractMonth());
                changed = true;
            }
            Money evMultiplier = new Money(contractDetails.evMultiplier());
            if (CoreUtils.nullSafeComparator(evMultiplier, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(transientContract.getEvMultiplier(), evMultiplier.getBigDecimalValue()) != 0) {
                transientContract.setEvMultiplier(evMultiplier.getBigDecimalValue());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getEvRule(), contractDetails.evRule()) != 0) {
                transientContract.setEvRule(contractDetails.evRule());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getIncludeExpired(),
                    contractDetails.contract().includeExpired()) != 0) {
                transientContract.setIncludeExpired(contractDetails.contract().includeExpired());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getLiquidHours(), contractDetails.liquidHours()) != 0) {
                transientContract.setLiquidHours(contractDetails.liquidHours());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getMarketName(), contractDetails.marketName()) != 0) {
                transientContract.setMarketName(contractDetails.marketName());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getOrderTypes(), contractDetails.orderTypes()) != 0) {
                String orderTypes = OrderType.MKT;
                if (contractDetails.orderTypes().contains(OrderType.STP)) {
                    orderTypes = orderTypes + "," + OrderType.STP;
                    changed = true;
                }
                if (contractDetails.orderTypes().contains(OrderType.STPLMT)) {
                    orderTypes = orderTypes + "," + OrderType.STPLMT;
                    changed = true;
                }
                if (contractDetails.orderTypes().contains(OrderType.LMT)) {
                    orderTypes = orderTypes + "," + OrderType.LMT;
                    changed = true;
                }
                transientContract.setOrderTypes(orderTypes);

            }
            if (CoreUtils.nullSafeComparator(transientContract.getSecId(), contractDetails.contract().secId()) != 0) {
                transientContract.setSecId(contractDetails.contract().secId());
                changed = true;
            }
            Money strike = new Money(contractDetails.contract().strike());
            if (CoreUtils.nullSafeComparator(strike, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(transientContract.getStrike(), strike.getBigDecimalValue()) != 0) {
                transientContract.setStrike(strike.getBigDecimalValue());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getTimeZoneId(), contractDetails.timeZoneId()) != 0) {
                transientContract.setTimeZoneId(contractDetails.timeZoneId());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getTradingHours(),
                    contractDetails.tradingHours()) != 0) {
                transientContract.setTradingHours(contractDetails.tradingHours());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getUnderConId(),
                    new Integer(contractDetails.underConid())) != 0) {
                transientContract.setUnderConId(new Integer(contractDetails.underConid()));
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getValidExchanges(),
                    contractDetails.validExchanges()) != 0) {
                transientContract.setValidExchanges(contractDetails.validExchanges());
                changed = true;
            }
            if (CoreUtils.nullSafeComparator(transientContract.getOptionType(),
                    contractDetails.contract().right().getApiString()) != 0) {
                transientContract.setOptionType(contractDetails.contract().right().getApiString());
                changed = true;
            }
        }

        return changed;
    }

    public static void populateTradeOrderfill(com.ib.client.Execution execution, TradeOrderfill tradeOrderfill) {

        ZonedDateTime date = TradingCalendar.getZonedDateTimeFromDateTimeString(execution.time().replaceAll("\\s", ""),
                "yyyyMMddHH:mm:ss", TradingCalendar.LOCAL_TIMEZONE);
        tradeOrderfill.setTime(date);
        tradeOrderfill.setExchange(execution.exchange());
        tradeOrderfill.setSide(execution.side());
        tradeOrderfill.setQuantity((int) execution.shares());
        tradeOrderfill.setPrice(new BigDecimal(execution.price()));
        tradeOrderfill.setAveragePrice(new BigDecimal(execution.avgPrice()));
        tradeOrderfill.setAccountNumber(execution.acctNumber());
        tradeOrderfill.setCumulativeQuantity(execution.cumQty());
        tradeOrderfill.setExecId(execution.execId());
        tradeOrderfill.setOrderReference(execution.orderRef());
        tradeOrderfill.setPermId(execution.permId());
    }

    private static com.ib.client.ExecutionFilter getIBExecutionFilter(Integer clientId, ZonedDateTime mktOpen,
                                                                      String secType, String symbol) throws IOException {

        com.ib.client.ExecutionFilter executionFilter = new com.ib.client.ExecutionFilter();
        if (null != secType)
            executionFilter.secType(secType);

        if (null != symbol)
            executionFilter.symbol(symbol);
        if (null != mktOpen) {
            executionFilter.time(TradingCalendar.getFormattedDate(mktOpen, "yyyyMMdd"));
        }

        if (null != clientId)
            executionFilter.clientId(clientId);
        return executionFilter;
    }


    public static void logOrderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice,
                                      int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

        _log.info("orderId: " + orderId + " status: " + status + " filled: " + filled + " remaining: " + remaining
                + " avgFillPrice: " + avgFillPrice + " permId: " + permId + " parentId: " + parentId
                + " lastFillPrice: " + lastFillPrice + " clientId: " + clientId + " whyHeld: " + whyHeld);
    }

    public static void logTradeOrder(com.ib.client.Order order) {

        _log.debug("OrderKey: " + +order.orderId() + " ClientId: " + order.clientId() + " PermId: " + order.permId()
                + " Action: " + order.action() + " TotalQuantity: " + order.totalQuantity() + " OrderType: "
                + order.orderType() + " LmtPrice: " + order.lmtPrice() + " AuxPrice: " + order.auxPrice() + " Tif: "
                + order.tif() + " OcaGroup: " + order.ocaGroup() + " OcaType: " + order.ocaType() + " OrderRef: "
                + order.orderRef() + " Transmit: " + order.transmit() + " DisplaySize: " + order.displaySize()
                + " TriggerMethod: " + order.triggerMethod() + " Hidden: " + order.hidden() + " ParentId: "
                + order.parentId() + " GoodAfterTime: " + order.goodAfterTime() + " GoodTillDate: "
                + order.goodTillDate() + " TrailStopPrice: " + order.trailStopPrice() + " TrailingPercent: "
                + order.trailingPercent() + " OverridePercentageConstraints: " + order.overridePercentageConstraints()
                + " AllOrNone: " + order.allOrNone() + " Account: " + order.account() + " FAGroup: " + order.faGroup()
                + " FAMethod: " + order.faMethod() + " FAPercent: " + order.faPercentage() + " FAProfile: "
                + order.faProfile());
    }


    public static void logContract(com.ib.client.Contract contract) {
        _log.debug("Symbol: " + contract.symbol() + " Sec Type: " + contract.secType() + " Exchange: "
                + contract.exchange() + " Con Id: " + contract.conid() + " Currency: " + contract.currency()
                + " SecIdType: " + contract.secIdType() + " Primary Exch: " + contract.primaryExch() + " Local Symbol: "
                + contract.localSymbol() + " SecId: " + contract.secId() + " Multiplier: " + contract.multiplier()
                + " lastTradeDateOrContractMonth: " + contract.lastTradeDateOrContractMonth());
    }

    private static void logContractDetails(com.ib.client.ContractDetails contractDetails) {
        _log.debug("Symbol: " + contractDetails.contract().symbol() + " Sec Type: " + contractDetails.contract().secType()
                + " Exchange: " + contractDetails.contract().exchange() + " con Id: " + contractDetails.contract().conid()
                + " Currency: " + contractDetails.contract().currency() + " SecIdType: "
                + contractDetails.contract().secIdType() + " Primary Exch: " + contractDetails.contract().primaryExch()
                + " Local Symbol: " + contractDetails.contract().localSymbol() + " SecId: "
                + contractDetails.contract().secId() + " Multiplier: " + contractDetails.contract().multiplier()
                + " Category: " + contractDetails.category() + " last TradeDate Or Contract Month: " + contractDetails.contract().lastTradeDateOrContractMonth()
                + " ContractMonth: " + contractDetails.contractMonth() + " Cusip: " + contractDetails.cusip()
                + " Industry: " + contractDetails.industry() + " IssueDate: " + contractDetails.issueDate()
                + " MarketName: " + contractDetails.marketName() + " MinTick: " + contractDetails.minTick()
                + " PriceMagnifier: " + contractDetails.priceMagnifier());
    }

    private static void logOrderState(com.ib.client.OrderState orderState) {
        _log.debug("Status: " + orderState.status() + " Comms Amt: " + orderState.commission() + " Comms Currency: "
                + orderState.commissionCurrency() + " Warning txt: " + orderState.warningText() + " Init Margin: "
                + orderState.initMargin() + " Maint Margin: " + orderState.maintMargin() + " Min Comms: "
                + orderState.minCommission() + " Max Comms: " + orderState.maxCommission());
    }

    private static void logExecution(com.ib.client.Execution execution) {
        _log.debug("execDetails OrderId: " + execution.orderId() + " ClientId: " + execution.clientId() + " PermId: "
                + execution.permId() + " ExecId: " + execution.execId() + " Time: " + execution.time() + " CumQty: "
                + execution.cumQty());
    }


    private static void logCommissionReport(com.ib.client.CommissionReport commissionReport) {
        _log.debug("execDetails ExecId: " + commissionReport.m_execId + " Commission: " + commissionReport.m_commission
                + " Currency: " + commissionReport.m_currency + " RealizedPNL: " + commissionReport.m_realizedPNL
                + " yieldRedemptionDate: " + commissionReport.m_yieldRedemptionDate + " Yield: "
                + commissionReport.m_yield);

    }

    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        _log.debug("softDollarTiers: ");
    }

    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {
        _log.debug("securityDefinitionOptionalParameter: ");
    }

    public void securityDefinitionOptionalParameterEnd(int reqId) {
        _log.debug("securityDefinitionOptionalParameterEnd: ");
    }

    public void connectAck() {
        _log.debug("connectAck: ");
    }

    public void positionMulti(int reqId, String account, String modelCode, com.ib.client.Contract contract, double pos, double avgCost) {
        _log.debug("positionMulti: ");
    }

    public void positionMultiEnd(int reqId) {
        _log.debug("positionMultiEnd: ");
    }

    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency) {
        _log.debug("accountUpdateMulti: ");
    }

    public void accountUpdateMultiEnd(int reqId) {
        _log.debug("accountUpdateMultiEnd: ");
    }

    public void verifyAndAuthMessageAPI(String apiData, String xyzChallange) {
        _log.debug("verifyAndAuthMessageAPI: ");
    }

    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
        _log.debug("verifyAndAuthCompleted: ");
    }

    public void position(String account, com.ib.client.Contract contract, double pos, double avgCost) {
        _log.debug("position: ");
    }

    public void deltaNeutralValidation(int reqId, DeltaNeutralContract underComp) {
        _log.debug("deltaNeutralValidation: ");
    }

    public void updatePortfolio(com.ib.client.Contract contract, double position, double marketPrice, double marketValue,
                                double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        _log.debug("updatePortfolio: ");
    }

    public void currentTime(long time) {
        _log.debug("currentTime: " + new Date(time));
    }

    public void fundamentalData(int reqId, String data) {
        _log.debug("fundamentalData: " + reqId + " " + data);
    }

    public void tickSnapshotEnd(int reqId) {
        _log.debug("tickSnapshotEnd: " + reqId);
    }

    public void accountSummary(int arg0, String arg1, String arg2, String arg3, String arg4) {
        _log.debug("accountSummary: " + arg0 + " " + arg1 + " " + arg2 + " " + arg3 + " " + arg4);
    }

    public void accountSummaryEnd(int reqId) {
        _log.debug("accountSummaryEnd: " + reqId);
    }

    public void position(String arg0, com.ib.client.Contract arg1, int arg2, double arg3) {
        _log.debug("position: " + arg0 + " " + arg1.toString() + " " + arg2 + " " + arg3);
    }

    public void positionEnd() {
        _log.debug("positionEnd: ");
    }

    public void displayGroupList(int arg0, String arg1) {
        _log.debug("displayGroupList: " + arg0 + " " + arg1);
    }


    public void displayGroupUpdated(int arg0, String arg1) {
        _log.debug("displayGroupUpdated: " + arg0 + " " + arg1);
    }

    public void verifyCompleted(boolean arg0, String arg1) {
        _log.debug("verifyCompleted: " + arg0 + " " + arg1);
    }

    public void verifyMessageAPI(String arg0) {
        _log.debug("verifyMessageAPI: " + arg0);
    }

    public void issueSignal() {
        _log.debug("issueSignal: ");
    }

    public void waitForSignal() {
        _log.debug("waitForSignal: ");
    }

    public void scannerParameters(String xml) {
        _log.debug("scannerParameters: " + xml);
    }

    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark,
                            String projection, String legsStr) {
        _log.debug("scannerData: " + reqId);
    }

    public void scannerDataEnd(int reqId) {
        _log.debug("scannerDataEnd: " + reqId);
    }


}
