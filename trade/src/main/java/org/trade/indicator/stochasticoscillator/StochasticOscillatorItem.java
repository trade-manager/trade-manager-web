package org.trade.indicator.stochasticoscillator;

import org.jfree.data.ComparableObjectItem;
import org.trade.core.persistent.strategy.series.indicator.stochasticoscillator.StochasticOscillator;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * An item representing data in the form (period, open, high, low, close).
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 * @since 1.0.4
 */
public class StochasticOscillatorItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period               the time period.
     * @param stochasticOscillator BigDecimal
     */
    public StochasticOscillatorItem(RegularTimePeriod period, BigDecimal stochasticOscillator) {
        super(period, new StochasticOscillator(stochasticOscillator));
    }

    /**
     * Returns the period.
     *
     * @return The period (never <code>null</code>).
     */
    public RegularTimePeriod getPeriod() {
        return (RegularTimePeriod) getComparable();
    }

    /**
     * Returns the y-value.
     *
     * @return The y-value.
     */
    public double getY() {
        return getStochasticOscillator();
    }

    /**
     * Set the Stochastic Oscillator value.
     *
     * @param stochasticOscillator double
     */
    public void setStochasticOscillator(double stochasticOscillator) {
        StochasticOscillator dataItem = (StochasticOscillator) getObject();
        if (dataItem != null) {
            dataItem.setStochasticOscillator(new BigDecimal(stochasticOscillator));
        }

    }

    /**
     * Returns the Stochastic Oscillator value.
     *
     * @return The Stochastic Oscillator value.
     */
    public double getStochasticOscillator() {
        StochasticOscillator dataItem = (StochasticOscillator) getObject();
        if (dataItem != null) {
            if (null == dataItem.getStochasticOscillator()) {
                return 0;
            }
            return dataItem.getStochasticOscillator().doubleValue();
        } else {
            return 0;
        }
    }
}
