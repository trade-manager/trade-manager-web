package org.trade.core.persistent.codetype;

import org.trade.core.persistent.strategy.series.indicator.IndicatorSeriesRecord;
import org.trade.core.persistent.tradestrategy.TradestrategyRecord;

import java.time.ZonedDateTime;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeValueRecord(Long id,
                              ZonedDateTime createdDate,
                              ZonedDateTime updatedDate,
                              Integer version,
                              Long domainId,
                              String codeValue,
                              CodeAttributeRecord codeAttribute,
                              IndicatorSeriesRecord indicatorSeries,
                              TradestrategyRecord tradestrategy) {

    /**
     * Method from note roles are LAZY loaded., hence we do not get the children.
     *
     * @param codeValue         CodeValue
     * @param withAttributes    Boolean
     * @param withTradestrategy Boolean
     * @return CodeTypeRecord
     */
    public static CodeValueRecord from(final CodeValue codeValue, Boolean withAttributes, Boolean withTradestrategy) {

        return new CodeValueRecord(
                codeValue.getId(),
                codeValue.getCreatedDate(),
                codeValue.getUpdatedDate(),
                codeValue.getVersion(),
                codeValue.getDomainId(),
                codeValue.getCodeValue(),
                (null != codeValue.getCodeAttribute() ? CodeAttributeRecord.from(codeValue.getCodeAttribute(), false) : null),
                (withAttributes && null != codeValue.getIndicatorSeries() ? IndicatorSeriesRecord.from(codeValue.getIndicatorSeries()) : null),
                (withTradestrategy && null != codeValue.getTradestrategy() ? TradestrategyRecord.from(codeValue.getTradestrategy()) : null)
        );
    }

    public Long getId() {
        return id;
    }

    /**
     * Method getCreatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    /**
     * Method getUpdatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Method getDomainId
     *
     * @return Long
     */
    public Long getDomainId() {

        return domainId;
    }

    /**
     * Method getCodeValue.
     *
     * @return String
     */
    public String getCodeValue() {
        return this.codeValue;
    }

    /**
     * Method getCodeAttribute.
     *
     * @return CodeAttributeRecord
     */
    public CodeAttributeRecord getCodeAttribute() {
        return this.codeAttribute;
    }

    /**
     * Method getIndicatorSeries.
     *
     * @return IndicatorSeriesRecord
     */
    public IndicatorSeriesRecord getIndicatorSeries() {
        return this.indicatorSeries;
    }

    /**
     * Method getTradestrategy.
     *
     * @return TradestrategyRecord
     */
    public TradestrategyRecord getTradestrategy() {
        return this.tradestrategy;
    }


}
