package org.trade.core.persistent.dao;


import org.trade.core.persistent.dao.series.indicator.CandleSeries;

import java.time.ZonedDateTime;
import java.util.List;

public interface CandleRepositoryCustom {

    void persistCandleSeries(final CandleSeries candleSeries) throws Exception;

    List<Candle> findByContractAndDateRange(Integer idContract, ZonedDateTime startPeriod,
                                            ZonedDateTime endPeriod, Integer barSize);

    List<Candle> findCandlesByContractDateRangeBarSize(Integer idContract, ZonedDateTime startOpenDate,
                                                       ZonedDateTime endOpenDate, Integer barSize);

    Candle findByUniqueKey(Integer idTradingday, Integer idContract, ZonedDateTime startPeriod,
                           ZonedDateTime endPeriod, Integer barSize);

    Long findCandleCount(Integer idTradingday, Integer idContract);

}
