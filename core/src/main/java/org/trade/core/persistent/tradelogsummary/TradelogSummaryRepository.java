package org.trade.core.persistent.tradelogsummary;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface TradelogSummaryRepository extends AspectRepository<TradelogSummary, Long> {

}
