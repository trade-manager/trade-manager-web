package org.trade.indicator.mfi;

import org.jfree.data.ComparableObjectItem;
import org.trade.core.persistent.strategy.series.indicator.mfi.MoneyFlowIndex;
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
public class MoneyFlowIndexItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period         the time period.
     * @param moneyFlowIndex BigDecimal
     */
    public MoneyFlowIndexItem(RegularTimePeriod period, BigDecimal moneyFlowIndex) {
        super(period, new MoneyFlowIndex(moneyFlowIndex));
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
        return getMoneyFlowIndex();
    }

    /**
     * Set the Money Flow Index value.
     *
     * @param moneyFlowIndex double
     */
    public void setMoneyFlowIndex(double moneyFlowIndex) {
        MoneyFlowIndex dataItem = (MoneyFlowIndex) getObject();
        if (dataItem != null) {
            dataItem.setMoneyFlowIndex(new BigDecimal(moneyFlowIndex));
        }

    }

    /**
     * Returns the Money Flow Index.
     *
     * @return The Money Flow Index value.
     */
    public double getMoneyFlowIndex() {
        MoneyFlowIndex dataItem = (MoneyFlowIndex) getObject();
        if (dataItem != null) {
            if (null == dataItem.getMoneyFlowIndex()) {
                return 0;
            }
            return dataItem.getMoneyFlowIndex().doubleValue();
        } else {
            return 0;
        }
    }
}
