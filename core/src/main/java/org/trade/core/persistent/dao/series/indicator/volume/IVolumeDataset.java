package org.trade.core.persistent.dao.series.indicator.volume;

import org.trade.core.persistent.dao.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IVolumeDataset extends XYDataset {

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average.
     */
    double getVolumeValue(int series, int item);

    /**
     * Returns the Moving Average for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Moving Average.
     */
    Number getVolume(int series, int item);

}
