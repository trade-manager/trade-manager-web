package org.trade.core.persistent.strategy.series.indicator.heikinashi;

import org.trade.core.persistent.strategy.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IHeikinAshiDataset extends XYDataset {

    /**
     * Returns the high-value for the specified series and item.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The value.
     */
    Number getHigh(int series, int item);

    /**
     * Returns the high-value (as a double primitive) for an item within a
     * series.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The high-value.
     */
    double getHighValue(int series, int item);

    /**
     * Returns the low-value for the specified series and item.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The value.
     */
    Number getLow(int series, int item);

    /**
     * Returns the low-value (as a double primitive) for an item within a
     * series.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The low-value.
     */
    double getLowValue(int series, int item);

    /**
     * Returns the open-value for the specified series and item.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The value.
     */
    Number getOpen(int series, int item);

    /**
     * Returns the open-value (as a double primitive) for an item within a
     * series.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The open-value.
     */
    double getOpenValue(int series, int item);

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The value.
     */
    Number getClose(int series, int item);

    /**
     * Returns the close-value (as a double primitive) for an item within a
     * series.
     *
     * @param series the series (zero-based index).
     * @param item   the item (zero-based index).
     * @return The close-value.
     */
    double getCloseValue(int series, int item);

}
