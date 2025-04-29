/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.indicator;

import org.jfree.data.general.SeriesChangeEvent;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.indicator.candle.CandleItemUI;
import org.trade.indicator.stochasticoscillator.StochasticOscillatorItemUI;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Fast, Slow or Full
 * <p>
 * There are three versions of the Stochastic Oscillator available on
 * SharpCharts. The Fast Stochastic Oscillator is based on George Lane's
 * original formulas for %K and %D. %K in the fast version that appears rather
 * choppy. %D is the 3-day SMA of %K. In fact, Lane used %D to generate buy or
 * sell signals based on bullish and bearish divergences. Lane asserts that a %D
 * divergence is the "only signal which will cause you to buy or sell." Because
 * %D in the Fast Stochastic Oscillator is used for signals, the Slow Stochastic
 * Oscillator was introduced to reflect this emphasis. The Slow Stochastic
 * Oscillator smooths %K with a 3-day SMA, which is exactly what %D is in the
 * Fast Stochastic Oscillator. Notice that %K in the Slow Stochastic Oscillator
 * equals %D in the Fast Stochastic Oscillator (chart 2).
 * <p>
 * %K = (Current Close - Lowest Low)/(Highest High - Lowest Low) * 100
 * <p>
 * Fast Stochastic Oscillator:
 * <p>
 * Fast %K = %K basic calculation
 * <p>
 * Fast %D = 3-period SMA of Fast %K
 * <p>
 * Slow Stochastic Oscillator:
 * <p>
 * Slow %K = Fast %K smoothed with 3-period SMA
 * <p>
 * Slow %D = 3-period SMA of Slow
 * <p>
 * %K The Full Stochastic Oscillator is a fully customizable version of the Slow
 * Stochastic Oscillator. Users can set the look-back period, the number of
 * periods to slow %K and the number of periods for the %D moving average. The
 * default parameters were used in these examples: Fast Stochastic Oscillator
 * (14,3), Slow Stochastic Oscillator (14,3) and Full Stochastic Oscillator
 * (14,3,3).
 * <p>
 * Full Stochastic Oscillator:
 * <p>
 * Full %K = Fast %K smoothed with X-period SMA Full %D = X-period SMA of Full
 * %K
 * <p>
 * <p>
 * Developed by Larry Williams, Williams %R is a momentum indicator that is the
 * inverse of the Fast Stochastic Oscillator. Also referred to as %R, Williams
 * %R reflects the level of the close relative to the highest high for the
 * look-back period. In contrast, the Stochastic Oscillator reflects the level
 * of the close relative to the lowest low. %R corrects for the inversion by
 * multiplying the raw value by -100. As a result, the Fast Stochastic
 * Oscillator and Williams %R produce the exact same lines, only the scaling is
 * different. Williams %R oscillates from 0 to -100. Readings from 0 to -20 are
 * considered overbought. Readings from -80 to -100 are considered oversold.
 * Unsurprisingly, signals derived from the Stochastic Oscillator are also
 * applicable to Williams %R.
 * <p>
 * %R = (Highest High - Close)/(Highest High - Lowest Low) * -100
 * <p>
 * Lowest Low = lowest low for the look-back period Highest High = highest high
 * for the look-back period %R is multiplied by -100 correct the inversion and
 * move the decimal.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class StochasticOscillatorSeriesUI extends IndicatorSeriesUI {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    public static final String LENGTH = "Length";
    public static final String PERCENT_D = "PercentD";
    public static final String KSMOOTHING = "KSmoothing";
    public static final String INVERSE = "Inverse";

    private Integer length;
    private Integer percentD;
    private Integer kSmoothing;
    private Boolean inverse;
    /*
     * Vales used to calculate StochasticOscillator. These need to be reset when
     * the series is cleared.
     */
    private double sumFullKRValues = 0.0;
    private double sumFullDValues = 0.0;
    private LinkedList<Double> yyValues = new LinkedList<>();
    private LinkedList<Double> fullKRValues = new LinkedList<>();
    private LinkedList<Double> fullDValues = new LinkedList<>();

    /**
     * Creates a new empty series. By default, items added to the series will be
     * sorted into ascending order by period, and duplicate periods will not be
     * allowed.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     */
    public StochasticOscillatorSeriesUI(Strategy strategy, String name, String type, String description,
                                        Boolean displayOnChart, Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    /**
     * Constructor for StochasticOscillatorSeries.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     * @param length         Integer
     * @param kSmoothing     Integer
     * @param percentD       Integer
     */
    public StochasticOscillatorSeriesUI(Strategy strategy, String name, String type, String description,
                                        Boolean displayOnChart, Integer chartRGBColor, Boolean subChart, Integer length, Integer kSmoothing,
                                        Integer percentD) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
        this.length = length;
        this.kSmoothing = kSmoothing;
        this.percentD = percentD;
    }

    public StochasticOscillatorSeriesUI() {
        super(StochasticOscillatorSeries);
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        StochasticOscillatorSeriesUI clone = (StochasticOscillatorSeriesUI) super.clone();
        clone.yyValues = new LinkedList<>();
        clone.fullKRValues = new LinkedList<>();
        clone.fullDValues = new LinkedList<>();
        return clone;
    }

    /**
     * Removes all data items from the series and, unless the series is already
     * empty, sends a {@link SeriesChangeEvent} to all registered listeners.
     * Clears down and resets all the local calculated fields.
     */
    public void clear() {
        super.clear();
        sumFullKRValues = 0.0;
        sumFullDValues = 0.0;
        yyValues.clear();
        fullKRValues.clear();
        fullDValues.clear();
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final StochasticOscillatorItemUI item = (StochasticOscillatorItemUI) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period               the period.
     * @param stochasticOscillator the StochasticOscillator.
     */
    public void add(RegularTimePeriod period, BigDecimal stochasticOscillator) {
        if (!this.isEmpty()) {
            StochasticOscillatorItemUI item0 = (StochasticOscillatorItemUI) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new StochasticOscillatorItemUI(period, stochasticOscillator), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem StochasticOscillatorItem
     */
    public void add(StochasticOscillatorItemUI dataItem, boolean notify) {
        if (!this.isEmpty()) {
            StochasticOscillatorItemUI item0 = (StochasticOscillatorItemUI) this.getDataItem(0);
            if (!dataItem.getPeriod().getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(dataItem, notify);
    }

    /**
     * Method getLength.
     *
     * @return Integer
     */
    public Integer getLength() {
        try {
            if (null == this.length)
                this.length = (Integer) CodeValue.getValueCode(LENGTH, this.getCodeValues());
            if (this.length < 1)
                this.length = 1;
        } catch (Exception e) {
            this.length = 1;
        }
        return this.length;
    }

    /**
     * Method setLength.
     *
     * @param length Integer
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * Method getPercentD.
     *
     * @return Integer
     */
    public Integer getPercentD() {
        try {
            if (null == this.percentD)
                this.percentD = (Integer) CodeValue.getValueCode(PERCENT_D, this.getCodeValues());
            if (this.percentD < 1)
                this.percentD = 1;
        } catch (Exception e) {
            this.percentD = 1;
        }
        return this.percentD;
    }

    /**
     * Method setPercentD.
     *
     * @param percentD Integer
     */
    public void setPercentD(Integer percentD) {
        this.percentD = percentD;
    }

    /**
     * Method getKSmoothing.
     *
     * @return Integer
     */
    public Integer getKSmoothing() {
        try {
            if (null == this.kSmoothing)
                this.kSmoothing = (Integer) CodeValue.getValueCode(KSMOOTHING, this.getCodeValues());
            if (this.kSmoothing < 1)
                this.kSmoothing = 1;
        } catch (Exception e) {
            this.kSmoothing = 1;
        }
        return this.kSmoothing;
    }

    /**
     * Method setKSmoothing.
     *
     * @param kSmoothing Integer
     */
    public void setSmoothing(Integer kSmoothing) {
        this.kSmoothing = kSmoothing;
    }

    /**
     * Method getInverse.
     *
     * @return Boolean
     */
    public Boolean getInverse() {
        try {
            if (null == this.inverse)
                this.inverse = (Boolean) CodeValue.getValueCode(INVERSE, this.getCodeValues());
        } catch (Exception e) {
            this.inverse = null;
        }
        return this.inverse;
    }

    /**
     * Method setInverse.
     *
     * @param inverse Boolean
     */
    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public void createSeries(CandleDatasetUI source, int seriesIndex) {

        if (source.getSeries(seriesIndex) == null) {
            throw new IllegalArgumentException("Null source (CandleDataset).");
        }

        for (int i = 0; i < source.getSeries(seriesIndex).getItemCount(); i++) {
            this.updateSeries(source.getSeries(seriesIndex), i, true);
        }
    }

    /**
     * Method updateSeries.
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public void updateSeries(CandleSeriesUI source, int skip, boolean newBar) {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }
        if (getLength() == null || getLength() < 1) {
            throw new IllegalArgumentException("SMA period must be greater than zero.");
        }

        if (source.getItemCount() > skip) {
            // get the current data item...
            CandleItemUI candleItem = (CandleItemUI) source.getDataItem(skip);
            if (0 != candleItem.getClose()) {
                if (this.yyValues.size() == getLength()) {
                    /*
                     * If the item does not exist in the series then this is a
                     * new time period and so we need to remove the last in the
                     * set and add the new periods values. Otherwise we just
                     * update the last value in the set. Sum is just used for
                     * performance save having to sum the last set of values
                     * each time.
                     */
                    if (newBar) {
                        this.yyValues.removeLast();
                        this.yyValues.addFirst(candleItem.getClose());
                    } else {
                        this.yyValues.removeFirst();
                        this.yyValues.addFirst(candleItem.getClose());
                    }
                } else {
                    if (newBar) {
                        this.yyValues.addFirst(candleItem.getClose());
                    } else {
                        this.yyValues.removeFirst();
                        this.yyValues.addFirst(candleItem.getClose());
                    }
                }

                if (this.yyValues.size() == getLength()) {

                    double high = Collections.max(this.yyValues);
                    double low = Collections.min(this.yyValues);

                    /*
                     * %K = (Current Close - Lowest Low)/(Highest High - Lowest
                     * Low) * 100
                     *
                     * %D = 3-day SMA of %K
                     *
                     * Lowest Low = lowest low for the look-back period Highest
                     * High = highest high for the look-back period %K is
                     * multiplied by 100 to move the decimal point two places
                     */
                    double fastKR = 0;
                    if ((high - low) > 0)
                        fastKR = ((candleItem.getClose() - low) / (high - low)) * 100;

                    if (this.getInverse()) {
                        /*
                         * %R = (Highest High - Close)/(Highest High - Lowest
                         * Low) * -100
                         *
                         * Lowest Low = lowest low for the look-back period
                         * Highest High = highest high for the look-back period
                         * %R is multiplied by -100 correct the inversion and
                         * move the decimal.
                         */
                        fastKR = ((high - candleItem.getClose()) / (high - low)) * -100;
                    }

                    if (this.fullKRValues.size() == this.getKSmoothing()) {
                        /*
                         * If the item does not exist in the series then this is
                         * a new time period and so we need to remove the last
                         * in the set and add the new periods values. Otherwise
                         * we just update the last value in the set. Sum is just
                         * used for performance save having to sum the last set
                         * of values each time.
                         */
                        if (newBar) {
                            sumFullKRValues = sumFullKRValues - this.fullKRValues.getLast() + fastKR;
                            this.fullKRValues.removeLast();
                            this.fullKRValues.addFirst(fastKR);
                        } else {
                            sumFullKRValues = sumFullKRValues - this.fullKRValues.getFirst() + fastKR;
                            this.fullKRValues.removeFirst();
                            this.fullKRValues.addFirst(fastKR);
                        }
                    } else {
                        if (newBar) {
                            sumFullKRValues = sumFullKRValues + fastKR;
                            this.fullKRValues.addFirst(fastKR);
                        } else {
                            sumFullKRValues = sumFullKRValues + fastKR - this.fullKRValues.getFirst();
                            this.fullKRValues.removeFirst();
                            this.fullKRValues.addFirst(fastKR);
                        }
                    }
                    if (this.fullKRValues.size() == this.getKSmoothing()) {

                        double fullKR = sumFullKRValues / this.getKSmoothing();

                        if (this.fullDValues.size() == this.getPercentD()) {
                            /*
                             * If the item does not exist in the series then
                             * this is a new time period and so we need to
                             * remove the last in the set and add the new
                             * periods values. Otherwise we just update the last
                             * value in the set. Sum is just used for
                             * performance save having to sum the last set of
                             * values each time.
                             */
                            if (newBar) {
                                sumFullDValues = sumFullDValues - this.fullDValues.getLast() + fullKR;
                                this.fullDValues.removeLast();
                                this.fullDValues.addFirst(fullKR);
                            } else {
                                sumFullDValues = sumFullDValues - this.fullDValues.getFirst() + fullKR;
                                this.fullDValues.removeFirst();
                                this.fullDValues.addFirst(fullKR);
                            }
                        } else {
                            if (newBar) {
                                sumFullDValues = sumFullDValues + fullKR;
                                this.fullDValues.addFirst(fullKR);
                            } else {
                                sumFullDValues = sumFullDValues + fullKR - this.fullDValues.getFirst();
                                this.fullDValues.removeFirst();
                                this.fullDValues.addFirst(fullKR);
                            }
                        }
                        if (this.fullDValues.size() == this.getPercentD()) {
                            double fullD = sumFullDValues / this.getPercentD();
                            if (newBar) {
                                StochasticOscillatorItemUI dataItem = new StochasticOscillatorItemUI(candleItem.getPeriod(),
                                        new BigDecimal(fullD));
                                this.add(dataItem, false);

                            } else {
                                StochasticOscillatorItemUI dataItem = (StochasticOscillatorItemUI) this
                                        .getDataItem(this.getItemCount() - 1);
                                dataItem.setStochasticOscillator(fullD);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method printSeries.
     */
    public void printSeries() {
        for (int i = 0; i < this.getItemCount(); i++) {
            StochasticOscillatorItemUI dataItem = (StochasticOscillatorItemUI) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Value: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getStochasticOscillator());
        }
    }
}
