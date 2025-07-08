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
     * @param contract    Contract
     * @param startPeriod Date
     * @param endPeriod   Date
     * @param barSize     Integer
     * @return List<Candle>
     */
    public List<Candle> findCandlesByContractDateRangeBarSize(Contract contract, ZonedDateTime startPeriod,
                                                              ZonedDateTime endPeriod, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != contract) {

            Join<Candle, Contract> contractJoin = from.join("contract");
            Predicate predicate = builder.equal(contractJoin.get("id"), contract.getId());
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
        query.orderBy(builder.asc(from.get("startPeriod")));
        TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    /**
     * Method findCandleCount.
     *
     * @param contract Contract
     * @return Long
     */
    public Long findCandleCount(Contract contract) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Root<Candle> from = query.from(Candle.class);
        Expression<Long> id = from.get("id");
        Expression<Long> minExpression = builder.count(id);

        List<Predicate> predicates = new ArrayList<>();

        if (null != contract) {

            Join<Candle, Contract> contractJoin = from.join("contract");
            Predicate predicate = builder.equal(contractJoin.get("id"), contract.getId());
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