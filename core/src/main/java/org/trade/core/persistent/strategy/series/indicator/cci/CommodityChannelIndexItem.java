package org.trade.core.persistent.strategy.series.indicator.cci;

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
public class CommodityChannelIndexItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period     the time period.
     * @param cciAverage BigDecimal
     */
    public CommodityChannelIndexItem(RegularTimePeriod period, BigDecimal cciAverage) {
        super(period, new CommodityChannelIndex(cciAverage));
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
        return getCommodityChannelIndex();
    }

    /**
     * Set the moving Average value.
     *
     * @param cciAverage double
     */
    public void setCommodityChannelIndex(double cciAverage) {
        CommodityChannelIndex dataItem = (CommodityChannelIndex) getObject();
        if (dataItem != null) {
            dataItem.setCommodityChannelIndex(new BigDecimal(cciAverage));
        }

    }

    /**
     * Returns the moving Average value.
     *
     * @return The moving Average value.
     */
    public double getCommodityChannelIndex() {
        CommodityChannelIndex dataItem = (CommodityChannelIndex) getObject();
        if (dataItem != null) {
            if (null == dataItem.getCommodityChannelIndex()) {
                return 0;
            }
            return dataItem.getCommodityChannelIndex().doubleValue();
        } else {
            return 0;
        }
    }
}
