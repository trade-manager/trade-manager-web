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
package org.trade.ui.contract;

import org.trade.base.BaseButton;
import org.trade.base.BasePanel;
import org.trade.base.BaseUIPropertyCodes;
import org.trade.base.TabbedAppPanel;
import org.trade.base.TabbedCloseButton;
import org.trade.base.Table;
import org.trade.base.TextDialog;
import org.trade.base.Tree;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.IPersistentModel;
import org.trade.core.persistent.PersistentModelException;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AllocationMethod;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.DAOGroup;
import org.trade.core.valuetype.DAOProfile;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.Tier;
import org.trade.core.valuetype.TradestrategyStatus;
import org.trade.strategy.data.CandleDataset;
import org.trade.strategy.data.CandleSeries;
import org.trade.strategy.data.IndicatorSeries;
import org.trade.strategy.data.StrategyData;
import org.trade.ui.chart.CandlestickChart;
import org.trade.ui.models.TradeOrderTableModel;
import org.trade.ui.models.TradingdayTreeModel;
import org.trade.ui.tables.TradeOrderTable;
import org.trade.ui.tables.renderer.TradingdayTreeCellRenderer;
import org.trade.ui.widget.ButtonEditor;
import org.trade.ui.widget.ButtonRenderer;
import org.trade.ui.widget.DecodeComboBoxEditor;
import org.trade.ui.widget.DecodeComboBoxRenderer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serial;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.List;

/**
 *
 */
