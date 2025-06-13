package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class CandleRepositoryImpl implements CandleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findByContractAndDateRange.
     *
     * @param contractId  Integer
     * @param startPeriod Date
     * @param endPeriod   Date
     * @return List<Candle>
     */
    public List<Candle> findByContractAndDateRange(Integer contractId, ZonedDateTime startPeriod,
                                                   ZonedDateTime endPeriod, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != contractId) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("idContract"), contractId);
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
        return typedQuery.getResultList();
    }

    /**
     * Method findCandlesByContractDateRangeBarSize.
     *
     * @param contractId    Integer
     * @param startOpenDate ZonedDateTime
     * @param endOpenDate   ZonedDateTime
     * @param barSize       Integer
     * @return List<Candle>
     */
    public List<Candle> findCandlesByContractDateRangeBarSize(Integer contractId, ZonedDateTime startOpenDate,
                                                              ZonedDateTime endOpenDate, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        query.orderBy(builder.asc(from.get("startPeriod")));
        List<Predicate> predicates = new ArrayList<>();

        if (null != contractId) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicateContract = builder.equal(contract.get("id"), contractId);
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
        return typedQuery.getResultList();
    }

    /**
     * Method findByUniqueKey.
     *
     * @param tradingdayId Integer
     * @param contractId   Integer
     * @param startPeriod  ZonedDateTime
     * @param endPeriod    ZonedDateTime
     * @return Candle
     */
    public List<Candle> findByUniqueKey(Integer tradingdayId, Integer contractId, ZonedDateTime startPeriod,
                                        ZonedDateTime endPeriod, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != tradingdayId) {

            Join<Candle, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.equal(tradingday.get("id"), tradingdayId);
            predicates.add(predicate);
        }

        if (null != contractId) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("id"), contractId);
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
        return typedQuery.getResultList();
    }

    /**
     * Method findCandleCount.
     *
     * @param tradingdayId Integer
     * @param contractId   Integer
     * @return Long
     */
    public Long findCandleCount(Integer tradingdayId, Integer contractId) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Root<Candle> from = query.from(Candle.class);
        Expression<Long> id = from.get("id");
        Expression<Long> minExpression = builder.count(id);

        List<Predicate> predicates = new ArrayList<>();

        if (null != tradingdayId) {

            Join<Candle, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.equal(tradingday.get("id"), tradingdayId);
            predicates.add(predicate);
        }

        if (null != contractId) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("id"), contractId);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        CriteriaQuery<Object> select = query.select(minExpression);
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        Object item = typedQuery.getSingleResult();

        if (null == item) {
            item = 0L;
        }

        return (Long) item;
    }
}