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
package org.trade.strategy.data;

import jakarta.persistence.Transient;
import org.jfree.data.general.SeriesChangeEvent;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.strategy.data.bollingerbands.BollingerBandsItem;
import org.trade.strategy.data.candle.CandleItem;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Developed by John Bollinger, Bollinger Bands are volatility bands placed
 * above and below a moving average. Volatility is based on the standard
 * deviation, which changes as volatility increases and decreases. The bands
 * automatically widen when volatility increases and narrow when volatility
 * decreases. This dynamic nature of Bollinger Bands also means they can be used
 * on different securities with the standard settings. For signals, Bollinger
 * Bands can be used to identify M-Tops and W-Bottoms or to determine the
 * strength of the trend.
 * <p>
 * Middle Band = 20-day simple moving average (SMA)
 * <p>
 * Upper Band = 20-day SMA + (20-day standard deviation of price x 2)
 * <p>
 * Lower Band = 20-day SMA - (20-day standard deviation of price x 2)
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class BollingerBandsSeries extends IndicatorSeries {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    public static final String LENGTH = "Length";
    public static final String NUMBER_OF_STD = "NumberOfSTD";

    private BigDecimal numberOfSTD;
    private Integer length;
    private boolean isUpper;
    /*
     * Vales used to calculate MA's. These need to be reset when the series is
     * cleared.
     */
    private double sum = 0.0;
    private LinkedList<Double> yyValues = new LinkedList<>();

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
    public BollingerBandsSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                                Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    /**
     * Constructor for BollingerBandsSeries.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     * @param numberOfSTD    BigDecimal
     * @param length         Integer
     */
    public BollingerBandsSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                                Integer chartRGBColor, Boolean subChart, BigDecimal numberOfSTD, Integer length) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
        this.numberOfSTD = numberOfSTD;
        this.length = length;
    }

    public BollingerBandsSeries() {
        super(IndicatorSeries.BollingerBandsSeries);
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        BollingerBandsSeries clone = (BollingerBandsSeries) super.clone();
        clone.yyValues = new LinkedList<>();
        return clone;
    }

    /**
     * Removes all data items from the series and, unless the series is already
     * empty, sends a {@link SeriesChangeEvent} to all registered listeners.
     * Clears down and resets all the local calculated fields.
     */
    public void clear() {
        super.clear();
        sum = 0.0;
        yyValues.clear();
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final BollingerBandsItem item = (BollingerBandsItem) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period         the period.
     * @param bollingerBands the bollingerBands.
     */
    public void add(RegularTimePeriod period, BigDecimal bollingerBands) {
        if (!this.isEmpty()) {
            BollingerBandsItem item0 = (BollingerBandsItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new BollingerBandsItem(period, bollingerBands), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem BollingerBandsItem
     */
    public void add(BollingerBandsItem dataItem, boolean notify) {
        if (!this.isEmpty()) {
            BollingerBandsItem item0 = (BollingerBandsItem) this.getDataItem(0);
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
    @Transient
    public Integer getLength() {
        try {
            if (null == this.length)
                this.length = (Integer) CodeValue.getValueCode(LENGTH, this.getCodeValues());
        } catch (Exception e) {
            this.length = null;
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
     * Method getNumberOfSTD.
     *
     * @return BigDecimal
     */
    @Transient
    public BigDecimal getNumberOfSTD() {
        try {
            if (null == this.numberOfSTD)
                this.numberOfSTD = (BigDecimal) CodeValue.getValueCode(NUMBER_OF_STD, this.getCodeValues());
        } catch (Exception e) {
            this.numberOfSTD = null;
        }
        return this.numberOfSTD;
    }

    /**
     * Method setNumberOfSTD.
     *
     * @param numberOfSTD BigDecimal
     */
    public void setNumberOfSTD(BigDecimal numberOfSTD) {
        this.numberOfSTD = numberOfSTD;
    }

    /**
     * Method getIsUpper.
     *
     * @return boolean
     */
    @Transient
    public boolean getIsUpper() {
        return this.isUpper;
    }

    /**
     * Method setLength.
     *
     * @param isUpper boolean
     */
    public void setIsUpper(boolean isUpper) {
        this.isUpper = isUpper;
    }

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public void createSeries(CandleDataset source, int seriesIndex) {

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
    public void updateSeries(CandleSeries source, int skip, boolean newBar) {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }
        if (getLength() == null || getLength() < 1) {
            throw new IllegalArgumentException("MA period must be greater than zero.");
        }

        if (getNumberOfSTD() == null || getNumberOfSTD().doubleValue() < 1) {
            throw new IllegalArgumentException("Number of STD's must be greater than zero.");
        }

        if (source.getItemCount() > skip) {

            // get the current data item...
            CandleItem candleItem = (CandleItem) source.getDataItem(skip);

            // work out the average for the earlier values...
            Number yy = candleItem.getY();

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
                    sum = sum - this.yyValues.getLast() + yy.doubleValue();
                    this.yyValues.removeLast();
                    this.yyValues.addFirst(yy.doubleValue());

                } else {
                    sum = sum - this.yyValues.getFirst() + yy.doubleValue();
                    this.yyValues.removeFirst();
                    this.yyValues.addFirst(yy.doubleValue());
                }
            } else {
                if (newBar) {
                    sum = sum + yy.doubleValue();
                    this.yyValues.addFirst(yy.doubleValue());
                } else {
                    sum = sum + yy.doubleValue() - this.yyValues.getFirst();
                    this.yyValues.removeFirst();
                    this.yyValues.addFirst(yy.doubleValue());
                }
            }

            if (this.yyValues.size() == getLength()) {
                double ma = calculateBBands(this.getNumberOfSTD(), this.yyValues, sum);
                if (newBar) {
                    BollingerBandsItem dataItem = new BollingerBandsItem(candleItem.getPeriod(),
                            new BigDecimal(ma));
                    this.add(dataItem, false);

                } else {
                    BollingerBandsItem dataItem = (BollingerBandsItem) this.getDataItem(this.getItemCount() - 1);
                    dataItem.setBollingerBands(ma);
                }
            }
        }
    }

    /**
     * Method calculateMA.
     *
     * @param numberOfSTD BigDecimal
     * @param yyValues    LinkedList<Double>
     * @param sum         Double
     * @return double
     */
    private double calculateBBands(BigDecimal numberOfSTD, LinkedList<Double> yyValues, Double sum) {

        if (this.isUpper) {
            return ((sum / this.getLength())
                    + (standardDeviation(yyValues, sum) * this.getNumberOfSTD().doubleValue()));
        } else {
            return ((sum / this.getLength())
                    - (standardDeviation(yyValues, sum) * this.getNumberOfSTD().doubleValue()));
        }
    }

    public double standardDeviation(LinkedList<Double> a, Double sum) {

        double sumTotal = 0;
        double mean = sum / (a.size() * 1.0);
        for (Double i : a)
            sumTotal += Math.pow((i - mean), 2);
        return Math.sqrt(sumTotal / (a.size() - 1)); // sample
    }

    /**
     * Method printSeries.
     */
    public void printSeries() {
        for (int i = 0; i < this.getItemCount(); i++) {
            BollingerBandsItem dataItem = (BollingerBandsItem) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Value: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getBollingerBands());
        }
    }
}
