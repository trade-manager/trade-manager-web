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
package org.trade.persistent.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.trade.core.dao.EntityManagerHelper;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.TradingCalendar;
import org.trade.dictionary.valuetype.Currency;
import org.trade.persistent.PersistentModelException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Stateless
public class PortfolioHome {

    public PortfolioHome() {

    }

    /**
     * Method findById.
     *
     * @param id Integer
     * @return Portfolio
     */
    public Portfolio findById(Integer id) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Portfolio instance = entityManager.find(Portfolio.class, id);
            instance.getPortfolioAccounts().size();
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
     * Method findAll.
     *
     * @return List<Portfolio>
     */
    public List<Portfolio> findAll() {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
            Root<Portfolio> from = query.from(Portfolio.class);
            query.select(from);
            List<Portfolio> items = entityManager.createQuery(query).getResultList();
            for (Portfolio portfolio : items) {
                portfolio.getPortfolioAccounts().size();
            }
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
     * Method findDefault.
     *
     * @return Portfolio
     */
    public Portfolio findDefault() {

        try {
            Portfolio portfolio = null;
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
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
            entityManager.getTransaction().commit();
            return portfolio;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findByName.
     *
     * @param name String
     * @return Portfolio
     */
    public Portfolio findByName(String name) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
            Root<Portfolio> from = query.from(Portfolio.class);
            query.select(from);
            query.where(builder.equal(from.get("name"), name));
            List<Portfolio> items = entityManager.createQuery(query).getResultList();
            for (Portfolio item : items) {
                item.getPortfolioAccounts().size();
            }
            entityManager.getTransaction().commit();
            if (items.size() > 0) {
                return items.get(0);
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
     * Method resetDefaultPortfolio.
     *
     * @param defaultPortfolio Portfolio
     */
    public void resetDefaultPortfolio(final Portfolio defaultPortfolio) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
            Root<Portfolio> from = query.from(Portfolio.class);
            query.select(from);
            List<Portfolio> items = entityManager.createQuery(query).getResultList();
            for (Portfolio item : items) {
                if (item.getId().equals(defaultPortfolio.getId())) {
                    item.setIsDefault(true);
                } else {
                    item.setIsDefault(false);
                }
                entityManager.persist(item);
            }
            entityManager.getTransaction().commit();
        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method persistPortfolio.
     *
     * @param portfolio Portfolio
     * @return Portfolio
     * @throws PersistentModelException
     */

    public synchronized Portfolio persistPortfolio(final Portfolio instance) throws PersistentModelException {
        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
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
            entityManager.getTransaction().commit();
            return (portfolio == null ? instance : portfolio);
        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findByAccountNumber.
     *
     * @param accountNumber String
     * @return Account
     */
    private Account findByAccountNumber(String accountNumber) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> query = builder.createQuery(Account.class);
            Root<Account> from = query.from(Account.class);
            query.select(from);
            query.where(builder.equal(from.get("accountNumber"), accountNumber));
            List<Account> items = entityManager.createQuery(query).getResultList();
            if (items.size() > 0) {
                return items.get(0);
            }
            return null;

        } catch (Exception re) {
            throw re;
        }
    }

    /**
     * Method findPortfolioByName.
     *
     * @param name String
     * @return Portfolio
     */
    private Portfolio findPortfolioByName(String name) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Portfolio> query = builder.createQuery(Portfolio.class);
            Root<Portfolio> from = query.from(Portfolio.class);
            query.select(from);
            query.where(builder.equal(from.get("name"), name));
            List<Portfolio> items = entityManager.createQuery(query).getResultList();
            if (items.size() > 0) {
                return items.get(0);
            }
            return null;

        } catch (Exception re) {
            throw re;
        }
    }

    /**
     * Method findByNameAndAccountNumber.
     *
     * @param portfolioName String
     * @param accountNumber String
     * @return Portfolio
     */
    private PortfolioAccount findByNameAndAccountNumber(String portfolioName, String accountNumber) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<PortfolioAccount> query = builder.createQuery(PortfolioAccount.class);
            Root<PortfolioAccount> from = query.from(PortfolioAccount.class);
            query.select(from);
            List<Predicate> predicates = new ArrayList<Predicate>();
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
            if (items.size() > 0) {
                return items.get(0);
            }
            return null;

        } catch (Exception re) {
            throw re;
        }
    }
}
