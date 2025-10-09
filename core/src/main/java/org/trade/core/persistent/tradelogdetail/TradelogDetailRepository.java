package org.trade.core.persistent.tradelogdetail;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradelogDetailRepository extends AspectRepository<TradelogDetail, Long> {

}
