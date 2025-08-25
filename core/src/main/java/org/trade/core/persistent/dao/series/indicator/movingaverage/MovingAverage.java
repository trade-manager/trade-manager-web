package org.trade.core.persistent.dao.series.indicator.movingaverage;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MovingAverage
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class MovingAverage implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal movingAverage;

    public MovingAverage() {
    }

    /**
     * Constructor for MovingAverage.
     *
     * @param movingAverage BigDecimal
     */
    public MovingAverage(BigDecimal movingAverage) {
        this.movingAverage = movingAverage;
    }

    /**
     * Method getMovingAverage.
     *
     * @return BigDecimal
     */
    public BigDecimal getMovingAverage() {
        return this.movingAverage;
    }

    /**
     * Method setMovingAverage.
     *
     * @param movingAverage BigDecimal
     */
    public void setMovingAverage(BigDecimal movingAverage) {
        this.movingAverage = movingAverage;
    }
}
