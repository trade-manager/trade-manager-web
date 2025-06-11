package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.trade.core.util.time.TradingCalendar;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Repository
public class TradelogReportRepositoryImpl implements TradelogReportRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Method findByTradelogReport.
     *
     * @param portfolio Portfolio
     * @param start     ZonedDateTime
     * @param end       ZonedDateTime
     * @param filter    boolean
     * @param symbol    String
     * @return TradelogReport
     */
    public TradelogReport findByTradelogReport(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                               boolean filter, String symbol, BigDecimal winLossAmount) {

        Query queryDetail = entityManager.createNativeQuery(TradelogDetail.getSQLString(),
                TradelogDetail.class);

        queryDetail.setParameter("idPortfolio", portfolio.getId());
        queryDetail.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        queryDetail.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        queryDetail.setParameter("filter", filter);
        queryDetail.setParameter("symbol", symbol);

        TradelogReport tradelogReport = new TradelogReport();

        for (Object item : queryDetail.getResultList()) {

            tradelogReport.add((TradelogDetail) item);
        }

        Query querySummary = entityManager.createNativeQuery(TradelogSummary.getSQLString(),
                TradelogSummary.class);

        querySummary.setParameter("idPortfolio", portfolio.getId());
        querySummary.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        querySummary.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        querySummary.setParameter("symbol", symbol);
        querySummary.setParameter("winLossAmount", winLossAmount);

        for (Object item : querySummary.getResultList()) {

            tradelogReport.add((TradelogSummary) item);
        }
        return tradelogReport;
    }

    /**
     * Method findByTradelogDetail.
     *
     * @param portfolio Portfolio
     * @param start     ZonedDateTime
     * @param end       ZonedDateTime
     * @param filter    boolean
     * @return TradelogReport
     */
    public TradelogReport findByTradelogDetail(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                               boolean filter, String symbol) {

        Query queryDetail = entityManager.createNativeQuery(TradelogDetail.getSQLString(),
                "TradelogDetailMapping");

        queryDetail.setParameter("idPortfolio", portfolio.getId());
        queryDetail.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        queryDetail.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        queryDetail.setParameter("filter", filter);
        queryDetail.setParameter("symbol", symbol);

        TradelogReport tradelogReport = new TradelogReport();

        for (Object item : queryDetail.getResultList()) {

            tradelogReport.add((TradelogDetail) item);
        }
        return tradelogReport;
    }

    /**
     * Method findByTradelogSummary.
     *
     * @param portfolio Portfolio
     * @param start     ZonedDateTime
     * @param end       ZonedDateTime
     * @return TradelogReport
     */
    public TradelogReport findByTradelogSummary(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end,
                                                String symbol, BigDecimal winLossAmount) {

        Query querySummary = entityManager.createNativeQuery(TradelogSummary.getSQLString(),
                TradelogSummary.class);

        querySummary.setParameter("idPortfolio", portfolio.getId());
        querySummary.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        querySummary.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        querySummary.setParameter("symbol", symbol);
        querySummary.setParameter("winLossAmount", winLossAmount);

        TradelogReport tradelogReport = new TradelogReport();

        for (Object item : querySummary.getResultList()) {

            tradelogReport.add((TradelogSummary) item);
        }

        return tradelogReport;
    }
}