package org.trade.core.persistent.dao.series.indicator.pivot;


import org.trade.core.persistent.dao.series.ComparableObjectItem;
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
public class PivotItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period     the time period.
     * @param pivotPrice the pivot price.
     * @param pivotSide  the pivot side.
     */
    public PivotItem(RegularTimePeriod period, BigDecimal pivotPrice, String pivotSide) {
        super(period, new Pivot(pivotPrice, pivotSide));
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
        return getPivotPrice();
    }

    /**
     * Set the privotPrice value.
     *
     * @param pivotPrice double
     */
    public void setPivotPrice(double pivotPrice) {
        Pivot dataItem = (Pivot) getObject();
        if (dataItem != null) {
            dataItem.setPivotPrice(new BigDecimal(pivotPrice));
        }

    }

    /**
     * Returns the pivotPrice value.
     *
     * @return The pivotPrice value.
     */
    public double getPivotPrice() {
        Pivot dataItem = (Pivot) getObject();
        if (dataItem != null) {
            if (null == dataItem.getPivotPrice()) {
                return 0;
            }
            return dataItem.getPivotPrice().doubleValue();
        } else {
            return 0;
        }
    }

    /**
     * Set the pivotSide value.
     *
     * @param pivotSide String
     */
    public void setPivotSide(String pivotSide) {
        Pivot dataItem = (Pivot) getObject();
        if (dataItem != null) {
            dataItem.setPivotSide(pivotSide);
        }

    }

    /**
     * Returns the pivotSide value.
     *
     * @return The pivotSide value.
     */
    public String getPivotSide() {
        Pivot dataItem = (Pivot) getObject();
        if (dataItem != null) {
            return dataItem.getPivotSide();
        } else {
            return null;
        }
    }
}
