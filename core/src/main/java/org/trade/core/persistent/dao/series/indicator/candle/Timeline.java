package org.trade.core.persistent.dao.series.indicator.candle;

import java.util.Date;

public interface Timeline {
    long toTimelineValue(long var1);

    long toTimelineValue(Date var1);

    long toMillisecond(long var1);

    boolean containsDomainValue(long var1);

    boolean containsDomainValue(Date var1);

    boolean containsDomainRange(long var1, long var3);

    boolean containsDomainRange(Date var1, Date var2);
}
