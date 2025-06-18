package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.trade.core.util.time.TradingCalendar;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;


@Repository
public class TradelogSummaryRepositoryImpl implements TradelogSummaryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Method findByTradelogSummary.
     *
     * @param portfolio Portfolio
     * @param start     ZonedDateTime
     * @param end       ZonedDateTime
     * @return TradelogSummary
     */
    public List<TradelogSummary> findByTradelogSummary(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                                       String symbol, BigDecimal winLossAmount) throws IOException {

        Query querySummary = entityManager.createNativeQuery(TradelogSummary.getSQLString(),
                TradelogSummary.class);

        querySummary.setParameter("idPortfolio", portfolio.getId());
        querySummary.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        querySummary.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        querySummary.setParameter("symbol", symbol);
        querySummary.setParameter("winLossAmount", winLossAmount);

        List<TradelogSummary> items = querySummary.getResultList();

        return items;
    }
}