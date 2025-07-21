package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

public interface TradingdayRepositoryCustom {

    Tradingdays findTradingdaysByDateRangeOrderByOpenDesc(ZonedDateTime startDate, ZonedDateTime endDate);

    Tradingday findByOpenCloseDateOrderByOpenDesc(ZonedDateTime openDate, ZonedDateTime closeDate);

    List<Tradestrategy> findTradestrategyByTradingday(Tradingday tradingday);
}
