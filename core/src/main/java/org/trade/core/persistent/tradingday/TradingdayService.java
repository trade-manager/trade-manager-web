package org.trade.core.persistent.tradingday;

import java.time.ZonedDateTime;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradingdayService {

    /**
     * Method findTradingdaysByDateRangeOrderByOpenAsc.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    Tradingdays findTradingdaysByDateRangeOrderByOpenAsc(ZonedDateTime startDate, ZonedDateTime endDate);

    /**
     * Method findTradingdayById.
     *
     * @param tradingdayId Long
     * @return Tradingday
     */
    Tradingday findTradingdayById(Long tradingdayId);

    /**
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findTradingdayByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate);

    /**
     * Method findByOpenCloseDateOrderByOpenAsc.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findByOpenCloseDateOrderByOpenAsc(ZonedDateTime openDate, ZonedDateTime closeDate);
}
