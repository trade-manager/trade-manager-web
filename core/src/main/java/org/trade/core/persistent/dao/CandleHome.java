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
package org.trade.core.persistent.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.trade.core.dao.EntityManagerHelper;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Stateless
public class CandleHome {

    public CandleHome() {

    }

    /**
     * Method persistCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    public synchronized void persistCandleSeries(final CandleSeries candleSeries) throws Exception {

        Candle transientInstance;
        try {

            if (candleSeries.isEmpty()) {
                return;
            }

            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Tradingday tradingday = null;
            Contract contract = findContractById(candleSeries.getContract().getId());
            for (int i = 0; i < candleSeries.getItemCount(); i++) {

                CandleItem candleItem = (CandleItem) candleSeries.getDataItem(i);
                if (null != candleItem.getCandle().getId()) {
                    Candle instance = entityManager.find(Candle.class, candleItem.getCandle().getId());
                    if (instance.equals(candleItem.getCandle())) {
                        continue;
                    } else {
                        // This should never happen.
                        throw new Exception("Count: " + i + " Symbol: " + candleSeries.getSymbol() + "candleid: "
                                + candleItem.getCandle().getId() + " open: "
                                + candleItem.getCandle().getStartPeriod());
                    }
                }

                if (!candleItem.getCandle().getTradingday().equals(tradingday)) {

                    if (null == candleItem.getCandle().getTradingday().getId()) {
                        tradingday = findTradingdayByDate(candleItem.getCandle().getTradingday().getOpen(),
                                candleItem.getCandle().getTradingday().getClose());
                    } else {
                        tradingday = findTradingdayById(candleItem.getCandle().getTradingday().getId());
                    }

                    if (null == tradingday) {
                        entityManager.persist(candleItem.getCandle().getTradingday());
                        entityManager.getTransaction().commit();
                        entityManager.getTransaction().begin();
                        tradingday = candleItem.getCandle().getTradingday();
                    } else {
                        Integer barSize = candleSeries.getBarSize();
                        String hqlDelete = "delete Candle where contract = :contract and tradingday = :tradingday and barSize = :barSize";
                        entityManager.createQuery(hqlDelete).setParameter("contract", contract)
                                .setParameter("tradingday", tradingday).setParameter("barSize", barSize)
                                .executeUpdate();
                        entityManager.getTransaction().commit();
                        entityManager.getTransaction().begin();
                    }
                }

                transientInstance = candleItem.getCandle();
                transientInstance.setTradingday(tradingday);
                transientInstance.setContract(contract);
                entityManager.persist(transientInstance);

                // Commit every 50 rows
                if ((Math.floor(i / 50d) == (i / 50d)) && (i > 0)) {
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                }
            }
            entityManager.getTransaction().commit();
        } catch (Exception re) {
            EntityManagerHelper.logError("Error persistCandleSeries failed :" + re.getMessage(), re);
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findByContractAndDateRange.
     *
     * @param idContract  Integer
     * @param startPeriod Date
     * @param endPeriod   Date
     * @return List<Candle>
     */
    public List<Candle> findByContractAndDateRange(Integer idContract, ZonedDateTime startPeriod,
                                                   ZonedDateTime endPeriod, Integer barSize) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
            Root<Candle> from = query.from(Candle.class);
            query.select(from);
            List<Predicate> predicates = new ArrayList<>();

            if (null != idContract) {
                Join<Candle, Contract> contract = from.join("contract");
                Predicate predicate = builder.equal(contract.get("idContract"), idContract);
                predicates.add(predicate);
            }
            if (null != startPeriod) {
                Expression<ZonedDateTime> start = from.get("startPeriod");
                Predicate predicate = builder.greaterThanOrEqualTo(start, startPeriod);
                predicates.add(predicate);
            }
            if (null != endPeriod) {
                Expression<ZonedDateTime> end = from.get("endPeriod");
                Predicate predicate = builder.lessThanOrEqualTo(end, endPeriod);
                predicates.add(predicate);
            }
            if (null != barSize) {
                Predicate predicate = builder.equal(from.get("barSize"), barSize);
                predicates.add(predicate);
            }
            query.where(predicates.toArray(new Predicate[]{}));
            TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
            List<Candle> items = typedQuery.getResultList();
            entityManager.getTransaction().commit();
            return items;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findCandlesByContractDateRangeBarSize.
     *
     * @param idContract    Integer
     * @param startOpenDate ZonedDateTime
     * @param endOpenDate   ZonedDateTime
     * @param barSize       Integer
     * @return List<Candle>
     */
    public List<Candle> findCandlesByContractDateRangeBarSize(Integer idContract, ZonedDateTime startOpenDate,
                                                              ZonedDateTime endOpenDate, Integer barSize) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
            Root<Candle> from = query.from(Candle.class);
            query.select(from);
            query.orderBy(builder.asc(from.get("startPeriod")));
            List<Predicate> predicates = new ArrayList<>();

            if (null != idContract) {

                Join<Candle, Contract> contract = from.join("contract");
                Predicate predicateContract = builder.equal(contract.get("id"), idContract);
                predicates.add(predicateContract);
            }

