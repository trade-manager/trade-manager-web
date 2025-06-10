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
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Currency;

import java.util.ArrayList;
import java.util.List;


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

            item.setIsDefault(item.getId().equals(defaultPortfolio.getId()));
            entityManager.persist(item);
        }

    }

    /**
     * Method persistPortfolio.
     *
     * @param instance Portfolio
     * @return Portfolio
     */
    public synchronized Portfolio persistPortfolio(final Portfolio instance) {

        Portfolio portfolio = findPortfolioByName(instance.getName());

        if (null == portfolio) {

            instance.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());

            for (PortfolioAccount item : instance.getPortfolioAccounts()) {

                Account account = findByAccountNumber(item.getAccount().getAccountNumber());

                if (null == account) {

                    item.getAccount().setCurrency(Currency.USD);
                    item.getAccount().setName(item.getAccount().getAccountNumber());
                    item.getAccount().setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                } else {

                    item.setAccount(account);
                }
            }
            entityManager.persist(instance);

        } else {

            if (0 != CoreUtils.nullSafeComparator(portfolio.getAllocationMethod(),
                    instance.getAllocationMethod())) {

                portfolio.setAllocationMethod(instance.getAllocationMethod());
                portfolio.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            }

            for (PortfolioAccount item : instance.getPortfolioAccounts()) {

                Account account = findByAccountNumber(item.getAccount().getAccountNumber());
                if (null == account) {

                    item.getAccount().setCurrency(Currency.USD);
                    item.getAccount().setName(item.getAccount().getAccountNumber());
                    item.getAccount().setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
                } else {

                    item.setAccount(account);
                }

                PortfolioAccount portfolioAccount = findByNameAndAccountNumber(portfolio.getName(),
                        item.getAccount().getAccountNumber());

                if (null == portfolioAccount) {

                    item.setPortfolio(portfolio);
                    portfolio.getPortfolioAccounts().add(item);
                }
            }
            entityManager.persist(portfolio);
        }

        return (portfolio == null ? instance : portfolio);
    }

    /**
     * Method findByAccountNumber.
     *
     * @param accountNumber String
     * @return Account
     */
    private Account findByAccountNumber(String accountNumber) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        query.select(from);
        query.where(builder.equal(from.get("accountNumber"), accountNumber));
        List<Account> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;
    }

    /**
     * Method findPortfolioByName.
     *
     * @param name String
     * @return Portfolio
     */
    private Portfolio findPortfolioByName(String name) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
        Root<Portfolio> from = query.from(Portfolio.class);
        query.select(from);
        query.where(builder.equal(from.get("name"), name));
        List<Portfolio> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;

    }

    /**
     * Method findByNameAndAccountNumber.
     *
     * @param portfolioName String
     * @param accountNumber String
     * @return Portfolio
     */
    private PortfolioAccount findByNameAndAccountNumber(String portfolioName, String accountNumber) {

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