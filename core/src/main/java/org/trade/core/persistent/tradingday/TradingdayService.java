package org.trade.core.persistent.tradingday;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradingdayService {

    /**
     * Method findAll.
     *
     * @return List<Tradingday>
     */
    List<Tradingday> findAll();

    /**
     * Method save.
     *
     * @param tradingday Tradingday
     * @return Tradingday
     */
    Tradingday save(Tradingday tradingday);

    /**
     * Method delete.
     *
     * @param tradingday Tradingday
     */
    void delete(Tradingday tradingday);

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
     * Method validateAndGet.
     *
     * @param id Long
     * @return Tradingday
     */
    Tradingday validateAndGet(Long id);

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
