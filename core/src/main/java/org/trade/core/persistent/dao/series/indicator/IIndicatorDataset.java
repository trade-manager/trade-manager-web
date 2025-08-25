package org.trade.core.persistent.dao.series.indicator;

import org.trade.core.persistent.ServiceException;

import java.awt.*;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples. This interface also defines how the data-set should be plotted.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IIndicatorDataset {

    String PACKAGE = IIndicatorDataset.class.getPackageName();

    /**
     * Method updateDataset.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     * @param newBar      boolean
     */
    void updateDataset(CandleDataset source, int seriesIndex, boolean newBar) throws ServiceException;

    void clear();

    /**
     * Method getSeriesColor.
     *
     * @param seriesIndex int
     * @return Color
     */
    Color getSeriesColor(int seriesIndex);

    /**
     * Method getDisplaySeries.
     *
     * @param seriesIndex int
     * @return boolean
     */
    boolean getDisplaySeries(int seriesIndex);

    /**
     * Method getSubChart.
     *
     * @param seriesIndex int
     * @return boolean
     */
    boolean getSubChart(int seriesIndex);

    /**
     * Method getType.
     *
     * @param seriesIndex int
     * @return String
     */
    String getType(int seriesIndex);

    /**
     * Method getSeries.
     *
     * @param seriesIndex int
     * @return IndicatorSeries
     */
    IndicatorSeries getSeries(int seriesIndex);

    /**
     * Method addSeries.
     *
     * @param series IndicatorSeries
     */
    void addSeries(IndicatorSeries series);

    /**
     * Method removeSeries.
     *
     * @param series IndicatorSeries
     */
    void removeSeries(IndicatorSeries series);

    /**
     * Method setSeries.
     *
     * @param index  int
     * @param series IndicatorSeries
     */
    void setSeries(int index, IndicatorSeries series);

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
     */
    int getSeriesCount();
}
