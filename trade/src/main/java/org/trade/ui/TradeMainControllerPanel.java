/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
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
package org.trade.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trade.base.BasePanel;
import org.trade.base.ComponentPrintService;
import org.trade.base.TabbedAppPanel;
import org.trade.base.TextDialog;
import org.trade.core.broker.BrokerDataRequestMonitor;
import org.trade.core.broker.BrokerModelException;
import org.trade.core.broker.IBrokerChangeListener;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.lookup.DBTableLookupServiceProvider;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.PortfolioAccount;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.persistent.dao.strategy.IStrategyChangeListener;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.persistent.dao.strategy.StrategyRuleException;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.OverrideConstraints;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TriggerMethod;
import org.trade.ui.configuration.CodeAttributePanel;
import org.trade.ui.configuration.ConfigurationPanel;
import org.trade.ui.contract.ContractPanel;
import org.trade.ui.portfolio.PortfolioPanel;
import org.trade.ui.strategy.StrategyPanel;
import org.trade.ui.tradingday.ConnectionPane;
import org.trade.ui.tradingday.FilterBackTestPane;
import org.trade.ui.tradingday.TradingdayPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main application classes are ..
 * <p>
 * org.trade.ui.TradeMainControllerPanel
 * <p>
 * The applications main controller this listens to TWSBrokerModel and handles
 * all UI events that are common across all tabs. Each tab has its own
 * controller to handle specific Tab related UI events e.g. Get Data/Run
 * Strategy. Otherwise they are handled in the controller of the individual Tab
 * e.g. Save/Search.
 * <p>
 * org.trade.broker.TWSBrokerModel
 * <p>
 * This handles all the requests/responses from IB TWS. This class is listened
 * to by the following TradeMainControllerPanel AbstractStrategyRule
 * <p>
 * org.trade.strategy.AbstractStrategyRule
 * <p>
 * Base strategy class. All strategies inherit from this class. Implemented
 * AbstractStrategyRule listen to the BaseCandleSeries which is the series that
 * is updated as new candle data is received via the TWSBrokerModel.
 * <p>
 * org.trade.strategy.data.StrategyData
 * <p>
 * This class contains data sets for a specific strategy. The data sets are a
 * BaseCandleSeries this is the data set that received data from the TWS API and
 * is listened to by any running strategies. The second Candle series is used to
 * display charts. Other series are used for indicators that have been setup for
 * the strategy.
 */
public class TradeMainControllerPanel extends TabbedAppPanel implements IBrokerChangeListener, IStrategyChangeListener {

    @Serial
    private static final long serialVersionUID = -7717664255656430982L;

    private final static Logger _log = LoggerFactory.getLogger(TradeMainControllerPanel.class);

    @Autowired
    private TradeService tradeService;

    private static Tradingdays m_tradingdays = null;
    private IBrokerModel m_brokerModel = null;
    private BrokerDataRequestMonitor brokerDataRequestProgressMonitor = null;

    private TradingdayPanel tradingdayPanel = null;
    private ContractPanel contractPanel = null;
    private StrategyPanel strategyPanel = null;
    private DynamicCode dynacode = null;

    /**
     * The main application controller which interacts between the view and the
     * applications underlying models. This controller also listens to events
     * from the broker model.
     * <p>
     *
     * @param frame the main application Frame.
     */

    public TradeMainControllerPanel(Frame frame) {

        super(frame);

        try {

            /*
             * Create the customized application Menu/Tool Bar.
             */
            setMenu(new TradeMainPanelMenu(this));
            /*
             * This allows the main controller to receive all events as it is
             * allways considered selected.
             */
            setSelected(true);
            Tradingday tradingday = Tradingday.newInstance(TradingCalendar.getCurrentTradingDay());
            Tradingday todayTradingday = tradeService.findTradingdayByOpenCloseDate(tradingday.getOpen(),
                    tradingday.getClose());

            if (null != todayTradingday) {
                tradingday = todayTradingday;
            }

            m_tradingdays = new Tradingdays();
            m_tradingdays.add(tradingday);
            String strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
            dynacode = new DynamicCode();
            dynacode.addSourceDir(new File(strategyDir));

            /*
             * Constructs a new Trading tab that contains all information
             * related to the tradeingday i.e. which strategy to trade, contract
             * information whether to trade. This is the tab used to load
             * contracts and decide how to trade them.
             *
             */

            tradingdayPanel = new TradingdayPanel(m_tradingdays, this, tradeService);
            /*
             * Constructs a new Contract tab that contains all information
             * related to the Tradestrategy i.e. charts, Orders for a particular
             * trading day.
             *
             */

            contractPanel = new ContractPanel(m_tradingdays, this, tradeService);

            /*
             * Constructs a new Portfolio tab that contains all information
             * related to a portfolio. This tab allows you to see the results of
             * trading activity. It records the summary information for each
             * month i.e. Batting avg, Simple Sharpe ratio and P/L information.
             *
             */

            PortfolioPanel portfolioPanel = new PortfolioPanel(this, tradeService);

            /*
             * Constructs a new Configuration tab that contains all information
             * related to configuration of Default entry parms, strategies,
             * indicators, accounts.
             *
             */

            ConfigurationPanel configurationPanel = new ConfigurationPanel(tradeService);

            /*
             * Constructs a new Strategy tab that contains all information
             * related to a Strategy. This tab allows you to see the java code
             * of a strategy. It will be replaced in the future with Drools and
             * this will be where you can edit the strategies and deploy them.
             *
             */

            strategyPanel = new StrategyPanel(tradeService);

            this.addTab("Tradingday", tradingdayPanel);
            this.addTab("Contract Details", contractPanel);
            this.addTab("Portfolio", portfolioPanel);
            this.addTab("Configuration", configurationPanel);
            this.addTab("Strategies", strategyPanel);
            this.setSelectPanel(tradingdayPanel);
        } catch (IOException ex) {

            this.setErrorMessage(
                    "Error During Initialization. Please make sure config.properties file is in the root dir.",
                    ex.getMessage(), ex);
            System.exit(0);
        } catch (Exception ex1) {

            this.setErrorMessage("Error During Initialization.", ex1.getMessage(), ex1);
            System.exit(0);
        }
    }

    /**
     * This is fired when the menu item to open a file is fired.
     */

    public void doOpen() {
    }

    /**
     * This is fired from the Tradingday Tab when the Request Executions button
     * is pressed. This should be used to fetch orders that have executed at the
     * broker while the system was down.
     *
     * @param tradestrategy the Tradestrategy for which you are requesting trade
     *                      executions
     */

    public void doFetch(final Tradestrategy tradestrategy) {

        try {

            if (null != tradestrategy.getId()) {

                int result = JOptionPane.showConfirmDialog(this.getFrame(),
                        "Do you want to save orders that did not orginate from this TM client?", "Information",
                        JOptionPane.YES_NO_OPTION);
                m_brokerModel.onReqExecutions(tradestrategy, result == JOptionPane.YES_OPTION);

            }
        } catch (BrokerModelException ex) {

            setErrorMessage("Error getting executions.", ex.getMessage(), ex);
        }
    }

    /**
     * This is fired from the main menu when the Broker data button is pressed.
     * This will run the Strategy for all the tradingdays.
     */

    public void doData() {

        if (m_tradingdays.isDirty()) {

            this.setStatusBarMessage("Please save before running strategy ...\n", BasePanel.WARNING);
        } else {

            runStrategy(m_tradingdays, true);
        }
    }

