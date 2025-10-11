package org.trade.core.persistent.tradeorder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradeOrderServiceImpl implements TradeOrderService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TradeOrderRepository tradeOrderRepository;

    public TradeOrderServiceImpl(final TradeOrderRepository tradeOrderRepository) {

        this.tradeOrderRepository = tradeOrderRepository;
    }

    public TradeOrder findById(final Long id) {

        return tradeOrderRepository.findById(id).orElse(null);
    }

    public TradeOrder findByOrderKey(final Integer orderKey) {

        return tradeOrderRepository.findByOrderKey(orderKey);
    }

    /**
     * Method findByMaxKey.
     *
     * @return Integer
     */
    public Integer findByMaxOrderKey() {

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