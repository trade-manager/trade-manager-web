package org.trade.core.persistent.dao;


import java.time.ZonedDateTime;
import java.util.List;

public interface CandleRepositoryCustom {


    List<Candle> findByContractAndDateRange(Integer idContract, ZonedDateTime startPeriod,
                                            ZonedDateTime endPeriod, Integer barSize);

    List<Candle> findCandlesByContractDateRangeBarSize(Integer idContract, ZonedDateTime startOpenDate,
                                                       ZonedDateTime endOpenDate, Integer barSize);

    List<Candle> findByUniqueKey(Integer idTradingday, Integer idContract, ZonedDateTime startPeriod,
                                 ZonedDateTime endPeriod, Integer barSize);

    Long findCandleCount(Integer idTradingday, Integer idContract);

}
