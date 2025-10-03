package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public class StrategyRepositoryImpl implements StrategyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    public Strategy findStrategyByName(String name) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Strategy> query = builder.createQuery(Strategy.class);
        Root<Strategy> from = query.from(Strategy.class);
        query.select(from);
        query.where(builder.equal(from.get("name"), name));
        List<Strategy> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            for (Strategy itme : items) {

                itme.getIndicatorSeries().size();
            }
            return items.getFirst();
        }

        return null;
    }
}