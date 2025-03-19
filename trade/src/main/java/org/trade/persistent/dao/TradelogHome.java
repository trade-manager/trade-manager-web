/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.persistent.dao;

import org.trade.core.dao.EntityManagerHelper;
import org.trade.core.util.TradingCalendar;

import javax.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 *
 */
@Stateless
public class TradelogHome {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public TradelogHome() {

    }

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
        EntityManager entityManagerLocal = EntityManagerHelper.getLocalEntityManager();
        try {
            entityManagerLocal.getTransaction().begin();
            Query queryDetail = entityManagerLocal.createNativeQuery(TradelogDetail.getSQLString(),
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
            Query querySummary = entityManagerLocal.createNativeQuery(TradelogSummary.getSQLString(),
                    TradelogSummary.class);

            querySummary.setParameter("idPortfolio", portfolio.getId());
            querySummary.setParameter("start", TradingCalendar.getFormattedDate(start, DATE_FORMAT));
            querySummary.setParameter("end", TradingCalendar.getFormattedDate(end, DATE_FORMAT));
            querySummary.setParameter("symbol", symbol);
            querySummary.setParameter("winLossAmount", winLossAmount);

            for (Object item : querySummary.getResultList()) {
                tradelogReport.add((TradelogSummary) item);
            }
            entityManagerLocal.getTransaction().commit();
            return tradelogReport;

        } catch (Exception re) {
            if ((entityManagerLocal.getTransaction() != null) && entityManagerLocal.getTransaction().isActive()) {
                entityManagerLocal.getTransaction().rollback();
            }
            throw re;
        } finally {
            entityManagerLocal.close();
        }
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
        EntityManager entityManagerLocal = EntityManagerHelper.getLocalEntityManager();
        try {
            entityManagerLocal.getTransaction().begin();
            Query queryDetail = entityManagerLocal.createNativeQuery(TradelogDetail.getSQLString(),
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
            entityManagerLocal.getTransaction().commit();
            return tradelogReport;

        } catch (Exception re) {
            if ((entityManagerLocal.getTransaction() != null) && entityManagerLocal.getTransaction().isActive()) {
                entityManagerLocal.getTransaction().rollback();
            }
            throw re;
        } finally {
            entityManagerLocal.close();
        }
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
        EntityManager entityManagerLocal = EntityManagerHelper.getLocalEntityManager();
        try {
            entityManagerLocal.getTransaction().begin();
            Query querySummary = entityManagerLocal.createNativeQuery(TradelogSummary.getSQLString(),
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
            entityManagerLocal.getTransaction().commit();
            return tradelogReport;

        } catch (Exception re) {
            if ((entityManagerLocal.getTransaction() != null) && entityManagerLocal.getTransaction().isActive()) {
                entityManagerLocal.getTransaction().rollback();
            }
            throw re;
        } finally {
            entityManagerLocal.close();
        }
    }
}
