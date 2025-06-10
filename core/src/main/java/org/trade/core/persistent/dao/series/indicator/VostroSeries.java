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
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;
import org.trade.core.persistent.dao.series.indicator.vostro.VostroItem;
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
@DiscriminatorValue("VostroSeries")
public class VostroSeries extends IndicatorSeries {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    public static final String LENGTH = "Length";
    public static final String MA_TYPE = "MAType";
    public static final String VOSTRO_PERIOD = "Vostro Period";
    public static final String VOSTRO_RANGE = "Vostro Range";
    public static final String PRICE_SOURCE = "Price Source";

    private String MAType;
    private Integer length;
    private Integer vostroPeriod;
    private BigDecimal vostroRange;
    private Integer priceSource;
    /*
     * Vales used to calculate MA's. These need to be reset when the series is
     * cleared.
     */

    private double multiplyer = 0;
    private double sum = 0.0;
    private double vostro1 = Double.MAX_VALUE;
    private double vostro2 = Double.MAX_VALUE;
    private LinkedList<Double> yyValues = new LinkedList<>();
    private LinkedList<Long> volValues = new LinkedList<>();
    private double highPlusLowSum = 0.0;
    private LinkedList<Double> highPlusLowValues = new LinkedList<>();
    private double highLessLowSum = 0.0;
    private LinkedList<Double> highLessLowValues = new LinkedList<>();
    private LinkedList<Double> vostro1Values = new LinkedList<>();
    private LinkedList<Double> vostro2Values = new LinkedList<>();

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
    public VostroSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                        Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    /**
     * Constructor for VostroSeries.
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
    public VostroSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                        Integer chartRGBColor, Boolean subChart, String MAType, Integer length) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
        this.MAType = MAType;
        this.length = length;
    }

    public VostroSeries() {
        super(IndicatorSeries.VostroSeries);
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        VostroSeries clone = (VostroSeries) super.clone();
        clone.yyValues = new LinkedList<>();
        clone.highPlusLowValues = new LinkedList<>();
        clone.highLessLowValues = new LinkedList<>();
        clone.volValues = new LinkedList<>();
        clone.vostro1Values = new LinkedList<>();
        clone.vostro2Values = new LinkedList<>();
        return clone;
    }

    /**
     * Removes all data items from the series and, unless the series is already
     * empty, sends a SeriesChangeEvent to all registered listeners.
     * Clears down and resets all the local calculated fields.
     */
    public void clear() {
        super.clear();
        multiplyer = 0;
        sum = 0.0;
        vostro1 = Double.MAX_VALUE;
        vostro2 = Double.MAX_VALUE;
        yyValues.clear();
        volValues.clear();
        highPlusLowSum = 0.0;
        highPlusLowValues.clear();
        highLessLowSum = 0.0;
        highLessLowValues.clear();
        vostro1Values.clear();
        vostro2Values.clear();
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(int index) {
        final VostroItem item = (VostroItem) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period the period.
     * @param vostro the vostro.
     */
    public void add(RegularTimePeriod period, BigDecimal vostro) throws ServiceException {
        if (!this.isEmpty()) {
            VostroItem item0 = (VostroItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new VostroItem(period, vostro), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem VostroItem
     */
    public void add(VostroItem dataItem, boolean notify) throws ServiceException {
        if (!this.isEmpty()) {
            VostroItem item0 = (VostroItem) this.getDataItem(0);
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
     * Method getVostroPeriod.
     *
     * @return Integer
     */
    @Transient
    public Integer getVostroPeriod() {
        try {
            if (null == this.vostroPeriod)
                this.vostroPeriod = (Integer) CodeValue.getValueCode(VOSTRO_PERIOD, this.getCodeValues());
        } catch (Exception e) {
            this.vostroPeriod = null;
        }
        return this.vostroPeriod;
    }

    /**
     * Method setCostroPeriod.
     *
     * @param vostroPeriod Integer
     */
    public void setVostroPeriod(Integer vostroPeriod) {
        this.vostroPeriod = vostroPeriod;
    }

    /**
     * Method getVostroRange.
     *
     * @return BigDecimal
     */
    @Transient
    public BigDecimal getVostroRange() {
        try {
            if (null == this.vostroRange)
                this.vostroRange = (BigDecimal) CodeValue.getValueCode(VOSTRO_RANGE, this.getCodeValues());
        } catch (Exception e) {
            this.vostroRange = null;
        }
        return this.vostroRange;
    }

    /**
     * Method setVostroRange.
     *
     * @param vostroRange BigDecimal
     */
    public void setVostroRange(BigDecimal vostroRange) {
        this.vostroRange = vostroRange;
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
    public void createSeries(CandleDataset source, int seriesIndex) throws ServiceException {

        if (source.getSeries(seriesIndex) == null) {
            throw new IllegalArgumentException("Null source (CandleDataset).");
        }

        for (int i = 0; i < source.getSeries(seriesIndex).getItemCount(); i++) {
            this.updateSeries(source.getSeries(seriesIndex), i, true);
        }
    }

    /**
     * Method updateSeries. int start() {
     * <p>
     * int gi_144 = 5;
     * <p>
     * int g_period_156 = 100;
     * <p>
     * for (gi_152 = 0; gi_152 < Bars; gi_152++) {
     * <p>
     * gd_120 = 0;
     * <p>
     * for (gi_148 = * gi_152; gi_148 < gi_144 + gi_152; gi_148++) gd_120 +=
     * (High[gi_148] + Low[gi_148]) / 2.0;
     * <p>
     * gd_128 = gd_120 / gi_144; gd_120 = 0;
     * <p>
     * for (gi_148 = gi_152; gi_148 < gi_144 + gi_152; gi_148++) gd_120 +=
     * High[gi_148] - Low[gi_148];
     * <p>
     * gd_136 = (gd_120 / gi_144)/gi_144;
     * <p>
     * g_ibuf_116[gi_152] = (Low[gi_152] - gd_128) / gd_136;
     * <p>
     * g_ibuf_112[gi_152] = (High[gi_152] - gd_128) / gd_136;
     * <p>
     * if (g_ibuf_112[gi_152] > 8.0 && High[gi_152] > iMA(NULL, 0, g_period_156,
     * 0, MODE_LWMA, PRICE_MEDIAN, gi_152)) g_ibuf_108[gi_152] = 90.0;
     * <p>
     * else {
     * <p>
     * if (g_ibuf_116[gi_152] < -8.0 && Low[gi_152] < iMA(NULL, 0, g_period_156,
     * 0, MODE_LWMA, PRICE_MEDIAN, gi_152)) g_ibuf_108[gi_152] = -90.0; else
     * g_ibuf_108[gi_152] = 0.0;
     * <p>
     * }
     * <p>
     * if (g_ibuf_112[gi_152] > 8.0 && g_ibuf_112[gi_152 - 1] > 8.0)
     * g_ibuf_108[gi_152] = 0;
     * <p>
     * if (g_ibuf_112[gi_152] > 8.0 && g_ibuf_112[gi_152 - 1] > 8.0 &&
     * g_ibuf_112[gi_152 - 2] > 8.0) g_ibuf_108[gi_152] = 0;
     * <p>
     * if (g_ibuf_116[gi_152] < -8.0 && g_ibuf_116[gi_152 - 1] < -8.0)
     * g_ibuf_108[gi_152] = 0;
     * <p>
     * if (g_ibuf_116[gi_152] < -8.0 && g_ibuf_116[gi_152 - 1] < -8.0 &&
     * g_ibuf_116[gi_152 - 2] < -8.0) g_ibuf_108[gi_152] = 0; } return (0);
     * <p>
     * }
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public void updateSeries(CandleSeries source, int skip, boolean newBar) throws ServiceException {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }
        if (getLength() == null || getLength() < 1) {
            throw new IllegalArgumentException("MA period must be greater than zero.");
        }
        if (getVostroPeriod() == null || getVostroPeriod() < 1) {
            throw new IllegalArgumentException("Vostro period must be greater than zero.");
        }
        if (getLength() < getVostroPeriod()) {
            throw new IllegalArgumentException("MA period must be greater than Vostro period.");
        }

        if (source.getItemCount() > skip) {
            // get the current data item...
            CandleItem candleItem = (CandleItem) source.getDataItem(skip);
            if (0 != this.getPrice(candleItem)) {
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

                if (this.highPlusLowValues.size() == getVostroPeriod()) {
                    /*
                     * If the item does not exist in the series then this is a
                     * new time period and so we need to remove the last in the
                     * set and add the new periods values. Otherwise we just
                     * update the last value in the set. Sum is just used for
                     * performance save having to sum the last set of values
                     * each time.
                     */

                    if (newBar) {
                        this.highPlusLowSum = this.highPlusLowSum - this.highPlusLowValues.getLast()
                                + (candleItem.getHigh() + candleItem.getLow());
                        this.highPlusLowValues.removeLast();
                        this.highPlusLowValues.addFirst((candleItem.getHigh() + candleItem.getLow()));

                        this.highLessLowSum = this.highLessLowSum - this.highLessLowValues.getLast()
                                + (candleItem.getHigh() - candleItem.getLow());
                        this.highLessLowValues.removeLast();
                        this.highLessLowValues.addFirst((candleItem.getHigh() - candleItem.getLow()));
                    } else {
                        this.highPlusLowSum = this.highPlusLowSum - this.highPlusLowValues.getFirst()
                                + (candleItem.getHigh() + candleItem.getLow());
                        this.highPlusLowValues.removeFirst();
                        this.highPlusLowValues.addFirst((candleItem.getHigh() + candleItem.getLow()));

                        this.highLessLowSum = this.highLessLowSum - this.highLessLowValues.getFirst()
                                + (candleItem.getHigh() - candleItem.getLow());
                        this.highLessLowValues.removeFirst();
                        this.highLessLowValues.addFirst((candleItem.getHigh() - candleItem.getLow()));
                    }
                } else {
                    if (newBar) {
                        this.highPlusLowSum = this.highPlusLowSum + (candleItem.getHigh() + candleItem.getLow());
                        this.highPlusLowValues.addFirst((candleItem.getHigh() + candleItem.getLow()));

                        this.highLessLowSum = this.highLessLowSum + (candleItem.getHigh() - candleItem.getLow());
                        this.highLessLowValues.addFirst((candleItem.getHigh() - candleItem.getLow()));
                    } else {
                        this.highPlusLowSum = this.highPlusLowSum + (candleItem.getHigh() + candleItem.getLow())
                                - this.highPlusLowValues.getFirst();
                        this.highPlusLowValues.removeFirst();
                        this.highPlusLowValues.addFirst((candleItem.getHigh() + candleItem.getLow()));

                        this.highLessLowSum = this.highLessLowSum + (candleItem.getHigh() - candleItem.getLow())
                                - this.highLessLowValues.getFirst();
                        this.highLessLowValues.removeFirst();
                        this.highLessLowValues.addFirst((candleItem.getHigh() - candleItem.getLow()));
                    }
                }

                if (this.yyValues.size() == getLength()) {

                    double ma = calculateMA(this.getMAType(), this.yyValues, this.volValues, sum);

                    double gd_128 = this.highPlusLowSum / 2.0d / this.getVostroPeriod();

                    double gd_136 = (this.highLessLowSum / this.getVostroPeriod()) / this.getVostroPeriod();

                    if (newBar && this.vostro1 != Double.MAX_VALUE) {
                        vostro1Values.addFirst(this.vostro1);
                        vostro2Values.addFirst(this.vostro2);
                        if (vostro1Values.size() > 2) {
                            vostro1Values.removeLast();
                            vostro2Values.removeLast();
                        }
                    }

                    this.vostro1 = (candleItem.getLow() - gd_128) / gd_136;
                    this.vostro2 = (candleItem.getHigh() - gd_128) / gd_136;

                    double vostro;
                    if (vostro2 > this.getVostroRange().doubleValue() && candleItem.getHigh() > ma) {
                        vostro = 90.0;
                    } else {
                        if (vostro1 < (-1 * this.getVostroRange().doubleValue()) && candleItem.getLow() < ma) {
                            vostro = -90.0;
                        } else {
                            vostro = 0.0;
                        }
                    }
                    if (!vostro2Values.isEmpty()) {
                        if (vostro2 > this.getVostroRange().doubleValue()
                                && vostro2Values.getFirst() > this.getVostroRange().doubleValue()) {
                            vostro = 0;
                        }
                    }

                    if (vostro2Values.size() > 1) {
                        if (vostro2 > this.getVostroRange().doubleValue()
                                && vostro2Values.getFirst() > this.getVostroRange().doubleValue()
                                && vostro2Values.getLast() > this.getVostroRange().doubleValue()) {
                            vostro = 0;
                        }
                    }
                    if (!vostro2Values.isEmpty()) {
                        if (vostro1 < (-1 * this.getVostroRange().doubleValue())
                                && vostro1Values.getFirst() < (-1 * this.getVostroRange().doubleValue())) {
                            vostro = 0;
                        }
                    }
                    if (vostro1Values.size() > 1) {
                        if (vostro1 < (-1 * this.getVostroRange().doubleValue())
                                && vostro1Values.getFirst() < (-1 * this.getVostroRange().doubleValue())
                                && vostro1Values.getLast() < (-1 * this.getVostroRange().doubleValue())) {
                            vostro = 0;
                        }
                    }

                    // _log.warn("Vostro Ind Time: " + candleItem.getPeriod()
                    // + " wma: " + ma + " vostro: " + vostro
                    // + " highPlusLowSum: " + this.highPlusLowSum
                    // + " highLessLowSum: " + this.highLessLowSum
                    // + " gd_128: " + gd_128 + " gd_136: " + gd_136
                    // + " vostro1: " + vostro1 + " vostro2: " + vostro2);
                    if (newBar) {
                        VostroItem dataItem = new VostroItem(candleItem.getPeriod(), new BigDecimal(vostro));
                        this.add(dataItem, false);

                    } else {
                        VostroItem dataItem = (VostroItem) this.getDataItem(this.getItemCount() - 1);
                        dataItem.setVostro(vostro);
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
            VostroItem dataItem = (VostroItem) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Value: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getVostro());
        }
    }

    @Transient
    public Vector<Object> getParam(String type) {

        Vector<Object> parms = super.getParam(type);
        parms.add(getLength());
        parms.add(getMAType());
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
            case 1 -> candle.getClose();
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
