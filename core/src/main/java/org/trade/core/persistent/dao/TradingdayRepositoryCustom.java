package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;

public interface TradingdayRepositoryCustom {
    void persist(final Tradingday detachedInstance) throws Exception;

    Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate);

    Tradingday findByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate);
}
