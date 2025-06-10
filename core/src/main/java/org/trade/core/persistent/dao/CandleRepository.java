package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CandleRepository extends JpaRepository<Candle, Integer>, CandleRepositoryCustom {

}
