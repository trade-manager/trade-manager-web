package org.trade.ui.chart;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.base.BasePanel;
import org.trade.base.BasePanelMenu;
import org.trade.base.ComponentPrintService;
import org.trade.base.ImageBuilder;
import org.trade.base.WaitCursorEventQueue;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.StrategyHome;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;
import org.trade.indicator.CandleDataset;
import org.trade.indicator.CandleSeries;
import org.trade.indicator.StrategyDataUI;
import org.trade.ui.MainPanelMenu;
import org.trade.ui.widget.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.Serial;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                // .getPriceDataSetDay(symbol);
                int numberOfDays = 2;
                StrategyDataUI strategyData = CandlestickChartApp.getPriceDataSetIntraday(symbol, numberOfDays,
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

    /**
     * Method getPriceDataSetDay.
     *
     * @param symbol String
     * @return StrategyData
     */
    protected static StrategyDataUI getPriceDataSetDay(String symbol) {

        try {

            List<Candle> candles = new ArrayList<>();
            Strategy daoStrategy = (Strategy) DAOStrategy.newInstance().getObject();
            StrategyHome home = new StrategyHome();
            String name = daoStrategy.getName();
            Strategy strategy = home.findByName(name);
            Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
            ZonedDateTime endDate = TradingCalendar.getDateTimeNowMarketTimeZone();
            ZonedDateTime startDate = endDate.minusMonths(3);

            /*
             * Polygon curl -X GET "https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/1746696600000/1746734400000?adjusted=true&sort=asc&limit=1500&apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp"
             */
            String strUrl = "https://api.polygon.io/v2/aggs/ticker/" + symbol + "/range/1/day/" + startDate.toInstant().toEpochMilli() + "/" + endDate.toInstant().toEpochMilli() + "?adjusted=true&sort=asc&limit=1500&apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp";

            _log.debug("Debug: CandlestickChartApp::getPriceDataSetIntraday URL: {}", strUrl);

            // create a request
            HttpClient client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(
                            URI.create(strUrl))
                    .header("accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                String jsonString = response.body();
                JSONObject contractObj = new JSONObject(jsonString);
                JSONArray bars = contractObj.getJSONArray("results");

                for (int i = 0; i < bars.length(); i++) {

                    JSONObject barObj = bars.getJSONObject(i);

                    ZonedDateTime time = TradingCalendar.getZonedDateTimeFromMilli((barObj.getLong("t")));
                    Tradingday tradingday = Tradingday.newInstance(time);
                    // values:Timestamp,close,high,low,open,volume
                    double close = barObj.getDouble("c");
                    double high = barObj.getDouble("h");
                    double low = barObj.getDouble("l");
                    double open = barObj.getDouble("o");
                    long volume = barObj.getLong("v");

                    _log.info("Time : {} Open: {} High: {} Low: {} Close: {} Volume: {}", time, open, high, low, close, volume);
                    CandlePeriod period = new CandlePeriod(TradingCalendar.getTradingDayStart(time),
                            TradingCalendar.getTradingDayEnd(time).minusSeconds(1));

                    Candle candle = new Candle(contract, period, open, high, low, close, volume, (open + close) / 2,
                            ((int) volume / 100), TradingCalendar.getDateTimeNowMarketTimeZone());

                    candle.setContract(contract);
                    candle.setTradingday(tradingday);
                    candle.setLastUpdateDate(candle.getStartPeriod());
                    candles.add(candle);
                }

                Collections.reverse(candles);
                CandleDataset candleDataset = new CandleDataset();
                int daySeconds = (int) TradingCalendar.getDurationInSeconds(TradingCalendar.getTradingDayStart(endDate),
                        TradingCalendar.getTradingDayEnd(endDate));
                CandleSeries candleSeries = new CandleSeries(contract.getSymbol(), contract, daySeconds, startDate, endDate);
                candleDataset.addSeries(candleSeries);
                StrategyDataUI strategyData = new StrategyDataUI(strategy, candleDataset);
                CandleDataset.populateSeries(strategyData, candles);
                return strategyData;
            } else {
                _log.error("Error: CandlestickChartApp::getPriceDataSetIntraday request to URL: {}, failed with status code: {}", strUrl, response.statusCode());
            }
        } catch (Exception ex) {
            _log.error("Error getting broker data msg: {}", ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Method getPriceDataSetIntraday.
     *
     * @param symbol  String
     * @param days    int
     * @param barSize int
     * @return StrategyData
     */
    protected static StrategyDataUI getPriceDataSetIntraday(String symbol, int days, int barSize) {
        try {

            ZonedDateTime endDate = TradingCalendar.getDateTimeNowMarketTimeZone();
            endDate = TradingCalendar.getTradingDayEnd(endDate);
            ZonedDateTime startDate = endDate.minusDays(days + 1);
            startDate = TradingCalendar.getPrevTradingDay(startDate);
            Strategy daoStrategy = (Strategy) DAOStrategy.newInstance().getObject();
            StrategyHome home = new StrategyHome();
            String name = daoStrategy.getName();
            Strategy strategy = home.findByName(name);
            Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
            CandleDataset candleDataset = new CandleDataset();
            CandleSeries candleSeries = new CandleSeries(contract.getSymbol(), contract, barSize, startDate,
                    endDate);
            candleDataset.addSeries(candleSeries);
            StrategyDataUI strategyData = new StrategyDataUI(strategy, candleDataset);

            while (startDate.isBefore(TradingCalendar.getTradingDayStart(endDate))) {

                if (TradingCalendar.isTradingDay(startDate)) {

                    /*
                     * Polygon curl -X GET "https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/minute/1746696600000/1746734400000?adjusted=true&sort=asc&limit=1500&apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp"
                     */
                    Integer periodSeconds = 60;
                    String strUrl = "https://api.polygon.io/v2/aggs/ticker/" + symbol + "/range/1/minute/" + startDate.toInstant().toEpochMilli() + "/" + TradingCalendar.getTradingDayEnd(startDate).toInstant().toEpochMilli() + "?adjusted=true&sort=asc&limit=1500&apiKey=WGlljpSus0Ai1mj2ayaASNTcxchw9aUp";

                    _log.debug("Debug: CandlestickChartApp::getPriceDataSetIntraday URL: {}", strUrl);

                    // create a request
                    HttpClient client = HttpClient.newHttpClient();
                    var request = HttpRequest.newBuilder(
                                    URI.create(strUrl))
                            .header("accept", "application/json")
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {

                        String jsonString = response.body();
                        JSONObject contractObj = new JSONObject(jsonString);
                        JSONArray bars = contractObj.getJSONArray("results");

                        for (int i = 0; i < bars.length(); i++) {

                            JSONObject barObj = bars.getJSONObject(i);

                            ZonedDateTime time = TradingCalendar.getZonedDateTimeFromMilli((barObj.getLong("t")));
                            // values:Timestamp,close,high,low,open,volume
                            double close = barObj.getDouble("c");
                            double high = barObj.getDouble("h");
                            double low = barObj.getDouble("l");
                            double open = barObj.getDouble("o");
                            long volume = barObj.getLong("v");

                            _log.info("Info: CandlestickChartApp::getPriceDataSetIntraday Time : {} Open: {} High: {} Low: {} Close: {} Volume: {}", time, open, high, low, close, volume);

                            if (startDate.isBefore(time) || startDate.equals(time)) {

                                strategyData.buildCandle(time, open, high, low, close, volume, (open + close) / 2,
                                        ((int) volume / 100), BarSize.FIVE_MIN / periodSeconds, null);
                            }
                        }
                    } else {
                        _log.error("Error: CandlestickChartApp::getPriceDataSetIntraday request to URL: {}, failed with status code: {}", strUrl, response.statusCode());
                    }
                }
                startDate = startDate.plusDays(1);
            }

            return strategyData;
        } catch (Exception ex) {
            _log.error("rror: CandlestickChartApp::getPriceDataSetIntraday getting broker data msg: {}", ex.getMessage(), ex);
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
