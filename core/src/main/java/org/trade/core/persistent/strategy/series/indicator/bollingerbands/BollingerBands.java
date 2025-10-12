package org.trade.core.persistent.strategy.series.indicator.bollingerbands;

// Generated Feb 21, 2011 12:43:33 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * BollingerBands
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BollingerBands implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal bollingerBands;

    public BollingerBands() {
    }

    /**
     * Constructor for BollingerBands.
     *
     * @param bollingerBands BigDecimal
     */
    public BollingerBands(BigDecimal bollingerBands) {
        this.bollingerBands = bollingerBands;
    }

    /**
     * Method getBollingerBands.
     *
     * @return BigDecimal
     */
    public BigDecimal getBollingerBands() {
        return this.bollingerBands;
    }

    /**
     * Method setBollingerBands.
     *
     * @param bollingerBands BigDecimal
     */
    public void setBollingerBands(BigDecimal bollingerBands) {
        this.bollingerBands = bollingerBands;
    }

}
