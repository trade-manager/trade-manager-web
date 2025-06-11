package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeOrderfillRepository extends JpaRepository<TradeOrderfill, Integer>, TradeOrderfillRepositoryCustom {

    TradeOrderfill findByExecId(String execId);
}
