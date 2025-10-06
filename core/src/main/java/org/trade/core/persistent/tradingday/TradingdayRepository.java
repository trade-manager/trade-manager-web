package org.trade.core.persistent.tradingday;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradingdayRepository extends AspectRepository<Tradingday, Long> {

    List<Tradingday> findByOpenAndCloseOrderByOpenAsc(ZonedDateTime open, ZonedDateTime close);
}
