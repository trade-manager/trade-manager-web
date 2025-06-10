package org.trade.ui.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trade.base.BasePanel;
import org.trade.base.BasePanelMenu;
import org.trade.base.ComponentPrintService;
import org.trade.base.ImageBuilder;
import org.trade.base.WaitCursorEventQueue;
import org.trade.core.broker.BrokerDataRequestMonitor;
import org.trade.core.broker.BrokerModelException;
import org.trade.core.broker.IBrokerChangeListener;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;
import org.trade.indicator.StrategyDataUI;
import org.trade.ui.MainPanelMenu;
import org.trade.ui.widget.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class CandlestickChartApp extends BasePanel implements IBrokerChangeListener {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4275291770705110409L;

    @Autowired
    private TradeService tradeService;

    private final static Logger _log = LoggerFactory.getLogger(CandlestickChartApp.class);
    private final JPanel m_menuPanel;
    private static IBrokerModel m_brokerModel = null;
    private static BrokerDataRequestMonitor m_brokerDataRequestProgressMonitor = null;
    private static JFrame m_frame = null;

    // Main method

    /**
     * Method main.
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                TradeAppLoadConfig.loadAppProperties();
                m_frame = new JFrame();
                String symbol = "MSFT";

                m_brokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._brokerTest, CandlestickChartApp.class);

                Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
                // contract.setId(Integer.MAX_VALUE);
                ZonedDateTime endDate = TradingCalendar.getDateTimeNowMarketTimeZone();
                endDate = TradingCalendar.getTradingDayEnd(TradingCalendar.getPrevTradingDay(endDate));
                ZonedDateTime startDate = TradingCalendar.getTradingDayStart(endDate);

                Strategy daoStrategy = (Strategy) DAOStrategy.newInstance().getObject();
                String name = daoStrategy.getName();
               // Strategy strategy = tradeService.findStrategyByName(name);
                //Tradestrategy tradestrategy = getTradestrategy(contract, strategy, ChartDays.TWO_DAYS, BarSize.FIVE_MIN, startDate, endDate);
                //   tradestrategy.setId(Integer.MAX_VALUE);
                //runStrategy(tradeService, tradestrategy, true);

            } catch (Exception ex) {
                _log.error("Error getting broker data msg: {}", ex.getMessage(), ex);
            }
        });
    }

    /**
     * Constructor for CandlestickChartApp.
     *
     * @param chart CandlestickChart
     */
    public CandlestickChartApp(CandlestickChart chart) {

        this.setLayout(new BorderLayout());

        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());

        JPanel jPanelProgressBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JProgressBar progressBar = new JProgressBar(0, 0);
        jPanelProgressBar.add(progressBar);

        JPanel jPanelClock = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Clock clock = new Clock();
        jPanelClock.add(clock);

        JPanel jPanelStatus = new JPanel();
        jPanelStatus.setLayout(new GridLayout());
        JTextField jTextFieldStatus = new JTextField();
        jTextFieldStatus.setRequestFocusEnabled(false);
        jTextFieldStatus.setMargin(new Insets(5, 5, 5, 5));
        jTextFieldStatus.setBackground(Color.white);
        jTextFieldStatus.setBorder(BorderFactory.createLoweredBevelBorder());
        jPanelStatus.add(jTextFieldStatus);

        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(jPanelClock, BorderLayout.WEST);
        jPanel3.add(jPanelProgressBar, BorderLayout.EAST);
        jPanel3.add(jPanelStatus, BorderLayout.CENTER);

        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(chart, BorderLayout.CENTER);
        jPanel1.add(jPanel2, BorderLayout.CENTER);
        jPanel1.add(jPanel3, BorderLayout.SOUTH);
        m_menuPanel = new JPanel();
        m_menuPanel.setLayout(new BorderLayout());
        jPanel1.add(m_menuPanel, BorderLayout.NORTH);
        this.add(jPanel1, BorderLayout.CENTER);
        this.setStatusBar(jTextFieldStatus);
        this.setProgressBar(progressBar);

        MainPanelMenu m_menuBar = new MainPanelMenu(this);
        setMenu(m_menuBar);
        /* This is always true as main panel needs to receive all events */
        setSelected(true);
    }

    /**
     * Method setMenu.
     *
     * @param menu BasePanelMenu
     */
    public void setMenu(BasePanelMenu menu) {

        m_menuPanel.removeAll();
        m_menuPanel.add(menu, BorderLayout.NORTH);
        super.setMenu(menu);
    }

    public void doWindowClose() {

    }

    public void doWindowActivated() {

    }

    /**
     * Method doWindowDeActivated.
     *
     * @return boolean
     */
    public boolean doWindowDeActivated() {
        return true;
    }

    public void doWindowOpen() {

    }

    public void doPrint() {

        try {

            PageFormat pageFormat = new PageFormat();
            ComponentPrintService vista = new ComponentPrintService(((JFrame) this.getFrame()).getContentPane(),
                    pageFormat);
            vista.scaleToFit(true);
            // vista.print();
            PrinterJob pj = PrinterJob.getPrinterJob();
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

    public void connectionOpened() {

    }

    public void connectionClosed(boolean forced) {

    }

    /**
     * Method executionDetailsEnd.
     *
     * @param execDetails ConcurrentHashMap<Integer,TradeOrder>
     */
    public void executionDetailsEnd(ConcurrentHashMap<Integer, TradeOrder> execDetails) {

    }

    /**
     * Method historicalDataComplete.
     *
     * @param tradestrategy Tradestrategy
     */
    public void historicalDataComplete(Tradestrategy tradestrategy) {

    }

    /**
     * Method managedAccountsUpdated.
     *
     * @param accountNumber String
     */
    public void managedAccountsUpdated(String accountNumber) {

    }

    /**
     * Method fAAccountsCompleted. Notifies all registered listeners that the
     * brokerManagerModel has received all FA Accounts information.
     */
    public void fAAccountsCompleted() {

    }

    /**
     * Method updateAccountTime.
     *
     * @param accountNumber String
     */
    public void updateAccountTime(String accountNumber) {

    }

    /**
     * Method brokerError.
     *
     * @param brokerError BrokerModelException
     */
    public void brokerError(BrokerModelException brokerError) {

    }

    /**
     * Method tradeOrderFilled.
     *
     * @param tradeOrder TradeOrder
     */
    public void tradeOrderFilled(TradeOrder tradeOrder) {

    }

    /**
     * Method tradeOrderCancelled.
     *
     * @param tradeOrder TradeOrder
     */
    public void tradeOrderCancelled(TradeOrder tradeOrder) {

    }

    /**
     * Method tradeOrderStatusChanged.
     *
     * @param tradeOrder TradeOrder
     */
    public void tradeOrderStatusChanged(TradeOrder tradeOrder) {

    }

    /**
     * Method positionClosed.
     *
     * @param tradePosition TradePosition
     */
    public void positionClosed(TradePosition tradePosition) {

    }

    /**
     * Method openOrderEnd.
     *
     * @param openOrders ConcurrentHashMap<Integer,TradeOrder>
     */
    public void openOrderEnd(ConcurrentHashMap<Integer, TradeOrder> openOrders) {

    }

    private static void createChart(Tradestrategy tradestrategy) {

        StrategyDataUI strategyData = StrategyDataUI.create(tradestrategy);

        CandlestickChart chart = new CandlestickChart(tradestrategy.getContract().getSymbol(), strategyData,
                Tradingday.newInstance(TradingCalendar.getDateTimeNowMarketTimeZone()));
        CandlestickChartApp panel = new CandlestickChartApp(chart);

        m_frame.getContentPane().add(panel);
        m_frame.setSize(1200, 900);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        m_frame.setLocation((d.width - m_frame.getSize().width) / 2, (d.height - m_frame.getSize().height) / 2);
        m_frame.setIconImage(ImageBuilder.getImage("trade.gif"));
        m_frame.validate();
        m_frame.repaint();
        m_frame.setVisible(true);
        EventQueue waitQue = new WaitCursorEventQueue(500);
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQue);
    }

    private static Tradestrategy getTradestrategy(Contract contract, Strategy strategy, Integer chartDays, Integer barSize, ZonedDateTime open, ZonedDateTime close) {

        Tradingday tradingday = new Tradingday(open, close);
        //  tradingday.setId(Integer.MAX_VALUE);
        Tradestrategy tradestrategy;
        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        int riskAmount = 0;

        try {

            chartDays = ConfigProperties.getPropAsInt("trade.backfill.duration");

            if (!ChartDays.newInstance(chartDays).isValid()) {

                chartDays = 2;
            }

            barSize = ConfigProperties.getPropAsInt("trade.backfill.barsize");

            if (!BarSize.newInstance(barSize).isValid()) {

                barSize = 300;
            }

            riskAmount = ConfigProperties.getPropAsInt("trade.risk");

        } catch (Exception e) {
            // Do nothing
        }

        tradestrategy = new Tradestrategy(
                new Contract(SECType.STOCK, "", Exchange.SMART, Currency.USD, null, null), tradingday, strategy,
                portfolio, new BigDecimal(riskAmount), null, null, true, chartDays, barSize);

        tradestrategy.setRiskAmount(new BigDecimal(riskAmount));
        tradestrategy.setBarSize(barSize);
        tradestrategy.setChartDays(chartDays);
        tradestrategy.setTrade(true);
        tradestrategy.setDirty(true);
        tradestrategy.setStrategy(strategy);
        tradestrategy.setPortfolio(portfolio);
        tradestrategy.setContract(contract);
        tradingday.addTradestrategy(tradestrategy);
        return tradestrategy;
    }

    /**
     * Method runStrategy.
     *
     * @param tradestrategy  Tradestrategy
     * @param brokerDataOnly boolean
     */
    private static void runStrategy(TradeService tradeService, Tradestrategy tradestrategy, boolean brokerDataOnly) {

        try {

            m_brokerModel.setBrokerDataOnly(brokerDataOnly);

            Tradingday tradingday = tradestrategy.getTradingday();

            if (Tradingdays.hasTradeOrders(tradingday) && !brokerDataOnly) {

                int result = JOptionPane.showConfirmDialog(m_frame,
                        "Tradingday: " + tradingday.getOpen()
                                + " has orders. Do you want to delete all orders?",
                        "Information", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {

                    tradeService.removeTradingdayTradeOrders(tradingday);
                }
            }

            try {

                if (brokerDataOnly && !m_brokerModel.validateBrokerData(tradestrategy)) {

                    return;
                }
            } catch (BrokerModelException ex) {

                JOptionPane.showConfirmDialog(m_frame, ex.getMessage(), "Warning",
                        JOptionPane.OK_CANCEL_OPTION);
                return;
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

                    int result = JOptionPane.showConfirmDialog(m_frame,
                            "Candle data already exists for Symbol: "
                                    + tradestrategy.getContract().getSymbol() + " Do you want to delete?",
                            "Information", JOptionPane.YES_NO_OPTION);

                    if (result == JOptionPane.YES_OPTION) {

                        for (Candle item : candles) {

                            tradeService.removeAspect(item);
                        }
                    } else {
                        return;
                    }
                }
            }

            Tradingdays tradingdays = new Tradingdays();
            tradingdays.add(tradingday);
            m_brokerDataRequestProgressMonitor = new BrokerDataRequestMonitor(m_brokerModel, tradeService,
                    tradingdays);
            m_brokerDataRequestProgressMonitor.addPropertyChangeListener(evt -> SwingUtilities.invokeLater(() -> {

                if ("progress".equals(evt.getPropertyName())) {

                    int progress = (Integer) evt.getNewValue();
                } else if ("information".equals(evt.getPropertyName())) {

                    if (m_brokerDataRequestProgressMonitor.isDone()) {

                        createChart(tradestrategy);
                    }
                } else if ("error".equals(evt.getPropertyName())) {

                    JOptionPane.showConfirmDialog(m_frame, "Error getting history data msg: " +
                                    ((Exception) evt.getNewValue()).getMessage() + " value: " + evt.getNewValue(), "Error",
                            JOptionPane.OK_CANCEL_OPTION);
                }
            }));

            m_brokerDataRequestProgressMonitor.execute();
        } catch (Exception ex) {

            JOptionPane.showConfirmDialog(m_frame, "Error running Strategies or Chart Data msg: " +
                            ex.getMessage(), "Error",
                    JOptionPane.OK_CANCEL_OPTION);
        }
    }

    /**
     *
     */
    static class ComponentPrintable implements Printable {
        private final Component m_component;

        /**
         * Constructor for ComponentPrintable.
         *
         * @param c Component
         */
        public ComponentPrintable(Component c) {
            m_component = c;
        }

        /**
         * Method print.
         *
         * @param g          Graphics
         * @param pageFormat PageFormat
         * @param pageIndex  int
         * @return int
         * @see Printable#print(Graphics, PageFormat, int)
         */
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) {

            if (pageIndex > 0) {

                return NO_SUCH_PAGE;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            boolean wasBuffered = disableDoubleBuffering(m_component);
            m_component.printAll(g2);
            restoreDoubleBuffering(m_component, wasBuffered);
            return PAGE_EXISTS;
        }

        /**
         * Method disableDoubleBuffering.
         *
         * @param c Component
         * @return boolean
         */
        private boolean disableDoubleBuffering(Component c) {

            if (!(c instanceof JComponent jc)) {
                return false;
            }

            boolean wasBuffered = jc.isDoubleBuffered();
            jc.setDoubleBuffered(false);
            return wasBuffered;
        }

        /**
         * Method restoreDoubleBuffering.
         *
         * @param c           Component
         * @param wasBuffered boolean
         */
        private void restoreDoubleBuffering(Component c, boolean wasBuffered) {

            if (c instanceof JComponent) {
                ((JComponent) c).setDoubleBuffered(wasBuffered);
            }
        }
    }
}
