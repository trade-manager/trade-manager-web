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
package org.trade.indicator;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.trade.core.util.CloneUtils;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.util.time.TimePeriodAnchor;
import org.trade.indicator.heikinashi.HeikinAshiItemUI;
import org.trade.indicator.heikinashi.IHeikinAshiDatasetUI;
import org.trade.ui.chart.renderer.HeikinAshiRenderer;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HeikinAshiDatasetUI extends AbstractXYDataset implements IIndicatorDatasetUI, IHeikinAshiDatasetUI, Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3931818830267435673L;

    /**
     * Storage for the data series.
     */
    private List<IndicatorSeriesUI> data;

    private TimePeriodAnchor xPosition = TimePeriodAnchor.START;

    /**
     * Creates a new instance of <code>OHLCSeriesCollection</code>.
     */
    public HeikinAshiDatasetUI() {
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
     * @see IIndicatorDatasetUI#addSeries(IndicatorSeriesUI)
     */
    public void addSeries(IndicatorSeriesUI series) {
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
     * @see IIndicatorDatasetUI#removeSeries(IndicatorSeriesUI)
     */
    public void removeSeries(IndicatorSeriesUI series) {
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
     * @see IIndicatorDatasetUI#setSeries(int,
     * IndicatorSeriesUI)
     */
    public void setSeries(int index, IndicatorSeriesUI series) {
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
     * org.trade.strategy.data.IIndicatorDatasetUI#getSeries(int)
     */
    public HeikinAshiSeriesUI getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
        return (HeikinAshiSeriesUI) this.data.get(series);
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
        HeikinAshiSeriesUI s = (HeikinAshiSeriesUI) this.data.get(series);
        HeikinAshiItemUI di = (HeikinAshiItemUI) s.getDataItem(item);
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
        HeikinAshiSeriesUI s = (HeikinAshiSeriesUI) this.data.get(series);
        HeikinAshiItemUI di = (HeikinAshiItemUI) s.getDataItem(item);
        return di.getY();
    }

    /**
     * Returns the open-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The open-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getOpenValue(int, int)
     */
    public double getOpenValue(int series, int item) {
        HeikinAshiSeriesUI s = (HeikinAshiSeriesUI) this.data.get(series);
        HeikinAshiItemUI di = (HeikinAshiItemUI) s.getDataItem(item);
        return di.getOpen();
    }

    /**
     * Returns the open-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The open-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getOpen(int, int)
     */
    public Number getOpen(int series, int item) {
        return getOpenValue(series, item);
    }

    /**
     * Returns the close-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The close-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getCloseValue(int, int)
     */
    public double getCloseValue(int series, int item) {
        HeikinAshiSeriesUI s = (HeikinAshiSeriesUI) this.data.get(series);
        HeikinAshiItemUI di = (HeikinAshiItemUI) s.getDataItem(item);
        return di.getClose();
    }

    /**
     * Returns the close-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The close-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getClose(int, int)
     */
    public Number getClose(int series, int item) {
        return getCloseValue(series, item);
    }

    /**
     * Returns the high-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The high-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getHighValue(int, int)
     */
    public double getHighValue(int series, int item) {
        HeikinAshiSeriesUI s = (HeikinAshiSeriesUI) this.data.get(series);
        HeikinAshiItemUI di = (HeikinAshiItemUI) s.getDataItem(item);
        return di.getHigh();
    }

    /**
     * Returns the high-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The high-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getHigh(int, int)
     */
    public Number getHigh(int series, int item) {
        return getHighValue(series, item);
    }

    /**
     * Returns the low-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The low-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset
     * #getLowValue(int, int)
     */
    public double getLowValue(int series, int item) {
        HeikinAshiSeriesUI s = (HeikinAshiSeriesUI) this.data.get(series);
        HeikinAshiItemUI di = (HeikinAshiItemUI) s.getDataItem(item);
        return di.getLow();
    }

    /**
     * Returns the low-value for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The low-value. * @see
     * org.trade.strategy.data.heikinashi.IHeikinAshiDataset#getLow(int,
     * int)
     */
    public Number getLow(int series, int item) {
        return getLowValue(series, item);
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
        if (!(obj instanceof HeikinAshiDatasetUI that)) {
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
        HeikinAshiDatasetUI clone = (HeikinAshiDatasetUI) super.clone();
        clone.data = (List<IndicatorSeriesUI>) CloneUtils.deepClone(this.data);
        return clone;
    }

    /**
     * Method updateDataset.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     * @param newBar      boolean
     */
    public void updateDataset(CandleDatasetUI source, int seriesIndex, boolean newBar) {
        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleDataset).");
        }

        for (int i = 0; i < this.getSeriesCount(); i++) {
            HeikinAshiSeriesUI series = this.getSeries(i);

            series.updateSeries(source.getSeries(seriesIndex), source.getSeries(seriesIndex).getItemCount() - 1,
                    newBar);

        }
    }

    /**
     * Method clear.
     *
     * @see IIndicatorDatasetUI#clear()
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
     * @see IIndicatorDatasetUI#getRenderer()
     */
    public XYItemRenderer getRenderer() {
        return new HeikinAshiRenderer(true);
    }

    /**
     * Method getSeriesColor.
     *
     * @param seriesIndex int
     * @return Color
     * @see IIndicatorDatasetUI#getSeriesColor(int)
     */
    public Color getSeriesColor(int seriesIndex) {
        return this.getSeries(seriesIndex).getSeriesColor();
    }

    /**
     * Method getDisplaySeries.
     *
     * @param seriesIndex int
     * @return boolean
     * @see IIndicatorDatasetUI#getDisplaySeries(int)
     */
    public boolean getDisplaySeries(int seriesIndex) {
        return this.getSeries(seriesIndex).getDisplaySeries();
    }

    /**
     * Method getSubChart.
     *
     * @param seriesIndex int
     * @return boolean
     * @see IIndicatorDatasetUI#getSubChart(int)
     */
    public boolean getSubChart(int seriesIndex) {
        return this.getSeries(seriesIndex).getSubChart();
    }

    /**
     * Method getType.
     *
     * @param seriesIndex int
     * @return String
     * @see IIndicatorDatasetUI#getType(int)
     */
    public String getType(int seriesIndex) {
        return this.data.get(seriesIndex).getType();
    }
}
