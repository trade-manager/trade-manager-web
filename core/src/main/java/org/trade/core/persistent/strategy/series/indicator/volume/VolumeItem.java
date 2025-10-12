package org.trade.core.persistent.strategy.series.indicator.volume;

import org.trade.core.persistent.strategy.series.ComparableObjectItem;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;

/**
 * An item representing data in the form (period, open, high, low, close).
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 * @since 1.0.4
 */
public class VolumeItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>CandleItem</code>.
     *
     * @param period the time period.
     * @param volume Long
     * @param side   boolean
     */
    public VolumeItem(RegularTimePeriod period, Long volume, boolean side) {
        super(period, new Volume(volume, side));
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
        return getVolume();
    }

    /**
     * Set the moving Average value.
     *
     * @param volume long
     */
    public void setVolume(long volume) {
        Volume dataItem = (Volume) getObject();
        if (dataItem != null) {
            dataItem.setVolume(volume);
        }

    }

    /**
     * Returns the moving Average value.
     *
     * @return The moving Average value.
     */
    public long getVolume() {
        Volume dataItem = (Volume) getObject();
        if (dataItem != null) {
            if (null == dataItem.getVolume()) {
                return 0;
            }
            return dataItem.getVolume();
        } else {
            return 0;
        }
    }

    /**
     * Method isSide.
     *
     * @return boolean
     */
    public boolean isSide() {
        Volume dataItem = (Volume) getObject();
        return dataItem.isSide();
    }

    /**
     * Method setSide.
     *
     * @param side boolean
     */
    public void setSide(boolean side) {
        Volume dataItem = (Volume) getObject();
        dataItem.setSide(side);
    }
}
