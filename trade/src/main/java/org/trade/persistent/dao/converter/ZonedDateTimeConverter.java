package org.trade.persistent.dao.converter;

import org.trade.core.util.TradingCalendar;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
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
        return ((Date) Date.from(instant));

    }

    public ZonedDateTime convertToEntityAttribute(Date value) {
        if (value == null) {
            return null;
        }
        final Instant instant = Instant.ofEpochMilli(value.getTime());
        return ZonedDateTime.ofInstant(instant, TradingCalendar.MKT_TIMEZONE);
    }
}
