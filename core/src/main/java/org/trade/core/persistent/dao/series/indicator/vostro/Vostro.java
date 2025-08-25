package org.trade.core.persistent.dao.series.indicator.vostro;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Vostro
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class Vostro implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal vostro;

    public Vostro() {
    }

    /**
     * Constructor for Vostro.
     *
     * @param vostro BigDecimal
     */
    public Vostro(BigDecimal vostro) {
        this.vostro = vostro;
    }

    /**
     * Method getVostro.
     *
     * @return BigDecimal
     */
    public BigDecimal getVostro() {
        return this.vostro;
    }

    /**
     * Method setVostro.
     *
     * @param vostro BigDecimal
     */
    public void setVostro(BigDecimal vostro) {
        this.vostro = vostro;
    }
}
