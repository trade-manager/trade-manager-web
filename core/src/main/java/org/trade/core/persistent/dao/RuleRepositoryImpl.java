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

import java.util.ArrayList;
import java.util.List;


@Repository
public class RuleRepositoryImpl implements RuleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findByMaxVersion.
     *
     * @param strategy Strategy
     * @return Integer
     */
    public Integer findByMaxVersion(Strategy strategy) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Root<Rule> from = query.from(Rule.class);

        Expression<Integer> id = from.get("version");
        Expression<Integer> minExpression = builder.max(id);
        CriteriaQuery<Object> select = query.select(minExpression);

        List<Predicate> predicates = new ArrayList<>();

        if (null != strategy) {

            Join<Rule, Strategy> strategies = from.join("strategy");
            Predicate predicate = builder.equal(strategies.get("id"), strategy.getId());
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        Object item = typedQuery.getSingleResult();

        if (null == item) {
            item = 0;
        }

        return (Integer) item;
    }
}