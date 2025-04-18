package org.trade.core.persistent.dao.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.trade.core.util.time.TradingCalendar;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Date> {

    public Date convertToDatabaseColumn(ZonedDateTime date) {
        if (date == null) {
            return null;
        }
        final Instant instant = date.toInstant();
        return Date.from(instant);

    }

    public ZonedDateTime convertToEntityAttribute(Date value) {
        if (value == null) {
            return null;
        }
        final Instant instant = Instant.ofEpochMilli(value.getTime());
        return ZonedDateTime.ofInstant(instant, TradingCalendar.MKT_TIMEZONE);
    }
}
