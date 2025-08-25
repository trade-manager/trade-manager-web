package org.trade.core.persistent.dao.series.indicator.vwap;

import org.trade.core.persistent.dao.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IVwapDataset extends XYDataset {

    /**
     * Returns the Vwap for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Vwap.
     */
    double getVwapValue(int series, int item);

    /**
     * Returns the Pivot for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Pivot.
     */
    Number getVwap(int series, int item);

}
