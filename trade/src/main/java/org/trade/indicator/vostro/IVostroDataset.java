package org.trade.indicator.vostro;

import org.jfree.data.xy.XYDataset;

/**
 * An interface that defines data in the form of Vostro
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IVostroDataset extends XYDataset {

    /**
     * Returns the Vostro for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Vostro.
     */
    double getVostroValue(int series, int item);

    /**
     * Returns the Vostro for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Vostro.
     */
    Number getVostro(int series, int item);

}
