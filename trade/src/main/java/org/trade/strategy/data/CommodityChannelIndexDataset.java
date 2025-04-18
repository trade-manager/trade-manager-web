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
package org.trade.strategy.data;

import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TimePeriodAnchor;
import org.trade.strategy.data.cci.CommodityChannelIndexItem;
import org.trade.strategy.data.cci.ICommodityChannelIndexDataset;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CommodityChannelIndexDataset extends AbstractXYDataset
        implements IIndicatorDataset, ICommodityChannelIndexDataset, Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3931818830267435673L;

    /**
     * Storage for the data series.
     */
    private List<IndicatorSeries> data;

    private TimePeriodAnchor xPosition = TimePeriodAnchor.START;

    /**
     * Creates a new instance of <code>OHLCSeriesCollection</code>.
     */
    public CommodityChannelIndexDataset() {
        this.data = new ArrayList<>();
    }

    /**
     * Returns the position within each time period that is used for the X value
     * when the collection is used as an {@link XYDataset}.
     *
     * @return The anchor position (never <code>null</code>).
     * @since 1.0.11
     */
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    /**
     * Sets the position within each time period that is used for the X values
     * when the collection is used as an {@link XYDataset}, then sends a
     * {@link DatasetChangeEvent} is sent to all registered listeners.
     *
     * @param anchor the anchor position (<code>null</code> not permitted).
     * @since 1.0.11
     */
    public void setXPosition(TimePeriodAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent} to
     * all registered listeners.
     *
     * @param series the series (<code>null</code> not permitted).
     * @see IIndicatorDataset#addSeries(IndicatorSeries)
     */
    public void addSeries(IndicatorSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Removes a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series the series (<code>null</code> not permitted).
     * @see IIndicatorDataset#removeSeries(IndicatorSeries)
     */
    public void removeSeries(IndicatorSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null series argument.");
        }
        this.data.remove(series);
        series.removeChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Replace a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series the series (<code>null</code> not permitted).
     * @param index  int
     * @see IIndicatorDataset#setSeries(int,
     * IndicatorSeries)
     */
    public void setSeries(int index, IndicatorSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null series argument.");
        }
        this.data.get(index).removeChangeListener(this);
        this.data.set(index, series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent} to
     * all registered listeners.
     */
    public void seriesUpdated() {
        fireDatasetChanged();
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count. * @see
     * org.jfree.data.general.SeriesDataset#getSeriesCount()
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series from the collection.
     *
     * @param series the series index (zero-based).
     * @return The series. * @throws IllegalArgumentException if
     * <code>series</code> is not in the range <code>0</code> to
     * <code>getSeriesCount() - 1</code>. * @see
     * org.trade.strategy.data.IIndicatorDataset#getSeries(int)
     */
    public CommodityChannelIndexSeries getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (CommodityChannelIndexSeries) this.data.get(series);
    }

    /**
     * Returns the key for a series.
     *
     * @param series the series index (in the range <code>0</code> to
     *               <code>getSeriesCount() - 1</code>).
     * @return The key for a series. * @throws IllegalArgumentException if
     * <code>series</code> is not in the specified range. * @see
     * org.jfree.data.general.SeriesDataset#getSeriesKey(int)
     */
    public Comparable<?> getSeriesKey(int series) {
        // defer argument checking
        return getSeries(series).getKey();
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series the series (zero-based index).
     * @return The item count. * @throws IllegalArgumentException if
     * <code>series</code> is not in the range <code>0</code> to
     * <code>getSeriesCount() - 1</code>. * @see
     * org.jfree.data.xy.XYDataset#getItemCount(int)
     */
    public int getItemCount(int series) {
        // defer argument checking
        return getSeries(series).getItemCount();
    }

    /**
     * Returns the x-value for a time period.
     *
     * @param period the time period (<code>null</code> not permitted).
     * @return The x-value.
     */
    protected synchronized long getX(RegularTimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond();
        } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond();
        } else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond();
        }
        return result;
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The x-value. * @see org.jfree.data.xy.XYDataset#getXValue(int,
     * int)
     */
    public double getXValue(int series, int item) {
        CommodityChannelIndexSeries s = (CommodityChannelIndexSeries) this.data.get(series);
        CommodityChannelIndexItem di = (CommodityChannelIndexItem) s.getDataItem(item);
        RegularTimePeriod period = di.getPeriod();
        return getX(period);
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The x-value. * @see org.jfree.data.xy.XYDataset#getX(int, int)
     */
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The y-value. * @see org.jfree.data.xy.XYDataset#getY(int, int)
     */
    public Number getY(int series, int item) {
        CommodityChannelIndexSeries s = (CommodityChannelIndexSeries) this.data.get(series);
        CommodityChannelIndexItem di = (CommodityChannelIndexItem) s.getDataItem(item);
        return di.getY();
    }

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average. * @see
     * org.trade.strategy.data.movingaverage.IMovingAverageDataset
     * #getMovingAverageValue(int, int)
     */
    public double getCommodityChannelIndexValue(int series, int item) {
        CommodityChannelIndexSeries s = (CommodityChannelIndexSeries) this.data.get(series);
        CommodityChannelIndexItem di = (CommodityChannelIndexItem) s.getDataItem(item);
        return di.getCommodityChannelIndex();
    }

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Pivot. * @see
     * org.trade.strategy.data.movingaverage.IMovingAverageDataset
     * #getMovingAverage(int, int)
     */
    public Number getCommodityChannelIndex(int series, int item) {
        return getCommodityChannelIndexValue(series, item);
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj the object (<code>null</code> permitted).
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CommodityChannelIndexDataset that)) {
            return false;
        }
        if (!this.xPosition.equals(that.xPosition)) {
            return false;
        }
        return this.data.equals(that.data);
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone. * @throws CloneNotSupportedException if there is a
     * problem.
     */
    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        CommodityChannelIndexDataset clone = (CommodityChannelIndexDataset) super.clone();
        clone.data = (List<IndicatorSeries>) ObjectUtils.deepClone(this.data);
        return clone;
    }

    /**
     * Method updateDataset.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     * @param newBar      boolean
     */
    public void updateDataset(CandleDataset source, int seriesIndex, boolean newBar) {
        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleDataset).");
        }

        for (int x = 0; x < this.getSeriesCount(); x++) {
            CommodityChannelIndexSeries series = this.getSeries(x);
            series.updateSeries(source.getSeries(seriesIndex), source.getSeries(seriesIndex).getItemCount() - 1,
                    newBar);
        }
    }

    /**
     * Method clear.
     *
     * @see IIndicatorDataset#clear()
     */
    public void clear() {

        for (int i = 0; i < this.getSeriesCount(); i++) {
            this.getSeries(i).clear();
        }
    }

    /**
     * Method getRenderer.
     *
     * @return XYItemRenderer
     * @see IIndicatorDataset#getRenderer()
     */
    public XYItemRenderer getRenderer() {
        return new StandardXYItemRenderer();
    }

    /**
     * Method getSeriesColor.
     *
     * @param seriesIndex int
     * @return Color
     * @see IIndicatorDataset#getSeriesColor(int)
     */
    public Color getSeriesColor(int seriesIndex) {
        return this.getSeries(seriesIndex).getSeriesColor();
    }

    /**
     * Method getDisplaySeries.
     *
     * @param seriesIndex int
     * @return boolean
     * @see IIndicatorDataset#getDisplaySeries(int)
     */
    public boolean getDisplaySeries(int seriesIndex) {
        return this.getSeries(seriesIndex).getDisplaySeries();
    }

    /**
     * Method getSubChart.
     *
     * @param seriesIndex int
     * @return boolean
     * @see IIndicatorDataset#getSubChart(int)
     */
    public boolean getSubChart(int seriesIndex) {
        return this.getSeries(seriesIndex).getSubChart();
    }

    /**
     * Method getType.
     *
     * @param seriesIndex int
     * @return String
     * @see IIndicatorDataset#getType(int)
     */
    public String getType(int seriesIndex) {
        return this.data.get(seriesIndex).getType();
    }
}
