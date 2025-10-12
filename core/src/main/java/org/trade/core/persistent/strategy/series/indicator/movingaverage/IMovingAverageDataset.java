package org.trade.core.persistent.strategy.series.indicator.movingaverage;

import org.trade.core.persistent.strategy.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IMovingAverageDataset extends XYDataset {

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average.
     */
    double getMovingAverageValue(int series, int item);

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average.
     */
    Number getMovingAverage(int series, int item);

}