public class ContractPanel extends BasePanel implements TreeSelectionListener, ChangeListener, ItemListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4026209743607182423L;

    private Tradingdays m_tradingdays = null;
    private IPersistentModel m_tradePersistentModel = null;
    private final JTabbedPane m_jTabbedPaneContract = new JTabbedPane();
    private TradingdayTreeModel m_treeModel = null;
    private Tree m_tree = null;
    private Table m_tradeOrderTable = null;
    private TradeOrderTableModel m_tradeOrderModel = null;
    private JEditorPane m_tradeLabel = null;
    private JEditorPane m_strategyLabel = null;
    private BaseButton executeButton = null;
    private BaseButton brokerDataButton = null;
    private BaseButton cancelButton = null;
    private BaseButton cancelStrategiesButton = null;
    private BaseButton refreshButton = null;
    private BaseButton closeAllPositionsButton = null;
    private BaseButton propertiesButton = null;
    private DecodeComboBoxEditor periodEditorComboBox = null;
    private Integer backfillOffsetDays = 0;
    private Boolean connected = false;
    private static final NumberFormat currencyFormater = NumberFormat.getCurrencyInstance();
    private static final SimpleAttributeSet bold = new SimpleAttributeSet();
    private static final SimpleAttributeSet colorRedAttr = new SimpleAttributeSet();
    private static final SimpleAttributeSet colorGreenAttr = new SimpleAttributeSet();

    static {
        StyleConstants.setBold(bold, true);
        StyleConstants.setBackground(colorRedAttr, Color.RED);
        StyleConstants.setBackground(colorGreenAttr, Color.GREEN);
    }

    /**
     * Constructor for ContractPanel.
     *
     * @param tradingdays          Tradingdays
     * @param controller           TabbedAppPanel
     * @param tradePersistentModel IPersistentModel
     */

    public ContractPanel(Tradingdays tradingdays, TabbedAppPanel controller, IPersistentModel tradePersistentModel) {

        try {
            if (null != getMenu())
                getMenu().addMessageListener(this);
            this.setLayout(new BorderLayout());
            m_tradePersistentModel = tradePersistentModel;
            m_tradingdays = tradingdays;

            currencyFormater.setMinimumFractionDigits(2);
            backfillOffsetDays = ConfigProperties.getPropAsInt("trade.backfill.offsetDays");
            propertiesButton = new BaseButton(this, BaseUIPropertyCodes.PROPERTIES, 0);
            propertiesButton.setEnabled(false);
            executeButton = new BaseButton(controller, BaseUIPropertyCodes.EXECUTE);
            executeButton.addMessageListener(this);
            brokerDataButton = new BaseButton(controller, BaseUIPropertyCodes.DATA);
            brokerDataButton.setToolTipText("Get Chart Data");
            cancelButton = new BaseButton(controller, BaseUIPropertyCodes.CANCEL);
            cancelButton.setToolTipText("Cancel Order");
            cancelButton.setTransferObject(new Aspects());
            cancelButton.addMessageListener(this);
            cancelStrategiesButton = new BaseButton(controller, BaseUIPropertyCodes.CANCEL);
            cancelStrategiesButton.setToolTipText("Cancel Strategy");
            refreshButton = new BaseButton(this, BaseUIPropertyCodes.REFRESH);
            BaseButton closeAllButton = new BaseButton(this, BaseUIPropertyCodes.CLOSE_ALL);
            closeAllPositionsButton = new BaseButton(controller, BaseUIPropertyCodes.CLOSE_ALL);
            closeAllPositionsButton.setToolTipText("Cancel Orders & Close Position");
            m_tradeOrderModel = new TradeOrderTableModel();
            m_tradeOrderTable = new TradeOrderTable(m_tradeOrderModel);
            m_tradeOrderTable.getSelectionModel().addListSelectionListener(new TradeOrderTableRowListener());
            m_tradeOrderTable.setDefaultEditor(TradeOrder.class, new ButtonEditor(propertiesButton));
            m_tradeOrderTable.setDefaultRenderer(TradeOrder.class, new ButtonRenderer(BaseUIPropertyCodes.PROPERTIES));
            m_treeModel = new TradingdayTreeModel(m_tradingdays);
            m_tree = new Tree(m_treeModel);
            // Listen for when the selection changes.
            m_tree.addTreeSelectionListener(this);
            m_tree.setCellRenderer(new TradingdayTreeCellRenderer());
            ToolTipManager.sharedInstance().registerComponent(m_tree);

            JPanel jPanel1 = new JPanel(new BorderLayout());
            JScrollPane jScrollPane1Tree = new JScrollPane();
            jScrollPane1Tree.getViewport().add(m_tree, BorderLayout.CENTER);
            JPanel jPanel2 = new JPanel(new BorderLayout());
            jPanel2.add(jScrollPane1Tree, BorderLayout.CENTER);
            jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Tradingday"),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            jPanel1.setBorder(new BevelBorder(BevelBorder.LOWERED));
            jPanel2.add(jPanel1, BorderLayout.NORTH);

            // Chart Panel
            JLabel jLabelPeriod = new JLabel("Period:");
            periodEditorComboBox = new DecodeComboBoxEditor((new BarSize()).getCodesDecodes());
            DecodeComboBoxRenderer periodRenderer = new DecodeComboBoxRenderer();
            periodEditorComboBox.setRenderer(periodRenderer);
            periodEditorComboBox.setItem(BarSize.newInstance(BarSize.FIVE_MIN));
            periodEditorComboBox.setEnabled(false);
            periodEditorComboBox.addItemListener(this);
            JPanel jPanel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jPanel6.setBorder(new BevelBorder(BevelBorder.RAISED));
            jPanel6.add(closeAllButton, null);
            jPanel6.add(brokerDataButton, null);
            jPanel6.add(cancelStrategiesButton, null);
            jPanel6.add(jLabelPeriod, null);
            jPanel6.add(periodEditorComboBox, null);
            JToolBar jToolBar = new JToolBar();
            jToolBar.setLayout(new BorderLayout());
            jToolBar.add(jPanel6);

            m_strategyLabel = new JEditorPane("text/rtf", "");
            m_strategyLabel.setAutoscrolls(false);
            m_strategyLabel.setEditable(false);
            m_tradeLabel = new JEditorPane("text/rtf", "");
            m_tradeLabel.setAutoscrolls(false);
            m_tradeLabel.setEditable(false);

            JPanel jPanel12 = new JPanel(new BorderLayout());
            jPanel12.add(m_strategyLabel, null);
            JPanel jPanel18 = new JPanel(new BorderLayout());
            jPanel18.add(jToolBar, BorderLayout.WEST);
            JPanel jPanel11 = new JPanel(new BorderLayout());
            jPanel11.add(jPanel18, BorderLayout.WEST);
            jPanel11.add(jPanel12, BorderLayout.CENTER);
            JPanel jPanel7 = new JPanel(new BorderLayout());
            jPanel7.add(m_jTabbedPaneContract, BorderLayout.CENTER);
            JScrollPane jScrollPane3 = new JScrollPane();
            jScrollPane3.getViewport().add(jPanel7, BorderLayout.CENTER);
            JPanel jPanel9 = new JPanel(new BorderLayout());
            jPanel9.add(jScrollPane3, BorderLayout.CENTER);
            jPanel9.add(jPanel11, BorderLayout.NORTH);
            // Order Panel
            m_tradeOrderTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JPanel jPanel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jPanel5.add(executeButton, null);
            jPanel5.add(cancelButton, null);
            jPanel5.add(refreshButton, null);
            jPanel5.add(closeAllPositionsButton, null);
            jPanel5.setBorder(new BevelBorder(BevelBorder.RAISED));
            JToolBar jToolBar1 = new JToolBar();
            jToolBar1.setLayout(new BorderLayout());
            jToolBar1.add(jPanel5);
            JPanel jPanel19 = new JPanel(new BorderLayout());
            jPanel19.add(jToolBar1, BorderLayout.WEST);
            JScrollPane jScrollPane2 = new JScrollPane();
            jScrollPane2.getViewport().add(m_tradeOrderTable, BorderLayout.CENTER);
            jScrollPane2.setBorder(new BevelBorder(BevelBorder.LOWERED));
            JPanel jPanel16 = new JPanel(new BorderLayout());
            Dimension d = m_tradeOrderTable.getPreferredSize();
            // Make changes to [i]d[/i] if you like...
            m_tradeOrderTable.setPreferredScrollableViewportSize(d);
            jScrollPane2.addMouseListener(m_tradeOrderTable);
            JPanel jPanel17 = new JPanel(new BorderLayout());
            jPanel17.add(jPanel19, BorderLayout.WEST);
            jPanel17.add(m_tradeLabel, BorderLayout.CENTER);
            jPanel16.add(jPanel17, BorderLayout.NORTH);
            jPanel16.add(jScrollPane2, BorderLayout.CENTER);

            // use the new JSplitPane to dynamically resize...
            JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, jPanel9, jPanel16);
            split.setOneTouchExpandable(true);
            split.setResizeWeight(0.8d);
            JPanel jPanel15 = new JPanel(new BorderLayout());
            jPanel15.add(split, BorderLayout.CENTER);

            JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jPanel2, jPanel15);
            mainSplitPane.setOneTouchExpandable(true);
            mainSplitPane.setResizeWeight(0.15d);
            this.add(mainSplitPane, BorderLayout.CENTER);
            m_jTabbedPaneContract.addChangeListener(this);
            this.reFreshTab();
        } catch (Exception ex) {
            this.setErrorMessage("Error during initialization.", ex.getMessage(), ex);
        }
    }

    public void doOpen() {
        try {
            m_treeModel.setData(m_tradingdays);
        } catch (Exception ex) {
            this.setErrorMessage("Error opening all tabs.", ex.getMessage(), ex);
        }
    }

    /**
     * Method doProperties.
     *
     * @param instance TradeOrder
     */

    public void doProperties(final TradeOrder instance) {
        try {

            if (null == instance.getTradestrategy().getPortfolio().getIndividualAccount()) {
                AllocationMethodPanel allocationMethodPanel = new AllocationMethodPanel(instance);
                TextDialog dialog = new TextDialog(this.getFrame(), "FA Account Properties", true,
                        allocationMethodPanel);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                if (!dialog.getCancel()) {
                    if (null != instance.getFAProfile()) {
                        instance.setFAGroup(null);
                        instance.setFAMethod(null);
                        instance.setFAPercent(null);
                        instance.setAccountNumber(null);
                    } else {
                        if (null != instance.getFAGroup()) {
                            instance.setAccountNumber(null);
                        } else {
                            instance.setAccountNumber(instance.getTradestrategy().getPortfolio()
                                    .getIndividualAccount().getAccountNumber());
                        }
                    }
                }
            } else {
                this.setStatusBarMessage("No properties for Individual accounts ...\n", BasePanel.INFORMATION);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error setting FA properties.", ex.getMessage(), ex);
        }
    }

    public void doCancel(final Aspects aspects) {
        this.setStatusBarMessage("Please select an order to cancel ...\n", BasePanel.INFORMATION);
    }

    public void doCloseAll() {
        try {
            int tabsCount = m_jTabbedPaneContract.getTabCount();
            for (int index = 0; index < tabsCount; index++) {
                doClose(0);
            }
            m_tree.clearSelection();
        } catch (Exception ex) {
            this.setErrorMessage("Error removing all tabs.", ex.getMessage(), ex);
        }
    }

    /**
     * Method doClose.
     *
     * @param tradestrategy Tradestrategy
     */
    public void doClose(final Tradestrategy tradestrategy) {
        for (int index = 0; index < m_jTabbedPaneContract.getTabCount(); index++) {
            ChartPanel chart = (ChartPanel) m_jTabbedPaneContract.getComponentAt(index);
            if ((null != chart)
                    && chart.getTradestrategy().getId().equals(tradestrategy.getId())) {
                doClose(index);
                break;
            }
        }
    }

    /**
     * Method doClose.
     *
     * @param index Integer
     */
    public void doClose(Integer index) {
        ChartPanel chartPanel = (ChartPanel) m_jTabbedPaneContract.getComponentAt(index);
        TabbedCloseButton tabbedCloseButton = (TabbedCloseButton) m_jTabbedPaneContract.getTabComponentAt(index);
        tabbedCloseButton.removeMessageListener(this);
        chartPanel.getCandlestickChart().removeChart();
        chartPanel = null;
        m_jTabbedPaneContract.remove(index);
        m_tree.clearSelection();
    }

    public void doDelete() {
    }

    public void doExecute() {
        this.setStatusBarMessage("Please select an order to execute ...\n", BasePanel.INFORMATION);
    }

    public void doWindowOpen() {

    }

    public void doWindowClose() {

    }

    public void doWindowActivated() {
        try {
            Object selectedObject = brokerDataButton.getTransferObject();
            if (null == selectedObject) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
                if (null != selectedNode)
                    selectedObject = selectedNode.getUserObject();
            }
            m_treeModel.setData(m_tradingdays);
            // Expand the tree
            for (int i = 0; i < m_tree.getRowCount(); i++) {
                m_tree.expandRow(i);
            }
            TreePath path = m_tree.findTreePathByObject(selectedObject);

            if (null != path) {
                m_tree.setSelectionPath(path);
                m_tree.scrollPathToVisible(path);
            }
        } catch (Exception ex) {
            this.setErrorMessage("Error window activated.", ex.getMessage(), ex);
        }
    }

    /**
     * Method doWindowDeActivated.
     *
     * @return boolean
     */
    public boolean doWindowDeActivated() {
        return true;
    }

    public void doRefresh() {
        ChartPanel currentTab = (ChartPanel) m_jTabbedPaneContract.getSelectedComponent();
        if (null != currentTab)
            doRefresh(currentTab.getTradestrategy());
    }

    /**
     * Method doRefresh.
     *
     * @param tradestrategy Tradestrategy
     */
    public void doRefresh(final Tradestrategy tradestrategy) {
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    ChartPanel currentTab = (ChartPanel) m_jTabbedPaneContract.getSelectedComponent();
                    if (null != currentTab) {
                        if (currentTab.getTradestrategy().equals(tradestrategy)) {
                            reFreshTab();
                        }
                    }
                } finally {
                    getFrame().setCursor(Cursor.getDefaultCursor());
                }
            });

        } catch (Exception ex) {
            setErrorMessage("Error refreshing Tradestrategy.", ex.getMessage(), ex);
        }
    }

    /**
     * Method valueChanged.
     *
     * @param evt TreeSelectionEvent
     * @see TreeSelectionListener#valueChanged(TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent evt) {

        try {
            /*
             * Returns the last path element of the selection.This method is
             * useful only when the selection model allows a single selection.
             */
            TreePath path = evt.getNewLeadSelectionPath();
            if (null == path) {
                // Nothing is selected.
                return;
            }

            Object nodeInfo = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

            if (nodeInfo instanceof Tradestrategy tradestrategy) {
                periodEditorComboBox.setItem(BarSize.newInstance(tradestrategy.getBarSize()));
                int currentTabIndex = -1;
                for (int index = 0; index < m_jTabbedPaneContract.getTabCount(); index++) {
                    ChartPanel chartPanel = (ChartPanel) m_jTabbedPaneContract.getComponentAt(index);
                    if ((null != chartPanel) && chartPanel.getTradestrategy().getId()
                            .equals(tradestrategy.getId())) {
                        currentTabIndex = index;
                        break;
                    }
                }
                if (currentTabIndex == -1) {
                    ChartPanel chartPanel = createChartPanel(tradestrategy);
                    m_jTabbedPaneContract.add(chartPanel.getCandlestickChart().getName(), chartPanel);
                    currentTabIndex = m_jTabbedPaneContract.getTabCount() - 1;
                    m_jTabbedPaneContract.setTabComponentAt(currentTabIndex,
                            new TabbedCloseButton(m_jTabbedPaneContract, this));
                }
                m_jTabbedPaneContract.setSelectedIndex(currentTabIndex);
            }
        } catch (PersistentModelException ex) {
            setErrorMessage("Error refreshing Tradestrategy.", ex.getMessage(), ex);
        } catch (Exception ex) {
            setErrorMessage("Error enabling chart.", ex.getMessage(), ex);
        }
    }

    /**
     * Method doTransfer. This method may be called from this panel or the
     * Portfolio Tab or Trading Tab.
     *
     * @param tradestrategy Tradestrategy
     */
    public void doTransfer(final Tradestrategy tradestrategy) {
        brokerDataButton.setTransferObject(tradestrategy);
    }

    /**
     * Method stateChanged. Different tab selected.
     *
     * @param evt ChangeEvent
     * @see ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent evt) {
        // When a different tab is selected set the index
        if (evt.getSource() instanceof JTabbedPane selectedTab) {
            if (selectedTab.isShowing()) {
                this.reFreshTab();
            }
        }
    }

    /**
     * Method setConnected.
     *
     * @param connected Boolean
     */
    public void setConnected(Boolean connected) {
        this.connected = connected;
        this.reFreshTab();
    }

    /**
     * Method itemStateChanged.
     *
     * @param e ItemEvent
     * @see ItemListener#itemStateChanged(ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {

            ChartPanel currentTab = (ChartPanel) m_jTabbedPaneContract.getSelectedComponent();
            Integer newPeriod = Integer.valueOf(((BarSize) e.getItem()).getValue());

            if (null != currentTab && !this.isConnected()) {
                if (newPeriod.equals(BarSize.DAY)) {
                    newPeriod = currentTab.getTradestrategy().getBarSize();
                }
                if (!newPeriod.equals(
                        currentTab.getTradestrategy().getStrategyData().getCandleDataset().getSeries(0).getBarSize())) {
                    if (newPeriod.compareTo(currentTab.getTradestrategy().getBarSize()) > -1) {
                        currentTab.getTradestrategy().getStrategyData().changeCandleSeriesPeriod(newPeriod);
                        this.clearStatusBarMessage();
                    } else {
                        this.setStatusBarMessage("Time period not supported by candle series", BasePanel.WARNING);
                    }
                }
            }
        }
    }

    /**
     * Method isConnected.
     *
     * @return boolean
     */
    private boolean isConnected() {
        return this.connected;
    }

    /**
     * Method createChartPanel.
     *
     * @param tradestrategy Tradestrategy
     * @return ChartPanel
     */
    private ChartPanel createChartPanel(Tradestrategy tradestrategy) throws PersistentModelException {

        ZonedDateTime startDate;
        ZonedDateTime endDate;

        if (null == tradestrategy.getStrategyData()) {
            tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
        }

        if (tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty()) {
            endDate = TradingCalendar.getDateAtTime(
                    TradingCalendar.addTradingDays(tradestrategy.getTradingday().getClose(), backfillOffsetDays),
                    tradestrategy.getTradingday().getClose());
            startDate = endDate.minusDays((tradestrategy.getChartDays() - 1));
            startDate = TradingCalendar.getPrevTradingDay(startDate);
            startDate = TradingCalendar.getDateAtTime(startDate, tradestrategy.getTradingday().getOpen());
            List<Candle> candles = m_tradePersistentModel.findCandlesByContractDateRangeBarSize(
                    tradestrategy.getContract().getId(), startDate, endDate, tradestrategy.getBarSize());
            if (candles.isEmpty()) {
                this.setStatusBarMessage("No chart data available for " + tradestrategy.getContract().getSymbol(),
                        BasePanel.INFORMATION);
            } else {
                // Populate the candle series.
                CandleDataset.populateSeries(tradestrategy.getStrategyData(), candles);
                candles.clear();
                populateIndicatorCandleSeries(tradestrategy, startDate, endDate);
            }
        }

        return new ChartPanel(tradestrategy);
    }

    /**
     * Method populateIndicatorCandleSeries. For any child indicators that are
     * candle based create a Tradestrategy that will get the data. If this
     * tradestrategy already exist share this with any other tradestrategy that
     * requires this.
     *
     * @param tradestrategy Tradestrategy
     * @param startDate     Date
     * @param endDate       Date
     */
    private void populateIndicatorCandleSeries(Tradestrategy tradestrategy, ZonedDateTime startDate,
                                               ZonedDateTime endDate) throws PersistentModelException {

        CandleDataset candleDataset = (CandleDataset) tradestrategy.getStrategyData()
                .getIndicatorByType(IndicatorSeries.CandleSeries);
        if (null != candleDataset) {
            for (int seriesIndex = 0; seriesIndex < candleDataset.getSeriesCount(); seriesIndex++) {

                CandleSeries series = candleDataset.getSeries(seriesIndex);

                Contract contract = m_tradePersistentModel.findContractByUniqueKey(series.getSecType(),
                        series.getSymbol(), series.getExchange(), series.getCurrency(), null);
                if (null != contract) {
                    Tradestrategy childTradestrategy = new Tradestrategy(contract, tradestrategy.getTradingday(),
                            new Strategy(), tradestrategy.getPortfolio(), new BigDecimal(0), null, null, false,
                            tradestrategy.getChartDays(), tradestrategy.getBarSize());
                    childTradestrategy.setDirty(false);

                    List<Candle> indicatorCandles = m_tradePersistentModel.findCandlesByContractDateRangeBarSize(
                            childTradestrategy.getContract().getId(), startDate, endDate,
                            childTradestrategy.getBarSize());
                    if (indicatorCandles.isEmpty()) {
                        this.setStatusBarMessage(
                                "No chart data available for " + childTradestrategy.getContract().getSymbol(),
                                BasePanel.INFORMATION);
                    } else {
                        StrategyData strategyData = StrategyData.create(childTradestrategy);
                        CandleDataset.populateSeries(strategyData, indicatorCandles);
                        indicatorCandles.clear();

                        CandleSeries childSeries = strategyData.getBaseCandleSeries();
                        childSeries.setDisplaySeries(series.getDisplaySeries());
                        childSeries.setSeriesRGBColor(series.getSeriesRGBColor());
                        childSeries.setSubChart(series.getSubChart());
                        childSeries.setSymbol(series.getSymbol());
                        childSeries.setSecType(series.getSecType());
                        childSeries.setCurrency(series.getCurrency());
                        childSeries.setExchange(series.getExchange());
                        candleDataset.setSeries(seriesIndex, childSeries);
                    }
                }
            }
        }
    }

    /**
     *
     */
    private class TradeOrderTableRowListener implements ListSelectionListener {
        /**
         * Method valueChanged.
         *
         * @param event ListSelectionEvent
         * @see ListSelectionListener#valueChanged(ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                ListSelectionModel model = (ListSelectionModel) event.getSource();
                if (model.getLeadSelectionIndex() > -1) {
                    clearStatusBarMessage();
                    int row = m_tradeOrderTable.convertRowIndexToModel(model.getLeadSelectionIndex());

                    int i = 0;

                    for (TradeOrder tradeOrder : m_tradeOrderModel.getData().getTradeOrders()) {
                        if (i == row) {
                            cancelButton.setTransferObject(tradeOrder);
                            executeButton.setTransferObject(tradeOrder);
                            propertiesButton.setTransferObject(tradeOrder);
                            break;
                        }
                        i++;
                    }
                } else {
                    cancelButton.setTransferObject(new Aspects());
                    executeButton.setTransferObject(null);
                    propertiesButton.setTransferObject(null);
                }
            }
        }
    }

    /**
     * Method setStrategyLabel.
     *
     * @param tradestrategy Tradestrategy
     */
    private void setStrategyLabel(final Tradestrategy tradestrategy) {
        try {
            m_strategyLabel.setText(null);
            String primaryExchange = "";
            String industry = "";
            String strategyDesc = "";
            if (null != tradestrategy) {
                primaryExchange = (tradestrategy.getContract().getPrimaryExchange() == null ? "No Data Available"
                        : tradestrategy.getContract().getPrimaryExchange());
                industry = (tradestrategy.getContract().getIndustry() == null ? "No Data Available"
                        : tradestrategy.getContract().getIndustry());
                strategyDesc = (tradestrategy.getStrategy().getDescription() == null ? "No Data Available"
                        : tradestrategy.getStrategy().getDescription());
            }

            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), "Primary Exch: ", false, bold);
            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), CoreUtils.padRight(primaryExchange, 8), false,
                    null);
            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), " Industry:", false, bold);
            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), CoreUtils.padRight(industry, 30), false, null);
            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), "\n", false, null);
            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), "Strategy:", false, bold);
            CoreUtils.setDocumentText(m_strategyLabel.getDocument(), CoreUtils.padRight(strategyDesc, 30), false, null);

        } catch (Exception ex) {
            this.setErrorMessage("Error setting Tradestrategy Label.", ex.getMessage(), ex);
        }
    }

    /**
     * Method reFreshTab.
     */
    private void reFreshTab() {
        try {
            this.clearStatusBarMessage();
            Tradestrategy tradestrategy = null;
            TradestrategyOrders tradestrategyOrders = null;
            ChartPanel currentTab = (ChartPanel) m_jTabbedPaneContract.getSelectedComponent();
            if (null == currentTab) {
                m_tradeOrderModel.setData(new Tradestrategy());
                closeAllPositionsButton.setTransferObject(new Tradestrategy());
            } else {
                /*
                 * Refresh the Tradestrategy this will get the latest orders.
                 */
                tradestrategy = m_tradePersistentModel.findTradestrategyById(currentTab.getTradestrategy());
                tradestrategyOrders = m_tradePersistentModel
                        .findPositionOrdersByTradestrategyId(currentTab.getTradestrategy().getId());
                currentTab.setTradestrategy(tradestrategy);
                m_tradeOrderModel.setData(tradestrategy);
                RowSorter<?> rsDetail = m_tradeOrderTable.getRowSorter();
                rsDetail.setSortKeys(null);
                periodEditorComboBox.setItem(BarSize.newInstance(tradestrategy.getBarSize()));
                closeAllPositionsButton.setTransferObject(tradestrategy);
            }
            /*
             * Refresh the header label above the chart and buttons.
             */
            setStrategyLabel(tradestrategy);
            enableChartButtons(tradestrategy);

            double netValue = 0;
            String openQuantity = "";
            double commision = 0;
            double unRealizedPL = 0;
            double realizedPL = 0;
            String symbol = "";
            String side = "";
            String tier = "";
            String status = "";
            String portfolio = "";
            String risk = "";
            if (null != tradestrategy) {
                symbol = tradestrategy.getContract().getSymbol();
                side = (tradestrategy.getSide() == null ? ""
                        : Side.newInstance(tradestrategy.getSide()).getDisplayName());
                tier = (tradestrategy.getTier() == null ? ""
                        : Tier.newInstance(tradestrategy.getTier()).getDisplayName());
                status = (tradestrategy.getStatus() == null ? ""
                        : TradestrategyStatus.newInstance(tradestrategy.getStatus()).getDisplayName());
                portfolio = tradestrategy.getPortfolio().getName();
                risk = currencyFormater.format(
                        (tradestrategy.getRiskAmount() == null ? 0 : tradestrategy.getRiskAmount().doubleValue()));

                // Collections.sort(trade.getTradeOrders(), new
                // TradeOrder());

                /*
                 * Sum up orders that are filled and at the same time add the
                 * fill price. This happens when orders stop out as there are
                 * multiple stop orders for a position with multiple targets.
                 */

                TradeOrder prevTradeOrder = null;
                Integer prevIdTradePosition = null;
                for (TradeOrder order : tradestrategyOrders.getTradeOrders()) {

                    if (order.getIsFilled()) {
                        Integer quantity = order.getFilledQuantity();
                        if (null == prevIdTradePosition
                                || !prevIdTradePosition.equals(order.getTradePosition().getId())) {
                            prevIdTradePosition = order.getTradePosition().getId();
                        }

                        if (null != prevTradeOrder) {
                            if (prevTradeOrder.getIsFilled()
                                    && prevTradeOrder.getFilledDate().equals(order.getFilledDate())
                                    && prevTradeOrder.getAverageFilledPrice().equals(order.getAverageFilledPrice())) {
                                quantity = quantity + prevTradeOrder.getFilledQuantity();
                            }
                        }
                        currentTab.getCandlestickChart().addBuySellTradeArrow(order.getAction(),
                                new Money(order.getAverageFilledPrice()), order.getFilledDate(), quantity);

                    }
                    prevTradeOrder = order;
                }
                if (null != prevIdTradePosition) {
                    TradePosition tradePosition = m_tradePersistentModel.findTradePositionById(prevIdTradePosition);

                    unRealizedPL = tradePosition
                            .getUnRealizedProfit(
                                    tradestrategy.getStrategyData().getBaseCandleSeries().getContract().getLastPrice())
                            .doubleValue();
                    realizedPL = tradePosition.getRealizedProfit().doubleValue();
                    netValue = tradePosition.getTotalNetValue().doubleValue();
                    openQuantity = String.valueOf(Math.abs(tradePosition.getOpenQuantity()));
                    commision = tradePosition.getTotalCommission().doubleValue();
                }
            }

            netValue = netValue - commision;

            m_tradeLabel.setText(null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), "Symbol:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padRight(symbol, 10), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Side:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padRight(side, 6), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Tier:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padRight(tier, 6), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Status:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padRight(status, 20), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Portfolio:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padRight(portfolio, 15), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Risk:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padLeft(risk, 10), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), "\n", false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), "Net Total:", false, bold);
            if (netValue < 0) {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(netValue), 10), false, colorRedAttr);
            } else if (netValue > 0) {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(netValue), 10), false, colorGreenAttr);
            } else {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(netValue), 10), false, null);
            }
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Realized P/L:", false, bold);
            if (realizedPL < 0) {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(realizedPL), 10), false, colorRedAttr);
            } else if (realizedPL > 0) {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(realizedPL), 10), false, colorGreenAttr);
            } else {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(realizedPL), 10), false, null);
            }
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " UnRealized P/L:", false, bold);
            if (unRealizedPL < 0) {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(unRealizedPL), 10), false, colorRedAttr);
            } else if (unRealizedPL > 0) {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(unRealizedPL), 10), false, colorGreenAttr);
            } else {
                CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(unRealizedPL), 10), false, null);
            }
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Open Qty:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), CoreUtils.padLeft(openQuantity, 10), false, null);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(), " Comms:", false, bold);
            CoreUtils.setDocumentText(m_tradeLabel.getDocument(),
                    CoreUtils.padLeft(currencyFormater.format(commision), 10), false, null);
        } catch (Exception ex) {
            this.setErrorMessage("Error refreshing Tab.", ex.getMessage(), ex);
        }
    }

    /**
     * Method enableChartButtons.
     *
     * @param tradestrategy Tradestrategy
     */
    private void enableChartButtons(final Tradestrategy tradestrategy) {
        propertiesButton.setEnabled(false);
        executeButton.setEnabled(false);
        closeAllPositionsButton.setEnabled(false);
        brokerDataButton.setEnabled(false);
        cancelButton.setEnabled(false);
        cancelStrategiesButton.setEnabled(false);
        m_tradeOrderTable.enablePopupMenu(false);
        periodEditorComboBox.setEnabled(false);
        refreshButton.setEnabled(false);
        brokerDataButton.setTransferObject(tradestrategy);
        cancelStrategiesButton.setTransferObject(tradestrategy);
        refreshButton.setTransferObject(tradestrategy);

        if (null != tradestrategy) {
            propertiesButton.setEnabled(true);
            cancelStrategiesButton.setEnabled(true);
            brokerDataButton.setEnabled(true);
            if (this.isConnected()) {
                executeButton.setEnabled(true);
                refreshButton.setEnabled(true);
                cancelButton.setEnabled(true);
                closeAllPositionsButton.setEnabled(true);
                m_tradeOrderTable.enablePopupMenu(true);
            } else {
                periodEditorComboBox.setEnabled(true);
            }
        }
    }

    /**
     *
     */
    static class ChartPanel extends JPanel {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = 6151552506157648783L;
        private Tradestrategy tradestrategy;
        private final CandlestickChart candlestickChart;

        /**
         * Constructor for ChartPanel.
         *
         * @param tradestrategy Tradestrategy
         */
        ChartPanel(Tradestrategy tradestrategy) {
            this.tradestrategy = tradestrategy;
            this.setLayout(new BorderLayout());

            String ledgend = "(" + tradestrategy.getContract().getSymbol() + ") "
                    + (tradestrategy.getContract().getLongName() == null ? "Contract details not available."
                    : tradestrategy.getContract().getLongName());
            this.candlestickChart = new CandlestickChart(ledgend, tradestrategy.getStrategyData(),
                    tradestrategy.getTradingday());
            this.candlestickChart.setName(tradestrategy.getContract().getSymbol());
            this.add(this.candlestickChart);
        }

        /**
         * Method getTradestrategy.
         *
         * @return Tradestrategy
         */
        public Tradestrategy getTradestrategy() {
            return this.tradestrategy;
        }

        /**
         * Method setTradestrategy.
         *
         * @param tradestrategy Tradestrategy
         */
        public void setTradestrategy(Tradestrategy tradestrategy) {
            this.tradestrategy = tradestrategy;
        }

        /**
         * Method getCandlestickChart.
         *
         * @return CandlestickChart
         */
        public CandlestickChart getCandlestickChart() {
            return this.candlestickChart;
        }
    }

    /**
     *
     */
    static class AllocationMethodPanel extends JPanel {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = 5972331201407363985L;

        /**
         * Constructor for FAPropertiesPanel.
         *
         * @param tradeOrder TradeOrder
         */

        public AllocationMethodPanel(final TradeOrder tradeOrder) throws Exception {

            GridBagLayout gridBagLayout1 = new GridBagLayout();
            JPanel jPanel1 = new JPanel(gridBagLayout1);
            this.setLayout(new BorderLayout());
            this.setBorder(
                    BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Select Profile or Group"),
                            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            JLabel profileLabel = new JLabel("Profile: ");
            profileLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            JLabel groupLabel = new JLabel("Group: ");
            groupLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            groupLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            JLabel mthodLabel = new JLabel("Method: ");
            mthodLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            mthodLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            JLabel percentLabel = new JLabel("Percent: ");
            percentLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            percentLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            DecodeComboBoxEditor profileEditorComboBox = new DecodeComboBoxEditor(
                    DAOProfile.newInstance().getCodesDecodes());
            DecodeComboBoxRenderer profileTableRenderer = new DecodeComboBoxRenderer();
            profileEditorComboBox.setRenderer(profileTableRenderer);
            if (null != tradeOrder.getFAProfile())
                profileEditorComboBox.setItem(DAOProfile.newInstance(tradeOrder.getFAProfile()));
            profileEditorComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (!Decode.NONE.equals(((DAOProfile) e.getItem()).getDisplayName())) {
                        tradeOrder.setFAProfile(((Portfolio) ((DAOProfile) e.getItem()).getObject()).getName());
                    } else {
                        tradeOrder.setFAProfile(null);
                    }
                }
            });

            DecodeComboBoxEditor groupEditorComboBox = new DecodeComboBoxEditor(
                    DAOGroup.newInstance().getCodesDecodes());
            DecodeComboBoxRenderer groupTableRenderer = new DecodeComboBoxRenderer();
            groupEditorComboBox.setRenderer(groupTableRenderer);
            if (null != tradeOrder.getFAGroup())
                groupEditorComboBox.setItem(DAOGroup.newInstance(tradeOrder.getFAGroup()));
            groupEditorComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (!Decode.NONE.equals(((DAOGroup) e.getItem()).getDisplayName())) {
                        tradeOrder.setFAGroup(((Portfolio) ((DAOGroup) e.getItem()).getObject()).getName());
                    } else {
                        tradeOrder.setFAGroup(null);
                    }
                }
            });

            DecodeComboBoxEditor methodEditorComboBox = new DecodeComboBoxEditor(
                    AllocationMethod.newInstance().getCodesDecodes());
            DecodeComboBoxRenderer methodTableRenderer = new DecodeComboBoxRenderer();
            methodEditorComboBox.setRenderer(methodTableRenderer);
            if (null != tradeOrder.getFAMethod())
                methodEditorComboBox.setItem(AllocationMethod.newInstance(tradeOrder.getFAMethod()));
            methodEditorComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (!Decode.NONE.equals(((AllocationMethod) e.getItem()).getDisplayName())) {
                        tradeOrder.setFAMethod(((AllocationMethod) e.getItem()).getCode());
                    } else {
                        tradeOrder.setFAMethod(null);
                    }
                }
            });
            NumberFormat percentFormat = NumberFormat.getNumberInstance();
            percentFormat.setMinimumFractionDigits(2);
            final JFormattedTextField percentTextField = new JFormattedTextField(percentFormat);

            if (null != tradeOrder.getFAPercent()) {
                percentTextField.setText(Integer.toString(tradeOrder.getFAPercent().intValue()));
            }

            percentTextField.addPropertyChangeListener(e -> {
                Object source = e.getSource();
                if ("value".equals(e.getPropertyName())) {
                    if (source == percentTextField) {
                        if (percentTextField.isEditValid() && null != e.getNewValue()) {
                            Number rate = ((Number) percentTextField.getValue()).doubleValue();
                            tradeOrder.setFAPercent(BigDecimal.valueOf(rate.doubleValue()));
                        }
                    }
                }
            });
            jPanel1.add(profileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 20, 5));
            jPanel1.add(groupLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 20, 5));
            jPanel1.add(mthodLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 20, 5));
            jPanel1.add(percentLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 20, 5));

            jPanel1.add(profileEditorComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 20), 20, 5));
            jPanel1.add(groupEditorComboBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 20), 20, 5));
            jPanel1.add(methodEditorComboBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 20), 20, 5));
            jPanel1.add(percentTextField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 20), 20, 5));
            this.add(jPanel1);
        }
    }
}
