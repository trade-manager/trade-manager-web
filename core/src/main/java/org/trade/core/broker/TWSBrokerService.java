package org.trade.core.broker;

import com.ib.client.CommissionReport;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.TagValue;
import com.ib.client.TickType;
import com.ib.client.Types;
import com.ib.controller.Alias;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.Bar;
import com.ib.controller.Formats;
import com.ib.controller.Group;
import com.ib.controller.Position;
import com.ib.controller.Profile;
import org.slf4j.LoggerFactory;
import org.trade.core.broker.client.Broker;
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
import org.trade.core.valuetype.SECType;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TriggerMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TWSBrokerService extends AbstractBrokerModel {

    private final static org.slf4j.Logger _log = LoggerFactory.getLogger(TWSBrokerService.class);

    private TradeService tradeService;

    // Use getId as key
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
    private static final Integer backfillDateFormat = 2;
    private static Integer backfillUseRTH = 1;
    private static final String backfillWhatToShow;
    private static Integer backfillOffsetDays = 0;
    private static final int SCALE = 5;

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
    private static String genericTicklist = "233";
    private static boolean marketUpdateOnClose;

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

    // APIController vars
    private final ILogger inLogger = new TWSLogger(Logger.getLogger(TWSBrokerService.class.getName()));
    private final ILogger outLogger = new TWSLogger(Logger.getLogger(TWSBrokerService.class.getName()));
    private Integer clientId = null;
    private final AtomicInteger reqId;
    private final AtomicInteger orderKey = null;
    private ApiController controller;

    public TWSBrokerService(TradeService tradeService) {

        try {

            this.tradeService = tradeService;
            reqId = new AtomicInteger((int) (System.currentTimeMillis() / 1000d));

        } catch (Exception ex) {

            throw new IllegalArgumentException("Error initializing IBrokerModel Msg: " + ex.getMessage());
        }
    }

    @Override
    public void onConnect(String host, Integer port, Integer clientId) {

        this.clientId = clientId;
        controller().connect(host, port, clientId, null);
        openOrders.clear();
    }

    @Override
    public boolean isConnected() {
        return controller().client().isConnected();
    }

    @Override
    public void onDisconnect() {

        onCancelAllRealtimeData();

        if (isConnected()) {

            for (String accountNumber : accountRequests.keySet()) {

                this.onCancelAccountUpdates(accountNumber);
            }
            controller().disconnect();
        }
        this.fireConnectionClosed(false);
    }

    @Override
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

    @Override
    public Integer getNextRequestId() {

        return reqId.incrementAndGet();
    }

    @Override
    public Broker getBackTestBroker(Integer reqId) {

        return null;
    }

    @Override
    public void onSubscribeAccountUpdates(boolean subscribe, String accountNumber) {
        try {
            Account account = tradeService.findAccountByAccountNumber(accountNumber);
            accountRequests.put(accountNumber, account);
            if (controller().client().isConnected()) {
                controller().reqAccountUpdates(subscribe, accountNumber, new AccountHandler(this, accountNumber));
//               controller().client().reqAccountUpdates(subscribe, accountNumber);
            } else {
                throw new BrokerModelException(0, 3010,
                        "Not conected to TWS historical account data cannot be retrieved");
            }

        } catch (Exception ex) {
            error(0, 3290, "Error requesting Account: " + accountNumber + " Msg: " + ex.getMessage());
        }
    }

    @Override
    public void onCancelAccountUpdates(String accountNumber) {
        synchronized (accountRequests) {
            if (accountRequests.containsKey(accountNumber)) {
                if (controller().client().isConnected()) {
                    controller().reqAccountUpdates(false, accountNumber, new AccountHandler(this, accountNumber));
//                    controller().client().reqAccountUpdates(false, accountNumber);
                }
                accountRequests.remove(accountNumber);
            }
        }
    }

    @Override
    public void onReqFinancialAccount() {
        try {
            if (controller().client().isConnected()) {
                controller().reqAdvisorData(Types.FADataType.ALIASES, new AdvisorHandler(this));
                //     controller().client().requestFA(EClientSocket.ALIASES);
            } else {
                throw new BrokerModelException(0, 3010, "Not conected Financial Account data cannot be retrieved");
            }
        } catch (Exception ex) {
            error(0, 3295, "Error requesting Financial Account Msg: " + ex.getMessage());
        }
    }

    @Override
    public void onReqReplaceFinancialAccount(int faDataType, String xml) {

    }

    @Override
    public void onReqManagedAccount() {

    }

    @Override
    public void onReqAllOpenOrders() throws BrokerModelException {
        // request list of all open orders
        if (controller().client().isConnected()) {
            openOrders.clear();
            controller().reqLiveOrders(new LiveOrderHandler(this));
//            controller().client().reqAllOpenOrders();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }
    }

    @Override
    public void onReqOpenOrders() throws BrokerModelException {
        // request list of all open orders
        if (controller().client().isConnected()) {
            openOrders.clear();
            controller().reqLiveOrders(new LiveOrderHandler(this));
//            controller().client().reqOpenOrders();
        } else {
            throw new BrokerModelException(0, 3010, "Not conected to TWS historical data cannot be retrieved");
        }

    }

    @Override
    public void onBrokerData(Tradestrategy tradestrategy, ZonedDateTime endDate) throws BrokerModelException {
        try {

            if (controller().client().isConnected()) {
                if (this.isHistoricalDataRunning(tradestrategy)) {
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
                if (!tradestrategy.getStrategyData().isRunning())
                    tradestrategy.getStrategyData().execute();

                historyDataRequests.put(Objects.requireNonNull(tradestrategy.getRequestId()), tradestrategy);

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

                controller().reqHistoricalData(TWSBrokerModel.getIBContract(tradestrategy.getContract()), endDateTime, chartDays, Types.DurationUnit.DAY, Types.BarSize.valueOf(BarSize.newInstance(tradestrategy.getBarSize()).getCode()),
                        Types.WhatToShow.valueOf(backfillWhatToShow), (backfillUseRTH == 1), new HistoricalDataHandler(this, tradestrategy.getRequestId()));

//                controller().client().reqHistoricalData(tradestrategy.getId(),
//                        TWSBrokerModel.getIBContract(tradestrategy.getContract()), endDateTime,
//                        ChartDays.newInstance(chartDays).getDisplayName(),
//                        BarSize.newInstance(tradestrategy.getBarSize()).getDisplayName(), backfillWhatToShow,
//                        backfillUseRTH, backfillDateFormat, chartOptions);

            } else {
                throw new BrokerModelException(tradestrategy.getRequestId(), 3100,
                        "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(tradestrategy.getRequestId(), 3110, "Error broker data Symbol: "
                    + tradestrategy.getContract().getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    @Override
    public void onReqRealTimeBars(Contract contract, boolean mktData) throws BrokerModelException {
        try {
            if (controller().client().isConnected()) {

                if (this.isRealtimeBarsRunning(contract)) {
                    throw new BrokerModelException(contract.getRequestId(), 3030,
                            "RealtimeBars request is already in progress for: " + contract.getSymbol()
                                    + " Please wait or cancel.");
                }
                realTimeBarsRequests.put(Objects.requireNonNull(contract.getRequestId()), contract);

                /*
                 * Bar interval is set to 5= 5sec this is the only thing
                 * supported by TWS for live data.
                 */
                ArrayList<TagValue> realTimeBarOptions = new ArrayList<>();
                controller().reqRealTimeBars(TWSBrokerModel.getIBContract(contract), Types.WhatToShow.valueOf(backfillWhatToShow), (backfillUseRTH == 1), new RealTimeBarHandler(this, contract.getRequestId()));

//                controller().client().reqRealTimeBars(contract.getId(), TWSBrokerModel.getIBContract(contract), 5,
//                        backfillWhatToShow, (backfillUseRTH > 0), realTimeBarOptions);

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

    @Override
    public void onReqMarketData(Contract contract, String genericTicklist, boolean snapshot) throws BrokerModelException {
        try {
            if (controller().client().isConnected()) {
                if (this.isMarketDataRunning(contract)) {
                    throw new BrokerModelException(contract.getRequestId(), 3030,
                            "MarketData request is already in progress for: " + contract.getSymbol()
                                    + " Please wait or cancel.");
                }
                List<TagValue> mktDataOptions = new ArrayList<>();
                marketDataRequests.put(Objects.requireNonNull(contract.getRequestId()), contract);
                controller().reqTopMktData(TWSBrokerModel.getIBContract(contract), genericTicklist, snapshot, new TopMktDataHandler(this, contract.getRequestId()));

//                controller().client().reqMktData(contract.getRequestId(), TWSBrokerModel.getIBContract(contract), genericTicklist, snapshot,
//                        mktDataOptions);

            } else {
                throw new BrokerModelException(contract.getRequestId(), 3040,
                        "Not conected to TWS market data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(contract.getRequestId(), 3050,
                    "Error broker data Symbol: " + contract.getSymbol() + " Msg: " + ex.getMessage());
        }
    }

    @Override
    public void onReqAllExecutions(ZonedDateTime mktOpenDate) throws BrokerModelException {
        try {
            /*
             * Request execution reports based on the supplied filter criteria
             */

            if (controller().client().isConnected()) {

                tradeOrdersExecutions.clear();
                commissionDetails.clear();
                executionDetails.clear();
                Integer reqId = this.getNextRequestId();
                controller().reqExecutions(getIBExecutionFilter(clientId, mktOpenDate,
                                null, null)
                        , new TradeReportHandler(this, reqId));
//                controller().client().reqExecutions(reqId, getIBExecutionFilter(m_clientId, mktOpenDate, null, null));
            } else {
                throw new BrokerModelException(0, 3020, "Not conected to TWS historical data cannot be retrieved");
            }
        } catch (Exception ex) {
            throw new BrokerModelException(0, 3020,
                    "Error request executions for Date: " + mktOpenDate + " Msg: " + ex.getMessage());
        }
    }

    @Override
    public void onReqExecutions(Tradestrategy tradestrategy, boolean addOrders) throws BrokerModelException {
        try {
            /*
             * Request execution reports based on the supplied filter criteria
             */
            Integer clientId = this.clientId;
            if (controller().client().isConnected()) {
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
                controller().reqExecutions(getIBExecutionFilter(clientId, tradestrategy.getTradingday().getOpen(),
                                tradestrategy.getContract().getSecType(), tradestrategy.getContract().getSymbol())
                        , new TradeReportHandler(this, reqId));

//                controller().client().reqExecutions(reqId,
//                        TWSBrokerModel.getIBExecutionFilter(clientId, tradestrategy.getTradingday().getOpen(),
//                                tradestrategy.getContract().getSecType(), tradestrategy.getContract().getSymbol()));
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

    @Override
    public boolean isHistoricalDataRunning(Contract contract) {
        for (Tradestrategy item : historyDataRequests.values()) {
            if (contract.equals(item.getContract())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRealtimeBarsRunning(Tradestrategy tradestrategy) {

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

    @Override
    public boolean isRealtimeBarsRunning(Contract contract) {
        if (controller().client().isConnected()) {
            return realTimeBarsRequests.containsKey(contract.getRequestId());
        }
        return false;
    }

    @Override
    public boolean isMarketDataRunning(Tradestrategy tradestrategy) {

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

    @Override
    public boolean isMarketDataRunning(Contract contract) {
        if (controller().client().isConnected()) {
            return marketDataRequests.containsKey(contract.getRequestId());
        }
        return false;
    }

    @Override
    public boolean isHistoricalDataRunning(Tradestrategy tradestrategy) {
        return historyDataRequests.containsKey(tradestrategy.getRequestId());
    }

    @Override
    public boolean isAccountUpdatesRunning(String accountNumber) {
        return accountRequests.containsKey(accountNumber);
    }

    @Override
    public void onCancelAllRealtimeData() {
        if (controller().client().isConnected()) {
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

    @Override
    public void onCancelRealtimeBars(Contract contract) {

        if (realTimeBarsRequests.containsKey(contract.getRequestId())) {

            if (controller().client().isConnected()) {

                controller().client().cancelRealTimeBars(contract.getRequestId());
            }

            synchronized (realTimeBarsRequests) {
                realTimeBarsRequests.remove(Objects.requireNonNull(contract.getRequestId()));
            }
        }
    }

    @Override
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

    @Override
    public void onCancelMarketData(Contract contract) {

        if (marketDataRequests.containsKey(contract.getRequestId())) {

            if (controller().client().isConnected()) {

                controller().client().cancelMktData(contract.getRequestId());
            }

            synchronized (marketDataRequests) {

                marketDataRequests.remove(Objects.requireNonNull(contract.getRequestId()));
            }
        }
    }

    @Override
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

    @Override
    public void onCancelBrokerData(Tradestrategy tradestrategy) {

        if (historyDataRequests.containsKey(tradestrategy.getRequestId())) {

            if (controller().client().isConnected()) {
                controller().client().cancelHistoricalData(tradestrategy.getRequestId());
            }

            synchronized (historyDataRequests) {
                historyDataRequests.remove(Objects.requireNonNull(tradestrategy.getRequestId()));
                historyDataRequests.notify();
            }
        }
    }

    @Override
    public void onCancelBrokerData(Contract contract) {

        for (Tradestrategy tradestrategy : historyDataRequests.values()) {

            if (contract.equals(tradestrategy.getContract())) {

                if (controller().client().isConnected()) {

                    controller().client().cancelHistoricalData(tradestrategy.getRequestId());
                }
                synchronized (historyDataRequests) {

                    historyDataRequests.remove(Objects.requireNonNull(tradestrategy.getRequestId()));
                    historyDataRequests.notify();
                }
            }
        }
    }

    @Override
    public void onCancelContractDetails(Contract contract) {

        if (controller().client().isConnected()) {

            if (contractRequests.contains(contract.getRequestId()))

                synchronized (contractRequests) {

                    contractRequests.remove(Objects.requireNonNull(contract.getRequestId()));
                }
        }
    }

    @Override
    public void onContractDetails(Contract contract) throws BrokerModelException {
        try {
            if (controller().client().isConnected()) {
                if (!contractRequests.containsKey(contract.getRequestId())) {
                    /*
                     * Null the IB Contract Id as these sometimes change. This
                     * will force a get of the IB data via the
                     * Exchange/Symbol/Currency.
                     */
                    contract.setContractIBId(null);
                    contractRequests.put(Objects.requireNonNull(contract.getRequestId()), contract);
                    logContract(TWSBrokerModel.getIBContract(contract));
                    controller().client().reqContractDetails(contract.getRequestId(), TWSBrokerModel.getIBContract(contract));
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

    @Override
    public ConcurrentHashMap<Integer, Tradestrategy> getHistoricalData() {
        return historyDataRequests;
    }

    @Override
    public TradeOrder onPlaceOrder(Contract contract, TradeOrder tradeOrder) throws BrokerModelException {
        try {
            if (controller().client().isConnected()) {
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
                    Order IBOrder = TWSBrokerModel.getIBOrder(tradeOrder);

                    // Log to debug comment out for performance.
                    logContract(IBContract);
                    logTradeOrder(IBOrder);
                    controller().placeOrModifyOrder(IBContract, IBOrder, new OrderHandler(this, tradeOrder.getOrderKey()));
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

    @Override
    public void onCancelOrder(TradeOrder tradeOrder) throws BrokerModelException {
        if (controller().client().isConnected()) {
            if (null != tradeOrder.getOrderKey()) {
                controller().client().cancelOrder(tradeOrder.getOrderKey());
            }
        } else {
            throw new BrokerModelException(tradeOrder.getOrderKey(), 3140,
                    "Not conected to TWS order cannot be placed");
        }
    }

    @Override
    public boolean validateBrokerData(Tradestrategy tradestrategy) {
        return false;
    }

    private ILogger getInLogger() {
        return inLogger;
    }

    private ILogger getOutLogger() {
        return outLogger;
    }

    private static class TWSLogger implements ILogger {
        final private Logger m_log;
        final private Level m_level = Level.INFO;

        TWSLogger(Logger log) {
            m_log = log;
        }

        public void log(String msg) {
            m_log.log(m_level, msg);
        }
    }

    public class ConnectionHandler implements IConnectionHandler {

        private final AbstractBrokerModel brokerModel;

        ConnectionHandler(AbstractBrokerModel brokerModel) {
            this.brokerModel = brokerModel;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        /**
         * IConnectionHandler method implemented.
         * connected()
         * disconnected()
         * accountList(ArrayList<String> list)
         * error(Exception e)
         * message(int id, int errorCode, String errorMsg)
         * show(String string)
         */

        public void connected() {
            show("connected");

            controller().reqCurrentTime(time -> show("Server date/time is " + Formats.fmtDate(time * 1000)));

            controller().reqBulletins(true, (msgId, newsType, message, exchange) -> {
                String str = String.format("Received bulletin:  type=%s  exchange=%s", newsType, exchange);
                show(str);
                show(message);
            });
        }

        public void disconnected() {
            _log.error("TWS Broker Model connectionClosed ");
            onCancelAllRealtimeData();
            getBrokerModel().fireConnectionClosed(true);
        }

        public void accountList(ArrayList<String> list) {
            try {
                String accountNumbers = null;
                for (String accountNumber : list) {
                    if (null == accountNumbers)
                        accountNumbers = accountNumber;
                    accountNumbers = "," + accountNumber;
                }
                _log.debug("Managed accounts: {}", accountNumbers);
                getBrokerModel().fireManagedAccountsUpdated(accountNumbers);

            } catch (Exception ex) {
                message(0, 3315, "Error updating Managed Accounts: " + ex.getMessage());
            } finally {
                /*
                 * Call FA Accounts to see if we are Financial Advisor.
                 */
                onReqFinancialAccount();
            }
            show("Received account list");
        }

        public void show(final String msg) {
            getOutLogger().log(msg);
        }

        public void error(Exception ex) {
            show(ex.toString());
        }

        public void message(int id, int errorCode, String errorMsg) {
            show(id + " " + errorCode + " " + errorMsg);
        }
    }

    public class AdvisorHandler implements ApiController.IAdvisorHandler {

        private final AbstractBrokerModel brokerModel;


        AdvisorHandler(AbstractBrokerModel brokerModel) {
            this.brokerModel = brokerModel;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        public void groups(ArrayList<Group> groups) {

            for (Group group : groups) {
                _log.debug("Group: {}/n", group.name());
                Portfolio portfolio = new Portfolio(group.name(), group.name());
                getPersistentModel().savePortfolio(portfolio);
            }

        }

        public void profiles(ArrayList<Profile> profiles) {

            for (Profile profile : profiles) {
                _log.debug("Profiles: {}/n", profile.name());
                Portfolio portfolio = new Portfolio(profile.name(), profile.name());
                getPersistentModel().savePortfolio(portfolio);
            }
            getBrokerModel().fireFAAccountsCompleted();
        }

        public void aliases(ArrayList<Alias> aliases) {

            for (Alias alias : aliases) {
                _log.debug("Aliases: {}/n", alias.alias());

                Account account = getPersistentModel().findAccountByAccountNumber(alias.account());
                if (null == account) {
                    account = new Account(alias.account(), alias.account(), Currency.USD,
                            AccountType.INDIVIDUAL);
                }
                account.setAlias(alias.alias());
                account = getPersistentModel().saveAspect(account);
            }
        }
    }

    public class AccountHandler implements ApiController.IAccountHandler {

        private final AbstractBrokerModel brokerModel;
        private final String accountNumber;

        AccountHandler(AbstractBrokerModel brokerModel, String accountNumber) {
            this.brokerModel = brokerModel;
            this.accountNumber = accountNumber;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        private String getAccountNumber() {
            return this.accountNumber;
        }

        public void accountValue(String account, String key, String value, String currency) {
        }

        public void accountTime(String timeStamp) {
        }

        public void accountDownloadEnd(String accountNumber) {
            _log.debug("accountDownloadEnd: {}", accountNumber);
        }

        public void updatePortfolio(Position position) {
        }
    }

    public class LiveOrderHandler implements ApiController.ILiveOrderHandler {

        private final AbstractBrokerModel brokerModel;

        LiveOrderHandler(AbstractBrokerModel brokerModel) {
            this.brokerModel = brokerModel;

        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        public void openOrder(com.ib.client.Contract contract, Order order, OrderState orderState) {
        }

        public void openOrderEnd() {
        }

        public void orderStatus(int orderId, com.ib.client.OrderStatus status, double filled, double remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
        }

        public void handle(int orderId, int errorCode, String errorMsg) {
        }  // add permId?
    }

    public class OrderHandler implements ApiController.IOrderHandler {

        private final AbstractBrokerModel brokerModel;
        private final Integer orderKey;

        OrderHandler(AbstractBrokerModel brokerModel, Integer orderKey) {
            this.brokerModel = brokerModel;
            this.orderKey = orderKey;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        private Integer getOrderKey() {
            return this.orderKey;
        }

        public void orderState(OrderState orderState) {
        }

        public void orderStatus(com.ib.client.OrderStatus status, double filled, double remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
            try {
                TradeOrder transientInstance = getPersistentModel().findTradeOrderByKey(getOrderKey());
                if (null == transientInstance) {
                    error(getOrderKey(), 3170, "Warning Order not found for Order Key: " + getOrderKey() + " make sure Client ID: "
                            + clientId + " is not the master in TWS. On orderStatus update.");
                    return;
                }
                /*
                 * Check to see if anything has changed as this method gets fired
                 * twice on order fills.
                 */
                boolean changed = false;
                if (CoreUtils.nullSafeComparator(transientInstance.getStatus(), status.name().toUpperCase()) != 0) {
                    transientInstance.setStatus(status.name().toUpperCase());
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
                    transientInstance.setOrderUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                    transientInstance.setStatus(status.name().toUpperCase());
                    transientInstance.setWhyHeld(whyHeld);
                    _log.debug("Order Status changed. Status: {}", status);
                    logOrderStatus(getOrderKey(), status.name(), filled, remaining, avgFillPrice, permId, parentId,
                            lastFillPrice, clientId, whyHeld);

                    boolean isFilled = transientInstance.getIsFilled();
                    transientInstance = getPersistentModel().saveTradeOrder(transientInstance);

                    if (OrderStatus.CANCELLED.equals(transientInstance.getStatus())) {
                        // Let the controller know a position was closed
                        getBrokerModel().fireTradeOrderCancelled(transientInstance);
                    } else {
                        getBrokerModel().fireTradeOrderStatusChanged(transientInstance);
                        // Let the controller know an order was filled
                        if (transientInstance.getIsFilled() && !isFilled)
                            getBrokerModel().fireTradeOrderFilled(transientInstance);
                    }
                }
            } catch (Exception ex) {
                error(getOrderKey(), 3200, "Errors updating open order status: " + ex.getMessage());
            }
        }

        public void handle(int errorCode, String errorMsg) {
            error(getOrderKey(), errorCode, errorMsg);
        }
    }

    public class HistoricalDataHandler implements ApiController.IHistoricalDataHandler {

        private final AbstractBrokerModel brokerModel;
        private final Integer reqId;

        HistoricalDataHandler(AbstractBrokerModel brokerModel, Integer reqId) {

            this.brokerModel = brokerModel;
            this.reqId = reqId;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        private Integer getTradestrategyReqId() {
            return this.reqId;
        }

        public void historicalData(Bar bar, boolean hasGaps) {

            try {

                long volume = bar.volume() * 100;

                if (historyDataRequests.containsKey(getTradestrategyReqId())) {
                    Tradestrategy tradestrategy = historyDataRequests.get(getTradestrategyReqId());

                    ZonedDateTime date;
                    /*
                     * There is a bug in the TWS interface format for dates
                     * should always be milli sec but when 1 day is selected as
                     * the period the dates come through as yyyyMMdd.
                     */
                    if (bar.formattedTime().length() == 8) {

                        date = TradingCalendar.getZonedDateTimeFromDateString(bar.formattedTime(), "yyyyMMdd",
                                TradingCalendar.MKT_TIMEZONE);
                    } else {

                        date = TradingCalendar.getZonedDateTimeFromMilli((Long.parseLong(bar.formattedTime()) * 1000));
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

                        BigDecimal price = (BigDecimal.valueOf(bar.close())).setScale(SCALE, RoundingMode.HALF_EVEN);
                        tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastAskPrice(price);
                        tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastBidPrice(price);
                        tradestrategy.getStrategyData().getBaseCandleSeries().getContract().setLastPrice(price);
                        tradestrategy.getStrategyData().buildCandle(date, bar.open(), bar.high(), bar.low(), bar.close(), volume, bar.wap(),
                                bar.count(), 1, null);
                    }
                }
            } catch (Exception ex) {

                error(getTradestrategyReqId(), 3260, ex.getMessage());
            }
        }

        public void historicalDataEnd() {

            try {

                Tradestrategy tradestrategy = historyDataRequests.get(getTradestrategyReqId());
                CandleSeries candleSeries = tradestrategy.getStrategyData().getBaseCandleSeries();
                tradeService.saveCandleSeries(candleSeries);

                _log.debug("HistoricalData complete Req Id: {} Symbol: {} Tradingday: {} candles to saved: {} Contract Tradestrategies size:: {}", getTradestrategyReqId(), tradestrategy.getContract().getSymbol(), tradestrategy.getTradingday().getOpen(), candleSeries.getItemCount(), tradestrategy.getContract().getTradestrategies().size());

                /*
                 * The last one has arrived the reqId is the
                 * tradeStrategyId. Remove this from the processing vector.
                 */

                synchronized (historyDataRequests) {
                    historyDataRequests.remove(getTradestrategyReqId());
                    historyDataRequests.notify();
                }

                /*
                 * Check to see if the trading day is today and this
                 * strategy is selected to trade and that the market is open
                 */
                synchronized (tradestrategy.getContract().getTradestrategies()) {

                    getBrokerModel().fireHistoricalDataComplete(tradestrategy);

                    if (tradestrategy.getTradingday().getClose()
                            .isAfter(TradingCalendar.getDateTimeNowMarketTimeZone())) {

                        if (!getBrokerModel().isRealtimeBarsRunning(tradestrategy.getContract())) {

                            tradestrategy.getContract().addTradestrategy(tradestrategy);
                            getBrokerModel().onReqRealTimeBars(tradestrategy.getContract(),
                                    tradestrategy.getStrategy().getMarketData());
                        } else {

                            Contract contract = realTimeBarsRequests.get(tradestrategy.getContract().getRequestId());
                            contract.addTradestrategy(tradestrategy);
                        }
                    }
                }
            } catch (Exception ex) {

                error(getTradestrategyReqId(), 3260, ex.getMessage());
            }
        }
    }

    public class RealTimeBarHandler implements ApiController.IRealTimeBarHandler {

        private final AbstractBrokerModel brokerModel;
        private final Integer reqId;

        RealTimeBarHandler(AbstractBrokerModel brokerModel, Integer reqId) {
            this.brokerModel = brokerModel;
            this.reqId = reqId;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        private Integer getReqId() {
            return this.reqId;
        }

        public void realtimeBar(Bar bar) {

            // Called when a candle finishes
            try {

                long volume = bar.volume() * 100;
                ZonedDateTime date = TradingCalendar.getZonedDateTimeFromMilli(bar.time() * 1000);

                // Only store data that is during mkt hours
                if (realTimeBarsRequests.containsKey(getReqId())) {

                    Contract contract = realTimeBarsRequests.get(getReqId());

                    synchronized (contract) {

                        contract.getTradestrategies().sort(Tradestrategy.TRADINGDAY_CONTRACT);
                        boolean updateCandleDB = true;

                        for (Tradestrategy tradestrategy : contract.getTradestrategies()) {

                            StrategyData strategyData = tradestrategy.getStrategyData();

                            if (TradingCalendar.isMarketHours(tradestrategy.getTradingday().getOpen(),
                                    tradestrategy.getTradingday().getClose(), date)) {

                                if (!getBrokerModel().isMarketDataRunning(contract)) {

                                    BigDecimal price = BigDecimal.valueOf(bar.close()).setScale(SCALE, RoundingMode.HALF_EVEN);
                                    strategyData.getBaseCandleSeries().getContract().setLastAskPrice(price);
                                    strategyData.getBaseCandleSeries().getContract().setLastBidPrice(price);
                                    strategyData.getBaseCandleSeries().getContract().setLastPrice(price);
                                }

                                ZonedDateTime lastUpdateDate = date.plusNanos(4999);
                                strategyData.buildCandle(date, bar.open(), bar.high(), bar.low(), bar.close(), volume, bar.wap(), bar.count(),
                                        (tradestrategy.getBarSize() / 5), lastUpdateDate);

                                if (!strategyData.getBaseCandleSeries().isEmpty()) {

                                    CandleItem candleItem = (CandleItem) strategyData.getBaseCandleSeries()
                                            .getDataItem(strategyData.getBaseCandleSeries().getItemCount() - 1);

                                    if (updateCandleDB) {

                                        getPersistentModel().saveAspect(candleItem.getCandle());
                                        updateCandleDB = false;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                error(getReqId(), 3270, ex.getMessage());
            }
        }
    }

    public class TopMktDataHandler implements ApiController.ITopMktDataHandler {

        private final AbstractBrokerModel brokerModel;
        private final Integer reqId;

        TopMktDataHandler(AbstractBrokerModel brokerModel, Integer reqId) {

            this.brokerModel = brokerModel;
            this.reqId = reqId;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        private Integer getReqId() {
            return this.reqId;
        }

        public void tickPrice(TickType tickType, double value, int canAutoExecute) {

            try {

                BigDecimal price = (new BigDecimal(value)).setScale(SCALE, RoundingMode.HALF_EVEN);

                synchronized (price) {

                    // _log.warn("tickPrice Field: " + field + " value :" + value
                    // + " time: " + System.currentTimeMillis());
                    if (!marketDataRequests.containsKey(getReqId()))
                        return;
                    Contract contract = marketDataRequests.get(getReqId());

                    /*
                     * Make sure the lastPrice is between the current Bid/Ask as
                     * prints can come in late in T/S i.e. bad ticks that are
                     * outside the current Bid/Ask.
                     */

                    for (Tradestrategy tradestrategy : contract.getTradestrategies()) {

                        Contract seriesContract = tradestrategy.getStrategyData().getBaseCandleSeries().getContract();

                        switch (tickType) {
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

                error(getReqId(), 3210, ex.getMessage());
            }
        }

        public void tickSize(TickType tickType, int value) {

            try {

                if (Objects.requireNonNull(tickType) == TickType.VOLUME) {
                    if (realTimeBarsRequests.containsKey(getReqId())) {
                        Contract contract = realTimeBarsRequests.get(getReqId());

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
                                            .getSymbol(), tickType.name(), value * 100);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {

                error(getReqId(), 3210, ex.getMessage());
            }
        }

        public void tickString(TickType tickType, String value) {

            try {

                /*
                 * 48 = RTVolume String = last trade price;last trade size;last
                 * trade time;total volume;vwap;single trade flag
                 */

                // _log.info("tickString reqId: " + reqId + " field: " + field
                // + " value: " + value);

                synchronized (value) {

                    if (!marketDataRequests.containsKey(getReqId())) {
                        return;
                    }

                    if (Objects.requireNonNull(tickType) == TickType.RT_VOLUME) {/*
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

                            Contract contract = marketDataRequests.get(getReqId());
                            // _log.warn("TickString ReqId: " + reqId + " Field: "
                            // + field + " String: " + value);
                            for (Tradestrategy tradestrategy : contract.getTradestrategies()) {

                                Contract seriesContract = tradestrategy.getStrategyData().getBaseCandleSeries()
                                        .getContract();
                                int index = tradestrategy.getStrategyData().getBaseCandleSeries().indexOf(time);

                                if (index < 0) {
                                    return;
                                }

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

                error(getReqId(), 3210, ex.getMessage());
            }
        }

        public void tickSnapshotEnd() {

            _log.debug("tickSnapshotEnd: {}", getReqId());
        }

        public void marketDataType(Types.MktDataType marketDataType) {

            _log.debug("ReqId: {}, marketDataType: {} ", getReqId(), marketDataType.name());
        }
    }

    public class TradeReportHandler implements ApiController.ITradeReportHandler {

        private final AbstractBrokerModel brokerModel;
        private final Integer reqId;

        TradeReportHandler(AbstractBrokerModel brokerModel, Integer reqId) {

            this.brokerModel = brokerModel;
            this.reqId = reqId;
        }

        private AbstractBrokerModel getBrokerModel() {
            return this.brokerModel;
        }

        private TradeService getPersistentModel() {
            return tradeService;
        }

        private Integer getReqId() {
            return this.reqId;
        }

        public void tradeReport(String tradeKey, com.ib.client.Contract contract, Execution execution) {

            try {

                logExecution(execution);
                TradeOrder transientInstance = getPersistentModel()
                        .findTradeOrderByKey(Math.abs(execution.orderId()));

                if (null == transientInstance) {

                    /*
                     * If the executionDetails is null and the order does not exist
                     * then we have made a request for order executions with a
                     * different clientId than the one which created this order.
                     */
                    if (null == getPersistentModel().findTradeOrderfillByExecId(execution.execId())) {

                        executionDetails.put(execution.execId(), execution);
                    }
                    return;
                }

                /*
                 * We already have this order fill.
                 */

                if (transientInstance.existTradeOrderfill(execution.execId())) {
                    return;
                }

                TradeOrderfill tradeOrderfill = new TradeOrderfill();
                populateTradeOrderfill(execution, tradeOrderfill);
                tradeOrderfill.setTradeOrder(transientInstance);
                transientInstance.addTradeOrderfill(tradeOrderfill);
                transientInstance.setAverageFilledPrice(tradeOrderfill.getAveragePrice());
                transientInstance.setFilledQuantity(tradeOrderfill.getCumulativeQuantity());
                transientInstance.setFilledDate(tradeOrderfill.getTime());
                boolean isFilled = transientInstance.getIsFilled();
                transientInstance = getPersistentModel().saveTradeOrderfill(transientInstance);

                // Let the controller know an order was filled
                if (transientInstance.getIsFilled() && !isFilled) {
                    getBrokerModel().fireTradeOrderFilled(transientInstance);
                }

                tradeOrdersExecutions.put(transientInstance.getOrderKey(), transientInstance);
                _log.error("execDetails tradeOrdersExecutions reqId: {}", getReqId());

            } catch (Exception ex) {

                error(getReqId(), 3160, "Errors saving execution: " + ex.getMessage());
            }
        }

        public void tradeReportEnd() {

            try {

                for (Integer key : tradeOrdersExecutions.keySet()) {

                    TradeOrder tradeorder = tradeOrdersExecutions.get(key);

                    if (tradeorder.getIsFilled()) {

                        if (tradeorder.hasTradePosition() && !tradeorder.getTradePosition().isOpen()) {

                            // Let the controller know a position was closed
                            getBrokerModel().firePositionClosed(tradeorder.getTradePosition());
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

                    if (getPersistentModel().existTradestrategyByRequestId(getReqId())) {

                        Tradestrategy tradestrategy = getPersistentModel().findTradestrategyByRequestId(getReqId());
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
                            populateTradeOrderfill(execution, tradeOrderfill);
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
                                    populateTradeOrderfill(execution1, tradeOrderfill1);
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
                            // tradeService.persistTradeOrder(tradeOrder);
                            double totalComms = 0;
                            for (String key : executionDetails.keySet()) {

                                Execution execution = executionDetails.get(key);

                                if (tradeOrder.getPermId().equals(execution.permId())) {

                                    TradeOrderfill tradeOrderfill = new TradeOrderfill();
                                    populateTradeOrderfill(execution, tradeOrderfill);
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
                            tradeOrder = getPersistentModel().saveTradeOrderfill(tradeOrder);
                            TradeOrder transientInstance = getPersistentModel()
                                    .findTradeOrderByKey(tradeOrder.getOrderKey());

                            // Let the controller know an order was filled
                            if (tradeOrder.getIsFilled()) {

                                getBrokerModel().fireTradeOrderFilled(transientInstance);
                            }
                        }
                    }
                }
                /*
                 * Let the controller know there are execution details.
                 */
                getBrokerModel().fireExecutionDetailsEnd(tradeOrdersExecutions);
            } catch (Exception ex) {

                error(getReqId(), 3330, "Error adding new open orders: " + ex.getMessage());
            }
        }

        public void commissionReport(String tradeKey, CommissionReport commissionReport) {

            try {

                logCommissionReport(commissionReport);
                TradeOrderfill transientInstance = getPersistentModel().findTradeOrderfillByExecId(commissionReport.m_execId);

                if (null != transientInstance) {

                    TradeOrder tradeOrder = getPersistentModel()
                            .findTradeOrderByKey(transientInstance.getTradeOrder().getOrderKey());

                    for (TradeOrderfill tradeOrderfill : tradeOrder.getTradeOrderfills()) {

                        if (tradeOrderfill.getExecId().equals(commissionReport.m_execId)) {

                            tradeOrderfill.setCommission(new BigDecimal(commissionReport.m_commission));
                            getPersistentModel().saveTradeOrderfill(tradeOrderfill.getTradeOrder());
                            return;
                        }
                    }
                } else {

                    commissionDetails.put(commissionReport.m_execId, commissionReport);
                }

            } catch (Exception ex) {

                error(1, 3280, "Errors saving execution: " + ex.getMessage());
            }
        }
    }

    private static boolean updateTradeOrder(Order ibOrder, OrderState ibOrderState,
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

    private static boolean populateContract(com.ib.client.ContractDetails contractDetails, Contract transientContract) {

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

            if (CoreUtils.nullSafeComparator(transientContract.getContractIBId(),
                    contractDetails.contract().conid()) != 0) {

                transientContract.setContractIBId(contractDetails.contract().conid());
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
                    contractDetails.underConid()) != 0) {

                transientContract.setUnderConId(contractDetails.underConid());
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

    private static void populateTradeOrderfill(Execution execution, TradeOrderfill tradeOrderfill) {

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

    private static void logOrderStatus(int orderId, String status, double filled, double remaining, double avgFillPrice,
                                       long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

        _log.info("orderId: {} status: {} filled: {} remaining: {} avgFillPrice: {} permId: {} parentId: {} lastFillPrice: {} clientId: {} whyHeld: {}", orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
    }

    private static void logTradeOrder(Order order) {

        _log.debug("OrderKey: {} ClientId: {} PermId: {} Action: {} TotalQuantity: {} OrderType: {} LmtPrice: {} AuxPrice: {} Tif: {} OcaGroup: {} OcaType: {} OrderRef: {} Transmit: {} DisplaySize: {} TriggerMethod: {} Hidden: {} ParentId: {} GoodAfterTime: {} GoodTillDate: {} TrailStopPrice: {} TrailingPercent: {} OverridePercentageConstraints: {} AllOrNone: {} Account: {} FAGroup: {} FAMethod: {} FAPercent: {} FAProfile: {}", +order.orderId(), order.clientId(), order.permId(), order.action(), order.totalQuantity(), order.orderType(), order.lmtPrice(), order.auxPrice(), order.tif(), order.ocaGroup(), order.ocaType(), order.orderRef(), order.transmit(), order.displaySize(), order.triggerMethod(), order.hidden(), order.parentId(), order.goodAfterTime(), order.goodTillDate(), order.trailStopPrice(), order.trailingPercent(), order.overridePercentageConstraints(), order.allOrNone(), order.account(), order.faGroup(), order.faMethod(), order.faPercentage(), order.faProfile());
    }

    private static void logContract(com.ib.client.Contract contract) {

        _log.debug("Symbol: {} Sec Type: {} Exchange: {} Con Id: {} Currency: {} SecIdType: {} Primary Exch: {} Local Symbol: {} SecId: {} Multiplier: {} lastTradeDateOrContractMonth: {}", contract.symbol(), contract.secType(), contract.exchange(), contract.conid(), contract.currency(), contract.secIdType(), contract.primaryExch(), contract.localSymbol(), contract.secId(), contract.multiplier(), contract.lastTradeDateOrContractMonth());
    }

    private static void logContractDetails(com.ib.client.ContractDetails contractDetails) {

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

    private ApiController controller() {

        if (controller == null) {

            controller = new ApiController(new ConnectionHandler(this), getInLogger(), getOutLogger());
        }
        return controller;
    }
}
