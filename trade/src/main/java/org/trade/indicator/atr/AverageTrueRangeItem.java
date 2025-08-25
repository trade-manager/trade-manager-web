package org.trade.indicator.atr;

import org.jfree.data.ComparableObjectItem;
import org.trade.core.persistent.dao.series.indicator.atr.AverageTrueRange;
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
public class AverageTrueRangeItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period           the time period.
     * @param averageTrueRange the AverageTrueRange.
     */
    public AverageTrueRangeItem(RegularTimePeriod period, BigDecimal averageTrueRange) {
        super(period, new AverageTrueRange(averageTrueRange));
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
        return getAverageTrueRange();
    }

    /**
     * Set the averageTrueRange value.
     *
     * @param averageTrueRange double
     */
    public void setAverageTrueRange(double averageTrueRange) {
        AverageTrueRange dataItem = (AverageTrueRange) getObject();
        if (dataItem != null) {
            dataItem.setAverageTrueRange(new BigDecimal(averageTrueRange));
        }

    }

    /**
     * Returns the averageTrueRange value.
     *
     * @return The averageTrueRange value.
     */
    public double getAverageTrueRange() {
        AverageTrueRange dataItem = (AverageTrueRange) getObject();
        if (dataItem != null) {
            if (null == dataItem.getAverageTrueRange()) {
                return 0;
            }
            return dataItem.getAverageTrueRange().doubleValue();
        } else {
            return 0;
        }
    }
}
