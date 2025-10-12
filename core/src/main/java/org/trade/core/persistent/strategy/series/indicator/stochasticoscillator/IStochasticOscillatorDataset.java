package org.trade.core.persistent.strategy.series.indicator.stochasticoscillator;

import org.trade.core.persistent.strategy.series.XYDataset;

/**
 * An interface that defines data in the form of (x, high, low, open, close)
 * tuples.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IStochasticOscillatorDataset extends XYDataset {

    /**
     * Returns the Stochastic Oscillator for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Stochastic Oscillator.
     */
    double getStochasticOscillatorValue(int series, int item);

    /**
     * Returns the Stochastic Oscillator for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The Stochastic Oscillator.
     */
    Number getStochasticOscillator(int series, int item);

}