    /**
     * This is fired from the Contract/Tradingday Tab when the Broker data
     * button is pressed. It is also fired doExceutionDetailEnd(). This should
     * be used to fetch executions for orders that may have been filled while
     * the system was down.
     *
     * @param tradestrategy the Tradestrategy for which you are requesting historical
     *                      data.
     */

    public void doData(final Tradestrategy tradestrategy) {

        if (tradestrategy.isDirty()) {

            this.setStatusBarMessage("Please save or refresh before running strategy ...\n", BasePanel.WARNING);
        } else {

            Tradingdays tradingdays = new Tradingdays();
            Tradingday tradingday = Tradingday.newInstance(tradestrategy.getTradingday().getOpen());
            // tradingday.setId(Integer.MAX_VALUE);
            tradingday.addTradestrategy(tradestrategy);
            tradingdays.add(tradingday);
            runStrategy(tradingdays, true);
        }
    }

    /**
     * This is fired from the Contract Tab when the Execute Order button is
     * pressed. This should be used to execute orders to the broker platform.
     *
     * @param tradeOrder TradeOrder
     */

    public void doExecute(final TradeOrder tradeOrder) {

        TradeOrder submittedTradeOrder = null;

        try {

            this.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            TradeOrder instance = tradeService.findTradeOrderByKey(tradeOrder.getOrderKey());

            if (null != instance) {

                if (!instance.getVersion().equals(tradeOrder.getVersion())) {

                    this.setStatusBarMessage("Please refresh order before submitting change ...\n", BasePanel.WARNING);
                }
            }
            Tradestrategy tradestrategy = tradeService.findTradestrategyById(tradeOrder.getTradestrategy());
            // Check the order is valid.
            instance.validate();
            submittedTradeOrder = m_brokerModel.onPlaceOrder(tradestrategy.getContract(), instance);
            setStatusBarMessage("Order sent to broker.\n", BasePanel.INFORMATION);

        } catch (Exception ex) {
            this.setErrorMessage("Error submitting Order " + Objects.requireNonNull(submittedTradeOrder).getOrderKey(), ex.getMessage(), ex);
        } finally {
            this.getFrame().setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * This is fired from the main menu when the Run Strategy button is pressed.
     * This will run the Strategy for all the tradingdays.
     */

    public void doRun() {

        try {

            if (m_tradingdays.isDirty()) {

                this.setStatusBarMessage("Please save or refresh before running strategy ...\n", BasePanel.WARNING);
            } else {

                runStrategy(m_tradingdays, false);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error running Trade Strategies.", ex.getMessage(), ex);
        }
    }

    /**
     * This is fired from the Tradingday Tab when the Run Strategy button is
     * pressed. This will run the Strategy for all the tradingdays.
     *
     * @param tradestrategy Tradestrategy
     */

    public void doRun(final Tradestrategy tradestrategy) {
        try {

            if (tradestrategy.isDirty()) {

                this.setStatusBarMessage("Please save or refresh before running strategy ...\n", BasePanel.WARNING);
            } else {
                Tradingdays tradingdays = new Tradingdays();
                Tradingday tradingday = Tradingday.newInstance(tradestrategy.getTradingday().getOpen());
                //  tradingday.setId(Integer.MAX_VALUE);
                tradingday.addTradestrategy(tradestrategy);
                tradingdays.add(tradingday);
                runStrategy(tradingdays, false);

            }
        } catch (Exception ex) {
            this.setErrorMessage("Error running Trade Strategies.", ex.getMessage(), ex);
        }
    }

    /**
     * This is fired from the main menu when the Back Test Strategy button is
     * pressed. This will run the Strategy for all the tradingdays.
     */

    public void doTest() {
        try {
            if (m_tradingdays.isDirty()) {
                this.setStatusBarMessage("Please save before running strategy ...\n", BasePanel.WARNING);
            } else {
                contractPanel.doCloseAll();
                /*
                 * If multiple Strategy/BarSize/ChartDays combination exist in
                 * the selected date range force the user to select one to
                 * process.
                 *
                 * These must be run one at a time and will require orders to be
                 * deleted between runs.
                 */
                m_tradingdays.getTradingdays().sort(Tradingday.DATE_ORDER_DESC);
                if (m_tradingdays.getTradingdays().isEmpty()) {
                    return;
                }
                ZonedDateTime toOpen = m_tradingdays.getTradingdays().getFirst().getOpen();
                ZonedDateTime fromOpen = m_tradingdays.getTradingdays().getLast()
                        .getOpen();
                List<Tradestrategy> strategyBarSizeChartHistItems = tradeService
                        .findTradestrategyDistinctByDateRange(fromOpen, toOpen);

                List<Tradestrategy> contractsItems = tradeService
                        .findTradestrategyContractDistinctByDateRange(fromOpen, toOpen);

                FilterBackTestPane filterTradestrategyPane = new FilterBackTestPane(fromOpen, toOpen,
                        strategyBarSizeChartHistItems, contractsItems);
                TextDialog dialog = new TextDialog(this.getFrame(), "Run back test for the following", true,
                        filterTradestrategyPane);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

                if (!dialog.getCancel()) {
                    Tradestrategy tradestrategy = filterTradestrategyPane.getSelectedStrategyBarSizeChartHist();
                    if (null == tradestrategy)
                        return;

                    List<Contract> contracts = filterTradestrategyPane.getSelectedContracts();

                    Tradingdays tradingdays = new Tradingdays();
                    for (Tradingday itemTradingday : m_tradingdays.getTradingdays()) {

                        if (!TradingCalendar.between(itemTradingday.getOpen(),
                                filterTradestrategyPane.getSelectedStartDate(),
                                filterTradestrategyPane.getSelectedEndDate()))
                            continue;

                        Tradingday tradingday = (Tradingday) itemTradingday.clone();
                        for (Tradestrategy item : itemTradingday.getTradestrategies()) {
                            if (tradestrategy.getBarSize().equals(item.getBarSize())
                                    && tradestrategy.getChartDays().equals(item.getChartDays())
                                    && tradestrategy.getStrategy().equals(item.getStrategy())) {
                                if (contracts.isEmpty()) {
                                    tradingday.addTradestrategy(item);
                                } else {
                                    for (Contract contract : contracts) {
                                        if (contract.equals(item.getContract())) {
                                            tradingday.addTradestrategy(item);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!tradingday.getTradestrategies().isEmpty()) {
                            tradingdays.add(tradingday);
                        }
                    }
                    runStrategy(tradingdays, false);
                }
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error reconciling open orders.", ex.getMessage(), ex);
        }
    }

    /**
     * This is fired from the Tradingday Tab when the Back Test Strategy button
     * is pressed. This will run the Strategy for the selected tradingday.
     *
     * @param tradestrategy Tradestrategy
     */

    public void doTest(final Tradestrategy tradestrategy) {

        if (tradestrategy.isDirty()) {

            this.setStatusBarMessage("Please save before running strategy ...\n", BasePanel.WARNING);
        } else {

            contractPanel.doClose(tradestrategy);
            Tradingdays tradingdays = new Tradingdays();
            Tradingday tradingday = Tradingday.newInstance(tradestrategy.getTradingday().getOpen());
            // tradingday.setId(Integer.MAX_VALUE);
            tradingday.addTradestrategy(tradestrategy);
            tradingdays.add(tradingday);
            runStrategy(tradingdays, false);
        }
    }

    /**
     * This method is fired when the system connects to TWS, if there are open
     * orders. i.e from a IBrokerModel event. If todays orders are not in the
     * openTradeOrders then we cancel then order.
     *
     * @param openTradeOrders Hashtable<Integer, TradeOrder> the open orders that are from
     *                        IB TWS.
     */

    public void openOrderEnd(final ConcurrentHashMap<Integer, TradeOrder> openTradeOrders) {

        SwingUtilities.invokeLater(() -> {
            try {

                Tradingday todayTradingday = m_tradingdays.getTradingday(
                        TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()),
                        TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone()));
                if (null == todayTradingday) {
                    return;
                }

                /*
                 * Save any tradeOrders that have been deleted from TM but
                 * are still active in the broker.
                 */
                for (TradeOrder openOrder : openTradeOrders.values()) {
                    if (null == openOrder.getId()) {
                        // Note we use the orderReference to store the
                        // tradestrategyId.

                        Tradestrategy tradestrategy = tradeService
                                .findTradestrategyById(Integer.parseInt(openOrder.getOrderReference()));
                        int result = JOptionPane.showConfirmDialog(getFrame(),
                                "Missing order key: " + openOrder.getOrderKey() + " for contract "
                                        + tradestrategy.getContract().getSymbol() + " do you want to save?",
                                "Information", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            openOrder.setTradestrategy(tradestrategy);
                            openOrder = tradeService.saveTradeOrder(openOrder);
                        }
                    }
                }

                /*
                 * Cancel any orders that were open and not filled.
                 */
                for (Tradestrategy tradestrategy : todayTradingday.getTradestrategies()) {
                    Tradestrategy instance = tradeService.findTradestrategyById(tradestrategy);
                    for (TradeOrder todayTradeOrder : instance.getTradeOrders()) {
                        if (todayTradeOrder.isActive()) {
                            if (!openTradeOrders.containsKey(todayTradeOrder.getOrderKey())) {
                                todayTradeOrder.setStatus(OrderStatus.CANCELLED);
                                todayTradeOrder.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                                tradeService.saveTradeOrder(todayTradeOrder);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                setErrorMessage("Error reconciling open orders.", ex.getMessage(), ex);
            }
        });
    }

    /**
     * This method is fired when the Brokermodel has completed the request for
     * Execution Details see doFetchExecution or connectionOpened i.e from a
     * IBrokerModel event all executions for the filter have now been received.
     * Check to see if we need to close any trades for these order fills.
     *
     * @param tradeOrders Hashtable<Integer, TradeOrder> the executed and open orders
     *                    that are from IB TWS.
     */
    public void executionDetailsEnd(final ConcurrentHashMap<Integer, TradeOrder> tradeOrders) {
        try {
            Tradingday todayTradingday = m_tradingdays.getTradingday(
                    TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()),
                    TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone()));
            if (null == todayTradingday) {
                return;
            }
            tradingdayPanel.doRefresh(todayTradingday);
            tradingdayPanel.doRefreshTradingdayTable(todayTradingday);

        } catch (Exception ex) {
            this.setErrorMessage("Error starting PositionManagerRule.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired when the Brokermodel has completed
     * executionDetails() or openOrder() and the order that was FILLED. If the
     * order opens a position and the stop price is set then this is an open
     * order created via a strategy. Check to see that we have a strategy
     * manager if so start the manager and close the strategy that opened the
     * position.
     *
     * @param tradeOrder TradeOrder
     * @see IBrokerChangeListener#tradeOrderFilled(TradeOrder)
     */
    public void tradeOrderFilled(final TradeOrder tradeOrder) {

        try {
            final Tradestrategy tradestrategy = m_tradingdays
                    .getTradestrategy(tradeOrder.getTradestrategyId().getId());

            if (null == tradestrategy) {
                this.setStatusBarMessage("Warning position opened but Tradestrategy not found for Order Key: "
                        + tradeOrder.getOrderKey() + " in the current Tradingday Tab selection.", BasePanel.WARNING);
                return;
            }

            if (!tradestrategy.getTrade()) {
                this.setStatusBarMessage(
                        "Warning position opened for Symbol: " + tradestrategy.getContract().getSymbol()
                                + "  but this tradestrategy is not set to trade. A manual order was created Key: "
                                + tradeOrder.getOrderKey(),
                        BasePanel.WARNING);
                return;
            }

            /*
             * If the order opens a position and the stop price is set then this
             * is an open order created via a strategy. Check to see that we
             * have a strategy manager if so start the manager and close the
             * strategy that opened the position.
             */
            if (tradeOrder.getIsOpenPosition() && null != tradeOrder.getStopPrice()) {

                /*
                 * If this Strategy has a manager start the Strategy Manager.
                 */

                if (tradestrategy.getStrategy().hasStrategyManager()) {
                    if (!tradingdayPanel
                            .isStrategyWorkerRunning(tradestrategy.getStrategy().getStrategyManager().getClassName()
                                    + tradestrategy.getId())) {
                        /*
                         * Kill the worker that got us in if still running its
                         * job is done.
                         */

                        tradingdayPanel.killStrategyWorker(
                                tradestrategy.getStrategy().getClassName() + tradestrategy.getId());
                        createStrategy(tradestrategy.getStrategy().getStrategyManager().getClassName(), tradestrategy);
                    }
                } else {
                    String key = tradestrategy.getStrategy().getClassName() + tradestrategy.getId();
                    if (tradingdayPanel.isStrategyWorkerRunning(key)) {
                        IStrategyRule strategy = tradingdayPanel.getStrategyWorker(key);
                        strategy.tradeOrderFilled(tradeOrder);
                    }
                }
            }
            tradestrategy.setStatus(tradeOrder.getTradestrategyId().getStatus());
            contractPanel.doRefresh(tradestrategy);

        } catch (Exception ex) {
            this.setErrorMessage("Error starting PositionManagerRule.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired when the Brokermodel has completed
     * executionDetails() or openOrder() and the order that was CANCELLED.
     *
     * @param tradeOrder TradeOrder
     * @see IBrokerChangeListener#tradeOrderCancelled(TradeOrder)
     */
    public void tradeOrderCancelled(final TradeOrder tradeOrder) {

        if (m_brokerModel.isConnected() && contractPanel.isSelected()) {

            SwingUtilities.invokeLater(() -> {
                try {
                    Tradestrategy tradestrategy = m_tradingdays
                            .getTradestrategy(tradeOrder.getTradestrategyId().getId());
                    if (null == tradestrategy) {
                        setStatusBarMessage(
                                "Warning position cancelled but Tradestrategy not found for Order Key: "
                                        + tradeOrder.getOrderKey() + " in the current Tradingday Tab selection.",
                                BasePanel.WARNING);
                        return;
                    }
                    contractPanel.doRefresh(tradestrategy);

                } catch (Exception ex) {
                    setErrorMessage("Error processing cancelled order.", ex.getMessage(), ex);
                }
            });
        }
    }

    /**
     * This method is fired when the Brokermodel has completed orderStatus().
     *
     * @param tradeOrder TradeOrder
     * @see IBrokerChangeListener#tradeOrderCancelled(TradeOrder)
     */
    public void tradeOrderStatusChanged(final TradeOrder tradeOrder) {

        if (m_brokerModel.isConnected() && contractPanel.isSelected()) {

            SwingUtilities.invokeLater(() -> {
                try {
                    Tradestrategy tradestrategy = m_tradingdays
                            .getTradestrategy(tradeOrder.getTradestrategyId().getId());
                    if (null == tradestrategy) {
                        setStatusBarMessage(
                                "Warning position opened but Tradestrategy not found for Order Key: "
                                        + tradeOrder.getOrderKey() + " in the current Tradingday Tab selection.",
                                BasePanel.WARNING);
                        return;
                    }
                    tradestrategy.setStatus(tradeOrder.getTradestrategyId().getStatus());
                    contractPanel.doRefresh(tradestrategy);

                } catch (Exception ex) {
                    setErrorMessage("Error changing tradeOrder status.", ex.getMessage(), ex);
                }
            });
        }
    }

    /**
     * This method is fired when the Brokermodel has completed
     * executionDetails() or openOrder() and the position was closed by the
     * order.
     *
     * @param tradePosition TradePosition
     * @see IBrokerChangeListener#positionClosed(TradePosition)
     */
    public void positionClosed(final TradePosition tradePosition) {

        if (m_brokerModel.isConnected()) {
            SwingUtilities.invokeLater(() -> {

                try {

                    TradePosition currTradePosition = tradeService
                            .findTradePositionById(tradePosition.getId());
                    for (TradeOrder tradeOrder : currTradePosition.getTradeOrders()) {
                        Tradestrategy tradestrategy = tradeService
                                .findTradestrategyById(tradeOrder.getTradestrategyId().getId());
                        m_tradingdays.getTradestrategy(tradestrategy.getId())
                                .setStatus(tradestrategy.getStatus());
                        contractPanel.doRefresh(tradestrategy);
                    }

                } catch (Exception ex) {
                    setErrorMessage("Error position closed: ", ex.getMessage(), ex);
                }
            });
        }
    }

    /**
     * Method strategyComplete.
     *
     * @param tradestrategy Tradestrategy
     */
    public void strategyComplete(String strategyClassName, Tradestrategy tradestrategy) {

        try {
            if (m_brokerModel.isConnected()) {
                tradestrategy = tradeService.findTradestrategyById(tradestrategy.getId());
                m_tradingdays.getTradestrategy(tradestrategy.getId()).setStatus(tradestrategy.getStatus());
                contractPanel.doRefresh(tradestrategy);
            }
            tradingdayPanel.removeStrategyWorker(strategyClassName + tradestrategy.getId());

        } catch (Exception ex) {
            this.setErrorMessage("Error strategyComplete : ", ex.getMessage(), ex);
        }
    }

    /**
     * Method strategyStarted.
     *
     * @param strategyClassName String
     * @param tradestrategy     Tradestrategy
     */
    public void strategyStarted(String strategyClassName, final Tradestrategy tradestrategy) {

    }

    /**
     * Method ruleComplete.
     *
     * @param tradestrategy Tradestrategy
     * @see IStrategyChangeListener#ruleComplete(Tradestrategy)
     */
    public void ruleComplete(final Tradestrategy tradestrategy) {

    }

    /**
     * Method strategyError.
     *
     * @param ex StrategyRuleException
     * @see IStrategyChangeListener#strategyError(StrategyRuleException)
     */
    public void strategyError(final StrategyRuleException ex) {

        SwingUtilities.invokeLater(() -> {
            try {
                if (ex.getErrorId() == 1) {
                    setErrorMessage("Error: " + ex.getErrorCode(), ex.getMessage(), ex);
                } else if (ex.getErrorId() == 2) {
                    setStatusBarMessage("Warning: " + ex.getMessage(), BasePanel.WARNING);
                } else if (ex.getErrorId() == 3) {
                    setStatusBarMessage("Information: " + ex.getMessage(), BasePanel.INFORMATION);
                } else {
                    setErrorMessage("Unknown Error Id Code: " + ex.getErrorCode(), ex.getMessage(), ex);
                }
            } finally {
                getFrame().setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    public void doHelp() {
        doAbout();
    }

    public void doDisclaimer() {
        try {
            File file = new File("docs/Disclaimer.html");
            JEditorPane disclaimerText;

            disclaimerText = new JEditorPane(file.toURI().toURL());
            disclaimerText.setEditable(false);
            TextDialog disclaimer = new TextDialog(this.getFrame(), "Disclaimer", false, disclaimerText);
            disclaimer.pack();
            disclaimer.setSize(new Dimension((int) (this.getFrame().getSize().getWidth() * 2 / 3),
                    (int) (this.getFrame().getSize().getHeight() * 2 / 3)));
            disclaimer.setLocationRelativeTo(this);
            disclaimer.setVisible(true);
        } catch (Exception ex) {
            this.setErrorMessage("Could not load about help.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from the main menu. It displays the application
     * version.
     */
    public void doAbout() {
        try {
            StringBuffer message = new StringBuffer();
            message.append("Product version: ");
            message.append(ConfigProperties.getPropAsString("component.name.version"));
            message.append("\nBuild Label:     ");
            message.append(ConfigProperties.getPropAsString("component.name.base"));
            message.append("\nBuild Time:      ");
            message.append(ConfigProperties.getPropAsString("component.name.date"));
            JOptionPane.showMessageDialog(this, message, "About Help", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            this.setErrorMessage("Could not load about help.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from the Broker API on completion of broker data
     * request. Note if this is the current trading day for this trade strategy
     * real time data has been started by the broker interface. Check to see if
     * a trade is already open for this trade strategy. If so fire up a trade
     * manager. If not fire of the strategy.
     *
     * @param tradestrategy Tradestrategy that has completed the request for historical
     *                      data
     * @see IBrokerChangeListener#historicalDataComplete(Tradestrategy)
     */

    public void historicalDataComplete(final Tradestrategy tradestrategy) {
        try {
            /*
             * Now we have the history data complete and the request for real
             * time data has started, so fire of the strategy for this
             * tradestrategy.
             */
            if (!m_brokerModel.isBrokerDataOnly()) {

                if (tradestrategy.getTrade()) {
                    if (tradestrategy.isThereOpenTradePosition()) {
                        int result = JOptionPane.showConfirmDialog(this.getFrame(),
                                "Position is open for: " + tradestrategy.getContract().getSymbol()
                                        + " do you want to run the Strategy ?",
                                "Information", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            if (tradestrategy.getStrategy().hasStrategyManager()) {
                                createStrategy(tradestrategy.getStrategy().getStrategyManager().getClassName(),
                                        tradestrategy);
                            } else {
                                createStrategy(tradestrategy.getStrategy().getClassName(), tradestrategy);
                            }

                        } else {
                            int result1 = JOptionPane.showConfirmDialog(this.getFrame(),
                                    "Position is open for: " + tradestrategy.getContract().getSymbol()
                                            + " do you want to delete all Orders?",
                                    "Information", JOptionPane.YES_NO_OPTION);
                            if (result1 == JOptionPane.YES_OPTION) {
                                tradeService.deleteTradestrategyTradeOrders(tradestrategy);
                            }
                        }

                    } else {
                        createStrategy(tradestrategy.getStrategy().getClassName(), tradestrategy);
                    }
                }
            }

        } catch (Exception ex) {
            this.setErrorMessage("Could not start strategy: " + tradestrategy.getStrategy().getName() + " for Symbol: "
                    + tradestrategy.getContract().getSymbol(), ex.getMessage(), ex);
        }
    }

    /**
     * This method connects to the Broker Platform and is fired when the main
     * menu item connect is pressed..
     */

    public void doConnect() {
        try {

            if ((null != m_brokerModel) && m_brokerModel.isConnected()) {

                int result = JOptionPane.showConfirmDialog(this.getFrame(),
                        "Already connected. Do you want to disconnect?", "Information", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {

                    doDisconnect();
                } else {

                    return;
                }
            } else {
                this.setBrokerMenu(null);
                this.setBrokerModel(IBrokerModel._brokerTest);
            }

            final ConnectionPane connectionPane = new ConnectionPane();
            TextDialog dialog = new TextDialog(this.getFrame(), "Connect to TWS", true, connectionPane, "Connect",
                    "Cancel");
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            /*
             * Update the default portfolio.
             */
            tradeService.resetDefaultPortfolio(connectionPane.getPortfolio());
            DBTableLookupServiceProvider.clearLookup();

            if (!dialog.getCancel()) {
                this.setBrokerModel(IBrokerModel._broker);
                this.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                this.setStatusBarMessage("Please wait while login proceeds", BasePanel.INFORMATION);
                SwingUtilities.invokeLater(() -> {

                    try {

                        m_brokerModel.onConnect(connectionPane.getHost(), connectionPane.getPort(),
                                connectionPane.getClientId());
                    } finally {
                        getFrame().setCursor(Cursor.getDefaultCursor());
                    }
                });

            } else {
                this.setBrokerMenu(IBrokerModel._brokerTest);
                this.setStatusBarMessage("Running in test.", BasePanel.INFORMATION);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Could Not Connect/Disconnect From TWS", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired after the tab has been created and placed in the tab
     * controller.
     */

    public void doWindowOpen() {
        doConnect();
    }

    /**
     * This method is fired when the tab closes.
     */

    public void doWindowClose() {
        tradingdayPanel.killAllStrategyWorker();
        doDisconnect();
        doExit();
    }

    /**
     * This method is fired from an event in the Broker Model. All exception
     * reported back from the broker interface are received here.
     * <p>
     * 0 - 999 are IB TWS error codes for Orders or data 1000 - 1999 are IB TWS
     * System error 2000 - 2999 are IB TWS Warning 4000 - 4999 are application
     * warnings 5000 - 5999 are application information
     *
     * @param ex BrokerManagerModelException the broker exception
     * @see IBrokerChangeListener#brokerError(BrokerModelException)
     */

    public void brokerError(final BrokerModelException ex) {
        /*
         * Dont block the broker thread.
         */
        SwingUtilities.invokeLater(() -> {
            try {
                if (ex.getErrorId() == 1) {
                    setErrorMessage("Error: " + ex.getErrorCode(), ex.getMessage(), ex);
                } else if (ex.getErrorId() == 2) {
                    setStatusBarMessage("Warning: " + ex.getMessage(), BasePanel.WARNING);
                } else if (ex.getErrorId() == 3) {
                    setStatusBarMessage("Information: " + ex.getMessage(), BasePanel.INFORMATION);
                } else {
                    setErrorMessage("Unknown Error Id Code: " + ex.getErrorCode(), ex.getMessage(), ex);
                }
            } finally {
                getFrame().setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    /**
     * This method is disconnects from the Broker Platform and is fired when the
     * main menu item disconnect is pressed..
     */

    public void doDisconnect() {

        try {

            tradingdayPanel.killAllStrategyWorker();

            if (m_brokerModel.isConnected()) {

                if ((null != brokerDataRequestProgressMonitor) && !brokerDataRequestProgressMonitor.isDone()) {

                    brokerDataRequestProgressMonitor.cancel(true);
                }

            }
            this.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.setStatusBarMessage("Please wait while disconnect proceeds", BasePanel.INFORMATION);
            SwingUtilities.invokeLater(() -> {

                try {

                    m_brokerModel.onDisconnect();
                } finally {
                    getFrame().setCursor(Cursor.getDefaultCursor());
                }
            });

            refreshTradingdays(m_tradingdays);
        } catch (Exception ex) {
            this.setErrorMessage("Could Not Disconnect From TWS", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from an event in the Broker Model. A connection has
     * been opened.
     *
     * @see IBrokerChangeListener#connectionOpened()
     */

    public void connectionOpened() {

        try {

            this.setBrokerMenu(IBrokerModel._broker);
            tradingdayPanel.setConnected(true);
            contractPanel.setConnected(true);

            Tradingday todayTradingday = m_tradingdays.getTradingday(
                    TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()),
                    TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone()));

            /*
             * Request all the executions for today. This will result in updates
             * to any trade orders that were filled while we were disconnected.
             */
            if (null != todayTradingday) {

                m_brokerModel.onReqAllExecutions(todayTradingday.getOpen());
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error finding excecutions.", ex.getMessage(), ex);
        } finally {
            this.setStatusBarMessage("Running live.", BasePanel.INFORMATION);
        }
    }

    /**
     * This method is fired from an event in the Broker Model. A connection has
     * been closed.
     */
    public void connectionClosed(boolean forced) {

        try {

            /*
             * If the connection was lost to TWS and it was not a doDisconnect()
             * i.e. it was forced. Try to reconnect.
             */
            tradingdayPanel.setConnected(false);
            contractPanel.setConnected(false);

            if (forced) {

                if (!m_brokerModel.isConnected()) {

                    doConnect();
                }
            } else {
                this.setBrokerModel(IBrokerModel._brokerTest);
                this.setBrokerMenu(IBrokerModel._brokerTest);
                this.setStatusBarMessage("Connected to Broker was closed.", BasePanel.INFORMATION);
            }

        } catch (Exception ex) {
            this.setErrorMessage("Error finding connection closed.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from an event in the Broker Model. The managed
     * accounts for this connection. Note each instance of TWS is connected to
     * one master account only unless you are a Financial Adviser. The list of
     * accounts is parsed.
     *
     * @param accountNumbers String csv list of managed accounts.
     * @see IBrokerChangeListener#managedAccountsUpdated(String)
     */

    public void managedAccountsUpdated(String accountNumbers) {
        Scanner scanLine = new Scanner(accountNumbers);

        try (scanLine) {
            scanLine.useDelimiter("\\,");

            int tokens = accountNumbers.replaceAll("[^,]", "").length();

            Portfolio defaultPortfolio = tradeService.findPortfolioDefault();

            while (scanLine.hasNext()) {
                String accountNumber = scanLine.next().trim();
                if (!accountNumber.isEmpty()) {
                    Account account = tradeService.findAccountByNumber(accountNumber);

                    if (null == account) {
                        account = new Account(accountNumber, accountNumber, Currency.USD, AccountType.INDIVIDUAL);
                    }
                    /*
                     * If there is only one account in the incoming string and
                     * the default portfolio has no accounts add this account to
                     * the default portfolio.
                     */
                    if (defaultPortfolio.getPortfolioAccounts().isEmpty() && tokens == 0) {
                        PortfolioAccount portfolioAccount = new PortfolioAccount(defaultPortfolio, account);
                        defaultPortfolio.getPortfolioAccounts().add(portfolioAccount);
                        defaultPortfolio = tradeService.savePortfolio(defaultPortfolio);
                        /*
                         * Update the account (key) to the current account only
                         * when the default Portfolio has no accounts.
                         */
                        defaultPortfolio.setName(account.getAccountNumber());
                        defaultPortfolio = tradeService.saveAspect(defaultPortfolio);

                    } else {
                        Portfolio portfolio = new Portfolio(account.getAccountNumber(), account.getAccountNumber());
                        PortfolioAccount portfolioAccount = new PortfolioAccount(portfolio, account);
                        portfolio.getPortfolioAccounts().add(portfolioAccount);
                        portfolio = tradeService.savePortfolio(portfolio);
                        if (tokens == 0) {
                            /*
                             * Update the default portfolio.
                             */
                            tradeService.resetDefaultPortfolio(portfolio);
                        }
                    }
                }
            }

            DBTableLookupServiceProvider.clearLookup();
            tradingdayPanel.doWindowActivated();
            defaultPortfolio = tradeService.findPortfolioByName(defaultPortfolio.getName());

            for (PortfolioAccount item : defaultPortfolio.getPortfolioAccounts()) {

                m_brokerModel.onSubscribeAccountUpdates(true, item.getAccount().getAccountNumber());
            }

            this.setStatusBarMessage(
                    "Connected to IB and subscribed to updates for default portfolio: " + defaultPortfolio.getName(),
                    BasePanel.INFORMATION);
        } catch (Exception ex) {
            this.setErrorMessage("Could not retreive account data Msg: ", ex.getMessage(), ex);
        }
    }

    /**
     * Method updateAccountTime.
     *
     * @param accountNumber String
     * @see IBrokerChangeListener#updateAccountTime(String)
     */
    public void updateAccountTime(final String accountNumber) {

        SwingUtilities.invokeLater(() -> {
            try {
                Account account = tradeService.findAccountByNumber(accountNumber);
                Portfolio portfolio = account.getDefaultPortfolio();
                if (null != portfolio) {
                    portfolio = tradeService.findPortfolioById(portfolio.getId());
                    tradingdayPanel.setPortfolioLabel(portfolio);
                    setStatusBarMessage("Account: " + accountNumber + " information updated.",
                            BasePanel.INFORMATION);
                }
            } catch (Exception ex) {
                setErrorMessage("Could not retreive account data Msg: ", ex.getMessage(), ex);
            }
        });
    }

    /**
     * Method fAAccountsCompleted. The brokerManagerModel has received all FA
     * Accounts information.
     */
    public void fAAccountsCompleted() {
        DBTableLookupServiceProvider.clearLookup();
    }

    /**
     * This method retrieves all the details about a contract.
     */

    public void doProperties() {

        try {

            for (Tradingday tradingday : m_tradingdays.getTradingdays()) {

                for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                    m_brokerModel.onContractDetails(tradestrategy.getContract());
                }
            }
        } catch (BrokerModelException ex) {
            this.setErrorMessage("Could not disconnect From TWS", ex.getMessage(), ex);
        }
    }

    /**
     * Method doStrategyParameters.
     *
     * @param tradestrategy Tradestrategy
     */
    public void doStrategyParameters(final Tradestrategy tradestrategy) {
        try {

            this.clearStatusBarMessage();
            CodeType codeType = tradeService.findCodeTypeByNameType(tradestrategy.getStrategy().getName(),
                    CodeType.StrategyParameters);

            if (null != codeType) {

                Tradestrategy instance = tradeService.findTradestrategyById(tradestrategy);
                CodeAttributePanel codeAttributePanel = new CodeAttributePanel(codeType, instance.getCodeValues());

                if (null != codeAttributePanel) {

                    TextDialog dialog = new TextDialog(this.getFrame(), "Strategy Parms", true, codeAttributePanel);
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);

                    if (!dialog.getCancel()) {

                        /*
                         * Populate the code values from the fields.
                         */
                        for (CodeValue value : codeAttributePanel.getCodeValues()) {
                            if (null == value.getTradestrategy())
                                value.setTradestrategy(instance);
                            tradeService.saveAspect(value);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            setErrorMessage("Error getting Indicator properties.", ex.getMessage(), ex);
        } finally {
            this.getFrame().setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * This method is fired from the Contract Tab when the Cancel Order button
     * is pressed. This should be used to cancel orders in the broker platform.
     *
     * @param order the TradeOrder that you would like to cancel.
     */

    public void doCancel(final TradeOrder order) {

        if (!order.getIsFilled()) {

            try {

                m_brokerModel.onCancelOrder(order);
            } catch (BrokerModelException ex) {

                this.setErrorMessage("Error cancelling Order " + order.getOrderKey(), ex.getMessage(), ex);
            }

        } else {
            this.setStatusBarMessage("Order is filled and cannot be cancelled", BasePanel.INFORMATION);
        }
    }

    /**
     * This method is fired from the Cancel Live data button on the main tool
     * bar. This will cancel all live data and all strategies that are running.
     */

    public void doCancel() {

        // Cancel the candleWorker if running
        m_brokerModel.onCancelAllRealtimeData();

        if ((null != brokerDataRequestProgressMonitor) && !brokerDataRequestProgressMonitor.isDone()) {

            brokerDataRequestProgressMonitor.cancel(true);
        }
        tradingdayPanel.killAllStrategyWorker();
        refreshTradingdays(m_tradingdays);
        this.setStatusBarMessage("Strategies and live data have been cancelled.", BasePanel.INFORMATION);
    }

    /**
     * This method is fired from the Contract Tab or Trading Tab when the Cancel
     * Strategy button is pressed. This should be used to cancel strategies in
     * the broker platform.
     *
     * @param tradestrategy the Tradestrategy that you would like to cancel.
     */

    public void doCancel(final Tradestrategy tradestrategy) {
        try {

            if (m_brokerModel.isRealtimeBarsRunning(tradestrategy)) {

                m_brokerModel.onCancelRealtimeBars(tradestrategy);
                this.setStatusBarMessage(
                        "Realtime data has been cancelled for Symbol: " + tradestrategy.getContract().getSymbol(),
                        BasePanel.INFORMATION);
            }

            // Cancel the StrategyWorker if running
            if (tradingdayPanel.isStrategyWorkerRunning(tradestrategy)) {

                tradingdayPanel.killAllStrategyWorkersForTradestrategy(tradestrategy);
                this.setStatusBarMessage(
                        "Strategy has been cancelled for Symbol: " + tradestrategy.getContract().getSymbol(),
                        BasePanel.INFORMATION);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Could not cancel strategy.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from the Main menu this will close all open
     * positions
     */
    public void doCloseAll() {
        try {
            for (Tradingday tradingday : m_tradingdays.getTradingdays()) {
                for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {
                    doCloseAll(tradestrategy);
                }
            }
        } catch (Exception ex) {
            this.setErrorMessage("Could not close position.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from the Contract Tab this will close all open
     * positions
     */
    public void doCloseAll(final Tradestrategy tradestrategy) {
        try {
            if (null == tradestrategy.getId()) {
                return;
            }
            TradestrategyOrders positionOrders = tradeService
                    .findPositionOrdersByTradestrategyId(tradestrategy.getId());
            Tradestrategy instance = tradeService.findTradestrategyById(tradestrategy.getId());
            for (TradeOrder order : positionOrders.getTradeOrders()) {
                if (order.isActive()) {
                    m_brokerModel.onCancelOrder(order);
                }
            }
            if (positionOrders.hasOpenTradePosition()) {
                int result = JOptionPane
                        .showConfirmDialog(this.getFrame(),
                                "Are you sure you want to close " + tradestrategy.getContract().getSymbol()
                                        + " open position with a market order?",
                                "Information", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {

                    TradeOrder openTradeOrder = null;
                    for (TradeOrder tradeOrder : positionOrders.getTradeOrders()) {
                        if (tradeOrder.getIsOpenPosition()) {
                            openTradeOrder = tradeOrder;
                        }
                    }
                    int openQuantity = Math.abs(positionOrders.getOpenTradePosition().getOpenQuantity());

                    if (openQuantity > 0) {
                        String action = Action.BUY;
                        if (Side.BOT.equals(positionOrders.getOpenTradePosition().getSide())) {
                            action = Action.SELL;
                        }
                        TradeOrder tradeOrder = new TradeOrder(instance, action,
                                TradingCalendar.getDateTimeNowMarketTimeZone(), OrderType.MKT, openQuantity, null, null,
                                OverrideConstraints.YES, TimeInForce.DAY, TriggerMethod.DEFAULT);
                        tradeOrder.setTransmit(true);
                        if (null != Objects.requireNonNull(openTradeOrder).getFAProfile()) {
                            tradeOrder.setFAProfile(openTradeOrder.getFAProfile());
                        } else {
                            if (openTradeOrder.getFAGroup() != null) {
                                tradeOrder.setFAGroup(openTradeOrder.getFAGroup());
                                tradeOrder.setFAMethod(openTradeOrder.getFAMethod());
                                tradeOrder.setFAPercent(openTradeOrder.getFAPercent());
                            } else {
                                if (null != instance.getPortfolio().getIndividualAccount()) {

                                    tradeOrder.setAccountNumber(
                                            instance.getPortfolio().getIndividualAccount().getAccountNumber());
                                }
                            }
                        }
                        tradeOrder = m_brokerModel.onPlaceOrder(instance.getContract(), tradeOrder);
                    }
                }
            }
        } catch (Exception ex) {
            this.setErrorMessage("Could not close position.", ex.getMessage(), ex);
        }
    }

    /**
     * This method is fired from the Main menu and will allow you to setup the
     * printer setting.
     */

    public void doPrintSetup() {
        doPrint();
    }

    /**
     * This method is fired from the Main menu and will allow you to preview a
     * print of the current tab.
     */
    public void doPrintPreview() {
        doPrint();
    }

    /**
     * This method is fired from the Main menu and will allow you to print the
     * current tab.
     */
    public void doPrint() {
        try {

            PrinterJob pj = PrinterJob.getPrinterJob();
            PageFormat pageFormat = new PageFormat();
            ComponentPrintService vista = new ComponentPrintService(((JFrame) this.getFrame()).getContentPane(),
                    pageFormat);
            vista.scaleToFit(true);

            pj.validatePage(pageFormat);
            pj.setPageable(vista);

            if (pj.printDialog()) {
                pj.print();
            }

        } catch (Exception ex) {
            _log.error("Error printing msg: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Method doTransfer.
     *
     * @param idTradestrategy Integer
     */
    public void doTransfer(Integer idTradestrategy) {
        try {
            Tradestrategy tradestrategy = m_tradingdays.getTradestrategy(idTradestrategy);
            if (null == tradestrategy) {
                tradestrategy = tradeService.findTradestrategyById(idTradestrategy);
            }
            if (null == m_tradingdays.getTradingday(tradestrategy.getTradingday().getOpen(),
                    tradestrategy.getTradingday().getClose())) {
                Tradingday tradingday = tradeService
                        .findTradingdayById(tradestrategy.getTradingday().getId());
                m_tradingdays.add(tradingday);
            }
            if (tradestrategy.isDirty()) {
                setStatusBarMessage("Please save ...\n", BasePanel.WARNING);
            } else {
                contractPanel.doTransfer(tradestrategy);
                this.setSelectPanel(contractPanel);
            }
        } catch (ServiceException ex) {
            this.setErrorMessage("Error finding Tradingday.", ex.getMessage(), ex);
        }
    }

    /**
     * Method tabChanged.
     *
     * @param currBasePanel BasePanel
     * @param newBasePanel  BasePanel
     */
    public void tabChanged(BasePanel currBasePanel, BasePanel newBasePanel) {

        getMenu().setEnabledDelete(false, "Delete all Order");
        getMenu().setEnabledRunStrategy(false);
        getMenu().setEnabledBrokerData(false);
        getMenu().setEnabledTestStrategy(false);
        getMenu().setEnabledConnect(!m_brokerModel.isConnected());

        if (tradingdayPanel == newBasePanel) {

            if (null == brokerDataRequestProgressMonitor || brokerDataRequestProgressMonitor.isDone()) {

                getMenu().setEnabledDelete(true, "Delete all Order");

                if (m_brokerModel.isConnected()) {

                    getMenu().setEnabledRunStrategy(true);
                } else {

                    getMenu().setEnabledTestStrategy(true);
                }
                getMenu().setEnabledBrokerData(true);
            }
        } else if (strategyPanel == newBasePanel) {

            getMenu().setEnabledDelete(true, "Delete rule");
        }
    }

    /**
     * Method runStrategy.
     *
     * @param tradingdays    Tradingdays
     * @param brokerDataOnly boolean
     */
    private void runStrategy(final Tradingdays tradingdays, boolean brokerDataOnly) {

        try {

            m_brokerModel.setBrokerDataOnly(brokerDataOnly);

            if ((null != brokerDataRequestProgressMonitor) && !brokerDataRequestProgressMonitor.isDone()) {

                this.setStatusBarMessage("Strategies already running please wait or cancel ...", BasePanel.INFORMATION);
                return;
            } else {

                if (brokerDataOnly && !m_brokerModel.isConnected()) {

                    int result = JOptionPane.showConfirmDialog(this.getFrame(),
                            "Polygon will be used to retrieve candle data.\n Do you want to continue ?",
                            "Information", JOptionPane.YES_NO_OPTION);

                    if (result == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                for (Tradingday tradingday : tradingdays.getTradingdays()) {

                    if (tradingdayPanel.isStrategyWorkerRunning(tradingday)) {

                        this.setStatusBarMessage("Strategies already running please wait or cancel ...",
                                BasePanel.INFORMATION);
                        return;
                    }

                    if (Tradingdays.hasTradeOrders(tradingday) && !brokerDataOnly) {

                        int result = JOptionPane.showConfirmDialog(this.getFrame(),
                                "Tradingday: " + tradingday.getOpen()
                                        + " has orders. Do you want to delete all orders?",
                                "Information", JOptionPane.YES_NO_OPTION);

                        if (result == JOptionPane.YES_OPTION) {

                            tradeService.deleteTradingdayTradeOrders(tradingday);
                        }
                    }

                    for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                        try {

                            if (brokerDataOnly && !m_brokerModel.validateBrokerData(tradestrategy)) {

                                return;
                            }
                        } catch (BrokerModelException ex) {

                            tradingdayPanel.doRefreshTradingdayTable(tradingday);
                            JOptionPane.showConfirmDialog(this.getFrame(), ex.getMessage(), "Warning",
                                    JOptionPane.OK_CANCEL_OPTION);
                            return;
                        }

                        if (m_brokerModel.isRealtimeBarsRunning(tradestrategy)) {

                            int result = JOptionPane.showConfirmDialog(this.getFrame(),
                                    "A real time data request is already running for Symbol: "
                                            + tradestrategy.getContract().getSymbol()
                                            + " cancel to run strategy or get data?",
                                    "Information", JOptionPane.YES_NO_OPTION);

                            if (result == JOptionPane.YES_OPTION) {

                                m_brokerModel.onCancelRealtimeBars(tradestrategy);
                            } else {
                                return;
                            }
                        }

                        if (brokerDataOnly && !m_brokerModel.isConnected()) {

                            ZonedDateTime endDate = TradingCalendar.getDateAtTime(
                                    TradingCalendar.getPrevTradingDay(TradingCalendar
                                            .addTradingDays(tradestrategy.getTradingday().getClose(), 0)),
                                    tradestrategy.getTradingday().getClose());
                            ZonedDateTime startDate = endDate.minusDays((tradestrategy.getChartDays() - 1));
                            startDate = TradingCalendar.getPrevTradingDay(startDate);

                            List<Candle> candles = tradeService.findCandlesByContractDateRangeBarSize(
                                    tradestrategy.getContract().getId(), startDate, endDate,
                                    tradestrategy.getBarSize());

                            if (!candles.isEmpty()) {

                                int result = JOptionPane.showConfirmDialog(this.getFrame(),
                                        "Candle data already exists for Symbol: "
                                                + tradestrategy.getContract().getSymbol() + " Do you want to delete?",
                                        "Information", JOptionPane.YES_NO_OPTION);

                                if (result == JOptionPane.YES_OPTION) {

                                    for (Candle item : candles) {

                                        tradeService.deleteAspect(item);
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                        /*
                         * See if this strategy has any parameters
                         */
                        // doStrategyParameters(tradestrategy);
                    }
                }
            }

            this.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (brokerDataOnly) {

                this.setStatusBarMessage("Running data retrieval please wait ...", BasePanel.INFORMATION);
            } else {
                this.setStatusBarMessage("Running strategy please wait ...", BasePanel.INFORMATION);
            }

            if (m_brokerModel.isConnected()) {

                getMenu().setEnabledBrokerData(false);
                getMenu().setEnabledRunStrategy(false);
                getMenu().setEnabledConnect(false);
            } else {
                getMenu().setEnabledTestStrategy(false);
            }
            getMenu().setEnabledSearchDeleteRefreshSave(false);
            tradingdayPanel.cleanStrategyWorker();
            contractPanel.doCloseAll();
            /*
             * Now run a thread that gets and saves historical data from IB TWS.
             */
            getProgressBar().setMaximum(100);
            brokerDataRequestProgressMonitor = new BrokerDataRequestMonitor(m_brokerModel, tradeService,
                    tradingdays);
            brokerDataRequestProgressMonitor.addPropertyChangeListener(evt -> SwingUtilities.invokeLater(() -> {

                if ("progress".equals(evt.getPropertyName())) {

                    int progress = (Integer) evt.getNewValue();
                    setProgressBarProgress(progress, brokerDataRequestProgressMonitor);
                } else if ("information".equals(evt.getPropertyName())) {

                    if (brokerDataRequestProgressMonitor.isDone()) {

                        refreshTradingdays(tradingdays);
                    }

                    setStatusBarMessage((String) evt.getNewValue(), BasePanel.INFORMATION);

                } else if ("error".equals(evt.getPropertyName())) {

                    setErrorMessage("Error getting history data.",
                            ((Exception) evt.getNewValue()).getMessage(), (Exception) evt.getNewValue());
                }
            }));
            brokerDataRequestProgressMonitor.execute();

        } catch (Exception ex) {
            this.setErrorMessage("Error running Strategies or Chart Data.", ex.getMessage(), ex);
        } finally {
            this.getFrame().setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Method createStrategy.
     *
     * @param strategyClassName String
     * @param tradestrategy     Tradestrategy
     */
    private synchronized void createStrategy(String strategyClassName, Tradestrategy tradestrategy) throws Exception {

        String key = strategyClassName + tradestrategy.getId();

        // Only allow one strategy worker per tradestrategy
        if (tradingdayPanel.isStrategyWorkerRunning(key)) {
            throw new StrategyRuleException(1, 100,
                    "Strategy already running: " + strategyClassName + " Symbol: "
                            + tradestrategy.getContract().getSymbol() + " Key: " + key + " seriesCount: "
                            + tradestrategy.getStrategyData().getBaseCandleSeries().getItemCount());
        }

        Vector<Object> parm = new Vector<>(0);
        parm.add(m_brokerModel);
        parm.add(tradestrategy.getStrategyData());
        parm.add(tradestrategy.getId());

        IStrategyRule strategy = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                IStrategyRule.PACKAGE + strategyClassName, parm);

        strategy.addMessageListener(this);

        if (!m_brokerModel.isConnected()) {
            /*
             * For back test the back tester listens to the strategy for orders
             * being created/completed.
             */
            strategy.addMessageListener(m_brokerModel.getBackTestBroker(tradestrategy.getId()));
        }
        strategy.execute();
        tradingdayPanel.addStrategyWorker(key, strategy);
    }

    /**
     * Method setBrokerModel.
     *
     * @param model String
     */
    private void setBrokerModel(String model) {

        try {

            if (null != m_brokerModel) {
                m_brokerModel.removeMessageListener(this);
                m_brokerModel = null;
            }
            if (IBrokerModel._brokerTest.equals(model)) {

                m_brokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._brokerTest, this);
                tradingdayPanel.setConnected(false);
                contractPanel.setConnected(false);
                /*
                 * Controller listens for problems from the TWS interface see
                 * doError()
                 */
                m_brokerModel.addMessageListener(this);

            } else if (IBrokerModel._broker.equals(model)) {
                m_brokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._broker, this);
                /*
                 * Controller listens for problems from the TWS interface see
                 * doError()
                 */
                m_brokerModel.addMessageListener(this);

            }
        } catch (Exception ex) {
            this.setErrorMessage("Error running Simulated Mode.", ex.getMessage(), ex);
        }
    }

    /**
     * Method setBrokerModel.
     *
     * @param model String
     */
    private void setBrokerMenu(String model) {

        try {

            if (IBrokerModel._brokerTest.equals(model)) {

                getMenu().setEnabledBrokerData(true);
                getMenu().setEnabledRunStrategy(false);
                getMenu().setEnabledTestStrategy(true);
                getMenu().setEnabledConnect(true);
                this.setStatusBarMessage("Running in simulated mode", BasePanel.INFORMATION);
            } else if (IBrokerModel._broker.equals(model)) {

                getMenu().setEnabledBrokerData(true);
                getMenu().setEnabledRunStrategy(true);
                getMenu().setEnabledTestStrategy(false);
                getMenu().setEnabledConnect(false);
            } else {

                getMenu().setEnabledBrokerData(false);
                getMenu().setEnabledRunStrategy(false);
                getMenu().setEnabledTestStrategy(false);
                getMenu().setEnabledConnect(true);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error running Simulated Mode.", ex.getMessage(), ex);
        }
    }

    /**
     * Method refreshTradingdays.
     *
     * @param tradingdays Tradingdays
     */
    private void refreshTradingdays(final Tradingdays tradingdays) {
        /*
         * Refresh to check to see if changes need to be saved.
         */
        if (!tradingdays.getTradingdays().isEmpty()) {
            for (Tradingday tradingday : tradingdays.getTradingdays()) {
                tradingdayPanel.doRefresh(tradingday);
            }

            tradingdayPanel.doRefreshTradingdayTable(
                    tradingdays.getTradingdays().getLast());
        }

        if (m_brokerModel.isConnected()) {
            getMenu().setEnabledBrokerData(true);
            getMenu().setEnabledRunStrategy(true);
            getMenu().setEnabledConnect(false);
        } else {
            getMenu().setEnabledTestStrategy(true);
            getMenu().setEnabledConnect(true);
            tradingdayPanel.cleanStrategyWorker();
        }
        getMenu().setEnabledSearchDeleteRefreshSave(true);

    }

    public static TradeMainPanelMenu getMenu() {
        return (TradeMainPanelMenu) TabbedAppPanel.getMenu();
    }

    /**
     * Method setProgressBarProgress.
     *
     * @param progress int
     * @param worker   SwingWorker<Void,String>
     */
    private void setProgressBarProgress(int progress, SwingWorker<Void, String> worker) {

        getProgressBar().setValue(progress);
        if (getProgressBar().getMaximum() > 0) {
            String message = String.format("Completed %d%%.", progress);
            setStatusBarMessage(message, BasePanel.PROGRESS);
        }

        if (worker.isDone() || (progress == 100)) {
            Toolkit.getDefaultToolkit().beep();
            if (worker.isCancelled()) {
                setStatusBarMessage("Process canceled.", BasePanel.INFORMATION);
            } else {
                setStatusBarMessage("Process completed.", BasePanel.INFORMATION);
                getProgressBar().setMaximum(0);
                getProgressBar().setMinimum(0);
            }
        }
    }
}
