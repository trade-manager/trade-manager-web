package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

                item.getPortfolioAccounts().size();
                portfolio = item;
                break;
            }
        }

        return portfolio;
    }

    /**
     * Method resetDefaultPortfolio.
     *
     * @param defaultPortfolio Portfolio
     */
    public void resetDefaultPortfolio(final Portfolio defaultPortfolio) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
        Root<Portfolio> from = query.from(Portfolio.class);
        query.select(from);
        List<Portfolio> items = entityManager.createQuery(query).getResultList();

        for (Portfolio item : items) {

            item.setIsDefault(Objects.equals(item.getId(), defaultPortfolio.getId()));
            entityManager.persist(item);
        }
    }

    /**
     * Method findByPortfolioNameAndAccountNumber.
     *
     * @param portfolioName String
     * @param accountNumber String
     * @return Portfolio
     */
    public PortfolioAccount findByPortfolioNameAndAccountNumber(String portfolioName, String accountNumber) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PortfolioAccount> query = builder.createQuery(PortfolioAccount.class);
        Root<PortfolioAccount> from = query.from(PortfolioAccount.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != accountNumber) {

            Join<PortfolioAccount, Account> account = from.join("account");
            Predicate predicate = builder.equal(account.get("accountNumber"), accountNumber);
            predicates.add(predicate);
        }

        if (null != portfolioName) {

            Join<PortfolioAccount, Portfolio> portfolio = from.join("portfolio");
            Predicate predicate = builder.equal(portfolio.get("name"), portfolioName);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<PortfolioAccount> typedQuery = entityManager.createQuery(query);
        List<PortfolioAccount> items = typedQuery.getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;
    }
}