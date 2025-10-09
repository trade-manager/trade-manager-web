package org.trade.core.persistent.tradelogsummary;

import org.trade.core.persistent.portfolio.Portfolio;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradelogSummaryService {

    /**
     * Method findByTradelogSummary.
     *
     * @param portfolio     Portfolio
     * @param start         ZonedDateTime
     * @param end           ZonedDateTime
     * @param symbol        String
     * @param winLossAmount BigDecimal
     * @return List<TradelogSummary>
     * @throws IOException File not found,
     */
    List<TradelogSummary> findByTradelogSummary(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                                String symbol, BigDecimal winLossAmount) throws IOException;
}
