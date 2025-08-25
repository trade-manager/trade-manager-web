package org.trade.indicator.macd;

import org.jfree.data.ComparableObjectItem;
import org.trade.core.persistent.dao.series.indicator.macd.MACD;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * An item representing data for MACD.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 * @since 1.0.4
 */
public class MACDItem extends ComparableObjectItem {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3888996139640449109L;

    /**
     * Creates a new instance of <code>MACDItem</code>.
     *
     * @param period the time period.
     * @param MACD   BigDecimal
     */
    public MACDItem(RegularTimePeriod period, BigDecimal MACD, BigDecimal signalLine, BigDecimal MACDHistogram) {
        super(period, new MACD(MACD, signalLine, MACDHistogram));
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
        return getMACD();
    }

    /**
     * Set the MACD value.
     *
     * @param MACD double
     */
    public void setMACD(double MACD) {
        MACD dataItem = (MACD) getObject();
        if (dataItem != null) {
            dataItem.setMACD(new BigDecimal(MACD));
        }

    }

    /**
     * Returns the MACD value.
     *
     * @return The MACD value.
     */
    public double getMACD() {
        MACD dataItem = (MACD) getObject();
        if (dataItem != null) {
            if (null == dataItem.getMACD()) {
                return 0;
            }
            return dataItem.getMACD().doubleValue();
        } else {
            return 0;
        }
    }

    /**
     * Set the SignalLine value.
     *
     * @param SignalLine double
     */
    public void setSignalLine(double SignalLine) {
        MACD dataItem = (MACD) getObject();
        if (dataItem != null) {
            dataItem.setSignalLine(new BigDecimal(SignalLine));
        }

    }

    /**
     * Returns the SignalLine value.
     *
     * @return The SignalLine value.
     */
    public double getSignalLine() {
        MACD dataItem = (MACD) getObject();
        if (dataItem != null) {
            if (null == dataItem.getSignalLine()) {
                return 0;
            }
            return dataItem.getSignalLine().doubleValue();
        } else {
            return 0;
        }
    }

    /**
     * Set the MACDHistogram value.
     *
     * @param MACD double
     */
    public void setMACDHistogram(double MACD) {
        MACD dataItem = (MACD) getObject();
        if (dataItem != null) {
            dataItem.setMACDHistogram(new BigDecimal(MACD));
        }

    }

    /**
     * Returns the MACDHistogram value.
     *
     * @return The MACDHistogram value.
     */
    public double getMACDHistogram() {
        MACD dataItem = (MACD) getObject();
        if (dataItem != null) {
            if (null == dataItem.getMACDHistogram()) {
                return 0;
            }
            return dataItem.getMACDHistogram().doubleValue();
        } else {
            return 0;
        }
    }
}
