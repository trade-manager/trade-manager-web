package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CandleRepository extends JpaRepository<Candle, Integer>, CandleRepositoryCustom {

    List<Candle> findByContractAndBarSize(Tradingday tradingday, Contract contract, Integer barSize);

    void deleteAll(List<Candle> candles);
}
