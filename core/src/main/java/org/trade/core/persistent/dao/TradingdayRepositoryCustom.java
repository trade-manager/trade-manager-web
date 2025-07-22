package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

public interface TradingdayRepositoryCustom {

    Tradingdays findTradingdaysByDateRangeOrderByOpenAsc(ZonedDateTime startDate, ZonedDateTime endDate);

    Tradingday findByOpenCloseDateOrderByOpenAsc(ZonedDateTime openDate, ZonedDateTime closeDate);

    List<Tradestrategy> findTradestrategyByTradingday(Tradingday tradingday);
}
