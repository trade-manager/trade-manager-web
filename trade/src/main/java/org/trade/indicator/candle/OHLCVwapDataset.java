package org.trade.indicator.candle;

import org.jfree.data.xy.OHLCDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface OHLCVwapDataset extends OHLCDataset {

    /**
     * Returns the Vwap for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Vwap.
     */
    double getVwapValue(int series, int item);

    /**
     * Returns the Vwap for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Vwap.
     */
    Number getVwap(int series, int item);

}
