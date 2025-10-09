package org.trade.core.persistent.tradelogdetail;

import org.trade.core.persistent.portfolio.Portfolio;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradelogDetailService {

    /**
     * Method findByTradelogDetail.
     *
     * @param portfolio     Portfolio
     * @param start         ZonedDateTime
     * @param end           ZonedDateTime
     * @param filter        boolean
     * @param symbol        String
     * @param winLossAmount BigDecimal
     * @return List<TradelogDetail>
     * @throws IOException File not found
     */
    List<TradelogDetail> findByTradelogDetail(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                              boolean filter, String symbol, BigDecimal winLossAmount) throws IOException;

}
