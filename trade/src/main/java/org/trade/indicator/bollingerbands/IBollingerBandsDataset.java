package org.trade.indicator.bollingerbands;

import org.jfree.data.xy.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IBollingerBandsDataset extends XYDataset {

    /**
     * Returns the BollingerBands for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The BollingerBands.
     */
    double getBollingerBandsValue(int series, int item);

    /**
     * Returns the BollingerBands for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The BollingerBands.
     */
    Number getBollingerBands(int series, int item);

}
