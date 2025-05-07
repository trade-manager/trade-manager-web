/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
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

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class IndicatorSeries extends ComparableObjectSeries implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = -4985280367851073683L;

    protected final static Logger _log = LoggerFactory.getLogger(IndicatorSeries.class);

    /*
     * These names must match the names of the classes for that series.
     */
    public static final String MovingAverageSeries = "MovingAverageSeries";
    public static final String PivotSeries = "PivotSeries";
    public static final String HeikinAshiSeries = "HeikinAshiSeries";
    public static final String VwapSeries = "VwapSeries";
    public static final String VolumeSeries = "VolumeSeries";
    public static final String CandleSeries = "CandleSeries";
    public static final String AverageTrueRangeSeries = "AverageTrueRangeSeries";
    public static final String RelativeStrengthIndexSeries = "RelativeStrengthIndexSeries";
    public static final String CommodityChannelIndexSeries = "CommodityChannelIndexSeries";
    public static final String BollingerBandsSeries = "BollingerBandsSeries";
    public static final String StochasticOscillatorSeries = "StochasticOscillatorSeries";
    public static final String MoneyFlowIndexSeries = "MoneyFlowIndexSeries";
    public static final String MACDSeries = "MACDSeries";
    public static final String VostroSeries = "VostroSeries";

    private Integer id;
    private String name;
    private String type;
    private String description;
    private Boolean displaySeries;
    private Integer seriesRGBColor;
    private Boolean subChart;
    private Strategy strategy;
    protected Integer version;
    private boolean dirty = false;
    private List<CodeValue> codeValues = new ArrayList<>(0);

    /**
     * Constructor for IndicatorSeries.
     *
     * @param type String
     */
    public IndicatorSeries(String type) {
        super(type, true, false);
        this.type = type;
        this.version = 0;
    }

    /**
     * Constructor for IndicatorSeries.
     *
     * @param type           String
     * @param displaySeries  Boolean
     * @param seriesRGBColor Integer
     * @param subChart       Boolean
     */
    public IndicatorSeries(String type, Boolean displaySeries, Integer seriesRGBColor, Boolean subChart) {
        super(type, true, false);
        this.type = type;
        this.displaySeries = displaySeries;
        this.seriesRGBColor = seriesRGBColor;
        this.subChart = subChart;
        this.version = 0;
    }

    /**
     * Constructor for IndicatorSeries.
     *
     * @param name           String
     * @param type           String
     * @param displaySeries  Boolean
     * @param seriesRGBColor Integer
     * @param subChart       Boolean
     */
    public IndicatorSeries(String name, String type, Boolean displaySeries, Integer seriesRGBColor, Boolean subChart) {
        super(name, true, false);
        this.type = type;
        this.displaySeries = displaySeries;
        this.seriesRGBColor = seriesRGBColor;
        this.subChart = subChart;
        this.version = 0;
    }

    /**
     * Constructor for IndicatorSeries.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displaySeries  Boolean
     * @param seriesRGBColor Integer
     * @param subChart       Boolean
     */
    public IndicatorSeries(Strategy strategy, String name, String type, String description, Boolean displaySeries,
                           Integer seriesRGBColor, Boolean subChart) {
        super(name, true, false);
        this.strategy = strategy;
        this.name = name;
        this.type = type;
        this.description = description;
        this.displaySeries = displaySeries;
        this.seriesRGBColor = seriesRGBColor;
        this.subChart = subChart;
        this.version = 0;
    }

    /**
     * Method getIdIndicatorSeries.
     *
     * @return Integer
     */
    public Integer getIdIndicatorSeries() {
        return this.id;
    }

    /**
     * Method setIdIndicatorSeries.
     *
     * @param id Integer
     */
    public void setIdIndicatorSeries(Integer id) {
        this.id = id;
    }

    /**
     * Method getName.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method getType.
     *
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Method setType.
     *
     * @param type String
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method getDescription.
     *
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Method setDescription.
     *
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method getSeriesRGBColor.
     *
     * @return Integer
     */
    public Integer getSeriesRGBColor() {
        return this.seriesRGBColor;
    }

    /**
     * Method getSeriesColor.
     *
     * @return Color
     */
    public Color getSeriesColor() {
        return new Color(this.seriesRGBColor);
    }

    /**
     * Method setSeriesRGBColor.
     *
     * @param seriesRGBColor Integer
     */
    public void setSeriesRGBColor(Integer seriesRGBColor) {
        this.seriesRGBColor = seriesRGBColor;
    }

    /**
     * Method getDisplaySeries.
     *
     * @return Boolean
     */
    public Boolean getDisplaySeries() {
        return this.displaySeries;
    }

    /**
     * Method setDisplaySeries.
     *
     * @param displaySeries Boolean
     */
    public void setDisplaySeries(Boolean displaySeries) {
        this.displaySeries = displaySeries;
    }

    /**
     * Method getSubChart.
     *
     * @return Boolean
     */
    public Boolean getSubChart() {
        return this.subChart;
    }

    /**
     * Method setSubChart.
     *
     * @param subChart Boolean
     */
    public void setSubChart(Boolean subChart) {
        this.subChart = subChart;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Method setVersion.
     *
     * @param version Integer
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    public boolean isDirty() {
        for (CodeValue item : this.getCodeValues()) {
            if (item.isDirty())
                return true;
        }
        return this.dirty;
    }

    /**
     * Method setDirty.
     *
     * @param dirty boolean
     */

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Method getStrategy.
     *
     * @return Strategy
     */
    public Strategy getStrategy() {
        return this.strategy;
    }

    /**
     * Method setStrategy.
     *
     * @param strategy Strategy
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Method getCodeValues.
     *
     * @return List<CodeValue>
     */
    public List<CodeValue> getCodeValues() {
        return this.codeValues;
    }

    /**
     * Method setCodeValues.
     *
     * @param codeValues List<CodeValue>
     */
    public void setCodeValues(List<CodeValue> codeValues) {
        this.codeValues = codeValues;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        IndicatorSeries clone = (IndicatorSeries) super.clone();
        clone.data = new ArrayList<>();
        return clone;
    }

    /**
     * Returns the data item at the specified index.
     *
     * @param index the item index.
     * @return The data item.
     */
    public ComparableObjectItem getDataItem(int index) {
        return super.getDataItem(index);
    }

    /**
     * Method updateSeries.
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public abstract void updateSeries(CandleSeries source, int skip, boolean newBar);

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public abstract void createSeries(CandleDataset source, int seriesIndex);

    /**
     * Method printSeries.
     */
    public abstract void printSeries();
}
