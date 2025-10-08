package org.trade.core.persistent.dao.series.indicator;

import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.codetype.CodeValueRecord;
import org.trade.core.persistent.strategy.StrategyRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record IndicatorSeriesRecord(Long Id, String type,
                                    Integer seriesRGBColor,
                                    boolean dirty,
                                    StrategyRecord strategy,
                                    List<CodeValueRecord> codeValues,
                                    String name,
                                    String description,
                                    Boolean displaySeries,
                                    Boolean subChart,
                                    Integer version) {


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
                (null != indicatorSeries.getStrategy() ? StrategyRecord.from(indicatorSeries.getStrategy()) : null),
                List.copyOf(codeValueRecords),
                indicatorSeries.getName(),
                indicatorSeries.getDescription(),
                indicatorSeries.getDisplaySeries(),
                indicatorSeries.getSubChart(),
                indicatorSeries.getVersion());
    }
}