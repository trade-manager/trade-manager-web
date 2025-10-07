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
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.tradingday.Tradingdays;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AllocationMethod;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ContentType;
import org.trade.core.valuetype.DAOGroup;
import org.trade.core.valuetype.DAOProfile;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.Tier;
import org.trade.core.valuetype.TradestrategyStatus;
import org.trade.indicator.CandleDataset;
import org.trade.indicator.CandleSeries;
import org.trade.indicator.IndicatorSeries;
import org.trade.indicator.StrategyDataUI;
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
import java.util.Hashtable;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ContractPanel extends BasePanel implements TreeSelectionListener, ChangeListener, ItemListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4026209743607182423L;

    private final TradeService tradeService;
    private Tradingdays tradingdays = null;
    private final JTabbedPane jTabbedPaneContract = new JTabbedPane();
    private TradingdayTreeModel treeModel = null;
    private Tree tree = null;
    private Table tradeOrderTable = null;
    private TradeOrderTableModel tradeOrderModel = null;
    private JEditorPane tradeLabel = null;
    private JEditorPane strategyLabel = null;
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
    private static final Hashtable<Long, StrategyDataUI> strategyDataTable = new Hashtable();

    static {

        StyleConstants.setBold(bold, true);
        StyleConstants.setBackground(colorRedAttr, Color.RED);
        StyleConstants.setBackground(colorGreenAttr, Color.GREEN);
    }

    /**
     * Constructor for ContractPanel.
     *
     * @param tradingdays Tradingdays
     * @param controller  TabbedAppPanel
     */
    public ContractPanel(Tradingdays tradingdays, TabbedAppPanel controller, final TradeService tradeService) {

        this.tradeService = tradeService;
        this.tradingdays = tradingdays;

        try {

            if (null != getMenu()) {

                getMenu().addMessageListener(this);
            }

            this.setLayout(new BorderLayout());
            this.currencyFormater.setMinimumFractionDigits(2);
            this.backfillOffsetDays = ConfigProperties.getPropAsInt("trade.backfill.offsetDays");
            this.propertiesButton = new BaseButton(this, BaseUIPropertyCodes.PROPERTIES, 0);
            this.propertiesButton.setEnabled(false);
            this.executeButton = new BaseButton(controller, BaseUIPropertyCodes.EXECUTE);
            this.executeButton.addMessageListener(this);
            this.brokerDataButton = new BaseButton(controller, BaseUIPropertyCodes.DATA);
            this.brokerDataButton.setToolTipText("Get Chart Data");
            this.cancelButton = new BaseButton(controller, BaseUIPropertyCodes.CANCEL);
            this.cancelButton.setToolTipText("Cancel Order");
            this.cancelButton.setTransferObject(new Aspects());
            this.cancelButton.addMessageListener(this);
            this.cancelStrategiesButton = new BaseButton(controller, BaseUIPropertyCodes.CANCEL);
            this.cancelStrategiesButton.setToolTipText("Cancel Strategy");
            this.refreshButton = new BaseButton(this, BaseUIPropertyCodes.REFRESH);
            BaseButton closeAllButton = new BaseButton(this, BaseUIPropertyCodes.CLOSE_ALL);
            this.closeAllPositionsButton = new BaseButton(controller, BaseUIPropertyCodes.CLOSE_ALL);
            this.closeAllPositionsButton.setToolTipText("Cancel Orders & Close Position");
            this.tradeOrderModel = new TradeOrderTableModel();
            this.tradeOrderTable = new TradeOrderTable(tradeOrderModel);
            this.tradeOrderTable.getSelectionModel().addListSelectionListener(new TradeOrderTableRowListener());
            this.tradeOrderTable.setDefaultEditor(TradeOrder.class, new ButtonEditor(propertiesButton));
            this.tradeOrderTable.setDefaultRenderer(TradeOrder.class, new ButtonRenderer(BaseUIPropertyCodes.PROPERTIES));
            this.treeModel = new TradingdayTreeModel(this.tradingdays);
            this.tree = new Tree(treeModel);
            // Listen for when the selection changes.
            this.tree.addTreeSelectionListener(this);
            this.tree.setCellRenderer(new TradingdayTreeCellRenderer());
            ToolTipManager.sharedInstance().registerComponent(this.tree);

            JPanel jPanel1 = new JPanel(new BorderLayout());
            JScrollPane jScrollPane1Tree = new JScrollPane();
            jScrollPane1Tree.getViewport().add(this.tree, BorderLayout.CENTER);
            JPanel jPanel2 = new JPanel(new BorderLayout());
            jPanel2.add(jScrollPane1Tree, BorderLayout.CENTER);
            jPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Tradingday"),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            jPanel1.setBorder(new BevelBorder(BevelBorder.LOWERED));
            jPanel2.add(jPanel1, BorderLayout.NORTH);

            // Chart Panel
            JLabel jLabelPeriod = new JLabel("Period:");
            this.periodEditorComboBox = new DecodeComboBoxEditor((new BarSize()).getCodesDecodes());
            DecodeComboBoxRenderer periodRenderer = new DecodeComboBoxRenderer();
            this.periodEditorComboBox.setRenderer(periodRenderer);
            this.periodEditorComboBox.setItem(BarSize.newInstance(BarSize.FIVE_MIN));
            this.periodEditorComboBox.setEnabled(false);
            this.periodEditorComboBox.addItemListener(this);
            JPanel jPanel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jPanel6.setBorder(new BevelBorder(BevelBorder.RAISED));
            jPanel6.add(closeAllButton, null);
            jPanel6.add(this.brokerDataButton, null);
            jPanel6.add(this.cancelStrategiesButton, null);
            jPanel6.add(jLabelPeriod, null);
            jPanel6.add(this.periodEditorComboBox, null);
            JToolBar jToolBar = new JToolBar();
            jToolBar.setLayout(new BorderLayout());
            jToolBar.add(jPanel6);

            this.strategyLabel = new JEditorPane(ContentType.TEXT, "");
            this.strategyLabel.setAutoscrolls(false);
            this.strategyLabel.setEditable(false);
            this.tradeLabel = new JEditorPane(ContentType.TEXT, "");
            this.tradeLabel.setAutoscrolls(false);
            this.tradeLabel.setEditable(false);

            JPanel jPanel12 = new JPanel(new BorderLayout());
            jPanel12.add(strategyLabel, null);
            JPanel jPanel18 = new JPanel(new BorderLayout());
            jPanel18.add(jToolBar, BorderLayout.WEST);
            JPanel jPanel11 = new JPanel(new BorderLayout());
            jPanel11.add(jPanel18, BorderLayout.WEST);
            jPanel11.add(jPanel12, BorderLayout.CENTER);
            JPanel jPanel7 = new JPanel(new BorderLayout());
            jPanel7.add(jTabbedPaneContract, BorderLayout.CENTER);
            JScrollPane jScrollPane3 = new JScrollPane();
            jScrollPane3.getViewport().add(jPanel7, BorderLayout.CENTER);
            JPanel jPanel9 = new JPanel(new BorderLayout());
            jPanel9.add(jScrollPane3, BorderLayout.CENTER);
            jPanel9.add(jPanel11, BorderLayout.NORTH);
            // Order Panel
            this.tradeOrderTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JPanel jPanel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            jPanel5.add(this.executeButton, null);
            jPanel5.add(this.cancelButton, null);
            jPanel5.add(this.refreshButton, null);
            jPanel5.add(this.closeAllPositionsButton, null);
            jPanel5.setBorder(new BevelBorder(BevelBorder.RAISED));
            JToolBar jToolBar1 = new JToolBar();
            jToolBar1.setLayout(new BorderLayout());
            jToolBar1.add(jPanel5);
            JPanel jPanel19 = new JPanel(new BorderLayout());
            jPanel19.add(jToolBar1, BorderLayout.WEST);
            JScrollPane jScrollPane2 = new JScrollPane();
            jScrollPane2.getViewport().add(this.tradeOrderTable, BorderLayout.CENTER);
            jScrollPane2.setBorder(new BevelBorder(BevelBorder.LOWERED));
            JPanel jPanel16 = new JPanel(new BorderLayout());
            Dimension d = this.tradeOrderTable.getPreferredSize();
            // Make changes to [i]d[/i] if you like...
            this.tradeOrderTable.setPreferredScrollableViewportSize(d);
            jScrollPane2.addMouseListener(this.tradeOrderTable);
            JPanel jPanel17 = new JPanel(new BorderLayout());
            jPanel17.add(jPanel19, BorderLayout.WEST);
            jPanel17.add(tradeLabel, BorderLayout.CENTER);
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
            this.jTabbedPaneContract.addChangeListener(this);
            this.reFreshTab();
        } catch (Exception ex) {

            this.setErrorMessage("Error during initialization.", ex.getMessage(), ex);
        }
    }

    public void doOpen() {

        try {

            this.treeModel.setData(this.tradingdays);
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

            int tabsCount = jTabbedPaneContract.getTabCount();

            for (int index = 0; index < tabsCount; index++) {

                doClose(0);
            }
            tree.clearSelection();
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

        for (int index = 0; index < jTabbedPaneContract.getTabCount(); index++) {

            ChartPanel chart = (ChartPanel) jTabbedPaneContract.getComponentAt(index);

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

        ChartPanel chartPanel = (ChartPanel) jTabbedPaneContract.getComponentAt(index);
        TabbedCloseButton tabbedCloseButton = (TabbedCloseButton) jTabbedPaneContract.getTabComponentAt(index);
        tabbedCloseButton.removeMessageListener(this);
        chartPanel.getCandlestickChart().removeChart();
        chartPanel = null;
        jTabbedPaneContract.remove(index);
        tree.clearSelection();
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
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (null != selectedNode)
                    selectedObject = selectedNode.getUserObject();
            }
            treeModel.setData(tradingdays);
            // Expand the tree
            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
            TreePath path = tree.findTreePathByObject(selectedObject);

            if (null != path) {
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
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
        ChartPanel currentTab = (ChartPanel) jTabbedPaneContract.getSelectedComponent();
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
                    ChartPanel currentTab = (ChartPanel) jTabbedPaneContract.getSelectedComponent();
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
                for (int index = 0; index < jTabbedPaneContract.getTabCount(); index++) {

                    ChartPanel chartPanel = (ChartPanel) jTabbedPaneContract.getComponentAt(index);

                    if ((null != chartPanel) && chartPanel.getTradestrategy().getId()
                            .equals(tradestrategy.getId())) {

                        currentTabIndex = index;
                        break;
                    }
                }

                if (currentTabIndex == -1) {

                    ChartPanel chartPanel = createChartPanel(tradestrategy);
                    jTabbedPaneContract.add(chartPanel.getCandlestickChart().getName(), chartPanel);
                    currentTabIndex = jTabbedPaneContract.getTabCount() - 1;
                    jTabbedPaneContract.setTabComponentAt(currentTabIndex,
                            new TabbedCloseButton(jTabbedPaneContract, this));
                }
                jTabbedPaneContract.setSelectedIndex(currentTabIndex);
            }
        } catch (ServiceException ex) {
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

            ChartPanel currentTab = (ChartPanel) jTabbedPaneContract.getSelectedComponent();
            Integer newPeriod = Integer.valueOf(((BarSize) e.getItem()).getValue());

            if (null != currentTab && !this.isConnected()) {
                if (newPeriod.equals(BarSize.DAY)) {
                    newPeriod = currentTab.getTradestrategy().getBarSize();
                }
                if (!newPeriod.equals(
                        currentTab.getTradestrategy().getStrategyData().getCandleDataset().getSeries(0).getBarSize())) {
                    if (newPeriod.compareTo(currentTab.getTradestrategy().getBarSize()) > -1) {
                        try {
                            currentTab.getTradestrategy().getStrategyData().changeCandleSeriesPeriod(newPeriod);
                        } catch (ServiceException ex) {
                            throw new RuntimeException(ex);
                        }
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
    private ChartPanel createChartPanel(Tradestrategy tradestrategy) throws ServiceException {

        ZonedDateTime endDate = tradestrategy.getTradingday().getClose();
        ZonedDateTime startDate = TradingCalendar.addTradingDays(tradestrategy.getTradingday().getOpen(),
                (-1 * (tradestrategy.getChartDays())));

        StrategyDataUI strategyDataUI = null;

        if (!strategyDataTable.contains(tradestrategy.getContract().getId())) {

            if (null == tradestrategy.getStrategyData()) {

                tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
            }

            strategyDataUI = StrategyDataUI.create(tradestrategy);
            strategyDataTable.put(tradestrategy.getContract().getId(), strategyDataUI);
        } else {

            strategyDataUI = strategyDataTable.get(tradestrategy.getContract().getId());
        }

        if (strategyDataUI.getBaseCandleSeries().isEmpty()) {

            List<Candle> candles = this.tradeService.findCandlesByContractDateRangeBarSize(
                    tradestrategy.getContract(), startDate, endDate, tradestrategy.getBarSize());

            if (candles.isEmpty()) {

                this.setStatusBarMessage("No chart data available for " + tradestrategy.getContract().getSymbol(),
                        BasePanel.INFORMATION);
            } else {

                // Populate the candle series.
                org.trade.core.persistent.dao.series.indicator.CandleDataset.populateSeries(tradestrategy.getStrategyData(), candles);
                CandleDataset.populateSeries(strategyDataUI, candles);
                candles.clear();
                populateIndicatorCandleSeries(tradestrategy, startDate, endDate);
                populateIndicatorCandleSeries(tradestrategy, strategyDataUI, startDate, endDate);
            }
        }

        return new ChartPanel(tradestrategy, strategyDataUI);
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
                                               ZonedDateTime endDate) throws ServiceException {

        org.trade.core.persistent.dao.series.indicator.CandleDataset candleDataset = (org.trade.core.persistent.dao.series.indicator.CandleDataset) tradestrategy.getStrategyData()
                .getIndicatorByType(org.trade.core.persistent.dao.series.indicator.IndicatorSeries.CandleSeries);

        if (null != candleDataset) {

            for (int seriesIndex = 0; seriesIndex < candleDataset.getSeriesCount(); seriesIndex++) {

                org.trade.core.persistent.dao.series.indicator.CandleSeries series = candleDataset.getSeries(seriesIndex);

                Contract contract = this.tradeService.findContractByUniqueKey(series.getSecType(),
                        series.getSymbol(), series.getExchange(), series.getCurrency(), null);

                if (null != contract) {

                    Tradestrategy childTradestrategy = new Tradestrategy(contract, tradestrategy.getTradingday(),
                            new Strategy(), tradestrategy.getPortfolio(), new BigDecimal(0), null, null, false,
                            tradestrategy.getChartDays(), tradestrategy.getBarSize());
                    childTradestrategy.setDirty(false);

                    List<Candle> indicatorCandles = this.tradeService.findCandlesByContractDateRangeBarSize(
                            childTradestrategy.getContract(), startDate, endDate,
                            childTradestrategy.getBarSize());

                    if (indicatorCandles.isEmpty()) {

                        this.setStatusBarMessage(
                                "No chart data available for " + childTradestrategy.getContract().getSymbol(),
                                BasePanel.INFORMATION);
                    } else {

                        StrategyData strategyData = StrategyData.create(childTradestrategy);
                        org.trade.core.persistent.dao.series.indicator.CandleDataset.populateSeries(strategyData, indicatorCandles);
                        indicatorCandles.clear();

                        org.trade.core.persistent.dao.series.indicator.CandleSeries childSeries = strategyData.getBaseCandleSeries();
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
     * Method populateIndicatorCandleSeries. For any child indicators that are
     * candle based create a Tradestrategy that will get the data. If this
     * tradestrategy already exist share this with any other tradestrategy that
     * requires this.
     *
     * @param tradestrategy  Tradestrategy
     * @param strategyDataUI StrategyDataUI
     * @param startDate      Date
     * @param endDate        Date
     */
    private void populateIndicatorCandleSeries(Tradestrategy tradestrategy, StrategyDataUI strategyDataUI, ZonedDateTime startDate,
                                               ZonedDateTime endDate) throws ServiceException {

        CandleDataset candleDatasetUI = (CandleDataset) strategyDataUI
                .getIndicatorByType(IndicatorSeries.CandleSeries);

        if (null != candleDatasetUI) {

            for (int seriesIndex = 0; seriesIndex < candleDatasetUI.getSeriesCount(); seriesIndex++) {

                CandleSeries series = candleDatasetUI.getSeries(seriesIndex);

                Contract contract = this.tradeService.findContractByUniqueKey(series.getSecType(),
                        series.getSymbol(), series.getExchange(), series.getCurrency(), null);

                if (null != contract) {

                    Tradestrategy childTradestrategy = new Tradestrategy(contract, tradestrategy.getTradingday(),
                            new Strategy(), tradestrategy.getPortfolio(), new BigDecimal(0), null, null, false,
                            tradestrategy.getChartDays(), tradestrategy.getBarSize());
                    childTradestrategy.setDirty(false);

                    List<Candle> indicatorCandles = this.tradeService.findCandlesByContractDateRangeBarSize(
                            childTradestrategy.getContract(), startDate, endDate,
                            childTradestrategy.getBarSize());

                    if (indicatorCandles.isEmpty()) {

                        this.setStatusBarMessage(
                                "No chart data available for " + childTradestrategy.getContract().getSymbol(),
                                BasePanel.INFORMATION);
                    } else {

                        StrategyDataUI childStrategyDataUI = StrategyDataUI.create(childTradestrategy);
                        CandleDataset.populateSeries(childStrategyDataUI, indicatorCandles);
                        indicatorCandles.clear();

                        CandleSeries childSeries = childStrategyDataUI.getBaseCandleSeries();
                        childSeries.setDisplaySeries(series.getDisplaySeries());
                        childSeries.setSeriesRGBColor(series.getSeriesRGBColor());
                        childSeries.setSubChart(series.getSubChart());
                        childSeries.setSymbol(series.getSymbol());
                        childSeries.setSecType(series.getSecType());
                        childSeries.setCurrency(series.getCurrency());
                        childSeries.setExchange(series.getExchange());
                        candleDatasetUI.setSeries(seriesIndex, childSeries);
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
                    int row = tradeOrderTable.convertRowIndexToModel(model.getLeadSelectionIndex());

                    int i = 0;

                    for (TradeOrder tradeOrder : tradeOrderModel.getData().getTradeOrders()) {

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
            strategyLabel.setText(null);
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

            CoreUtils.setDocumentText(strategyLabel.getDocument(), "Primary Exch: ", false, bold);
            CoreUtils.setDocumentText(strategyLabel.getDocument(), CoreUtils.padRight(primaryExchange, 8), false,
                    null);
            CoreUtils.setDocumentText(strategyLabel.getDocument(), " Industry:", false, bold);
            CoreUtils.setDocumentText(strategyLabel.getDocument(), CoreUtils.padRight(industry, 30), false, null);
            CoreUtils.setDocumentText(strategyLabel.getDocument(), "\n", false, null);
            CoreUtils.setDocumentText(strategyLabel.getDocument(), "Strategy:", false, bold);
            CoreUtils.setDocumentText(strategyLabel.getDocument(), CoreUtils.padRight(strategyDesc, 30), false, null);

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
            ChartPanel currentTab = (ChartPanel) jTabbedPaneContract.getSelectedComponent();
            if (null == currentTab) {
                tradeOrderModel.setData(new Tradestrategy());
                closeAllPositionsButton.setTransferObject(new Tradestrategy());
            } else {
                /*
                 * Refresh the Tradestrategy this will get the latest orders.
                 */
                tradestrategy = this.tradeService.findTradestrategyById(currentTab.getTradestrategy());
                tradestrategyOrders = this.tradeService
                        .findPositionOrdersByTradestrategyId(currentTab.getTradestrategy().getId());
                currentTab.setTradestrategy(tradestrategy);
                tradeOrderModel.setData(tradestrategy);
                RowSorter<?> rsDetail = tradeOrderTable.getRowSorter();
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
                Long prevIdTradePosition = null;
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

                    TradePosition tradePosition = this.tradeService.findTradePositionById(prevIdTradePosition);
                    unRealizedPL = tradePosition.getUnRealizedProfit(tradestrategy.getStrategyData().getBaseCandleSeries().getContract().getLastPrice()).doubleValue();
                    realizedPL = tradePosition.getRealizedProfit().doubleValue();
                    netValue = tradePosition.getTotalNetValue().doubleValue();
                    openQuantity = String.valueOf(Math.abs(tradePosition.getOpenQuantity()));
                    commision = tradePosition.getTotalCommission().doubleValue();
                }
            }

            netValue = netValue - commision;

            tradeLabel.setText(null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), "Symbol:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padRight(symbol, 10), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Side:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padRight(side, 6), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Tier:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padRight(tier, 6), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Status:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padRight(status, 20), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Portfolio:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padRight(portfolio, 15), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Risk:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padLeft(risk, 10), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), "\n", false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), "Net Total:", false, bold);
            if (netValue < 0) {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(netValue), 10), false, colorRedAttr);
            } else if (netValue > 0) {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(netValue), 10), false, colorGreenAttr);
            } else {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(netValue), 10), false, null);
            }
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Realized P/L:", false, bold);
            if (realizedPL < 0) {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(realizedPL), 10), false, colorRedAttr);
            } else if (realizedPL > 0) {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(realizedPL), 10), false, colorGreenAttr);
            } else {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(realizedPL), 10), false, null);
            }
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " UnRealized P/L:", false, bold);
            if (unRealizedPL < 0) {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(unRealizedPL), 10), false, colorRedAttr);
            } else if (unRealizedPL > 0) {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(unRealizedPL), 10), false, colorGreenAttr);
            } else {
                CoreUtils.setDocumentText(tradeLabel.getDocument(),
                        CoreUtils.padLeft(currencyFormater.format(unRealizedPL), 10), false, null);
            }
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Open Qty:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), CoreUtils.padLeft(openQuantity, 10), false, null);
            CoreUtils.setDocumentText(tradeLabel.getDocument(), " Comms:", false, bold);
            CoreUtils.setDocumentText(tradeLabel.getDocument(),
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
        tradeOrderTable.enablePopupMenu(false);
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
                tradeOrderTable.enablePopupMenu(true);
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
        ChartPanel(Tradestrategy tradestrategy, StrategyDataUI strategyDataUI) {

            this.tradestrategy = tradestrategy;
            this.setLayout(new BorderLayout());

            String ledgend = "(" + tradestrategy.getContract().getSymbol() + ") "
                    + (tradestrategy.getContract().getLongName() == null ? "Contract details not available."
                    : tradestrategy.getContract().getLongName());

            this.candlestickChart = new CandlestickChart(ledgend, strategyDataUI,
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
