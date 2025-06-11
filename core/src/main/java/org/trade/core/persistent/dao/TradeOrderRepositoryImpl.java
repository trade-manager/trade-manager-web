package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.trade.core.util.time.TradingCalendar;


@Repository
public class TradeOrderRepositoryImpl implements TradeOrderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method persist.
     *
     * @param transientInstance TradeOrder
     * @return TradeOrder
     */
    public TradeOrder persist(final TradeOrder transientInstance) {

        transientInstance.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());

        if (null == transientInstance.getId()) {

            if (null != transientInstance.getTradePosition()) {

                if (null != transientInstance.getTradePosition().getId()) {

                    entityManager.find(TradePosition.class,
                            transientInstance.getTradePosition().getId());

                    TradePosition instance = entityManager.merge(transientInstance.getTradePosition());
                    transientInstance.setTradePosition(instance);
                }
            }
            entityManager.persist(transientInstance);
            return transientInstance;
        } else {

            TradeOrder instance = entityManager.merge(transientInstance);
            transientInstance.setVersion(instance.getVersion());
            return instance;
        }
    }

    /**
     * Method findByMaxKey.
     *
     * @return Integer
     */
    public Integer findByMaxKey() {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Root<TradeOrder> from = query.from(TradeOrder.class);

        Expression<Integer> id = from.get("orderKey");
        Expression<Integer> minExpression = builder.max(id);
        CriteriaQuery<Object> select = query.select(minExpression);
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        Object item = typedQuery.getSingleResult();

        if (null == item) {
            item = 0;
        }

        return (Integer) item;
    }
}