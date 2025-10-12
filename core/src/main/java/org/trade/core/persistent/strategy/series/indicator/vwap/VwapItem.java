package org.trade.core.persistent.strategy.series.indicator.vwap;

import org.trade.core.persistent.strategy.series.ComparableObjectItem;
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
public class VwapItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period    the time period.
     * @param vwapPrice BigDecimal
     */
    public VwapItem(RegularTimePeriod period, BigDecimal vwapPrice) {
        super(period, new Vwap(vwapPrice));
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
        return getVwapPrice();
    }

    /**
     * Set the privotPrice value.
     *
     * @param vwapPrice double
     */
    public void setVwapPrice(double vwapPrice) {
        Vwap dataItem = (Vwap) getObject();
        if (dataItem != null) {
            dataItem.setVwapPrice(new BigDecimal(vwapPrice));
        }

    }

    /**
     * Returns the pivotPrice value.
     *
     * @return The pivotPrice value.
     */
    public double getVwapPrice() {
        Vwap dataItem = (Vwap) getObject();
        if (dataItem != null) {
            if (null == dataItem.getVwapPrice()) {
                return 0;
            }
            return dataItem.getVwapPrice().doubleValue();
        } else {
            return 0;
        }
    }
}
