package org.trade.core.persistent.dao;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface TradelogReportRepositoryCustom {

    TradelogReport findByTradelogReport(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                        boolean filter, String symbol, BigDecimal winLossAmount) throws IOException;

    TradelogReport findByTradelogDetail(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                        boolean filter, String symbol) throws IOException;

    TradelogReport findByTradelogSummary(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                         String symbol, BigDecimal winLossAmount) throws IOException;
}
