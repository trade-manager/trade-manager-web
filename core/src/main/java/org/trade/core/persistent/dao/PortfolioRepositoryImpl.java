package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findDefault.
     *
     * @return Portfolio
     */
    public Portfolio findDefault() {

        Portfolio portfolio = null;
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
        Root<Portfolio> from = query.from(Portfolio.class);
        query.select(from);
        List<Portfolio> items = entityManager.createQuery(query).getResultList();

        for (Portfolio item : items) {

            if (item.getIsDefault()) {

                // item.getPortfolioAccounts().size();
                portfolio = item;
                break;
            }
        }

        return portfolio;
    }

    /**
     * Method findAllPortfolios.
     *
     * @return Portfolio
     */
    public List<Portfolio> findAllPortfolios() {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
        Root<Portfolio> from = query.from(Portfolio.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }
}