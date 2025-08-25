package org.trade.indicator.rsi;

import org.jfree.data.ComparableObjectItem;
import org.trade.core.persistent.dao.series.indicator.rsi.RelativeStrengthIndex;
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
public class RelativeStrengthIndexItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>RelativeStrengthIndexItem</code>.
     *
     * @param period                the time period.
     * @param relativeStrengthIndex the relativeStrengthIndex.
     */
    public RelativeStrengthIndexItem(RegularTimePeriod period, BigDecimal relativeStrengthIndex) {
        super(period, new RelativeStrengthIndex(relativeStrengthIndex));
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
        return getRelativeStrengthIndex();
    }

    /**
     * Set the relativeStrengthIndex value.
     *
     * @param relativeStrengthIndex double
     */
    public void setRelativeStrengthIndex(double relativeStrengthIndex) {
        RelativeStrengthIndex dataItem = (RelativeStrengthIndex) getObject();
        if (dataItem != null) {
            dataItem.setRelativeStrengthIndex(new BigDecimal(relativeStrengthIndex));
        }

    }

    /**
     * Returns the relativeStrengthIndex value.
     *
     * @return The relativeStrengthIndex value.
     */
    public double getRelativeStrengthIndex() {
        RelativeStrengthIndex dataItem = (RelativeStrengthIndex) getObject();
        if (dataItem != null) {
            if (null == dataItem.getRelativeStrengthIndex()) {
                return 0;
            }
            return dataItem.getRelativeStrengthIndex().doubleValue();
        } else {
            return 0;
        }
    }
}
