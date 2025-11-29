package org.trade.core.persistent.strategy.series.indicator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.series.ComparableObjectItem;
import org.trade.core.persistent.strategy.series.ComparableObjectSeries;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "indicatorseries")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("IndicatorSeries")
public abstract class IndicatorSeries extends ComparableObjectSeries implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = -4985280367851073683L;

    protected final static Logger _log = LoggerFactory.getLogger(IndicatorSeries.class);

    /*
     * These names must match the names of the classes for that series.
     */
    public static final String INDICATOR_PACKAGE = "org.trade.core.persistent.strategy.series.indicator.";
    public static final String MovingAverageSeries = MovingAverageSeries.class.getSimpleName();
    public static final String PivotSeries = PivotSeries.class.getSimpleName();
    public static final String HeikinAshiSeries = HeikinAshiSeries.class.getSimpleName();
    public static final String VwapSeries = VwapSeries.class.getSimpleName();
    public static final String VolumeSeries = VolumeSeries.class.getSimpleName();
    public static final String CandleSeries = CandleSeries.class.getSimpleName();
    public static final String AverageTrueRangeSeries = AverageTrueRangeSeries.class.getSimpleName();
    public static final String RelativeStrengthIndexSeries = RelativeStrengthIndexSeries.class.getSimpleName();
    public static final String CommodityChannelIndexSeries = CommodityChannelIndexSeries.class.getSimpleName();
    public static final String BollingerBandsSeries = BollingerBandsSeries.class.getSimpleName();
    public static final String StochasticOscillatorSeries = StochasticOscillatorSeries.class.getSimpleName();
    public static final String MoneyFlowIndexSeries = MoneyFlowIndexSeries.class.getSimpleName();
    public static final String MACDSeries = MACDSeries.class.getSimpleName();
    public static final String VostroSeries = VostroSeries.class.getSimpleName();

    private Long id;
    private String type;
    private Integer seriesRGBColor;
    private boolean dirty = false;
    private Strategy strategy;
    private List<CodeValue> codeValues = new ArrayList<>(0);

    @Column(name = "name", length = 45, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "display_series", length = 1, nullable = false)
    private Boolean displaySeries;

    @Column(name = "sub_chart", length = 1, nullable = false)
    private Boolean subChart;

    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    protected Integer version;

    public IndicatorSeries() {
        super();
    }

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
     * @param type String
     */
    public IndicatorSeries(Long id, String type, Integer seriesRGBColor, Boolean dirty, String name, String description, Boolean displaySeries, Boolean subChart) {

        super(type, true, false);
        this.type = type;
        this.seriesRGBColor = seriesRGBColor;
        this.dirty = dirty;
        this.name = name;
        this.description = description;
        this.displaySeries = displaySeries;
        this.subChart = subChart;
        this.version = 0;
        setId(id);
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
     * Method getIndicatorSeriesId.
     *
     * @return Long
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * Method setIndicatorSeriesId.
     *
     * @param id Long
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Method getType.
     *
     * @return String
     */
    @Column(name = "type", length = 45, insertable = false, updatable = false, unique = true, nullable = false)
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
     * Method getSeriesRGBColor.
     *
     * @return Integer
     */
    @Column(name = "series_rgb_color", nullable = false)
    public Integer getSeriesRGBColor() {
        return this.seriesRGBColor;
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
     * Method getStrategy.
     *
     * @return Strategy
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id", nullable = false)
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
    @OneToMany(mappedBy = "indicatorSeries", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {
            CascadeType.ALL})
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
     * Method getSeriesColor.
     *
     * @return Color
     */
    @Transient
    public Color getSeriesColor() {
        return new Color(this.seriesRGBColor);
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
    @Transient
    public boolean isDirty() {

        for (CodeValue item : this.getCodeValues()) {

            if (item.isDirty()) {
                return true;
            }
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
    @Transient
    public ComparableObjectItem getDataItem(Integer index) {
        return super.getDataItem(index);
    }

    /**
     * Method updateSeries.
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public abstract void updateSeries(CandleSeries source, Integer skip, Boolean newBar) throws ServiceException;

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public abstract void createSeries(CandleDataset source, Integer seriesIndex) throws ServiceException;

    /**
     * Method printSeries.
     */
    public abstract void printSeries();

    @Transient
    public List<Object> getParam(String type) {

        List<Object> params = new ArrayList<>(0);
        params.add(strategy);
        params.add(this.getName());

        if (null == type) {

            params.add(this.getType());
        } else {

            params.add(type);
        }
        params.add(this.getDescription());
        params.add(this.getDisplaySeries());
        params.add(this.getSeriesRGBColor());
        params.add(this.getSubChart());
        return params;
    }
}
