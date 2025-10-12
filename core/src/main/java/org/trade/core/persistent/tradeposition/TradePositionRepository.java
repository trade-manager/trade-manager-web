package org.trade.core.persistent.tradeposition;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradePositionRepository extends AspectRepository<TradePosition, Long> {

}
