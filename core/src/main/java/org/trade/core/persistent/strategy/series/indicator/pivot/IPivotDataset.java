package org.trade.core.persistent.strategy.series.indicator.pivot;

import org.trade.core.persistent.strategy.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IPivotDataset extends XYDataset {

    /**
     * Returns the Vwap for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Vwap.
     */
    double getPivotValue(int series, int item);

    /**
     * Returns the Pivot for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Pivot.
     */
    Number getPivot(int series, int item);

    /**
     * Returns the Pivot for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Pivot.
     */
    String getPivotSide(int series, int item);

}
