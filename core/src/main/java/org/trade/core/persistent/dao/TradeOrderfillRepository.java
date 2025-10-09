package org.trade.core.persistent.dao;

import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradeOrderfillRepository extends AspectRepository<TradeOrderfill, Long>, TradeOrderfillRepositoryCustom {

    TradeOrderfill findByExecId(String execId);
}
