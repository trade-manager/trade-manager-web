package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradeOrderRepository extends AspectRepository<TradeOrder, Long>, TradeOrderRepositoryCustom {

    TradeOrder findByOrderKey(Integer orderKey);
}
