package org.trade.core.persistent.dao.series.indicator.vostro;

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
public class VostroItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period the time period.
     * @param vostro BigDecimal
     */
    public VostroItem(RegularTimePeriod period, BigDecimal vostro) {
        super(period, new Vostro(vostro));
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
        return getVostro();
    }

    /**
     * Set the Vostro value.
     *
     * @param vostro double
     */
    public void setVostro(double vostro) {
        Vostro dataItem = (Vostro) getObject();
        if (dataItem != null) {
            dataItem.setVostro(new BigDecimal(vostro));
        }
    }

    /**
     * Returns the Vostro value.
     *
     * @return The Vostro value.
     */
    public double getVostro() {
        Vostro dataItem = (Vostro) getObject();
        if (dataItem != null) {
            if (null == dataItem.getVostro()) {
                return 0;
            }
            return dataItem.getVostro().doubleValue();
        } else {
            return 0;
        }
    }
}
