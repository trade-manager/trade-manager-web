package org.trade.core.persistent.tradelogdetail;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.util.time.TradingCalendar;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradelogDetailServiceImpl implements TradelogDetailService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final TradelogDetailRepository tradelogDetailRepository;

    public TradelogDetailServiceImpl(final TradelogDetailRepository tradelogDetailRepository) {

        this.tradelogDetailRepository = tradelogDetailRepository;
    }

    /**
     * Method findByTradelogDetail.
     *
     * @param portfolio Portfolio
     * @param start     ZonedDateTime
     * @param end       ZonedDateTime
     * @param filter    boolean
     * @return List<TradelogDetail>
     */
    public List<TradelogDetail> findByTradelogDetail(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                                     boolean filter, String symbol, BigDecimal winLossAmount) throws IOException {

        Query queryDetail = entityManager.createNativeQuery(TradelogDetail.getSQLString(),
                "TradelogDetailMapping");

        queryDetail.setParameter("portfolioId", portfolio.getId());
        queryDetail.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        queryDetail.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        queryDetail.setParameter("filter", filter);
        queryDetail.setParameter("symbol", symbol);
        return queryDetail.getResultList();
    }
}