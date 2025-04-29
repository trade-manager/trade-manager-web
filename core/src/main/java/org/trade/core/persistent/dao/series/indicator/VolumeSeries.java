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
import org.trade.core.persistent.PersistentModelException;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.volume.VolumeItem;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;

/**
 * A list of (RegularTimePeriod, open, high, low, close) data items.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("VolumeSeries")
public class VolumeSeries extends IndicatorSeries {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    private long barWidthInMilliseconds;

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

    public VolumeSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                        Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    /**
     * Constructor for VolumeSeries.
     *
     * @param strategy               Strategy
     * @param name                   String
     * @param type                   String
     * @param description            String
     * @param displayOnChart         Boolean
     * @param chartRGBColor          Integer
     * @param subChart               Boolean
     * @param barWidthInMilliseconds long
     */
    public VolumeSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                        Integer chartRGBColor, Boolean subChart, long barWidthInMilliseconds) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
        this.barWidthInMilliseconds = barWidthInMilliseconds;
    }

    public VolumeSeries() {
        super(IndicatorSeries.VolumeSeries);
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final VolumeItem item = (VolumeItem) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period the period.
     * @param volume Long
     * @param side   boolean
     */
    public void add(RegularTimePeriod period, Long volume, boolean side) throws PersistentModelException {
        if (!this.isEmpty()) {
            VolumeItem item0 = (VolumeItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new VolumeItem(period, volume, side), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem VolumeItem
     */
    public void add(VolumeItem dataItem, boolean notify) throws PersistentModelException {
        if (!this.isEmpty()) {
            VolumeItem item0 = (VolumeItem) this.getDataItem(0);
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
    public void createSeries(CandleDataset source, int seriesIndex) throws PersistentModelException {

        if (source.getSeries(seriesIndex) == null) {
            throw new IllegalArgumentException("Null source (XYDataset).");
        }

        for (int i = 0; i < source.getSeries(seriesIndex).getItemCount(); i++) {
            this.updateSeries(source.getSeries(seriesIndex), i, true);
        }
    }

    /**
     * Method updateSeries.
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public void updateSeries(CandleSeries source, int skip, boolean newBar) throws PersistentModelException {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }
        /*
         * Do not want to add the new bar.
         */
        if (source.getItemCount() > skip) {

            // get the current data item...
            CandleItem candleItem = (CandleItem) source.getDataItem(skip);
            /*
             * If the item does not exist in the series then this is a new time
             * period and so we need to remove the last in the set and add the
             * new periods values. Otherwise we just update the last value in
             * the set.
             */
            if (newBar) {
                VolumeItem dataItem = new VolumeItem(candleItem.getPeriod(), candleItem.getVolume(),
                        candleItem.getSide());
                this.add(dataItem, true);
            } else {
                VolumeItem dataItem = (VolumeItem) this.getDataItem(this.getItemCount() - 1);
                dataItem.setVolume(candleItem.getVolume());
                dataItem.setSide(candleItem.getSide());
            }

        }
    }

    /**
     * Method printSeries.
     */
    public void printSeries() {
        for (int i = 0; i < this.getItemCount(); i++) {
            VolumeItem dataItem = (VolumeItem) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Volume: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getVolume());
        }
    }

    /**
     * Method getBarWidthInMilliseconds.
     *
     * @return long
     */
    @Transient
    public long getBarWidthInMilliseconds() {
        return this.barWidthInMilliseconds;
    }

    /**
     * Method setBarWidthInMilliseconds.
     *
     * @param barWidthInMilliseconds long
     */
    public void setBarWidthInMilliseconds(long barWidthInMilliseconds) {
        this.barWidthInMilliseconds = barWidthInMilliseconds;
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
