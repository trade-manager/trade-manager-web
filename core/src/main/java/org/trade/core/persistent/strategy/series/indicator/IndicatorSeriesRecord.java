package org.trade.core.persistent.strategy.series.indicator;

import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.codetype.CodeValueRecord;
import org.trade.core.persistent.strategy.StrategyRecord;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record IndicatorSeriesRecord(Long Id,
                                    String type,
                                    Integer seriesRGBColor,
                                    boolean dirty,
                                    String name,
                                    String description,
                                    Boolean displaySeries,
                                    Boolean subChart,
                                    Integer version,
                                    StrategyRecord strategy,
                                    List<CodeValueRecord> codeValues) {


    public static IndicatorSeriesRecord from(IndicatorSeries indicatorSeries) {

        List<CodeValueRecord> codeValueRecords = new ArrayList<>();

        if (null != indicatorSeries.getCodeValues() && !indicatorSeries.getCodeValues().isEmpty()) {

            for (CodeValue codeValue : indicatorSeries.getCodeValues()) {

                codeValueRecords.add(CodeValueRecord.from(codeValue));
            }
        }

        return new IndicatorSeriesRecord(
                indicatorSeries.getId(),
                indicatorSeries.getType(),
                indicatorSeries.getSeriesRGBColor(),
                indicatorSeries.isDirty(),
                indicatorSeries.getName(),
                indicatorSeries.getDescription(),
                indicatorSeries.getDisplaySeries(),
                indicatorSeries.getSubChart(),
                indicatorSeries.getVersion(),
                null, //(null != indicatorSeries.getStrategy() ? StrategyRecord.from(indicatorSeries.getStrategy()) : null),
                List.copyOf(codeValueRecords));
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Color getSeriesColor() {
        return new Color(this.seriesRGBColor);
    }

    public String getType() {
        return this.type;
    }

    public Integer getSeriesRGBColor() {
        return this.seriesRGBColor;
    }

    public Boolean getDisplaySeries() {
        return this.displaySeries;
    }

    public Boolean getSubChart() {
        return this.subChart;
    }

    public Integer getVersion() {
        return this.version;
    }

    public StrategyRecord getStrategy() {
        return this.strategy;
    }

    public List<CodeValueRecord> getCodeValues() {
        return this.codeValues;
    }
}