package org.trade.core.persistent.tradingday;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradingdayRepository extends AspectRepository<Tradingday, Long> {

    /**
     * Method findByOpenAndCloseOrderByOpenAsc.
     *
     * @param open  ZonedDateTime
     * @param close ZonedDateTime
     * @return List<Tradingday>
     */
    List<Tradingday> findByOpenAndCloseOrderByOpenAsc(ZonedDateTime open, ZonedDateTime close);
}
