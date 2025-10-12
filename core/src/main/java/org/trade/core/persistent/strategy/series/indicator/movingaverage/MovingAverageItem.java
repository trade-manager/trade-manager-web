package org.trade.core.persistent.strategy.series.indicator.movingaverage;

import org.trade.core.persistent.strategy.series.ComparableObjectItem;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * An item representing data in the form (period, open, high, low, close).
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class MovingAverageItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period        the time period.
     * @param movingAverage BigDecimal
     */
    public MovingAverageItem(RegularTimePeriod period, BigDecimal movingAverage) {
        super(period, new MovingAverage(movingAverage));
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
        return getMovingAverage();
    }

    /**
     * Set the moving Average value.
     *
     * @param movingAverage double
     */
    public void setMovingAverage(double movingAverage) {
        MovingAverage dataItem = (MovingAverage) getObject();
        if (dataItem != null) {
            dataItem.setMovingAverage(new BigDecimal(movingAverage));
        }
    }

    /**
     * Returns the moving Average value.
     *
     * @return The moving Average value.
     */
    public double getMovingAverage() {
        MovingAverage dataItem = (MovingAverage) getObject();
        if (dataItem != null) {
            if (null == dataItem.getMovingAverage()) {
                return 0;
            }
            return dataItem.getMovingAverage().doubleValue();
        } else {
            return 0;
        }
    }
}
