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
     * Method findById.
     *
     * @param tradingdayId Long
     * @return Tradingday
     */
    Tradingday findById(Long tradingdayId);

    /**
     * Method findByOpenCloseDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate);

    /**
     * Method findByOpenCloseDateOrderByOpenAsc.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    Tradingday findByOpenCloseDateOrderByOpenAsc(ZonedDateTime openDate, ZonedDateTime closeDate);
}
