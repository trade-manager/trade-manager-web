package org.trade.core.persistent.dao;

import org.trade.core.persistent.portfolio.Portfolio;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradelogSummaryRepositoryCustom {

    List<TradelogSummary> findByTradelogSummary(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                                String symbol, BigDecimal winLossAmount) throws IOException;
}
