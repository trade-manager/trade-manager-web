package org.trade.core.persistent.dao.series.indicator.mfi;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MoneyFlowIndex
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class MoneyFlowIndex implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal moneyFlowIndex;

    public MoneyFlowIndex() {
    }

    /**
     * Constructor for MoneyFlowIndex.
     *
     * @param moneyFlowIndex BigDecimal
     */
    public MoneyFlowIndex(BigDecimal moneyFlowIndex) {
        this.moneyFlowIndex = moneyFlowIndex;
    }

    /**
     * Method getMoneyFlowIndex.
     *
     * @return BigDecimal
     */
    public BigDecimal getMoneyFlowIndex() {
        return this.moneyFlowIndex;
    }

    /**
     * Method setMoneyFlowIndex.
     *
     * @param moneyFlowIndex BigDecimal
     */
    public void setMoneyFlowIndex(BigDecimal moneyFlowIndex) {
        this.moneyFlowIndex = moneyFlowIndex;
    }

}
