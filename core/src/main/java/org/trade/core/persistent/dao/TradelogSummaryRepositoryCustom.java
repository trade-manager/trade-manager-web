package org.trade.core.persistent.dao;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface TradelogSummaryRepositoryCustom {

    List<TradelogSummary> findByTradelogSummary(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                                String symbol, BigDecimal winLossAmount) throws IOException;
}
