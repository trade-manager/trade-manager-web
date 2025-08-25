package org.trade.core.broker;

import com.ib.client.CommissionReport;
import com.ib.client.ContractDetails;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.EClientSocket;
import com.ib.client.EReaderSignal;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.OrderState;
import com.ib.client.SoftDollarTier;
import com.ib.client.TagValue;
import com.ib.client.TickType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.broker.client.Broker;
import org.trade.core.broker.request.TWSAccountAliasRequest;
import org.trade.core.broker.request.TWSAllocationRequest;
import org.trade.core.broker.request.TWSGroupRequest;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.OverrideConstraints;
import org.trade.core.valuetype.Percent;
import org.trade.core.valuetype.SECType;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TriggerMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TWSBrokerModel extends AbstractBrokerModel implements EWrapper, EReaderSignal {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 595280836716405557L;

    private final static Logger _log = LoggerFactory.getLogger(TWSBrokerModel.class);

    private final TradeService tradeService;

    private static final ConcurrentHashMap<Integer, Tradestrategy> historyDataRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> realTimeBarsRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> marketDataRequests = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Contract> contractRequests = new ConcurrentHashMap<>();
    // Use account number as key
    private static final ConcurrentHashMap<String, Account> accountRequests = new ConcurrentHashMap<>();

    // All Use orderKey as key
    private static final ConcurrentHashMap<Integer, TradeOrder> openOrders = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, TradeOrder> tradeOrdersExecutions = new ConcurrentHashMap<>();

    // Use execId as key
    private static final ConcurrentHashMap<String, Execution> executionDetails = new ConcurrentHashMap<>();

    // Use commsReport.m_execId as key
    private static final ConcurrentHashMap<String, CommissionReport> commissionDetails = new ConcurrentHashMap<>();

    private final EClientSocket client;
    private final AtomicInteger reqId;
    private AtomicInteger orderKey = null;
    private Integer clientId = null;
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

    private static final Integer backfillDateFormat = 2;
    private static final Integer backfillUseRTH;
    private static final String backfillWhatToShow;
    private static final Integer backfillOffsetDays;
    private static final String genericTicklist;
    private static final boolean marketUpdateOnClose;

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

    public TWSBrokerModel(TradeService tradeService) {

        try {

            this.tradeService = tradeService;
            client = new EClientSocket(this, this);
            reqId = new AtomicInteger((int) (System.currentTimeMillis() / 1000d));

        } catch (Exception ex) {
            throw new IllegalArgumentException("Error initializing IBrokerModel Msg: " + ex.getMessage());
        }
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public ConcurrentHashMap<Integer, Tradestrategy> getHistoryDataRequests() {
        return historyDataRequests;
    }

    public void onConnect(String host, Integer port, Integer clientId) {

        this.clientId = clientId;
        client.eConnect(host, port, clientId);
        openOrders.clear();
    }

    public void onDisconnect() {

        onCancelAllRealtimeData();

        if (client.isConnected()) {

            for (String accountNumber : accountRequests.keySet()) {

                this.onCancelAccountUpdates(accountNumber);
            }
            client.eDisconnect();
        }
        this.fireConnectionClosed(false);
    }

    public void connectionClosed() {

        _log.error("TWS Broker Model connectionClosed ");
        onCancelAllRealtimeData();
        this.fireConnectionClosed(true);
    }

    public Broker getBackTestBroker(Integer reqId) {
        return null;
    }

    public void onReqFinancialAccount() {

        try {

            if (client.isConnected()) {

                client.requestFA(EClientSocket.ALIASES);
            } else {
                throw new BrokerModelException(0, 3010, "Not conected Financial Account data cannot be retrieved");
            }
        } catch (Exception ex) {
            error(0, 3295, "Error requesting Financial Account Msg: " + ex.getMessage());
        }
    }

    public void onReqReplaceFinancialAccount(int faDataType, String xml) {

        try {

            if (client.isConnected()) {

                client.replaceFA(faDataType, xml);
            } else {
                throw new BrokerModelException(0, 3010, "Not conected Financial Account data cannot be replaced");
            }
        } catch (Exception ex) {
            error(0, 3295, "Error replacing Financial Account Msg: " + ex.getMessage());
        }
    }

    public void onReqManagedAccount() throws BrokerModelException {

        // request list of all open orders
        if (client.isConnected()) {

            client.reqManagedAccts();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    public void onSubscribeAccountUpdates(boolean subscribe, String accountNumber) {

        try {

            Account account = tradeService.findAccountByAccountNumber(accountNumber);
            accountRequests.put(accountNumber, account);

            if (client.isConnected()) {

                client.reqAccountUpdates(subscribe, accountNumber);
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
        if (client.isConnected()) {
            openOrders.clear();
            client.reqAllOpenOrders();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    public void onReqOpenOrders() throws BrokerModelException {
        // request list of all open orders
        if (client.isConnected()) {
            openOrders.clear();
            client.reqOpenOrders();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    public void onReqAllExecutions(ZonedDateTime mktOpenDate) throws BrokerModelException {
        try {
            /*
             * Request execution reports based on the supplied filter criteria
             */

            if (client.isConnected()) {

                tradeOrdersExecutions.clear();
                commissionDetails.clear();
                executionDetails.clear();
                Integer reqId = this.getNextRequestId();
                client.reqExecutions(reqId, TWSBrokerModel.getIBExecutionFilter(clientId, mktOpenDate, null, null));
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
            Integer clientId = this.clientId;

            if (client.isConnected()) {

                tradeOrdersExecutions.clear();
                commissionDetails.clear();
                executionDetails.clear();
                /*
                 * This will get all orders i.e. those created by this client
                 * and those created by other clients in TWS.
                 */
                if (addOrders) {

                    clientId = 0;
                }

                Integer reqId = tradestrategy.getRequestId();
                this.client.reqExecutions(reqId,
                        TWSBrokerModel.getIBExecutionFilter(clientId, tradestrategy.getTradingday().getOpen(),
                                tradestrategy.getContract().getSecType(), tradestrategy.getContract().getSymbol()));
            } else {

                throw new BrokerModelException(tradestrategy.getRequestId(), 3020,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {

            throw new BrokerModelException(tradestrategy.getRequestId(), 3020,
                    "Error request executions for symbol: " + tradestrategy.getContract().getSymbol() + " Msg: "
                            + ex.getMessage());
        }
    }


    public void onReqRealTimeBars(Contract contract, boolean mktData) throws BrokerModelException {

        try {

            if (client.isConnected()) {

                if (this.isRealtimeBarsRequestRunning(contract)) {

                    throw new BrokerModelException(contract.getRequestId(), 3030,
                            "RealtimeBars request is already in progress for: " + contract.getSymbol()
                                    + " Please wait or cancel.");
                }
                realTimeBarsRequests.put(contract.getRequestId(), contract);

                /*
                 * Bar interval is set to 5= 5sec this is the only thing
                 * supported by TWS for live data.
                 */
                ArrayList<TagValue> realTimeBarOptions = new ArrayList<>();
                client.reqRealTimeBars(contract.getRequestId(), TWSBrokerModel.getIBContract(contract), 5,
                        backfillWhatToShow, (backfillUseRTH > 0), realTimeBarOptions);

                if (mktData) {

                    onReqMarketData(contract, genericTicklist, false);
                }
            } else {

                throw new BrokerModelException(contract.getRequestId(), 3040,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {

            throw new BrokerModelException(contract.getRequestId(), 3050,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public void onReqMarketData(Contract contract, String genericTicklist, boolean snapshot)
            throws BrokerModelException {

        try {

            if (client.isConnected()) {

                if (this.isMarketDataRequestRunning(contract)) {

                    throw new BrokerModelException(contract.getRequestId(), 3030,
                            "MarketData request is already in progress for: " + contract.getSymbol()
                                    + " Please wait or cancel.");
                }

                List<TagValue> mktDataOptions = new ArrayList<>();
                marketDataRequests.put(contract.getRequestId(), contract);
                client.reqMktData(contract.getRequestId(), TWSBrokerModel.getIBContract(contract), genericTicklist, snapshot,
                        mktDataOptions);

            } else {

                throw new BrokerModelException(contract.getRequestId(), 3040,
                        "Not conected to TWS market data cannot be retrieved");
            }
        } catch (Exception ex) {

            throw new BrokerModelException(contract.getRequestId(), 3050,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public void onContractDetails(Contract contract) throws BrokerModelException {

        try {

            if (client.isConnected()) {

                if (!contractRequests.containsKey(contract.getRequestId())) {

                    /*
                     * Null the IB Contract Id as these sometimes change. This
                     * will force a get of the IB data via the
                     * Exchange/Symbol/Currency.
                     */
                    contract.setContractIBId(null);
                    contractRequests.put(contract.getRequestId(), contract);
                    TWSBrokerModel.logContract(TWSBrokerModel.getIBContract(contract));
                    client.reqContractDetails(contract.getRequestId(), TWSBrokerModel.getIBContract(contract));
                }
            } else {

                throw new BrokerModelException(contract.getRequestId(), 3080,
                        "Not conected to TWS contract data cannot be retrieved");
            }
        } catch (Exception ex) {

            throw new BrokerModelException(contract.getRequestId(), 3090,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public void onBrokerData(Tradestrategy tradestrategy, ZonedDateTime startDate, ZonedDateTime endDate) throws BrokerModelException {

        try {

            if (client.isConnected()) {

                if (this.isHistoricalDataRequestRunning(tradestrategy)) {

                    throw new BrokerModelException(tradestrategy.getRequestId(), 3010,
                            "HistoricalData request is already in progress for: "
                                    + tradestrategy.getContract().getSymbol() + " Please wait or cancel.");
                }

                /*
                 * When running data via the TWS API we start the
                 * DatasetContainers internal thread to process candle updates
                 * and all indicator updates. That reduces the delay to the
                 * broker interface thread for messages coming in.
                 */
                if (!tradestrategy.getStrategyData().isRunning()) {

                    tradestrategy.getStrategyData().execute();
                }

                historyDataRequests.put(tradestrategy.getRequestId(), tradestrategy);

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

                _log.info("onBrokerData Req Id: {} Symbol: {} end Time: {} Period length: {} Bar size: {} WhatToShow: {} Regular Trading Hrs: {} Date format: " + backfillDateFormat, tradestrategy.getId(), tradestrategy.getContract().getSymbol(), endDateTime, ChartDays.newInstance(chartDays).getDisplayName(), BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow, backfillUseRTH);
                List<TagValue> chartOptions = new ArrayList<>();

                client.reqHistoricalData(tradestrategy.getRequestId(),
                        TWSBrokerModel.getIBContract(tradestrategy.getContract()), endDateTime,
                        ChartDays.newInstance(chartDays).getDisplayName(),
                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
                        backfillUseRTH, backfillDateFormat, chartOptions);

            } else {
                throw new BrokerModelException(tradestrategy.getRequestId(), 3100,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(tradestrategy.getRequestId(), 3110, "Error broker data Symbol: "
                    + tradestrategy.getContract().getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    public boolean isAccountUpdatesRunning(String accountNumber) {
        return accountRequests.containsKey(accountNumber);
    }

    public boolean isHistoricalDataRequestRunning(Contract contract) {

        for (Tradestrategy item : historyDataRequests.values()) {

            if (contract.equals(item.getContract())) {

                return true;
            }
        }
        return false;
    }

    public boolean isHistoricalDataRequestRunning(Tradestrategy tradestrategy) {

        return historyDataRequests.containsKey(tradestrategy.getRequestId());
    }

    public boolean isRealtimeBarsRequestRunning(Contract contract) {
        if (client.isConnected()) {
            return realTimeBarsRequests.containsKey(contract.getRequestId());
        }
        return false;
    }

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

    public boolean isMarketDataRequestRunning(Contract contract) {
        if (client.isConnected()) {
            return marketDataRequests.containsKey(contract.getRequestId());
        }
        return false;
    }

    public boolean isMarketDataRequestRunning(Tradestrategy tradestrategy) {
        if (marketDataRequests.containsKey(tradestrategy.getContract().getRequestId())) {
            Contract contract = marketDataRequests.get(tradestrategy.getContract().getRequestId());
            for (Tradestrategy item : contract.getTradestrategies()) {
                if (item.equals(tradestrategy)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onCancelAllRealtimeData() {

        if (client.isConnected()) {
            for (Tradestrategy tradestrategy : historyDataRequests.values()) {
                this.onCancelBrokerData(tradestrategy);
            }
            for (Contract contract : realTimeBarsRequests.values()) {
                this.onCancelRealtimeBars(contract);
            }
            for (Contract contract : marketDataRequests.values()) {
                this.onCancelMarketData(contract);
            }
            for (Contract contract : contractRequests.values()) {
                this.onCancelContractDetails(contract);
            }
        }
        contractRequests.clear();
        historyDataRequests.clear();
        realTimeBarsRequests.clear();
        marketDataRequests.clear();

    }

    public void onCancelAccountUpdates(String accountNumber) {
        synchronized (accountRequests) {
            if (accountRequests.containsKey(accountNumber)) {
                if (client.isConnected()) {
                    client.reqAccountUpdates(false, accountNumber);
                }
                accountRequests.remove(accountNumber);
            }
        }
    }

    public void onCancelContractDetails(Contract contract) {

        if (client.isConnected()) {

            if (contractRequests.contains(contract.getRequestId())) {

                synchronized (contractRequests) {
                    contractRequests.remove(contract.getRequestId());
                }
            }
        }
    }

    public void onCancelBrokerData(Tradestrategy tradestrategy) {

        if (historyDataRequests.containsKey(tradestrategy.getRequestId())) {

            if (client.isConnected()) {

                client.cancelHistoricalData(tradestrategy.getRequestId());
            }

            synchronized (historyDataRequests) {
                historyDataRequests.remove(tradestrategy.getRequestId());
                historyDataRequests.notify();
            }
        }
    }

    public void onCancelBrokerData(Contract contract) {

        for (Tradestrategy tradestrategy : historyDataRequests.values()) {

            if (contract.equals(tradestrategy.getContract())) {

                if (client.isConnected()) {
                    client.cancelHistoricalData(tradestrategy.getRequestId());
                }

                synchronized (historyDataRequests) {
                    historyDataRequests.remove(tradestrategy.getRequestId());
                    historyDataRequests.notify();
                }
            }
        }
    }

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
                onCancelMarketData(contract);
            }
        }
    }

    public void onCancelRealtimeBars(Contract contract) {

        if (realTimeBarsRequests.containsKey(contract.getRequestId())) {

            if (client.isConnected()) {

                client.cancelRealTimeBars(contract.getRequestId());
            }

            synchronized (realTimeBarsRequests) {
                realTimeBarsRequests.remove(contract.getRequestId());
            }
        }
    }

    public void onCancelMarketData(Tradestrategy tradestrategy) {
        if (marketDataRequests.containsKey(tradestrategy.getContract().getRequestId())) {

            Contract contract = marketDataRequests.get(tradestrategy.getContract().getRequestId());

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

        if (marketDataRequests.containsKey(contract.getRequestId())) {

            if (client.isConnected()) {

                client.cancelMktData(contract.getRequestId());
            }

            synchronized (marketDataRequests) {
                marketDataRequests.remove(contract.getRequestId());
            }
        }
    }

    public TradeOrder onPlaceOrder(Contract contract, TradeOrder tradeOrder) throws BrokerModelException {

        try {

            if (client.isConnected()) {

                synchronized (tradeOrder) {

                    if (null == tradeOrder.getOrderKey()) {

                        tradeOrder.setOrderKey(orderKey.getAndIncrement());
                    }

                    if (null == tradeOrder.getClientId()) {

                        tradeOrder.setClientId(this.clientId);
                    }
                    tradeOrder = tradeService.saveTradeOrder(tradeOrder);

                    _log.debug("Order Placed Key: {}", tradeOrder.getOrderKey());
                    com.ib.client.Contract IBContract = TWSBrokerModel.getIBContract(contract);
                    com.ib.client.Order IBOrder = TWSBrokerModel.getIBOrder(tradeOrder);

                    // Log to debug comment out for performance.
                    logContract(IBContract);
                    logTradeOrder(IBOrder);

                    client.placeOrder(tradeOrder.getOrderKey(), IBContract, IBOrder);
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

        if (client.isConnected()) {

            if (null != tradeOrder.getOrderKey()) {

                client.cancelOrder(tradeOrder.getOrderKey());
            }
        } else {

            throw new BrokerModelException(tradeOrder.getOrderKey(), 3140,
                    "Not conected to TWS order cannot be placed");
        }
    }

    public void execDetails(int reqId, com.ib.client.Contract contractIB, Execution execution) {

        try {

            TWSBrokerModel.logExecution(execution);
            TradeOrder instance = tradeService
                    .findTradeOrderByKey(Math.abs(execution.orderId()));

            if (null == instance) {

                /*
                 * If the executionDetails is null and the order does not exist
                 * then we have made a request for order executions with a
                 * different clientId than the one which created this order.
                 */
                if (null == tradeService.findTradeOrderfillByExecId(execution.execId())) {

                    executionDetails.put(execution.execId(), execution);
                }
                return;
            }

            /*
             * We already have this order fill.
             */

            if (instance.existTradeOrderfill(execution.execId())) {
                return;
            }

            TradeOrderfill tradeOrderfill = new TradeOrderfill();
            TWSBrokerModel.populateTradeOrderfill(execution, tradeOrderfill);
            tradeOrderfill.setTradeOrder(instance);
            instance.addTradeOrderfill(tradeOrderfill);
            instance.setAverageFilledPrice(tradeOrderfill.getAveragePrice());
            instance.setFilledQuantity(tradeOrderfill.getCumulativeQuantity());
            instance.setFilledDate(tradeOrderfill.getTime());
            boolean isFilled = instance.getIsFilled();
            instance = tradeService.saveTradeOrderfill(instance);

            // Let the controller know an order was filled
            if (instance.getIsFilled() && !isFilled) {
                this.fireTradeOrderFilled(instance);
            }
            tradeOrdersExecutions.put(instance.getOrderKey(), instance);
            _log.error("execDetails tradeOrdersExecutions reqId: {}", reqId);

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

                if (tradeService.existTradestrategyByRequestId(reqId)) {

                    Tradestrategy tradestrategy = tradeService.findTradestrategyByRequestId(reqId);
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
                    ConcurrentHashMap<Integer, TradeOrder> tradeOrders = new ConcurrentHashMap<>();

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
                                if (tradeOrder.getOrderCreateDate().isAfter(tradeOrderfill1.getTime())) {

                                    tradeOrder.setOrderCreateDate(tradeOrderfill1.getTime());
                                }
                            }
                        }
                        tradeOrder.setQuantity(quantity);
                        tradeOrders.put(tradeOrder.getOrderKey(), tradeOrder);
                    }

                    List<TradeOrder> orders = new ArrayList<>();

                    for (Integer orderKey : tradeOrders.keySet()) {

                        TradeOrder tradeOrder = tradeOrders.get(orderKey);
                        orders.add(tradeOrder);
                    }
                    orders.sort(TradeOrder.CREATE_ORDER);

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
                        tradeOrder = tradeService.saveTradeOrderfill(tradeOrder);
                        TradeOrder instance = tradeService
                                .findTradeOrderByKey(tradeOrder.getOrderKey());

                        // Let the controller know an order was filled
                        if (tradeOrder.getIsFilled()) {

                            this.fireTradeOrderFilled(instance);
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

            TradeOrder instance = tradeService.findTradeOrderByKey(order.orderId());

            if (null == instance) {

                error(orderId, 3170,
                        "Warning Order not found for Order Key: " + order.orderId() + " make sure Client ID: "
                                + this.clientId + " is not the master in TWS. On openOrder update.");
                instance = new TradeOrder();
                instance.setOrderKey(order.orderId());
                instance.setOrderCreateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                TWSBrokerModel.updateTradeOrder(order, orderState, instance);
                openOrders.put(instance.getOrderKey(), instance);
                return;
            }

            /*
             * Check to see if anything has changed as this method gets fired
             * twice on order fills.
             */

            if (TWSBrokerModel.updateTradeOrder(order, orderState, instance)) {

                if (OrderStatus.FILLED.equals(instance.getStatus())) {
                    _log.debug("Open order filled Order Key:{}", instance.getOrderKey());
                    instance = tradeService.saveTradeOrder(instance);

                    if (instance.hasTradePosition() && !instance.getTradePosition().isOpen()) {

                        // Let the controller know a position was closed
                        this.firePositionClosed(instance.getTradePosition());
                    }
                } else {

                    _log.debug("Open order state changed. Status:{}", orderState.status());
                    instance = tradeService.saveTradeOrder(instance);

                    if (OrderStatus.CANCELLED.equals(instance.getStatus())) {

                        // Let the controller know a position was closed
                        this.fireTradeOrderCancelled(instance);
                    } else {

                        this.fireTradeOrderStatusChanged(instance);
                    }
                }
            }
            openOrders.put(instance.getOrderKey(), instance);
        } catch (Exception ex) {

            error(orderId, 3180, "Errors updating open order: " + ex.getMessage());
        }
    }

    public void openOrderEnd() {

        _log.debug("openOrderEnd");
        // Let the controller know there are open orders
        for (TradeOrder openOrder : openOrders.values()) {

            _log.debug("openOrderEnd Open Order Key: {} Order status: {}", openOrder.getOrderKey(), openOrder.getStatus());
        }
        this.fireOpenOrderEnd(openOrders);
    }

    public void orderStatus(int orderId, String status, double filled, double remaining,
                            double avgFillPrice, int permId, int parentId, double lastFillPrice,
                            int clientId, String whyHeld) {

        try {

            TradeOrder instance = tradeService.findTradeOrderByKey(orderId);

            if (null == instance) {

                error(orderId, 3170, "Warning Order not found for Order Key: " + orderId + " make sure Client ID: "
                        + this.clientId + " is not the master in TWS. On orderStatus update.");
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

            if (CoreUtils.nullSafeComparator((int) filled, instance.getFilledQuantity()) == 1) {

                if (filled > 0) {

                    instance.setAverageFilledPrice(new BigDecimal(avgFillPrice));
                    instance.setFilledQuantity((int) filled);
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
                instance = tradeService.saveTradeOrder(instance);

                if (OrderStatus.CANCELLED.equals(instance.getStatus())) {

                    // Let the controller know a position was closed
                    this.fireTradeOrderCancelled(instance);
                } else {

                    this.fireTradeOrderStatusChanged(instance);
                    // Let the controller know an order was filled
                    if (instance.getIsFilled() && !isFilled) {
                        this.fireTradeOrderFilled(instance);
                    }
                }
            }
        } catch (Exception ex) {

            error(orderId, 3200, "Errors updating open order status: " + ex.getMessage());
        }
    }

    public void error(Exception ex) {

        _log.error("IBrokerModel error msg: {}", ex.getMessage());
        // this.fireBrokerError(new BrokerManagerModelException(ex));
    }

    public void error(String msg) {

        _log.error("IBrokerModel error str: {}", msg);
        // this.fireBrokerError(new BrokerManagerModelException(str));
    }

    public void error(int id, int code, String msg) {

        String symbol = "N/A";
        BrokerModelException brokerModelException;

        if (contractRequests.containsKey(id)) {

            symbol = contractRequests.get(id).getSymbol();
        }

        if (historyDataRequests.containsKey(id)) {

            Tradestrategy tradestrategy = historyDataRequests.get(id);
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
            synchronized (historyDataRequests) {
                historyDataRequests.remove(id);
                historyDataRequests.notify();
            }
        }

        if (realTimeBarsRequests.containsKey(id)) {

            symbol = realTimeBarsRequests.get(id).getSymbol();
        }

        if (marketDataRequests.containsKey(id)) {

            symbol = marketDataRequests.get(id).getSymbol();
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

            if (realTimeBarsRequests.containsKey(id)) {

                synchronized (realTimeBarsRequests) {
                    realTimeBarsRequests.remove(id);
                }
            }

            if (marketDataRequests.containsKey(id)) {

                synchronized (marketDataRequests) {
                    marketDataRequests.remove(id);
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
                if (!marketDataRequests.containsKey(reqId)) {
                    return;
                }
                Contract contract = marketDataRequests.get(reqId);

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
            if (Objects.requireNonNull(TickType.get(field)) == TickType.VOLUME) {
                if (realTimeBarsRequests.containsKey(reqId)) {
                    Contract contract = realTimeBarsRequests.get(reqId);

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
                                candle.setVolume(value * 100L);
                                candle.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                                datasetContainer.getBaseCandleSeries()
                                        .fireSeriesChanged();
                                _log.info("TickSize Symbol: {} {} : {}", tradestrategy.getContract()
                                        .getSymbol(), TickType.getField(field), value * 100);
                            }
                        }
                    }
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

                if (!marketDataRequests.containsKey(reqId)) {
                    return;
                }

                if (Objects.requireNonNull(TickType.get(field)) == TickType.RT_VOLUME) {/*
                 * If there is no price ignore this value.
                 */
                    if (value.startsWith(";")) {
                        return;
                    }

                    StringTokenizer st = new StringTokenizer(value, ";");
                    int tokenNumber = 0;
                    BigDecimal price = new BigDecimal(0);
                    ZonedDateTime time = null;
                    while (st.hasMoreTokens()) {

                        tokenNumber++;
                        String token = st.nextToken();

                        switch (tokenNumber) {
                            case 1: {
                                price = (BigDecimal.valueOf(Double.parseDouble(token))).setScale(SCALE,
                                        RoundingMode.HALF_EVEN);
                                break;
                            }
                            case 2: {
                                _log.debug("TickString Trade Size: {}", Integer.parseInt(token));
                                break;
                            }
                            case 3: {
                                time = TradingCalendar.getZonedDateTimeFromMilli(Long.parseLong(token));
                                break;
                            }
                            case 4: {
                                _log.debug("TickString Total Volume: {}", Integer.parseInt(token) * 100);
                                break;
                            }
                            case 5: {
                                _log.debug("TickString Total Vwap: {}", token);
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

                        Contract contract = marketDataRequests.get(reqId);
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
                }
            }
        } catch (Exception ex) {

            error(reqId, 3210, ex.getMessage());
        }
    }

    public void tickOptionComputation(int reqId, int field, double impliedVol, double delta, double optPrice,
                                      double pvDividend, double gamma, double vega, double theta, double undPrice) {

        _log.debug("tickOptionComputation:{}", reqId);
    }

    public void tickGeneric(int reqId, int tickType, double value) {

        _log.debug("tickGeneric: {} tickType: {} tickValue: {}", reqId, tickType, value);
    }

    public void tickEFP(int reqId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture,
                        int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {

        _log.debug("tickEFP:{}", reqId);
    }

    public void updatePortfolio(com.ib.client.Contract contract, int position, double marketPrice, double marketValue,
                                double averageCost, double unrealizedPNL, double realizedPNL, String accountNumber) {

        _log.debug("updatePortfolio Account#: {} contract:{} position:{} marketPrice:{} marketValue:{} averageCost:{} unrealizedPNL:{} realizedPNL:{}", accountNumber, contract.symbol(), position, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL);
    }

    public void updateAccountValue(String key, String value, String currency, String accountNumber) {

        synchronized (key) {

            _log.debug("updateAccountValue Account#: {} Key:{} Value:{} Currency:{}", accountNumber, key, value, currency);

            if (accountRequests.containsKey(accountNumber)) {

                Account account = accountRequests.get(accountNumber);

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

            _log.debug("updateAccountTime:{}", timeStamp);

            for (String accountNumber : accountRequests.keySet()) {

                Account account = accountRequests.get(accountNumber);
                synchronized (account) {
                    /*
                     * Don't use the incoming time stamp as this does not show
                     * seconds just HH:mm format.
                     */
                    if (account.isDirty()) {

                        account = tradeService.saveAspect(account, true);
                        accountRequests.replace(accountNumber, account);
                        this.fireUpdateAccountTime(accountNumber);
                    }
                }
            }
        } catch (Exception ex) {

            error(0, 3310, "Errors updating Trade Account: " + ex.getMessage());
        }
    }

    public void accountDownloadEnd(String accountNumber) {

        _log.debug("accountDownloadEnd: {}", accountNumber);
    }

    public Integer getNextRequestId() {

        return reqId.incrementAndGet();
    }

    public void nextValidId(int orderId) {

        try {

            _log.debug("nextValidId: {}", orderId);
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

    public void contractDetails(int reqId, ContractDetails contractDetails) {

        try {

            if (contractRequests.containsKey(reqId)) {

                Contract contract = contractRequests.get(reqId);
                // Refresh the contract as contractDetails and contract are the same in PolygonBroker
                // If the same contract is being back tested over multiple days it could have been updated
                // by a previous request.
                contract = tradeService.findContractById(contract.getId());
                TWSBrokerModel.logContractDetails(contractDetails);

                if (TWSBrokerModel.populateContract(contract, contractDetails)) {

                    contract = tradeService.saveAspect(contract);
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

        _log.debug("bondContractDetails:{}", reqId);
    }

    public void contractDetailsEnd(int reqId) {

        if (contractRequests.containsKey(reqId)) {

            synchronized (contractRequests) {
                contractRequests.remove(reqId);
            }
        }
    }

    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {

        _log.debug("updateMktDepth: {} {} {} {} {} {}", tickerId, position, operation, side, price, size);
    }

    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price,
                                 int size) {

        _log.debug("updateMktDepthL2: {} {} {} {} {} {}", tickerId, position, operation, side, price, size);
    }

    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {

        _log.debug("updateNewsBulletin: {} {} {} {}", msgId, msgType, message, origExchange);
    }

    public void managedAccounts(String accountNumbers) {

        try {

            _log.debug("Managed accounts: {}", accountNumbers);
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

            inputSource = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

            switch (faDataType) {
                case EClientSocket.ALIASES: {

                    _log.debug("Aliases: /n{}", xml);
                    final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
                    final Aspects aspects = (Aspects) request.fromXML(inputSource);

                    for (Aspect aspect : aspects.getAspects()) {

                        Account item = (Account) aspect;
                        Account account = tradeService.findAccountByAccountNumber(item.getAccountNumber());
                        if (null == account) {
                            account = new Account(item.getAccountNumber(), item.getAccountNumber(), Currency.USD,
                                    AccountType.INDIVIDUAL);
                        }
                        account.setAlias(item.getAlias());
                        account = tradeService.saveAspect(account);
                    }
                    client.requestFA(EClientSocket.GROUPS);
                    break;
                }
                case EClientSocket.PROFILES: {

                    _log.debug("Profiles: /n{}", xml);
                    final TWSAllocationRequest request = new TWSAllocationRequest();
                    final Aspects aspects = (Aspects) request.fromXML(inputSource);
                    for (Aspect aspect : aspects.getAspects()) {
                        tradeService.savePortfolio((Portfolio) aspect);
                    }
                    this.fireFAAccountsCompleted();
                    break;
                }
                case EClientSocket.GROUPS: {

                    _log.debug("Groups: /n{}", xml);
                    final TWSGroupRequest request = new TWSGroupRequest();
                    final Aspects aspects = (Aspects) request.fromXML(inputSource);

                    for (Aspect aspect : aspects.getAspects()) {

                        tradeService.savePortfolio((Portfolio) aspect);
                    }
                    client.requestFA(EClientSocket.PROFILES);
                    break;
                }
                default: {
                    _log.debug("receiveFA: /n{}", xml);
                }
            }
        } catch (Exception ex) {

            error(faDataType, 3235, ex.getMessage());
        } finally {

            try {
                if (null != inputSource) {
                    inputSource.close();
                }
            } catch (IOException ex) {

                error(faDataType, 3236, ex.getMessage());
            }
        }
    }

    public void marketDataType(int reqId, int marketDataType) {

        _log.debug("marketDataType: {} {}", reqId, marketDataType);
    }

    public void historicalData(int reqId, String dateString, double open, double high, double low, double close,
                               int volume, int tradeCount, double vwap, boolean hasGaps) {
        try {

            volume = volume * 100;

            if (historyDataRequests.containsKey(reqId)) {

                Tradestrategy tradestrategy = historyDataRequests.get(reqId);

                if (dateString.contains("finished-")) {

                    historicalDataComplete(reqId);
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
                                tradestrategy.getTradingday().getClose(), date)) {
                            return;
                        }
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

    public void historicalDataComplete(int reqId) {

        try {

            if (historyDataRequests.containsKey(reqId)) {

                Tradestrategy tradestrategy = historyDataRequests.get(reqId);
                CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();
                tradeService.saveCandleSeries(candleSeries);

                _log.debug("HistoricalDataComplete complete Req Id: {} Symbol: {} Tradingday: {} candles to saved: {} Contract Tradestrategies size:: {}", reqId, tradestrategy.getContract().getSymbol(), tradestrategy.getTradingday().getOpen(), candleSeries.getItemCount(), tradestrategy.getContract().getTradestrategies().size());

                /*
                 * The last one has arrived the reqId is the
                 * tradeStrategyId. Remove this from the processing List.
                 */

                synchronized (historyDataRequests) {

                    historyDataRequests.remove(reqId);
                    historyDataRequests.notify();
                }

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
            if (realTimeBarsRequests.containsKey(reqId)) {

                Contract contract = realTimeBarsRequests.get(reqId);

                synchronized (contract) {

                    contract.getTradestrategies().sort(Tradestrategy.TRADINGDAY_CONTRACT);
                    boolean updateCandleDB = true;

                    for (Tradestrategy tradestrategy : contract.getTradestrategies()) {

                        StrategyData strategyData = tradestrategy.getStrategyData();

                        if (TradingCalendar.isMarketHours(tradestrategy.getTradingday().getOpen(),
                                tradestrategy.getTradingday().getClose(), date)) {

                            if (!this.isMarketDataRequestRunning(contract)) {

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

                                    tradeService.saveAspect(candleItem.getCandle());
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
            TradeOrderfill instance = tradeService.findTradeOrderfillByExecId(commsReport.m_execId);

            if (null != instance) {

                TradeOrder tradeOrder = tradeService
                        .findTradeOrderByKey(instance.getTradeOrder().getOrderKey());

                for (TradeOrderfill tradeOrderfill : tradeOrder.getTradeOrderfills()) {

                    if (tradeOrderfill.getExecId().equals(commsReport.m_execId)) {

                        tradeOrderfill.setCommission(new BigDecimal(commsReport.m_commission));
                        tradeService.saveTradeOrderfill(tradeOrderfill.getTradeOrder());
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
        //if (null != contract.getIdContractIB()) {
        // ibContract.m_conId = contract.getIdContractIB();
        //}
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

            ibOrder.overridePercentageConstraints(order.getOverrideConstraints() != 0);
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

    private static boolean updateTradeOrder(com.ib.client.Order ibOrder, OrderState ibOrderState,
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

            Integer overridePercentageConstraints = (ibOrder.overridePercentageConstraints() ? 1 : 0);

            if (CoreUtils.nullSafeComparator(order.getOverrideConstraints(), overridePercentageConstraints) != 0) {

                order.setOverrideConstraints(overridePercentageConstraints);
                changed = true;
            }

            if (CoreUtils.nullSafeComparator(order.getAllOrNothing(), ibOrder.allOrNone()) != 0) {

                order.setAllOrNothing(ibOrder.allOrNone());
                changed = true;
            }
            if (changed) {
                order.setOrderUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            }
        }
        return changed;
    }

    private static boolean populateContract(Contract contract, ContractDetails transientContract) {

        /*
         * For stock the localsymbol must match. For futues they will not e.g
         * Symbol ES Local will be ES06. TODO Need to find out how to handle
         * same symbol different local symbols when using exchange SMART.
         */
        if (CoreUtils.nullSafeComparator(contract.getSymbol(), transientContract.contract().localSymbol()) != 0
                && SECType.STOCK.equals(contract.getSecType())) {

            return false;
        }

        if (CoreUtils.nullSafeComparator(contract.getSymbol(), transientContract.contract().symbol()) == 0) {

            if (CoreUtils.nullSafeComparator(contract.getLocalSymbol(),
                    transientContract.contract().localSymbol()) != 0) {

                contract.setLocalSymbol(transientContract.contract().localSymbol());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getContractIBId(),
                    transientContract.contract().conid()) != 0) {

                contract.setContractIBId(transientContract.contract().conid());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getPrimaryExchange(),
                    transientContract.contract().primaryExch()) != 0) {

                contract.setPrimaryExchange(transientContract.contract().primaryExch());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getExchange(),
                    transientContract.contract().exchange()) != 0) {

                contract.setExchange(transientContract.contract().exchange());
                contract.setDirty(true);
            }

            if (null != transientContract.contract().lastTradeDateOrContractMonth()) {

                ZonedDateTime expiryDateTime = TradingCalendar.getZonedDateTimeFromDateString(
                        transientContract.contract().lastTradeDateOrContractMonth(), "yyyyMMdd", TradingCalendar.MKT_TIMEZONE);

                if (CoreUtils.nullSafeComparator(contract.getExpiry(), expiryDateTime) != 0) {

                    contract.setExpiry(expiryDateTime);
                    contract.setDirty(true);
                }
            }

            if (CoreUtils.nullSafeComparator(contract.getSecIdType(),
                    transientContract.contract().secIdType().getApiString()) != 0) {

                contract.setSecIdType(transientContract.contract().secIdType().getApiString());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getLongName(), transientContract.longName()) != 0) {

                contract.setLongName(transientContract.longName());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getCurrency(),
                    transientContract.contract().currency()) != 0) {

                contract.setCurrency(transientContract.contract().currency());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getCategory(), transientContract.category()) != 0) {

                contract.setCategory(transientContract.category());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getIndustry(), transientContract.industry()) != 0) {

                contract.setIndustry(transientContract.industry());
                contract.setDirty(true);
            }

            Money minTick = new Money(transientContract.minTick());

            if (CoreUtils.nullSafeComparator(minTick, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(contract.getMinTick(), minTick.getBigDecimalValue()) != 0) {

                contract.setMinTick(minTick.getBigDecimalValue());
                contract.setDirty(true);
            }

            Money priceMagnifier = new Money(transientContract.priceMagnifier());

            if (CoreUtils.nullSafeComparator(priceMagnifier, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(contract.getPriceMagnifier(),
                    priceMagnifier.getBigDecimalValue()) != 0) {

                contract.setPriceMagnifier(priceMagnifier.getBigDecimalValue());
                contract.setDirty(true);
            }

            Money multiplier = new Money(transientContract.contract().multiplier());

            if (CoreUtils.nullSafeComparator(multiplier, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(contract.getPriceMultiplier(), multiplier.getBigDecimalValue()) != 0) {

                contract.setPriceMultiplier(multiplier.getBigDecimalValue());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getSubCategory(), transientContract.subcategory()) != 0) {

                contract.setSubCategory(transientContract.subcategory());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getTradingClass(),
                    transientContract.contract().tradingClass()) != 0) {

                contract.setTradingClass(transientContract.contract().tradingClass());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getComboLegDescription(),
                    transientContract.contract().comboLegsDescrip()) != 0) {

                contract.setComboLegDescription(transientContract.contract().comboLegsDescrip());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getContractMonth(),
                    transientContract.contractMonth()) != 0) {

                contract.setContractMonth(transientContract.contractMonth());
                contract.setDirty(true);
            }

            Money evMultiplier = new Money(transientContract.evMultiplier());

            if (CoreUtils.nullSafeComparator(evMultiplier, new Money(Double.MAX_VALUE)) != 0 && CoreUtils
                    .nullSafeComparator(contract.getEvMultiplier(), evMultiplier.getBigDecimalValue()) != 0) {

                contract.setEvMultiplier(evMultiplier.getBigDecimalValue());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getEvRule(), transientContract.evRule()) != 0) {

                contract.setEvRule(transientContract.evRule());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getIncludeExpired(),
                    transientContract.contract().includeExpired()) != 0) {

                contract.setIncludeExpired(transientContract.contract().includeExpired());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getLiquidHours(), transientContract.liquidHours()) != 0) {

                contract.setLiquidHours(transientContract.liquidHours());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getMarketName(), transientContract.marketName()) != 0) {

                contract.setMarketName(transientContract.marketName());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getOrderTypes(), transientContract.orderTypes()) != 0) {

                String orderTypes = OrderType.MKT;

                if (transientContract.orderTypes().contains(OrderType.STP)) {

                    orderTypes = orderTypes + "," + OrderType.STP;
                    contract.setDirty(true);
                }

                if (transientContract.orderTypes().contains(OrderType.STPLMT)) {

                    orderTypes = orderTypes + "," + OrderType.STPLMT;
                    contract.setDirty(true);
                }

                if (transientContract.orderTypes().contains(OrderType.LMT)) {

                    orderTypes = orderTypes + "," + OrderType.LMT;
                    contract.setDirty(true);
                }
                contract.setOrderTypes(orderTypes);

            }

            if (CoreUtils.nullSafeComparator(contract.getSecId(), transientContract.contract().secId()) != 0) {

                contract.setSecId(transientContract.contract().secId());
                contract.setDirty(true);
            }

            Money strike = new Money(transientContract.contract().strike());

            if (CoreUtils.nullSafeComparator(strike, new Money(Double.MAX_VALUE)) != 0
                    && CoreUtils.nullSafeComparator(contract.getStrike(), strike.getBigDecimalValue()) != 0) {

                contract.setStrike(strike.getBigDecimalValue());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getTimeZoneId(), transientContract.timeZoneId()) != 0) {

                contract.setTimeZoneId(transientContract.timeZoneId());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getTradingHours(),
                    transientContract.tradingHours()) != 0) {

                contract.setTradingHours(transientContract.tradingHours());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getUnderConId(),
                    transientContract.underConid()) != 0) {

                contract.setUnderConId(transientContract.underConid());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getValidExchanges(),
                    transientContract.validExchanges()) != 0) {

                contract.setValidExchanges(transientContract.validExchanges());
                contract.setDirty(true);
            }

            if (CoreUtils.nullSafeComparator(contract.getOptionType(),
                    transientContract.contract().right().getApiString()) != 0) {

                contract.setOptionType(transientContract.contract().right().getApiString());
                contract.setDirty(true);
            }
        }

        return contract.isDirty();
    }

    public static void populateTradeOrderfill(Execution execution, TradeOrderfill tradeOrderfill) {

        ZonedDateTime date = TradingCalendar.getZonedDateTimeFromDateTimeString(execution.time().replaceAll("\\s", ""),
                "yyyyMMddHH:mm:ss", TradingCalendar.LOCAL_TIMEZONE);
        tradeOrderfill.setTime(date);
        tradeOrderfill.setExchange(execution.exchange());
        tradeOrderfill.setSide(execution.side());
        tradeOrderfill.setQuantity((int) execution.shares());
        tradeOrderfill.setPrice(BigDecimal.valueOf(execution.price()));
        tradeOrderfill.setAveragePrice(BigDecimal.valueOf(execution.avgPrice()));
        tradeOrderfill.setAccountNumber(execution.acctNumber());
        tradeOrderfill.setCumulativeQuantity(execution.cumQty());
        tradeOrderfill.setExecId(execution.execId());
        tradeOrderfill.setOrderReference(execution.orderRef());
        tradeOrderfill.setPermId(execution.permId());
        tradeOrderfill.setDirty(true);
    }

    private static com.ib.client.ExecutionFilter getIBExecutionFilter(Integer clientId, ZonedDateTime mktOpen,
                                                                      String secType, String symbol) {

        com.ib.client.ExecutionFilter executionFilter = new com.ib.client.ExecutionFilter();

        if (null != secType) {

            executionFilter.secType(secType);
        }

        if (null != symbol) {

            executionFilter.symbol(symbol);
        }
        if (null != mktOpen) {

            executionFilter.time(TradingCalendar.getFormattedDate(mktOpen, "yyyyMMdd"));
        }

        if (null != clientId) {

            executionFilter.clientId(clientId);
        }
        return executionFilter;
    }

    public static void logOrderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice,
                                      int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

        _log.info("orderId: {} status: {} filled: {} remaining: {} avgFillPrice: {} permId: {} parentId: {} lastFillPrice: {} clientId: {} whyHeld: {}", orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
    }

    public static void logTradeOrder(com.ib.client.Order order) {

        _log.debug("OrderKey: {} ClientId: {} PermId: {} Action: {} TotalQuantity: {} OrderType: {} LmtPrice: {} AuxPrice: {} Tif: {} OcaGroup: {} OcaType: {} OrderRef: {} Transmit: {} DisplaySize: {} TriggerMethod: {} Hidden: {} ParentId: {} GoodAfterTime: {} GoodTillDate: {} TrailStopPrice: {} TrailingPercent: {} OverridePercentageConstraints: {} AllOrNone: {} Account: {} FAGroup: {} FAMethod: {} FAPercent: {} FAProfile: {}", order.orderId(), order.clientId(), order.permId(), order.action(), order.totalQuantity(), order.orderType(), order.lmtPrice(), order.auxPrice(), order.tif(), order.ocaGroup(), order.ocaType(), order.orderRef(), order.transmit(), order.displaySize(), order.triggerMethod(), order.hidden(), order.parentId(), order.goodAfterTime(), order.goodTillDate(), order.trailStopPrice(), order.trailingPercent(), order.overridePercentageConstraints(), order.allOrNone(), order.account(), order.faGroup(), order.faMethod(), order.faPercentage(), order.faProfile());
    }


    public static void logContract(com.ib.client.Contract contract) {

        _log.debug("Symbol: {} Sec Type: {} Exchange: {} Con Id: {} Currency: {} SecIdType: {} Primary Exch: {} Local Symbol: {} SecId: {} Multiplier: {} lastTradeDateOrContractMonth: {}", contract.symbol(), contract.secType(), contract.exchange(), contract.conid(), contract.currency(), contract.secIdType(), contract.primaryExch(), contract.localSymbol(), contract.secId(), contract.multiplier(), contract.lastTradeDateOrContractMonth());
    }

    private static void logContractDetails(ContractDetails contractDetails) {

        _log.debug("Symbol: {} Sec Type: {} Exchange: {} con Id: {} Currency: {} SecIdType: {} Primary Exch: {} Local Symbol: {} SecId: {} Multiplier: {} Category: {} last TradeDate Or Contract Month: {} ContractMonth: {} Cusip: {} Industry: {} IssueDate: {} MarketName: {} MinTick: {} PriceMagnifier: {}", contractDetails.contract().symbol(), contractDetails.contract().secType(), contractDetails.contract().exchange(), contractDetails.contract().conid(), contractDetails.contract().currency(), contractDetails.contract().secIdType(), contractDetails.contract().primaryExch(), contractDetails.contract().localSymbol(), contractDetails.contract().secId(), contractDetails.contract().multiplier(), contractDetails.category(), contractDetails.contract().lastTradeDateOrContractMonth(), contractDetails.contractMonth(), contractDetails.cusip(), contractDetails.industry(), contractDetails.issueDate(), contractDetails.marketName(), contractDetails.minTick(), contractDetails.priceMagnifier());
    }

    private static void logOrderState(OrderState orderState) {

        _log.debug("Status: {} Comms Amt: {} Comms Currency: {} Warning txt: {} Init Margin: {} Maint Margin: {} Min Comms: {} Max Comms: {}", orderState.status(), orderState.commission(), orderState.commissionCurrency(), orderState.warningText(), orderState.initMargin(), orderState.maintMargin(), orderState.minCommission(), orderState.maxCommission());
    }

    private static void logExecution(Execution execution) {

        _log.debug("execDetails OrderId: {} ClientId: {} PermId: {} ExecId: {} Time: {} CumQty: {}", execution.orderId(), execution.clientId(), execution.permId(), execution.execId(), execution.time(), execution.cumQty());
    }


    private static void logCommissionReport(CommissionReport commissionReport) {

        _log.debug("execDetails ExecId: {} Commission: {} Currency: {} RealizedPNL: {} yieldRedemptionDate: {} Yield: {}", commissionReport.m_execId, commissionReport.m_commission, commissionReport.m_currency, commissionReport.m_realizedPNL, commissionReport.m_yieldRedemptionDate, commissionReport.m_yield);

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

        _log.debug("currentTime: {}", new Date(time));
    }

    public void fundamentalData(int reqId, String data) {

        _log.debug("fundamentalData: {} {}", reqId, data);
    }

    public void tickSnapshotEnd(int reqId) {

        _log.debug("tickSnapshotEnd: {}", reqId);
    }

    public void accountSummary(int arg0, String arg1, String arg2, String arg3, String arg4) {

        _log.debug("accountSummary: {} {} {} {} {}", arg0, arg1, arg2, arg3, arg4);
    }

    public void accountSummaryEnd(int reqId) {

        _log.debug("accountSummaryEnd: {}", reqId);
    }

    public void position(String arg0, com.ib.client.Contract arg1, int arg2, double arg3) {

        _log.debug("position: {} {} {} {}", arg0, arg1.toString(), arg2, arg3);
    }

    public void positionEnd() {

        _log.debug("positionEnd: ");
    }

    public void displayGroupList(int arg0, String arg1) {

        _log.debug("displayGroupList: {} {}", arg0, arg1);
    }


    public void displayGroupUpdated(int arg0, String arg1) {

        _log.debug("displayGroupUpdated: {} {}", arg0, arg1);
    }

    public void verifyCompleted(boolean arg0, String arg1) {

        _log.debug("verifyCompleted: {} {}", arg0, arg1);
    }

    public void verifyMessageAPI(String arg0) {

        _log.debug("verifyMessageAPI: {}", arg0);
    }

    public void issueSignal() {

        _log.debug("issueSignal: ");
    }

    public void waitForSignal() {

        _log.debug("waitForSignal: ");
    }

    public void scannerParameters(String xml) {

        _log.debug("scannerParameters: {}", xml);
    }

    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark,
                            String projection, String legsStr) {

        _log.debug("scannerData: {}", reqId);
    }

    public void scannerDataEnd(int reqId) {

        _log.debug("scannerDataEnd: {}", reqId);
    }
}
