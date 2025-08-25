package org.trade.indicator.cci;

import org.jfree.data.xy.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface ICommodityChannelIndexDataset extends XYDataset {

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average.
     */
    double getCommodityChannelIndexValue(int series, int item);

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average.
     */
    Number getCommodityChannelIndex(int series, int item);

}
