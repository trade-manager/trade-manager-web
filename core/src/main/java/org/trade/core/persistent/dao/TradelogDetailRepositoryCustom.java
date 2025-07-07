package org.trade.core.persistent.dao;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface TradelogDetailRepositoryCustom {

    List<TradelogDetail> findByTradelogDetail(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                              boolean filter, String symbol, BigDecimal winLossAmount) throws IOException;

}