            if (null != startOpenDate) {

                Join<Candle, Tradingday> tradingdayOpenDate = from.join("tradingday");
                Predicate predicateStartDate = builder
                        .greaterThanOrEqualTo(tradingdayOpenDate.get("open"), startOpenDate);
                predicates.add(predicateStartDate);
                Predicate predicateEndDate = builder
                        .lessThanOrEqualTo(tradingdayOpenDate.get("open"), endOpenDate);
                predicates.add(predicateEndDate);
            }

            if (null != barSize) {

                Predicate predicate = builder.equal(from.get("barSize"), barSize);
                predicates.add(predicate);
            }

            query.where(predicates.toArray(new Predicate[]{}));
            TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
            List<Candle> items = typedQuery.getResultList();
            entityManager.getTransaction().commit();
            return items;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findById.
     *
     * @param idCandle Integer
     * @return Candle
     */
    public Candle findById(Integer idCandle) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Candle instance = entityManager.find(Candle.class, idCandle);
            entityManager.getTransaction().commit();
            return instance;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findByUniqueKey.
     *
     * @param idTradingday Integer
     * @param idContract   Integer
     * @param startPeriod  ZonedDateTime
     * @param endPeriod    ZonedDateTime
     * @return Candle
     */
    public Candle findByUniqueKey(Integer idTradingday, Integer idContract, ZonedDateTime startPeriod,
                                  ZonedDateTime endPeriod, Integer barSize) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
            Root<Candle> from = query.from(Candle.class);
            query.select(from);
            List<Predicate> predicates = new ArrayList<>();

            if (null != idTradingday) {

                Join<Candle, Tradingday> tradingday = from.join("tradingday");
                Predicate predicate = builder.equal(tradingday.get("id"), idTradingday);
                predicates.add(predicate);
            }

            if (null != idContract) {

                Join<Candle, Contract> contract = from.join("contract");
                Predicate predicate = builder.equal(contract.get("id"), idContract);
                predicates.add(predicate);
            }

            if (null != startPeriod) {

                Predicate predicate = builder.equal(from.get("startPeriod"), startPeriod);
                predicates.add(predicate);
            }
            if (null != endPeriod) {

                Predicate predicate = builder.equal(from.get("endPeriod"), endPeriod);
                predicates.add(predicate);
            }
            if (null != barSize) {

                Predicate predicate = builder.equal(from.get("barSize"), barSize);
                predicates.add(predicate);
            }

            query.where(predicates.toArray(new Predicate[]{}));
            TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
            List<Candle> items = typedQuery.getResultList();
            entityManager.getTransaction().commit();
            if (!items.isEmpty()) {
                return items.getFirst();
            }
            return null;
        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findCandleCount.
     *
     * @param idTradingday Integer
     * @param idContract   Integer
     * @return Long
     */
    public Long findCandleCount(Integer idTradingday, Integer idContract) {

        try {

            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> query = builder.createQuery();
            Root<Candle> from = query.from(Candle.class);
            Expression<Long> id = from.get("id");
            Expression<Long> minExpression = builder.count(id);

            List<Predicate> predicates = new ArrayList<>();

            if (null != idTradingday) {

                Join<Candle, Tradingday> tradingday = from.join("tradingday");
                Predicate predicate = builder.equal(tradingday.get("id"), idTradingday);
                predicates.add(predicate);
            }

            if (null != idContract) {

                Join<Candle, Contract> contract = from.join("contract");
                Predicate predicate = builder.equal(contract.get("id"), idContract);
                predicates.add(predicate);
            }

            query.where(predicates.toArray(new Predicate[]{}));
            CriteriaQuery<Object> select = query.select(minExpression);
            TypedQuery<Object> typedQuery = entityManager.createQuery(select);
            Object item = typedQuery.getSingleResult();
            entityManager.getTransaction().commit();

            if (null == item) {
                item = 0L;
            }

            return (Long) item;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findContractById.
     *
     * @param id Integer
     * @return Contract
     */
    private Contract findContractById(Integer id) {

        EntityManager entityManager = EntityManagerHelper.getEntityManager();
        return entityManager.find(Contract.class, id);
    }

    /**
     * Method findContractById.
     *
     * @param id Integer
     * @return Contract
     */
    private Tradingday findTradingdayById(Integer id) {

        EntityManager entityManager = EntityManagerHelper.getEntityManager();
        return entityManager.find(Tradingday.class, id);
    }

    /**
     * Method findTradingdayByDate.
     *
     * @param open  ZonedDateTime
     * @param close ZonedDateTime
     * @return Tradingday
     */
    private Tradingday findTradingdayByDate(ZonedDateTime open, ZonedDateTime close) {

        EntityManager entityManager = EntityManagerHelper.getEntityManager();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
        Root<Tradingday> from = query.from(Tradingday.class);
        query.select(from);

        if (null != open) {

            query.where(builder.equal(from.get("open"), open));
        }

        if (null != close) {

            query.where(builder.equal(from.get("close"), close));
        }

        List<Tradingday> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;

    }
}
