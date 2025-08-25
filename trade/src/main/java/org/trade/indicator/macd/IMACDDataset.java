package org.trade.indicator.macd;

import org.jfree.data.xy.XYDataset;

/**
 * An interface that defines data for MACD
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IMACDDataset extends XYDataset {

    /**
     * Returns the MACD for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The MACD.
     */
    double getMACDValue(int series, int item);

    /**
     * Returns the MACD for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The MACD.
     */
    Number getMACD(int series, int item);

}
