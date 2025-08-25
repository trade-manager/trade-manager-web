package org.trade.core.persistent.dao.series.indicator.macd;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MACD
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class MACD implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal MACD;
    private BigDecimal signalLine;
    private BigDecimal MACDHistogram;

    public MACD() {
    }

    /**
     * Constructor for MACD.
     *
     * @param MACD BigDecimal
     */
    public MACD(BigDecimal MACD, BigDecimal signalLine, BigDecimal MACDHistogram) {
        this.MACD = MACD;
        this.signalLine = signalLine;
        this.MACDHistogram = MACDHistogram;
    }

    /**
     * Method getMACD.
     *
     * @return BigDecimal
     */
    public BigDecimal getMACD() {
        return this.MACD;
    }

    /**
     * Method setMACD.
     *
     * @param MACD BigDecimal
     */
    public void setMACD(BigDecimal MACD) {
        this.MACD = MACD;
    }

    /**
     * Method getSignalLine.
     *
     * @return BigDecimal
     */
    public BigDecimal getSignalLine() {
        return this.signalLine;
    }

    /**
     * Method setSignalLine.
     *
     * @param signalLine BigDecimal
     */
    public void setSignalLine(BigDecimal signalLine) {
        this.signalLine = signalLine;
    }

    /**
     * Method getMACDHistogram.
     *
     * @return BigDecimal
     */
    public BigDecimal getMACDHistogram() {
        return this.MACDHistogram;
    }

    /**
     * Method setMACDHistogram.
     *
     * @param MACDHistogram BigDecimal
     */
    public void setMACDHistogram(BigDecimal MACDHistogram) {
        this.MACDHistogram = MACDHistogram;
    }
}
