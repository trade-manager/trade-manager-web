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
public class TradelogDetailRepositoryImpl implements TradelogDetailRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

        queryDetail.setParameter("idPortfolio", portfolio.getId());
        queryDetail.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
        queryDetail.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
        queryDetail.setParameter("filter", filter);
        queryDetail.setParameter("symbol", symbol);

        TradelogReport tradelogReport = new TradelogReport();

        List<TradelogDetail> items = queryDetail.getResultList();
        return items;
    }
}