package org.trade.core.persistent.strategy.series.indicator;

import org.modelmapper.AbstractConverter;
import org.trade.core.util.JSONMapper;

public class IndicatorSeriesConverter extends AbstractConverter<IndicatorSeriesRecord, IndicatorSeries> {

    protected IndicatorSeries convert(IndicatorSeriesRecord source) {

        if (null == source) {

            return null;
        }

        String type = source.getType();
        Class<IndicatorSeries> indicatorClass = (Class<IndicatorSeries>) IndicatorSeries.Type.getValueClassForType(source.getType());
        IndicatorSeries indicatorSeries = JSONMapper.convertRecordToEntity(source, indicatorClass);

        // You decide the logic for which concrete class to instantiate
        if (null != indicatorSeries) {

            return indicatorSeries;
        }

        throw new IllegalArgumentException("Unknown type for: " + source.getType());
    }
}
