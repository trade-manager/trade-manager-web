package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.List;


public interface CandleRepository extends AspectRepository<Candle, Integer>, CandleRepositoryCustom {

    List<Candle> findByTradingdayAndContractAndBarSize(Tradingday tradingday, Contract contract, Integer barSize);

}
