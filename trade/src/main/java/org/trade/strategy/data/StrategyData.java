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
package org.trade.strategy.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.factory.ClassFactory;
import org.trade.core.util.TradingCalendar;
import org.trade.core.util.Worker;
import org.trade.persistent.dao.Strategy;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.persistent.dao.Tradingday;
import org.trade.strategy.data.base.RegularTimePeriod;
import org.trade.strategy.data.candle.CandleItem;
import org.trade.strategy.data.candle.CandlePeriod;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 */
public class StrategyData extends Worker {

    private final static Logger _log = LoggerFactory.getLogger(StrategyData.class);

    private CandleDataset baseCandleDataset = null;
    private CandleDataset candleDataset = null;
    private final List<IIndicatorDataset> indicators = new ArrayList<IIndicatorDataset>();

    private boolean seriesChanged = true;
    private final Object lockStrategyWorker = new Object();
    private int currentBaseCandleCount = -1;
    private int lastBaseCandleProcessed = -1;

    /**
     * Constructor for StrategyData.
     *
     * @param strategy          Strategy
     * @param baseCandleDataset CandleDataset
     */
    public StrategyData(Strategy strategy, CandleDataset baseCandleDataset) {

        this.baseCandleDataset = baseCandleDataset;
        candleDataset = new CandleDataset();
        candleDataset.addSeries(CandleDataset.createSeries(baseCandleDataset, 0, getBaseCandleSeries().getContract(),
                getBaseCandleSeries().getBarSize(), getBaseCandleSeries().getStartTime(),
                getBaseCandleSeries().getEndTime()));
        for (IndicatorSeries indicator : strategy.getIndicatorSeries()) {

            try {
                /*
                 * For each indicator create a series that is a clone for this
                 * trade strategy.
                 */
                IndicatorSeries series = (IndicatorSeries) indicator.clone();
                series.setKey(series.getName());
                series.createSeries(candleDataset, 0);
                IIndicatorDataset indicatorDataset = this.getIndicatorByType(indicator.getType());
                if (null == indicatorDataset) {
                    /*
                     * Data-set and Series names should have the same name with
                     * applicable extension of Series/Dataset. this allows
                     * substitution and does not require us to have another
                     * table in the DB to represent the Dataset which is just a
                     * holder for series and is required by the Chart API.
                     */
                    String datasetName = indicator.getType().replaceAll("Series", "Dataset");
                    Vector<Object> parm = new Vector<Object>();
                    indicatorDataset = (IIndicatorDataset) ClassFactory
                            .getCreateClass(IIndicatorDataset.PACKAGE + datasetName, parm, this);
                    this.indicators.add(indicatorDataset);
                }
                indicatorDataset.addSeries(series);

            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Could not construct StrategyData Object. Either indicator was not found or was not clonable Msg: "
                                + ex.getMessage());
            }
        }
    }

    /*
     * The main process thread. This will run until it is either canceled or is
     * done.
     *
     * (non-Javadoc)
     *
     * @see org.trade.strategy.impl.Worker#doInBackground()
     */

    protected Void doInBackground() {
        /*
         * We initialize here to keep this instances as part of this worker
         * thread
         */
        try {

            this.seriesChanged = false;
            do {
                /*
                 * Lock until a candle arrives. First time in we process the
                 * current candle. This thread is processing candles behind the
                 * main broker queue thread. So the lastBaseCandleProcessed will
                 * increase in value until we catch up then the thread will lock
                 * until a new candle arrives.
                 */
                synchronized (lockStrategyWorker) {
                    while ((!this.seriesChanged && currentBaseCandleCount == lastBaseCandleProcessed)
                            || this.getBaseCandleSeries().isEmpty()) {
                        lockStrategyWorker.wait();
                    }
                    this.seriesChanged = false;
                }

                if (!this.isCancelled()) {

                    if (!this.getBaseCandleSeries().isEmpty()) {

                        /*
                         * Another candle has been added. Add the new candle to
                         * the base series in the dataset.
                         */
                        boolean newBar = false;
                        if (this.currentBaseCandleCount > this.lastBaseCandleProcessed) {
                            this.lastBaseCandleProcessed++;
                            newBar = true;
                        }
                        synchronized (this.getBaseCandleDataset()) {
                            this.getCandleDataset().getSeries(0).updateSeries(this.getBaseCandleSeries(),
                                    this.lastBaseCandleProcessed, newBar);
                        }
                    }
                }

            } while (!this.isDone() && !this.isCancelled());

        } catch (InterruptedException interExp) {
            // Do nothing.
        } catch (Exception ex1) {
            _log.error("Error processing candle symbol: " + this.getBaseCandleSeries().getSymbol()
                    + " Base series size: " + this.getBaseCandleSeries().getItemCount() + " BarSize: "
                    + this.getBaseCandleSeries().getBarSize() + " currentBaseCandleCount: "
                    + this.currentBaseCandleCount + " Candle series size: "
                    + this.getCandleDataset().getSeries(0).getItemCount() + " lastBaseCandleProcessed: "
                    + this.lastBaseCandleProcessed + " BarSize: " + this.getCandleDataset().getSeries(0).getBarSize()
                    + " Message: " + ex1.getMessage(), ex1);

        } finally {
            /*
             * Ok we are complete clean up.
             */
        }
        return null;
    }

    public void cancel() {
        this.setIsCancelled(true);
        /*
         * Unlock the doInBackground that may be waiting for a candle. This will
         * cause a clean finish to the process.
         */
        synchronized (lockStrategyWorker) {
            seriesChanged = true;
            lockStrategyWorker.notifyAll();
        }
    }

    protected void done() {
        // Free some memory!!
        // this.clearBaseCandleSeries();
    }

    /**
     * Method changeCandleSeriesPeriod.
     *
     * @param newPeriod int
     */
    public void changeCandleSeriesPeriod(int newPeriod) {
        /*
         * Clear down the dependent data sets and re populate from the base
         * candle series.
         */
        clearChartDatasets();
        this.getCandleDataset().getSeries(0).setBarSize(newPeriod);
        for (int i = 0; i < getBaseCandleSeries().getItemCount(); i++) {
            CandleItem candelItem = (CandleItem) getBaseCandleSeries().getDataItem(i);
            boolean newBar = this.getCandleDataset().getSeries(0).buildCandle(candelItem.getPeriod().getStart(),
                    candelItem.getOpen(), candelItem.getHigh(), candelItem.getLow(), candelItem.getClose(),
                    candelItem.getVolume(), candelItem.getVwap(), candelItem.getCount(),
                    this.getCandleDataset().getSeries(0).getBarSize() / getBaseCandleSeries().getBarSize(), null);
            updateIndicators(this.getCandleDataset(), newBar);
        }
        this.getCandleDataset().getSeries(0).fireSeriesChanged();
    }

    /**
     * Method buildCandle.
     *
     * @param time           Date
     * @param open           double
     * @param high           double
     * @param low            double
     * @param close          double
     * @param volume         long
     * @param vwap           double
     * @param tradeCount     int
     * @param rollupInterval int This is the barSize we are trading on over the barSize of
     *                       the incoming data. Note the results should be an integer. i.e
     *                       5min/1min 60min/5min but not 5min/2min.
     * @param lastUpdateDate Date the update time.
     * @return boolean
     */
    public boolean buildCandle(ZonedDateTime time, double open, double high, double low, double close, long volume,
                               double vwap, int tradeCount, int rollupInterval, ZonedDateTime lastUpdateDate) {

        boolean newBar = this.getBaseCandleSeries().buildCandle(time, open, high, low, close, volume, vwap, tradeCount,
                rollupInterval, lastUpdateDate);

        this.currentBaseCandleCount = this.getBaseCandleSeries().getItemCount() - 1;

        CandleItem candleItem = (CandleItem) this.getBaseCandleSeries().getDataItem(this.currentBaseCandleCount);
        this.getBaseCandleSeries().updatePercentChanged(candleItem);
        updateIndicators(this.getBaseCandleDataset(), newBar);
        this.getBaseCandleSeries().fireSeriesChanged();
        /*
         * If thread Indicators the updates to all indicators and the subsequent
         * firing of base series changed is performed via the worker thread.
         * This should be used when this method is called from a broker thread
         * i.e. messaged bus thread.
         */
        if (this.isRunning()) {
            /*
             * Unlock the doInBackground that may be waiting for a candle. This
             * will cause a clean finish to the process.
             */
            synchronized (lockStrategyWorker) {
                this.seriesChanged = true;
                lockStrategyWorker.notifyAll();
            }
            // _log.info("buildCandle symbol: "
            // + this.getBaseCandleSeries().getSymbol() + " Count: "
            // + this.currentCandleCount);
        } else {

            /*
             * Another candle has been added. Add the new candle to the base
             * series in the dataset.
             */
            synchronized (this.getBaseCandleDataset()) {
                this.getCandleDataset().updateDataset(this.getBaseCandleDataset(), 0, newBar);
            }
        }
        return newBar;
    }

    /**
     * Method updateIndicators. Update all the indicators before notifying any
     * strategy workers of this even.
     *
     * @param source CandleDataset
     * @param newBar boolean
     */
    private void updateIndicators(CandleDataset source, boolean newBar) {

        for (IIndicatorDataset indicator : indicators) {
            /*
             * CandleSeries are only updated via the API i.e. these are not true
             * indicators and are shared across Data-sets.
             */
            if (!IndicatorSeries.CandleSeries.equals(indicator.getType(0))) {
                indicator.updateDataset(source, 0, newBar);
            }
        }
    }

    /**
     * Method updateIndicators. Update all the indicators before notifying any
     * strategy workers of this even.
     *
     * @param source CandleDataset
     * @param newBar boolean
     */
    public void createIndicators(CandleDataset source) {

        for (IIndicatorDataset indicator : indicators) {
            if (!IndicatorSeries.CandleSeries.equals(indicator.getType(0))) {
                for (int x = 0; x < indicator.getSeriesCount(); x++) {
                    IndicatorSeries series = indicator.getSeries(x);
                    /*
                     * CandleSeries are only updated via the API i.e. these are
                     * not true indicators and are shared across Data-sets.
                     */
                    series.createSeries(source, 0);
                }
            }
        }
    }

    public synchronized void clearBaseCandleDataset() {
        if (this.isRunning())
            this.cancel();
        this.currentBaseCandleCount = -1;
        this.lastBaseCandleProcessed = this.currentBaseCandleCount;
        clearChartDatasets();
        getBaseCandleDataset().clear();
    }

    public void clearChartDatasets() {
        for (IIndicatorDataset indicator : indicators) {
            if (!IndicatorSeries.CandleSeries.equals(indicator.getType(0))) {
                indicator.clear();
            }
        }
        getCandleDataset().clear();
    }

    /**
     * Method getIndicators.
     *
     * @return List<IIndicatorDataset>
     */
    public List<IIndicatorDataset> getIndicators() {
        return indicators;
    }

    /**
     * Method getIndicators.
     *
     * @param type String
     * @return IIndicatorDataset
     */
    public IIndicatorDataset getIndicatorByType(String type) {
        for (int index = 0; index < indicators.size(); index++) {
            IIndicatorDataset series = indicators.get(index);
            if (series.getType(0).equals(type)) {
                return series;
            }
        }
        return null;
    }

    /**
     * Method getBaseCandleDataset.
     *
     * @return CandleDataset
     */
    public CandleDataset getBaseCandleDataset() {
        return baseCandleDataset;
    }

    /**
     * Method getBaseCandleSeries.
     *
     * @return CandleSeries
     */
    public CandleSeries getBaseCandleSeries() {
        return baseCandleDataset.getSeries(0);
    }

    /**
     * Method getCandleDataset.
     *
     * @return CandleDataset
     */
    public CandleDataset getCandleDataset() {
        return candleDataset;
    }

    /**
     * Method create.
     *
     * @param tradestrategy Tradestrategy
     * @return StrategyData
     */
    public static StrategyData create(final Tradestrategy tradestrategy) {

        CandleDataset candleDataset = new CandleDataset();
        CandleSeries candleSeries = new CandleSeries(tradestrategy.getContract().getSymbol(),
                tradestrategy.getContract(), tradestrategy.getBarSize(), tradestrategy.getTradingday().getOpen(),
                tradestrategy.getTradingday().getClose());
        candleDataset.addSeries(candleSeries);
        return new StrategyData(tradestrategy.getStrategy(), candleDataset);
    }

    /**
     * Method doDummyData.
     *
     * @param series             CandleSeries
     * @param start              Tradingday
     * @param noDays             int
     * @param barSize            int
     * @param longTrade          boolean
     * @param milliSecondsDeplay int
     */
    public static void doDummyData(CandleSeries series, Tradingday start, int noDays, int barSize, boolean longTrade,
                                   int milliSecondsDeplay) {

        double high = 33.98;
        double low = 33.84;
        double open = 33.90;
        double close = 33.95;
        double vwap = 34.94;
        int longShort = 1;
        if (!longTrade) {
            high = 34.15;
            low = 34.01;
            open = 34.10;
            close = 34.03;
            vwap = 34.02;
            longShort = -1;
        }
        long volume = 100000;
        int tradeCount = 100;
        if (barSize == 1) {
            barSize = (int) TradingCalendar.getDurationInSeconds(start.getOpen(), start.getClose());
        }

        long count = (TradingCalendar.getDurationInSeconds(start.getOpen(), start.getClose()) / barSize) * noDays;

        RegularTimePeriod period = new CandlePeriod(start.getOpen(), barSize);
        series.clear();
        for (int i = 0; i < count; i++) {
            series.buildCandle(period.getStart(), open, high, low, close, volume, vwap, tradeCount, 1, null);
            high = high + (0.02 * longShort);
            low = low + (0.02 * longShort);
            open = open + (0.02 * longShort);
            close = close + (0.02 * longShort);
            vwap = vwap + (0.02 * longShort);
            period = period.next();
            if (period.getStart().equals(start.getClose())) {
                period = new CandlePeriod(
                        TradingCalendar.getTradingDayStart(TradingCalendar.getNextTradingDay(period.getStart())),
                        barSize);
            }
            try {
                if (milliSecondsDeplay > 0)
                    Thread.sleep(milliSecondsDeplay);

            } catch (InterruptedException e) {
                _log.error(" Thread interupt: " + e.getMessage());
            }
        }
    }

    public void printDatasets() {

        this.getBaseCandleSeries().printSeries();

        for (int i = 0; i < this.getCandleDataset().getSeriesCount(); i++) {
            IndicatorSeries series = this.getCandleDataset().getSeries(i);
            series.printSeries();
        }

        for (IIndicatorDataset indicatorDataset : this.getIndicators()) {
            for (int i = 0; i < indicatorDataset.getSeriesCount(); i++) {
                IndicatorSeries series = indicatorDataset.getSeries(i);
                series.printSeries();
            }
        }
    }
}
