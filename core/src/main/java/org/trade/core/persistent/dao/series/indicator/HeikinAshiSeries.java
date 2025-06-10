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
package org.trade.core.persistent.dao.series.indicator;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.heikinashi.HeikinAshiItem;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.Vector;

/**
 * Heikin-Ashi Candlesticks are an offshoot from Japanese candlesticks.
 * Heikin-Ashi Candlesticks use the open-close data from the prior period and
 * the open-high-low-close data from the current period to create a combo
 * candlestick. The resulting candlestick filters out some noise in an effort to
 * better capture the trend. In Japanese, Heikin means "average" and "ashi"
 * means "pace" (EUDict.com). Taken together, Heikin-Ashi represents the
 * average-pace of prices. Heikin-Ashi Candlesticks are not used like normal
 * candlesticks. Dozens of bullish or bearish reversal patterns consisting of
 * 1-3 candlesticks are not to be found. Instead, these candlesticks can be used
 * to identify trending periods, potential reversal points and classic technical
 * analysis patterns.
 * <p>
 * <p>
 * Heikin-Ashi Candlesticks are based on price data from the current
 * open-high-low-close, the current Heikin-Ashi values and the prior Heikin-Ashi
 * values. Yes, it is a bit complicated. In the formula below, a "(0)" denotes
 * the current period. A "(-1)" denotes the prior period. "HA" refers to
 * Heikin-Ashi. Let's take each data point one at a time.
 * <p>
 * <p>
 * 1. The Heikin-Ashi Close is simply an average of the open, high, low and
 * close for the current period.
 * <p>
 * HA-Close = (Open(0) + High(0) + Low(0) + Close(0)) / 4
 * <p>
 * 2. The Heikin-Ashi Open is the average of the prior Heikin-Ashi candlestick
 * open plus the close of the prior Heikin-Ashi candlestick.
 * <p>
 * HA-Open = (HA-Open(-1) + HA-Close(-1)) / 2
 * <p>
 * 3. The Heikin-Ashi High is the maximum of three data points: the current
 * period's high, the current Heikin-Ashi candlestick open or the current
 * Heikin-Ashi candlestick close.
 * <p>
 * HA-High = Maximum of the High(0), HA-Open(0) or HA-Close(0)
 * <p>
 * 4. The Heikin-Ashi low is the minimum of three data points: the current
 * period's low, the current Heikin-Ashi candlestick open or the current
 * Heikin-Ashi candlestick close.
 * <p>
 * HA-Low = Minimum of the Low(0), HA-Open(0) or HA-Close(0)
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

@Entity
@DiscriminatorValue("HeikinAshiSeries")
public class HeikinAshiSeries extends IndicatorSeries {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    /**
     * Creates a new empty series. By default, items added to the series will be
     * sorted into ascending order by period, and duplicate periods will not be
     * allowed.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     */

    public HeikinAshiSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                            Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);

    }

    public HeikinAshiSeries() {
        super(IndicatorSeries.HeikinAshiSeries);
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final HeikinAshiItem item = (HeikinAshiItem) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period         the period.
     * @param contract       Contract
     * @param open           double
     * @param high           double
     * @param low            double
     * @param close          double
     * @param lastUpdateDate ZonedDateTime
     */
    public void add(Contract contract, RegularTimePeriod period, double open, double high, double low, double close,
                    ZonedDateTime lastUpdateDate) throws ServiceException {
        if (!this.isEmpty()) {
            HeikinAshiItem item0 = (HeikinAshiItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new HeikinAshiItem(contract, period, open, high, low, close, lastUpdateDate), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem HeikinAshiItem
     */
    public void add(HeikinAshiItem dataItem, boolean notify) throws ServiceException {
        if (!this.isEmpty()) {
            HeikinAshiItem item0 = (HeikinAshiItem) this.getDataItem(0);
            if (!dataItem.getPeriod().getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(dataItem, notify);
    }

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public void createSeries(CandleDataset source, int seriesIndex) throws ServiceException {

        if (source.getSeries(seriesIndex) == null) {
            throw new IllegalArgumentException("Null source (CandleDataset).");
        }
        for (int i = 0; i < source.getSeries(seriesIndex).getItemCount(); i++) {
            this.updateSeries(source.getSeries(seriesIndex), i, true);
        }
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Method updateSeries. Heikin Ashi charts calculate their own open (HAO),
     * high (HAH), low (HAL), and close (HAC), using the actual open (O), high
     * (H), low (L), and close (C), of the time frame (e.g. the open, high, low,
     * and close, of each five minutes).
     * <p>
     * HAO = (HAO-1 + HAC-1) / 2
     * <p>
     * HAC = (O + H + L + C) / 4
     * <p>
     * HAH = Highest(H, HAO, HAC)
     * <p>
     * HAL = Lowest(L, HAO, HAC)
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */

    public void updateSeries(CandleSeries source, int skip, boolean newBar) throws ServiceException {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }

        if (source.getItemCount() > skip) {
            // get the current data item...
            CandleItem candleItem = (CandleItem) source.getDataItem(skip);

            if (source.getItemCount() > 1) {
                /*
                 * Get the prev candle the new candle may be just forming or
                 * completed if back testing. Hiekin-Ashi bats must be formed
                 * from completed bars.
                 */
                int index = this.indexOf(candleItem.getPeriod());
                double xOpenPrev;
                double xClosePrev;
                if (index < 1) {
                    if (this.isEmpty()) {
                        xOpenPrev = candleItem.getOpen();
                        xClosePrev = candleItem.getClose();
                    } else {
                        HeikinAshiItem prevItem = (HeikinAshiItem) this.getDataItem(this.getItemCount() - 1);
                        xClosePrev = prevItem.getClose();
                        xOpenPrev = prevItem.getOpen();
                    }
                } else {
                    HeikinAshiItem prevItem = (HeikinAshiItem) this.getDataItem(this.getItemCount() - 2);
                    xClosePrev = prevItem.getClose();
                    xOpenPrev = prevItem.getOpen();
                }

                double xClose = (candleItem.getOpen() + candleItem.getHigh() + candleItem.getLow()
                        + candleItem.getClose()) / 4;

                double xOpen = (xOpenPrev + xClosePrev) / 2;

                double xHigh = Math.max(candleItem.getHigh(), Math.max(xClosePrev, xOpenPrev));

                double xLow = Math.min(candleItem.getLow(), Math.min(xClosePrev, xOpenPrev));

                if (index < 0) {
                    this.add(new HeikinAshiItem(source.getContract(), candleItem.getPeriod(), xOpen, xHigh, xLow,
                            xClose, candleItem.getLastUpdateDate()), false);
                } else {
                    HeikinAshiItem currDataItem = (HeikinAshiItem) this.getDataItem(index);
                    currDataItem.setOpen(xOpen);
                    currDataItem.setHigh(xHigh);
                    currDataItem.setLow(xLow);
                    currDataItem.setClose(xClose);
                    currDataItem.setLastUpdateDate(candleItem.getLastUpdateDate());
                }
            }
        }
    }

    /**
     * Method printSeries.
     */
    public void printSeries() {
        for (int i = 0; i < this.getItemCount(); i++) {
            HeikinAshiItem dataItem = (HeikinAshiItem) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Open: {} Close: {} High: {} Low: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getOpen(), dataItem.getClose(), dataItem.getHigh(), dataItem.getLow());
        }
    }

    @Transient
    public Vector<Object> getParam(String type) {

        return super.getParam(type);
    }
}
