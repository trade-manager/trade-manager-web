package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

public interface TradingdayRepositoryCustom {

    Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate);

    Tradingday findByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate);

    List<Tradestrategy> findTradestrategyByTradingdayId(Integer idTradingday);
}
