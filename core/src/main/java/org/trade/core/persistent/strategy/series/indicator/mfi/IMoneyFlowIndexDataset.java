package org.trade.core.persistent.strategy.series.indicator.mfi;

import org.trade.core.persistent.strategy.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IMoneyFlowIndexDataset extends XYDataset {

    /**
     * Returns the Money Flow Index for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Money Flow Index.
     */
    double getMoneyFlowIndexValue(int series, int item);

    /**
     * Returns the Money Flow Index for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Money Flow Index.
     */
    Number getMoneyFlowIndex(int series, int item);

}
