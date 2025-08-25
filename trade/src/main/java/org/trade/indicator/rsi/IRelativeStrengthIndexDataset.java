package org.trade.indicator.rsi;

import org.jfree.data.xy.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IRelativeStrengthIndexDataset extends XYDataset {

    /**
     * Returns the relativeStrengthIndex for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The relativeStrengthIndex.
     */
    double getRelativeStrengthIndexValue(int series, int item);

    /**
     * Returns the relativeStrengthIndex for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The relativeStrengthIndex.
     */
    Number getRelativeStrengthIndex(int series, int item);

}
