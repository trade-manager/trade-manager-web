package org.trade.ui.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.base.BasePanel;
import org.trade.base.BasePanelMenu;
import org.trade.base.ComponentPrintService;
import org.trade.base.ImageBuilder;
import org.trade.base.WaitCursorEventQueue;
import org.trade.core.TradeAppLoadConfig;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.StrategyHome;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;
import org.trade.indicator.CandleDatasetUI;
import org.trade.indicator.CandleSeriesUI;
import org.trade.indicator.StrategyDataUI;
import org.trade.ui.MainPanelMenu;
import org.trade.ui.widget.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serial;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 */
public class CandlestickChartApp extends BasePanel {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4275291770705110409L;
    /**
     *
     */

    private final static Logger _log = LoggerFactory.getLogger(CandlestickChartApp.class);

    private final JPanel m_menuPanel;

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
                JFrame frame = new JFrame();
                String symbol = "MSFT";
                // StrategyData data = CandlestickChartTest
                // .getPriceDataSetYahooDay(symbol);
                int numberOfDays = 2;
                StrategyDataUI strategyData = CandlestickChartApp.getPriceDataSetYahooIntraday(symbol, numberOfDays,
                        BarSize.FIVE_MIN);
                CandlestickChart chart = new CandlestickChart(symbol, strategyData,
                        Tradingday.newInstance(TradingCalendar.getDateTimeNowMarketTimeZone()));
                CandlestickChartApp panel = new CandlestickChartApp(chart);

                frame.getContentPane().add(panel);
                frame.setSize(1200, 900);
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

                frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
                frame.setIconImage(ImageBuilder.getImage("trade.gif"));
                frame.validate();
                frame.repaint();
                frame.setVisible(true);
                EventQueue waitQue = new WaitCursorEventQueue(500);
                Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQue);
            } catch (Exception ex) {
                _log.error("Error getting Yahoo data msg: {}", ex.getMessage(), ex);
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

    /**
     * Method getPriceDataSetYahooDay.
     *
     * @param symbol String
     * @return StrategyData
     */
    protected static StrategyDataUI getPriceDataSetYahooDay(String symbol) {
        try {

            List<Candle> candles = new ArrayList<>();
            Strategy daoStrategy = (Strategy) DAOStrategy.newInstance().getObject();
            StrategyHome home = new StrategyHome();
            String name = daoStrategy.getName();
            Strategy strategy = home.findByName(name);
            Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
            ZonedDateTime today = TradingCalendar.getDateTimeNowMarketTimeZone();
            ZonedDateTime startDate = today.minusMonths(3);

            /*
             * Yahoo finance So IBM form 1/1/2012 thru 06/30/2012
             * http://ichart.finance
             * .yahoo.com/table.csv?s=IBM&a=0&b=1&c=2012&d=5
             * &e=30&f=2012&ignore=.csv"
             */

            String strUrl = "http://ichart.finance.yahoo.com/table.csv?s=" + symbol + "&a=" + startDate.getMonth()
                    + "&b=" + startDate.getDayOfMonth() + "&c=" + startDate.getYear() + "&d=" + today.getMonth() + "&e="
                    + today.getDayOfMonth() + "&f=" + today.getYear() + "&ignore=.csv";

            URL url = new URI(strUrl).toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateTimeFormatter df = DateTimeFormatter.ofPattern("y-M-d");

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");
                ZonedDateTime date = ZonedDateTime.parse(st.nextToken(), df);
                Tradingday tradingday = Tradingday.newInstance(date);
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                long volume = Long.parseLong(st.nextToken());
                // double adjClose = Double.parseDouble( st.nextToken() );
                CandlePeriod period = new CandlePeriod(TradingCalendar.getTradingDayStart(date),
                        TradingCalendar.getTradingDayEnd(date).minusSeconds(1));

                Candle candle = new Candle(contract, period, open, high, low, close, volume, (open + close) / 2,
                        ((int) volume / 100), TradingCalendar.getDateTimeNowMarketTimeZone());

                candle.setContract(contract);
                candle.setTradingday(tradingday);
                candle.setLastUpdateDate(candle.getStartPeriod());
                candles.add(candle);
            }
            in.close();

            Collections.reverse(candles);
            CandleDatasetUI candleDataset = new CandleDatasetUI();
            int daySeconds = (int) TradingCalendar.getDurationInSeconds(TradingCalendar.getTradingDayStart(today),
                    TradingCalendar.getTradingDayEnd(today));
            CandleSeriesUI candleSeries = new CandleSeriesUI(contract.getSymbol(), contract, daySeconds, startDate, today);
            candleDataset.addSeries(candleSeries);
            StrategyDataUI strategyData = new StrategyDataUI(strategy, candleDataset);
            CandleDatasetUI.populateSeries(strategyData, candles);
            return strategyData;
        } catch (Exception ex) {
            _log.error("Error getting Yahoo data msg: {}", ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Method getPriceDataSetYahooIntraday.
     *
     * @param symbol        String
     * @param days          int
     * @param periodSeconds int
     * @return StrategyData
     */
    protected static StrategyDataUI getPriceDataSetYahooIntraday(String symbol, int days, int periodSeconds) {
        try {
            ZonedDateTime today = TradingCalendar.getDateTimeNowMarketTimeZone();
            ZonedDateTime startDate = today.minusDays(days);
            startDate = TradingCalendar.getPrevTradingDay(startDate);
            Strategy daoStrategy = (Strategy) DAOStrategy.newInstance().getObject();
            StrategyHome home = new StrategyHome();
            String name = daoStrategy.getName();
            Strategy strategy = home.findByName(name);
            Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
            CandleDatasetUI candleDataset = new CandleDatasetUI();
            CandleSeriesUI candleSeries = new CandleSeriesUI(contract.getSymbol(), contract, periodSeconds, startDate,
                    today);
            candleDataset.addSeries(candleSeries);
            StrategyDataUI strategyData = new StrategyDataUI(strategy, candleDataset);

            /*
             * Yahoo finance
             * http://chartapi.finance.yahoo.com/instrument/1.0/IBM
             * /chartdata;type=quote;range=1d/csv/
             */

            String strUrl = "http://chartapi.finance.yahoo.com/instrument/1.0/" + symbol
                    + "/chartdata;type=quote;range=" + days + "d/csv/";

            _log.info("URL : {}", strUrl);
            URL url = new URI(strUrl).toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {

                if (!inputLine.contains(":")) {
                    StringTokenizer scanLine = new StringTokenizer(inputLine, ",");
                    while (scanLine.hasMoreTokens()) {
                        ZonedDateTime time = TradingCalendar
                                .getZonedDateTimeFromMilli(Long.parseLong(scanLine.nextToken()) * 1000);

                        // values:Timestamp,close,high,low,open,volume
                        double close = Double.parseDouble(scanLine.nextToken());
                        double high = Double.parseDouble(scanLine.nextToken());
                        double low = Double.parseDouble(scanLine.nextToken());
                        double open = Double.parseDouble(scanLine.nextToken());
                        long volume = Long.parseLong(scanLine.nextToken());
                        _log.info("Time : {} Open: {} High: {} Low: {} Close: {} Volume: {}", time, open, high, low, close, volume);
                        if (startDate.isBefore(time)) {
                            strategyData.buildCandle(time, open, high, low, close, volume, (open + close) / 2,
                                    ((int) volume / 100), periodSeconds / BarSize.FIVE_MIN, null);
                        }
                    }
                }
            }
            in.close();
            return strategyData;
        } catch (Exception ex) {
            _log.error("Error getting Yahoo data msg: {}", ex.getMessage(), ex);
        }
        return null;
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
            if (pageIndex > 0)
                return NO_SUCH_PAGE;
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
            if (!(c instanceof JComponent jc))
                return false;
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
            if (c instanceof JComponent)
                ((JComponent) c).setDoubleBuffered(wasBuffered);
        }
    }
}
