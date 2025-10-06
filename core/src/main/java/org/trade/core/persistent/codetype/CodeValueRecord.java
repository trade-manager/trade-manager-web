package org.trade.core.persistent.codetype;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeValueRecord(Long id, String codeValue, CodeAttributeRecord codeAttribute
        /* IndicatorSeriesRecord indicatorSeries, TradestrategyRecord tradestrategy*/) {

    public static CodeValueRecord from(final CodeValue codeValue) {

        return new CodeValueRecord(
                codeValue.getId(),
                codeValue.getCodeValue(),
                CodeAttributeRecord.from(codeValue.getCodeAttribute())
                //codeValue.getIndicatorSeries(),
                //codeValue.getTradestrategy()
        );
    }
}
