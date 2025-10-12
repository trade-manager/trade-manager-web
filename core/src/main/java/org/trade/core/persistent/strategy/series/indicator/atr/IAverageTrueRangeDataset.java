package org.trade.core.persistent.strategy.series.indicator.atr;


import org.trade.core.persistent.strategy.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IAverageTrueRangeDataset extends XYDataset {

    /**
     * Returns the AverageTrueRange for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The AverageTrueRange.
     */
    double getAverageTrueRangeValue(int series, int item);

    /**
     * Returns the AverageTrueRange for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The AverageTrueRange.
     */
    Number getAverageTrueRange(int series, int item);

}
