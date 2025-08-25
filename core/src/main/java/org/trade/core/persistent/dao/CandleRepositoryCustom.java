package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CandleRepositoryCustom {


    List<Candle> findCandlesByContractDateRangeBarSize(Contract contract, ZonedDateTime startPeriod,
                                                       ZonedDateTime endPeriod, Integer barSize);

    Long findCandleCount(Contract contract);
}
