package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;


public interface TradeOrderfillRepository extends AspectRepository<TradeOrderfill, Integer>, TradeOrderfillRepositoryCustom {

    TradeOrderfill findByExecId(String execId);
}
