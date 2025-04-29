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
package org.trade.indicator;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.general.SeriesChangeEvent;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.series.indicator.candle.CandlePeriod;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Percent;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.indicator.candle.CandleItemUI;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;

/**
 * A list of (RegularTimePeriod, open, high, low, close) data items.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class CandleSeriesUI extends IndicatorSeriesUI {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    public static final String SYMBOL = "Symbol";
    public static final String CURRENCY = "Currency";
    public static final String EXCHANGE = "Exchange";
    public static final String SEC_TYPE = "SECType";

    private Contract contract;
    private String symbol;
    private String currency;
    private String exchange;
    private String secType;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private int barSize = 0;

    private Candle candleBar = null;
    private final Percent percentChangeFromClose = new Percent(0);
    private final Percent percentChangeFromOpen = new Percent(0);

    // Parms used for the rolling candle bar.
    private RollingCandle rollingCandle = new RollingCandle();
    private RollingCandle prevRollingCandle = null;

    private Double sumVwapVolume = 0d;
    private Long sumVolume = 0L;
    private Integer sumTradeCount = 0;

    private final LinkedList<RollingCandle> rollingCandleValues = new LinkedList<>();
    private final LinkedList<Double> openValues = new LinkedList<>();
    private final LinkedList<Double> highValues = new LinkedList<>();
    private final LinkedList<Double> lowValues = new LinkedList<>();
    private final LinkedList<Long> volumeValues = new LinkedList<>();
    private final LinkedList<Integer> tradeCountValues = new LinkedList<>();
    private final LinkedList<Double> vwapVolumeValues = new LinkedList<>();

    public CandleSeriesUI() {
        super(CandleSeries, true, 0, false);
    }

    /**
     * Creates a new empty series. By default, items added to the series will be
     * sorted into ascending order by period, and duplicate periods will not be
     * allowed.
     *
     * @param series the Contract for this candle series.
     * @param bars   the length in minutes for each bar ie. 5, 15, 30, 60
     */
    public CandleSeriesUI(CandleSeriesUI series, int bars, ZonedDateTime startTime, ZonedDateTime endTime) {
        super(series.getContract().getSymbol(), CandleSeries, series.getDisplaySeries(), 0,
                series.getSubChart());
        this.symbol = series.getContract().getSymbol();
        this.contract = series.getContract();
        this.barSize = series.getBarSize();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates a new empty series. By default, items added to the series will be
     * sorted into ascending order by period, and duplicate periods will not be
     * allowed.
     *
     * @param legend   the title that appears on the bottom of the chart.
     * @param contract the Contract for this candle series.
     * @param barSize  the length in minutes for each bar ie. 5, 15, 30, 60
     */

    public CandleSeriesUI(String legend, Contract contract, int barSize, ZonedDateTime startTime, ZonedDateTime endTime) {
        super(legend, CandleSeries, true, 0, false);
        this.contract = contract;
        this.symbol = contract.getSymbol();
        this.barSize = barSize;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Constructor for CandleSeries.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     */
    public CandleSeriesUI(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                          Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */

    public void createSeries(CandleDatasetUI source, int seriesIndex) {

    }

    /**
     * Returns the contract ID.
     *
     * @return contractId.
     */
    public Contract getContract() {
        if (null == this.contract) {
            this.contract = new Contract(this.getSecType(), this.getSymbol(), this.getExchange(), this.getCurrency(),
                    null, null);
        }
        return this.contract;
    }

    /**
     * Method getStartTime.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * Method setStartTime.
     *
     * @param startTime ZonedDateTime
     */
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Method getEndTime.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * Method setEndTime.
     *
     * @param endTime ZonedDateTime
     */
    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Method getSymbol.
     *
     * @return String
     */
    public String getSymbol() {
        try {
            if (null == this.symbol)
                this.symbol = (String) CodeValue.getValueCode(SYMBOL, this.getCodeValues());
        } catch (Exception e) {
            this.symbol = null;
        }
        return this.symbol;
    }

    /**
     * Method setSymbol.
     *
     * @param symbol String
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Method getCurrency.
     *
     * @return String
     */
    public String getCurrency() {
        try {
            if (null == this.currency)
                this.currency = (String) CodeValue.getValueCode(CURRENCY, this.getCodeValues());
        } catch (Exception e) {
            this.currency = null;
        }
        return this.currency;
    }

    /**
     * Method setCurrency.
     *
     * @param currency String
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Method getExchange.
     *
     * @return String
     */
    public String getExchange() {
        try {
            if (null == this.exchange)
                this.exchange = (String) CodeValue.getValueCode(EXCHANGE, this.getCodeValues());
        } catch (Exception e) {
            this.exchange = null;
        }
        return this.exchange;
    }

    /**
     * Method setExchange.
     *
     * @param exchange String
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * Method getSecType.
     *
     * @return String
     */
    public String getSecType() {
        try {
            if (null == this.secType)
                this.secType = (String) CodeValue.getValueCode(SEC_TYPE, this.getCodeValues());
        } catch (Exception e) {
            this.secType = null;
        }
        return this.secType;
    }

    /**
     * Method setSecType.
     *
     * @param secType String
     */
    public void setSecType(String secType) {
        this.secType = secType;
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final CandleItemUI item = (CandleItemUI) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Returns the time period for the specified item.
     *
     * @return The time period.
     */
    public int getBarSize() {
        return this.barSize;
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param barSize Integer
     */
    public void setBarSize(Integer barSize) {
        this.barSize = barSize;
    }

    /**
     * Returns the data item at the specified index.
     *
     * @param index the item index.
     * @return The data item.
     */
    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }

    /**
     * Adds a data item to the series.
     *
     * @param period         the period.
     * @param open           the open-value.
     * @param high           the high-value.
     * @param low            the low-value.
     * @param close          the close-value.
     * @param volume         the volume-value.
     * @param vwap           the vwap-value.
     * @param tradeCount     the tradeCount-value.
     * @param contract       Contract
     * @param lastUpdateDate Date
     */
    public void add(Contract contract, Tradingday tradingday, RegularTimePeriod period, double open, double high,
                    double low, double close, long volume, double vwap, int tradeCount, ZonedDateTime lastUpdateDate) {
        if (!this.isEmpty()) {
            CandleItemUI item0 = (CandleItemUI) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new CandleItemUI(contract, tradingday, period, open, high, low, close, volume, vwap, tradeCount,
                lastUpdateDate), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param candleItem CandleItem
     * @param notify     boolean
     */
    public void add(CandleItemUI candleItem, boolean notify) {
        if (!this.isEmpty()) {
            CandleItemUI item0 = (CandleItemUI) this.getDataItem(0);
            if (!candleItem.getPeriod().getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(candleItem, notify);
    }

    /**
     * Returns the true/false if the date falls within a period.
     *
     * @param date the date for which we want a period.
     * @return exists
     */
    public int indexOf(ZonedDateTime date) {

        for (int i = this.data.size(); i > 0; i--) {
            CandleItemUI item = (CandleItemUI) this.data.get(i - 1);
            if (date.isAfter(item.getPeriod().getEnd())) {
                return -1;
            }
            if ((date.isAfter(item.getPeriod().getStart()) || date.equals(item.getPeriod().getStart()))
                    && (date.isBefore(item.getPeriod().getEnd()) || date.equals(item.getPeriod().getEnd()))) {
                return i - 1;
            }
        }
        return -1;
    }

    /**
     * Returns the last completed candle or -1 if still building.
     *
     * @param time           the date for which we want a candle period to be updated.
     * @param open           the open-value.
     * @param high           the high-value.
     * @param low            the low-value.
     * @param close          the close-value.
     * @param volume         the volume value.
     * @param vwap           the volume weighted price.
     * @param tradeCount     the number of trades.
     * @param rollupInterval the interval to roll up Vwap
     * @param lastUpdateDate Date the update time.
     * @return completedCandle the last completed candle or -1 if still building
     */
    boolean buildCandle(ZonedDateTime time, double open, double high, double low, double close, long volume,
                        double vwap, int tradeCount, int rollupInterval, ZonedDateTime lastUpdateDate) {

        int index = this.indexOf(time);
        // _log.error("Symbol :" + this.getSymbol() + " Bar Time: " + time
        // + " Index: " + index + " open: " + open + " high: " + high
        // + " low: " + low + " close: " + close + " volume: " + volume
        // + " vwap: " + vwap + " tradeCount: " + tradeCount
        // + " rollupInterval: " + rollupInterval);

        CandleItemUI candleItem;
        boolean newCandle = false;
        if (index > -1) {

            candleItem = (CandleItemUI) this.getDataItem(index);

            if (null == lastUpdateDate)
                lastUpdateDate = candleItem.getPeriod().getEnd();

            this.rollCandle(candleItem.getPeriod(), rollupInterval, open, high, low, close, volume, tradeCount, vwap,
                    lastUpdateDate);

            if (candleItem.getHigh() < high) {
                candleItem.setHigh(high);
            }
            if (candleItem.getLow() > low) {
                candleItem.setLow(low);
            }
            candleItem.setClose(close);
            if (rollupInterval > 1) {
                candleItem.setVolume(candleItem.getVolume() + volume);
                candleItem.setCount(candleItem.getCount() + tradeCount);
            } else {
                candleItem.setVolume(volume);
                candleItem.setCount(tradeCount);
            }
            candleItem.setVwap(this.rollingCandle.getVwap());
            candleItem.setLastUpdateDate(lastUpdateDate);
        } else {

            RegularTimePeriod period = this.getPeriodStart(time, this.getBarSize());
            Tradingday tradingday = new Tradingday(
                    TradingCalendar.getDateAtTime(period.getStart(), this.getStartTime()),
                    TradingCalendar.getDateAtTime(period.getStart(), this.getEndTime()));

            if (null == lastUpdateDate)
                lastUpdateDate = period.getEnd();

            this.rollCandle(period, rollupInterval, open, high, low, close, volume, tradeCount, vwap, lastUpdateDate);

            candleItem = new CandleItemUI(this.getContract(), tradingday, period, open, high, low, close, volume,
                    this.rollingCandle.getVwap(), tradeCount, lastUpdateDate);
            this.add(candleItem, false);

            newCandle = true;
        }
        // printCandleItem(candleItem);
        return newCandle;
    }

    /**
     * Removes all data items from the series and, unless the series is already
     * empty, sends a {@link SeriesChangeEvent} to all registered listeners.
     * Clears down and resets all the Vwap calculated fields.
     */
    public void clear() {
        this.openValues.clear();
        this.highValues.clear();
        this.lowValues.clear();
        this.volumeValues.clear();
        this.tradeCountValues.clear();
        this.vwapVolumeValues.clear();
        this.rollingCandleValues.clear();
        super.clear();
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        CandleSeriesUI clone = (CandleSeriesUI) super.clone();
        clone.contract = (Contract) this.getContract().clone();
        clone.symbol = this.getSymbol();
        clone.currency = this.getCurrency();
        clone.exchange = this.getExchange();
        clone.secType = this.getSecType();
        clone.startTime = this.getStartTime();
        clone.endTime = this.getEndTime();
        clone.barSize = this.getBarSize();
        clone.rollingCandle = new RollingCandle();
        return clone;
    }

    /**
     * Method getPeriodStart.
     *
     * @param time    Date
     * @param barSize int
     * @return RegularTimePeriod
     */
    public RegularTimePeriod getPeriodStart(ZonedDateTime time, int barSize) {
        /*
         * For 60min time period start the clock at 9:00am. This matches most
         * charting platforms.
         */
        ZonedDateTime startBusDate = TradingCalendar.getDateAtTime(time, this.getStartTime());

        if (3600 == barSize) {
            if (startBusDate.getMinute() == 30) {
                startBusDate = startBusDate.minusMinutes(30);
                if (time.getMinute() == 30 && startBusDate.equals(time)) {
                    time = time.minusMinutes(30);
                }
            }
        }

        long periodsFromDayStart = TradingCalendar.getDurationInSeconds(startBusDate, time) / barSize;
        ZonedDateTime startDateTime = startBusDate.plusSeconds((periodsFromDayStart * barSize));
        return new CandlePeriod(startDateTime, barSize);
    }

    /**
     * Method getRollingCandle.
     *
     * @return RollingCandle
     */
    public RollingCandle getRollingCandle() {
        return this.rollingCandleValues.getFirst();
    }

    /**
     * Method getRollingCandle.
     *
     * @param index int
     * @return RollingCandle
     */
    public RollingCandle getRollingCandle(int index) {
        return this.rollingCandleValues.get(index);
    }

    /**
     * Method getRollingCandleSize.
     *
     * @return int
     */
    public int getRollingCandleSize() {
        return this.rollingCandleValues.size();
    }

    /**
     * Method getPreviousRollingCandle.
     *
     * @return RollupCandle
     */
    public RollingCandle getPreviousRollingCandle() {
        return this.prevRollingCandle;
    }

    /**
     * Method updateSeries.
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public void updateSeries(CandleSeriesUI source, int skip, boolean newBar) {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }
        /*
         * Do not want to add the new bar.
         */
        if (source.getItemCount() > skip) {

            // get the current data item...
            CandleItemUI candleItem = (CandleItemUI) source.getDataItem(skip);
            /*
             * If the item does not exist in the series then this is a new time
             * period and so we need to remove the last in the set and add the
             * new periods values. Otherwise we just update the last value in
             * the set.
             */
            if (newBar) {
                this.add(candleItem, true);
            } else {
                CandleItemUI dataItem = (CandleItemUI) this.getDataItem(this.getItemCount() - 1);
                this.update(dataItem.getPeriod(), dataItem.getCandle());
            }
        }
    }

    /**
     * Method getAverageBar.
     *
     * @param startDate Date
     * @param endDate   Date
     * @param wieghted  boolean
     * @return Candle
     */
    public Candle getAverageBar(ZonedDateTime startDate, ZonedDateTime endDate, boolean wieghted) {

        int itemCount = this.getItemCount() - 1;
        long sumVolume = 0;
        int sunTradeCount = 0;
        double sunHighPriceXVolume = 0;
        double sunLowPriceXVolume = 0;
        double sunOpenPriceXVolume = 0;
        double sunClosePriceXVolume = 0;
        double sunClosePriceXVolumeVwap = 0;
        double numberOfCandles = 0;
        CandleItemUI candle;
        for (int i = itemCount; i > -1; i--) {
            candle = (CandleItemUI) this.getDataItem(i);
            if ((candle.getPeriod().getStart().equals(startDate) || candle.getPeriod().getStart().isAfter(startDate))
                    && (candle.getPeriod().getStart().equals(endDate)
                    || candle.getPeriod().getStart().isBefore(endDate))) {

                if (candle.getVolume() > 0)
                    numberOfCandles++;

                sunHighPriceXVolume = sunHighPriceXVolume + ((wieghted ? candle.getVolume() : 1) * candle.getHigh());
                sunLowPriceXVolume = sunLowPriceXVolume + ((wieghted ? candle.getVolume() : 1) * candle.getLow());
                sunOpenPriceXVolume = sunOpenPriceXVolume + ((wieghted ? candle.getVolume() : 1) * candle.getOpen());
                sunClosePriceXVolume = sunClosePriceXVolume + ((wieghted ? candle.getVolume() : 1) * candle.getClose());
                sunClosePriceXVolumeVwap = sunClosePriceXVolumeVwap + (candle.getVolume() * candle.getClose());
                sumVolume = sumVolume + candle.getVolume();
                sunTradeCount = sunTradeCount + candle.getCount();
            }
        }
        if (numberOfCandles > 0 && sumVolume > 0) {

            CandlePeriod period = new CandlePeriod(startDate, endDate);
            Candle avgCandle = new Candle(getContract(), period, 0, 0, 0, Double.MAX_VALUE,
                    TradingCalendar.getDateTimeNowMarketTimeZone());
            avgCandle.setHigh(BigDecimal.valueOf(sunHighPriceXVolume / (wieghted ? sumVolume : numberOfCandles)));
            avgCandle.setLow(BigDecimal.valueOf(sunLowPriceXVolume / (wieghted ? sumVolume : numberOfCandles)));
            avgCandle.setOpen(BigDecimal.valueOf(sunOpenPriceXVolume / (wieghted ? sumVolume : numberOfCandles)));
            avgCandle.setClose(BigDecimal.valueOf(sunClosePriceXVolume / (wieghted ? sumVolume : numberOfCandles)));
            avgCandle.setVwap(new BigDecimal(sunClosePriceXVolumeVwap / sumVolume));
            avgCandle.setVolume(sumVolume);
            avgCandle.setTradeCount(sunTradeCount);
            return avgCandle;
        }
        return null;
    }

    /**
     * Method getBar.
     *
     * @param startDate Date
     * @param endDate   Date
     * @return Candle
     */
    public Candle getBar(ZonedDateTime startDate, ZonedDateTime endDate) {

        if (null != this.candleBar) {
            if (this.candleBar.getStartPeriod().equals(startDate) && this.candleBar.getEndPeriod().equals(endDate)) {
                return this.candleBar;
            } else {
                this.candleBar = null;
            }
        }

        int itemCount = this.getItemCount() - 1;
        long sumVolume = 0;
        int sumTradeCount = 0;
        double sunClosePriceXVolumeVwap = 0;
        CandleItemUI candle = null;
        for (int i = itemCount; i > -1; i--) {
            candle = (CandleItemUI) this.getDataItem(i);
            if ((candle.getPeriod().getStart().equals(startDate) || candle.getPeriod().getStart().isAfter(startDate))
                    && (candle.getPeriod().getStart().isBefore(endDate))) {
                if (null == this.candleBar) {
                    this.candleBar = new Candle(getContract(), candle.getPeriod(), 0, 0, Double.MAX_VALUE, 0,
                            TradingCalendar.getDateTimeNowMarketTimeZone());
                    this.candleBar.setEndPeriod(candle.getPeriod().getEnd());
                }

                if (this.candleBar.getClose().doubleValue() == 0)
                    this.candleBar.setClose(BigDecimal.valueOf(candle.getClose()));

                if (this.candleBar.getHigh().doubleValue() < candle.getHigh())
                    this.candleBar.setHigh(BigDecimal.valueOf(candle.getHigh()));

                if (this.candleBar.getLow().doubleValue() > candle.getLow())
                    this.candleBar.setLow(BigDecimal.valueOf(candle.getLow()));

                sunClosePriceXVolumeVwap = sunClosePriceXVolumeVwap + (candle.getVolume() * candle.getClose());
                sumVolume = sumVolume + candle.getVolume();
                sumTradeCount = sumTradeCount + candle.getCount();
            }
        }
        if (null != candle) {
            this.candleBar.setStartPeriod(candle.getPeriod().getStart());
            this.candleBar.setOpen(BigDecimal.valueOf(candle.getOpen()));
            this.candleBar.setTradeCount(sumTradeCount);
            if (sumVolume > 0) {
                this.candleBar.setVwap(new BigDecimal(sunClosePriceXVolumeVwap / sumVolume));
                this.candleBar.setVolume(sumVolume);
            } else {
                this.candleBar.setVwap(new BigDecimal(sunClosePriceXVolumeVwap));
                this.candleBar.setVolume(0L);
            }

        }
        return this.candleBar;
    }

    /**
     * Method getPercentChangeFromClose.
     *
     * @return Percent
     */
    public Percent getPercentChangeFromClose() {
        return percentChangeFromClose;
    }

    /**
     * Method getPercentChangeFromOpen.
     *
     * @return Percent
     */
    public Percent getPercentChangeFromOpen() {
        return percentChangeFromOpen;
    }

    /**
     * Method updatePercentChanged.
     *
     * @param candleItem CandleItem
     */
    public void updatePercentChanged(CandleItemUI candleItem) {

        ZonedDateTime prevDay = TradingCalendar.getPrevTradingDay(candleItem.getPeriod().getStart());
        ZonedDateTime prevDayEnd = TradingCalendar.getDateAtTime(prevDay, this.getEndTime());
        prevDayEnd = prevDayEnd.minusSeconds(1);
        ZonedDateTime prevDayStart = TradingCalendar.getDateAtTime(prevDay, this.getStartTime());
        ZonedDateTime todayOpen = TradingCalendar.getDateAtTime(candleItem.getPeriod().getStart(), this.getStartTime());
        int index = this.indexOf(todayOpen);
        if (index > -1) {
            CandleItemUI openCandleItem = (CandleItemUI) this.getDataItem(index);
            try {
                percentChangeFromOpen.setValue(
                        new Percent((candleItem.getClose() - openCandleItem.getOpen()) / openCandleItem.getOpen()));
            } catch (ValueTypeException ex) {
                _log.error("Could not set ValueType Msg: {}", ex.getMessage(), ex);
            }
            if (candleItem.getPeriod().getStart().isAfter(prevDayEnd)) {
                if (this.indexOf(prevDayStart) > -1 && this.indexOf(prevDayEnd) > -1) {
                    Candle prevDayCandle = this.getBar(prevDayStart, prevDayEnd);
                    // _log.info("prevDayCandle Start:"
                    // + prevDayCandle.getStartPeriod() + " End period: "
                    // + prevDayCandle.getEndPeriod() + " Open:"
                    // + prevDayCandle.getOpen() + " High: "
                    // + prevDayCandle.getHigh() + " Low:"
                    // + prevDayCandle.getLow() + " Close: "
                    // + prevDayCandle.getClose());
                    try {
                        percentChangeFromClose
                                .setValue(new Percent((candleItem.getClose() - prevDayCandle.getClose().doubleValue())
                                        / prevDayCandle.getClose().doubleValue()));
                    } catch (ValueTypeException ex) {
                        _log.error("Could not set ValueType Msg: {}", ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    /**
     * Method printSeries.
     */
    public void printSeries() {
        for (int i = 0; i < this.getItemCount(); i++) {
            CandleItemUI dataItem = (CandleItemUI) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Open: {} Close: {} High: {} Low: {} Volume: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getOpen(), dataItem.getClose(), dataItem.getHigh(), dataItem.getLow(), dataItem.getVolume());
        }
    }

    /**
     * Method printCandleItem.
     *
     * @param dataItem CandleItem
     */
    public void printCandleItem(CandleItemUI dataItem) {

        _log.debug("Symbol: {} Start Time: {} Open: {} High: {} Low: {} Close: {} Vwap: {} Volume: {} Count: {} LastUpdateDate: {}", this.getSymbol(), dataItem.getPeriod().getStart(), dataItem.getOpen(), dataItem.getHigh(), dataItem.getLow(), dataItem.getClose(), dataItem.getVwap(), dataItem.getVolume(), dataItem.getCount(), dataItem.getLastUpdateDate());
    }

    /**
     * Method rollCandle. Creates a rolling candle that is the sum of the under
     * lying candle. So 5 sec bars rolled up to 5min bars will rollup interval
     * of 5min/5sec = 60.
     *
     * @param period         the candle period.
     * @param rollupInterval the rollup Interval.
     * @param open           the open-value.
     * @param high           the high-value.
     * @param low            the low-value.
     * @param close          the close-value.
     * @param volume         the volume value.
     * @param vwap           the volume weighted price.
     * @param tradeCount     the number of trades.
     * @param lastUpdateDate the lastUpdateDate
     */
    private void rollCandle(RegularTimePeriod period, int rollupInterval, double open, double high, double low,
                            double close, long volume, int tradeCount, double vwap, ZonedDateTime lastUpdateDate) {

        if (rollupInterval != this.rollingCandle.rollupInterval || this.isEmpty()) {

            /*
             * Going to a lower period i.e say we were 5 min bars now going to
             * 5sec bars within the current 5min bar.
             */
            if (!this.isEmpty()) {

                /*
                 * Build current bar
                 */
                CandleItemUI candleItem = (CandleItemUI) this.getDataItem(this.getItemCount() - 1);
                if (candleItem.getPeriod().equals(period)) {
                    this.rollingCandle = new RollingCandle(period, rollupInterval, candleItem.getOpen(),
                            candleItem.getHigh(), candleItem.getLow(), candleItem.getClose(), candleItem.getVolume(),
                            candleItem.getCount(), candleItem.getVwap(), lastUpdateDate);

                    this.sumVwapVolume = candleItem.getVwap() * candleItem.getVolume();
                    this.sumVolume = candleItem.getVolume();
                    this.sumTradeCount = candleItem.getCount();
                } else {
                    this.sumVwapVolume = 0d;
                    this.sumVolume = 0L;
                    this.sumTradeCount = 0;
                    this.rollingCandle.rollupInterval = rollupInterval;
                }
                if (this.getItemCount() > 1) {
                    CandleItemUI prevCandleItem = (CandleItemUI) this.getDataItem(this.getItemCount() - 2);
                    this.prevRollingCandle = new RollingCandle(prevCandleItem.getPeriod(),
                            this.rollingCandle.rollupInterval, prevCandleItem.getOpen(), prevCandleItem.getHigh(),
                            prevCandleItem.getLow(), prevCandleItem.getClose(), prevCandleItem.getVolume(),
                            prevCandleItem.getCount(), prevCandleItem.getVwap(), prevCandleItem.getLastUpdateDate());
                }
            } else {
                this.rollingCandle.rollupInterval = rollupInterval;
                this.rollingCandle.open = open;
                this.sumVwapVolume = 0d;
                this.sumVolume = 0L;
                this.sumTradeCount = 0;
            }

            this.openValues.clear();
            this.highValues.clear();
            this.lowValues.clear();
            this.volumeValues.clear();
            this.tradeCountValues.clear();
            this.vwapVolumeValues.clear();
            this.rollingCandleValues.clear();
        }

        updateRollingCandle(period, rollupInterval, open, high, low, close, volume, tradeCount, vwap, lastUpdateDate);
    }

    /**
     * Method updateRollupCandle. Creates a rolling candle that is the sum of
     * the under lying candle. So 5 sec bars rolled up to 5min bars will rollup
     * interval of 5min/5sec = 60.
     *
     * @param period         the candle period.
     * @param rollupInterval the rollup Interval.
     * @param open           the open-value.
     * @param high           the high-value.
     * @param low            the low-value.
     * @param close          the close-value.
     * @param volume         the volume value.
     * @param vwap           the volume weighted price.
     * @param tradeCount     the number of trades.
     * @param lastUpdateDate the lastUpdateDate
     */
    private void updateRollingCandle(RegularTimePeriod period, int rollupInterval, double open, double high, double low,
                                     double close, long volume, int tradeCount, double vwap, ZonedDateTime lastUpdateDate) {

        if (rollupInterval == this.rollingCandleValues.size()) {
            this.prevRollingCandle = this.rollingCandleValues.removeLast();

            this.rollingCandle.open = this.openValues.removeLast();
            if (this.openValues.isEmpty())
                this.rollingCandle.open = open;

            if (this.rollingCandle.high == this.highValues.removeLast()) {
                if (this.highValues.isEmpty()) {
                    this.rollingCandle.high = high;
                } else {
                    this.rollingCandle.high = Collections.max(this.highValues);
                }
            }

            if (this.rollingCandle.low == this.lowValues.removeLast()) {
                if (this.lowValues.isEmpty()) {
                    this.rollingCandle.low = low;
                } else {
                    this.rollingCandle.low = Collections.min(this.lowValues);
                }
            }

            sumVolume = sumVolume - this.volumeValues.removeLast();
            sumVwapVolume = sumVwapVolume - this.vwapVolumeValues.removeLast();
            sumTradeCount = sumTradeCount - this.tradeCountValues.removeLast();
        }

        this.rollingCandle.period = period;
        this.rollingCandle.lastUpdateDate = lastUpdateDate;

        this.openValues.addFirst(open);

        this.highValues.addFirst(high);
        if (high > this.rollingCandle.high)
            this.rollingCandle.high = high;

        this.lowValues.addFirst(low);
        if (low < this.rollingCandle.low)
            this.rollingCandle.low = low;

        this.rollingCandle.close = close;

        this.tradeCountValues.addFirst(tradeCount);
        sumTradeCount = sumTradeCount + tradeCount;
        this.rollingCandle.tradeCount = sumTradeCount;

        this.volumeValues.addFirst(volume);
        sumVolume = sumVolume + volume;
        this.rollingCandle.volume = sumVolume;

        this.vwapVolumeValues.addFirst(vwap * volume);
        sumVwapVolume = sumVwapVolume + this.vwapVolumeValues.getFirst();

        if (sumVolume > 0) {
            this.rollingCandle.vwap = sumVwapVolume / sumVolume;
        } else {
            this.rollingCandle.vwap = this.rollingCandle.close;
        }

        // _log.info("**Date: " + period.getStart() + " sumVwapVolume: "
        // + sumVwapVolume + " sumVolume: " + sumVolume + " volume: "
        // + volume + " vwap: " + this.rollingCandle.vwap);
        try {
            this.rollingCandleValues.addFirst((RollingCandle) this.rollingCandle.clone());
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            _log.error("Error updateRollingCandle cannot clone candle Msg: {}", e.getMessage());
        }
    }

    public static class RollingCandle implements Cloneable {

        private int rollupInterval = 0;
        private RegularTimePeriod period = null;
        private double open = 0;
        private double high = 0;
        private double low = Double.MAX_VALUE;
        private double close = 0;
        private long volume = 0;
        private int tradeCount = 0;
        private double vwap = 0;
        private ZonedDateTime lastUpdateDate = null;

        public RollingCandle() {
        }

        public RollingCandle(RegularTimePeriod period, int rollupInterval, double open, double high, double low,
                             double close, long volume, int tradeCount, double vwap, ZonedDateTime lastUpdateDate) {
            this.rollupInterval = rollupInterval;
            this.period = period;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
            this.tradeCount = tradeCount;
            this.vwap = vwap;
            this.lastUpdateDate = lastUpdateDate;
        }

        /**
         * Method getPeriod.
         *
         * @return CandlePeriod
         */
        public RegularTimePeriod getPeriod() {
            return this.period;
        }

        /**
         * Method getRollupInterval.
         *
         * @return int
         */
        public int getRollupInterval() {
            return this.rollupInterval;
        }

        /**
         * Method getOpen.
         *
         * @return double
         */
        public double getOpen() {
            return this.open;
        }

        /**
         * Method getHigh.
         *
         * @return double
         */
        public double getHigh() {
            return this.high;
        }

        /**
         * Method getLow.
         *
         * @return double
         */
        public double getLow() {
            return this.low;
        }

        /**
         * Method getClose.
         *
         * @return double
         */
        public double getClose() {
            return this.close;
        }

        /**
         * Method getVwap.
         *
         * @return double
         */
        public double getVwap() {
            return this.vwap;
        }

        /**
         * Method getVolume.
         *
         * @return long
         */
        public long getVolume() {
            return this.volume;
        }

        /**
         * Method getTradeCount.
         *
         * @return int
         */
        public int getTradeCount() {
            return this.tradeCount;
        }

        /**
         * Method getLastUpdateDate.
         *
         * @return ZonedDateTime
         */
        public ZonedDateTime getLastUpdateDate() {
            return this.lastUpdateDate;
        }

        public boolean getSide() {
            return this.close >= this.open;
        }

        /**
         * Method clone.
         *
         * @return Object
         */
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}