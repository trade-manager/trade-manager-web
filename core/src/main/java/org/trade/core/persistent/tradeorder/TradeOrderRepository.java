package org.trade.core.persistent.tradeorder;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradeOrderRepository extends AspectRepository<TradeOrder, Long> {

    /**
     * Method findByOrderKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    TradeOrder findByOrderKey(Integer orderKey);
}
