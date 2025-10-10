package org.trade.core.persistent.codetype;

import org.trade.core.persistent.dao.series.indicator.IndicatorSeriesRecord;
import org.trade.core.persistent.tradestrategy.TradestrategyRecord;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeValueRecord(Long id, String codeValue, CodeAttributeRecord codeAttribute,
                              IndicatorSeriesRecord indicatorSeries, TradestrategyRecord tradestrategy) {

    public static CodeValueRecord from(final CodeValue codeValue) {

        return new CodeValueRecord(
                codeValue.getId(),
                codeValue.getCodeValue(),
                (null != codeValue.getCodeAttribute() ? CodeAttributeRecord.from(codeValue.getCodeAttribute()) : null),
                null, //(null != codeValue.getIndicatorSeries() ? IndicatorSeriesRecord.from(codeValue.getIndicatorSeries()) : null),
                null //(null != codeValue.getTradestrategy() ? TradestrategyRecord.from(codeValue.getTradestrategy()) : null)
        );
    }
}
