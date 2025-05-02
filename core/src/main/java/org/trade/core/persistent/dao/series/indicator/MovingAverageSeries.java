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
package org.trade.core.persistent.dao.series.indicator;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import org.trade.core.persistent.PersistentModelException;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.movingaverage.MovingAverageItem;
import org.trade.core.util.time.RegularTimePeriod;
import org.trade.core.valuetype.CalculationType;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Vector;

/**
 * A list of (RegularTimePeriod, open, high, low, close) data items.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

@Entity
@DiscriminatorValue("MovingAverageSeries")
public class MovingAverageSeries extends IndicatorSeries {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    public static final String LENGTH = "Length";
    public static final String MA_TYPE = "MAType";
    public static final String PRICE_SOURCE = "Price Source";

    private Integer priceSource;
    private String MAType;
    private Integer length;
    /*
     * Vales used to calculate MA's. These need to be reset when the series is
     * cleared.
     */
    private double sum = 0.0;
    private double multiplyer = 0;
    private LinkedList<Double> yyValues = new LinkedList<>();
    private LinkedList<Long> volValues = new LinkedList<>();

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
    public MovingAverageSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                               Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    /**
     * Constructor for MovingAverageSeries.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     * @param MAType         String
     * @param length         Integer
     */
    public MovingAverageSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                               Integer chartRGBColor, Boolean subChart, String MAType, Integer length, Integer priceSource) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
        this.MAType = MAType;
        this.length = length;
        this.priceSource = priceSource;
    }

    public MovingAverageSeries() {
        super(IndicatorSeries.MovingAverageSeries);
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        MovingAverageSeries clone = (MovingAverageSeries) super.clone();
        clone.yyValues = new LinkedList<>();
        clone.volValues = new LinkedList<>();
        return clone;
    }

    /**
     * Removes all data items from the series and, unless the series is already
     * empty, sends a SeriesChangeEvent to all registered listeners.
     * Clears down and resets all the local calculated fields.
     */
    public void clear() {
        super.clear();
        sum = 0.0;
        multiplyer = 0;
        yyValues.clear();
        volValues.clear();
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final MovingAverageItem item = (MovingAverageItem) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period        the period.
     * @param movingAverage the movingAverage.
     */
    public void add(RegularTimePeriod period, BigDecimal movingAverage) throws PersistentModelException {
        if (!this.isEmpty()) {
            MovingAverageItem item0 = (MovingAverageItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new MovingAverageItem(period, movingAverage), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem MovingAverageItem
     */
    public void add(MovingAverageItem dataItem, boolean notify) throws PersistentModelException {
        if (!this.isEmpty()) {
            MovingAverageItem item0 = (MovingAverageItem) this.getDataItem(0);
            if (!dataItem.getPeriod().getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(dataItem, notify);
    }

    /**
     * Method getPriceSource.
     *
     * @return Integer
     */
    @Transient
    public Integer getPriceSource() {
        try {
            if (null == this.priceSource)
                this.priceSource = (Integer) CodeValue.getValueCode(PRICE_SOURCE, this.getCodeValues());
        } catch (Exception e) {
            this.priceSource = null;
        }
        return this.priceSource;
    }

    /**
     * Method setMAType.
     *
     * @param priceSource Integer
     */
    public void setPriceSource(Integer priceSource) {
        this.priceSource = priceSource;
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
     * Method getMAType.
     *
     * @return String
     */
    @Transient
    public String getMAType() {
        try {
            if (null == this.MAType)
                this.MAType = (String) CodeValue.getValueCode(MA_TYPE, this.getCodeValues());
        } catch (Exception e) {
            this.MAType = null;
        }
        return this.MAType;
    }

    /**
     * Method setMAType.
     *
     * @param MAType String
     */
    public void setMAType(String MAType) {
        this.MAType = MAType;
    }

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public void createSeries(CandleDataset source, int seriesIndex) throws PersistentModelException {

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
    public void updateSeries(CandleSeries source, int skip, boolean newBar) throws PersistentModelException {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }
        if (getLength() == null || getLength() < 1) {
            throw new IllegalArgumentException("MA period must be greater than zero.");
        }

        if (source.getItemCount() > skip) {
            // get the current data item...
            CandleItem candleItem = (CandleItem) source.getDataItem(skip);
            if (0 != candleItem.getClose()) {
                double price = this.getPrice(candleItem);
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
                        sum = sum - this.yyValues.getLast() + price;
                        this.yyValues.removeLast();
                        this.yyValues.addFirst(price);
                        this.volValues.removeLast();
                        this.volValues.addFirst(candleItem.getVolume());
                    } else {
                        sum = sum - this.yyValues.getFirst() + price;
                        this.yyValues.removeFirst();
                        this.yyValues.addFirst(price);
                    }
                } else {
                    if (newBar) {
                        sum = sum + price;
                        this.yyValues.addFirst(price);
                        this.volValues.addFirst(candleItem.getVolume());
                    } else {
                        sum = sum + price - this.yyValues.getFirst();
                        this.yyValues.removeFirst();
                        this.yyValues.addFirst(price);
                        this.volValues.removeFirst();
                        this.volValues.addFirst(candleItem.getVolume());
                    }
                }

                if (this.yyValues.size() == getLength()) {
                    double ma = calculateMA(this.getMAType(), this.yyValues, this.volValues, sum);
                    if (newBar) {
                        MovingAverageItem dataItem = new MovingAverageItem(candleItem.getPeriod(), new BigDecimal(ma));
                        this.add(dataItem, false);

                    } else {
                        MovingAverageItem dataItem = (MovingAverageItem) this.getDataItem(this.getItemCount() - 1);
                        dataItem.setMovingAverage(ma);
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
            MovingAverageItem dataItem = (MovingAverageItem) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Value: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getMovingAverage());
        }
    }

    @Transient
    public Vector<Object> getParam() {

        Vector<Object> parms = super.getParam();
        parms.add(getMAType());
        parms.add(getLength());
        parms.add(getPriceSource());
        return parms;
    }

    /**
     * Method calculateMA.
     *
     * @param calcType  String
     * @param yyValues  LinkedList<Double>
     * @param volValues LinkedList<Long>
     * @param sum       Double
     * @return double
     */
    private double calculateMA(String calcType, LinkedList<Double> yyValues, LinkedList<Long> volValues, Double sum) {

        double ma = 0;
        if (CalculationType.LINEAR.equals(calcType)) {
            ma = sum / getLength();
        } else if (CalculationType.EXPONENTIAL.equals(calcType)) {
            /*
             * Multiplier: (2 / (Time periods + 1) ) = (2 / (10 + 1) ) = 0.1818
             * (18.18%). EMA: {Close - EMA(previous day)} x * multiplier +
             * EMA(previous day).
             */
            if (multiplyer == 0) {
                ma = sum / getLength();
                multiplyer = 2 / (getLength() + 1.0d);
            } else {
                ma = ((yyValues.getFirst() - yyValues.get(1)) * multiplyer) + yyValues.get(1);
            }
            /*
             * Use the EMA in the stored values as we need the previous one for
             * the calc.
             */
            yyValues.removeFirst();
            yyValues.addFirst(ma);

        } else if (CalculationType.WEIGHTED.equals(calcType)) {

            double sumYY = 0;
            int count = 0;
            for (int i = yyValues.size(); i > 0; i--) {
                count = count + (getLength() + 1 - i);
                sumYY = sumYY + (yyValues.get(i - 1) * (getLength() + 1 - i));
            }
            ma = sumYY / count;

        } else if (CalculationType.WEIGHTED_VOLUME.equals(calcType)) {

            double sumYY = 0;
            double count = 0;
            for (int i = yyValues.size(); i > 0; i--) {
                count = count + ((getLength() + 1 - i) * volValues.get(i - 1));
                sumYY = sumYY + (yyValues.get(i - 1) * volValues.get(i - 1) * (getLength() + 1 - i));
            }
            ma = sumYY / count;
        } else if (CalculationType.TRIANGULAR.equals(calcType)) {

            double sumYY = 0;
            int count = 0;
            int half = getLength() / 2;
            int y = 0;
            for (int x = 1; x <= half; x++) {
                sumYY = sumYY + (yyValues.get(y) * x);
                count = count + x;
                y++;
            }
            if ((getLength() % 2) != 0) {
                int z = half + 1;
                sumYY = sumYY + (yyValues.get(y) * z);
                count = count + z;
                y++;
            }
            for (int x = half; x >= 1; x--) {
                sumYY = sumYY + (yyValues.get(y) * x);
                count = count + x;
                y++;
            }
            ma = sumYY / count;
        }
        return ma;
    }

    /**
     * Method get the price.
     *
     * @param candle CandleItem
     * @return double
     */
    private double getPrice(CandleItem candle) {

        return switch (this.getPriceSource()) {
            case 2 -> candle.getOpen();
            case 3 -> candle.getHigh();
            case 4 -> candle.getLow();
            case 5 -> (candle.getHigh() + candle.getLow()) / 2.0d;
            case 6 -> (candle.getHigh() + candle.getLow() + candle.getClose()) / 3.0d;
            case 7 -> (candle.getOpen() + candle.getHigh() + candle.getLow() + candle.getClose()) / 4.0d;
            default -> candle.getClose();
        };
    }
}
