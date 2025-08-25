package org.trade.core.persistent.dao.series.indicator.stochasticoscillator;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * StochasticOscillator
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class StochasticOscillator implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal stochasticOscillator;

    public StochasticOscillator() {
    }

    /**
     * Constructor for StochasticOscillator.
     *
     * @param stochasticOscillator BigDecimal
     */
    public StochasticOscillator(BigDecimal stochasticOscillator) {
        this.stochasticOscillator = stochasticOscillator;
    }

    /**
     * Method getStochasticOscillator.
     *
     * @return BigDecimal
     */
    public BigDecimal getStochasticOscillator() {
        return this.stochasticOscillator;
    }

    /**
     * Method setStochasticOscillator.
     *
     * @param stochasticOscillator BigDecimal
     */
    public void setStochasticOscillator(BigDecimal stochasticOscillator) {
        this.stochasticOscillator = stochasticOscillator;
    }

}
