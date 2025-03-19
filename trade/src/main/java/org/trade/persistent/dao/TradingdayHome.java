/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
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

import org.trade.core.dao.EntityManagerHelper;

import javax.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Stateless
public class TradingdayHome {

    public TradingdayHome() {

    }

    /**
     * Method persist. This method saves all the trade-strategies for all the
     * tradingdays from the Trading Tab.
     *
     * @param detachedInstance Tradingday as set of tradingdays with associated
     *                         tradestrategies.
     * @throws Exception
     */
    public void persist(final Tradingday detachedInstance) throws Exception {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            /*
             * Check the incoming tradingday to see if it exists if it does
             * merge with the persisted one if not persist.
             */
            Tradingday tradingday = null;
            if (null == detachedInstance.getId()) {
                tradingday = this.findTradingdayByOpenCloseDate(detachedInstance.getOpen(),
                        detachedInstance.getClose());
                if (null == tradingday) {
                    entityManager.persist(detachedInstance);
                } else {
                    detachedInstance.setId(tradingday.getId());
                    detachedInstance.setVersion(tradingday.getVersion());
                    tradingday = entityManager.merge(detachedInstance);
                }
                entityManager.getTransaction().commit();
            } else {
                tradingday = entityManager.merge(detachedInstance);
                entityManager.getTransaction().commit();
                detachedInstance.setVersion(tradingday.getVersion());
            }

            for (Tradestrategy tradestrategy : detachedInstance.getTradestrategies()) {
                // If it has trades do nothing
                if (tradestrategy.getTradeOrders().isEmpty() && tradestrategy.isDirty()) {
                    entityManager.getTransaction().begin();

                    /*
                     * If the tradingday existed use the persisted version.
                     */
                    if (null != tradingday) {
                        tradestrategy.setTradingday(tradingday);
                    }
                    /*
                     * The strategy will always exist as these cannot be created
                     * via this tab, as they are a drop down list. So find the
                     * persisted one and set this.
                     */
                    Strategy strategy = this.findStrategyByName(tradestrategy.getStrategy().getName());
                    if (null != strategy) {
                        tradestrategy.setStrategy(strategy);
                    }
                    /*
                     * Check to see if the contract exists if it does merge and
                     * set the new persisted one. If no persist the contract.
                     */
                    Contract contract = this.findContractByUniqueKey(tradestrategy.getContract().getSecType(),
                            tradestrategy.getContract().getSymbol(), tradestrategy.getContract().getExchange(),
                            tradestrategy.getContract().getCurrency(), tradestrategy.getContract().getExpiry());
                    if (null != contract) {
                        tradestrategy.setContract(contract);
                    }
                    /*
                     * Persist or merge the tradestrategy.
                     */
                    if (null == tradestrategy.getId()) {
                        entityManager.persist(tradestrategy);
                        entityManager.getTransaction().commit();
                    } else {
                        Tradestrategy instance = entityManager.merge(tradestrategy);
                        entityManager.getTransaction().commit();
                        tradestrategy.setVersion(instance.getVersion());
                    }
                    tradestrategy.setDirty(false);
                }
            }
            entityManager.getTransaction().begin();
            List<Tradestrategy> tradestrategies = findTradestrategyByIdTradingday(detachedInstance.getId());

            for (Tradestrategy tradestrategy : tradestrategies) {
                boolean exists = false;
                for (Tradestrategy newTradestrategy : detachedInstance.getTradestrategies()) {
                    if (newTradestrategy.equals(tradestrategy)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    if (tradestrategy.getTradeOrders().isEmpty()) {
                        entityManager.remove(tradestrategy);
                    } else {
                        throw new Exception("The following Contract:" + tradestrategy.getContract().getSymbol()
                                + " Strategy:" + tradestrategy.getStrategy().getName()
                                + " already exists with trades. \n Please delete orders before removing.");
                    }
                }
            }
            entityManager.getTransaction().commit();
            detachedInstance.setDirty(false);

        } catch (Exception re) {
            EntityManagerHelper.logError("Error saving Tradingdays: " + re.getMessage(), re);
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findTradingdayById.
     *
     * @param id Integer
     * @return Tradingday
     */
    public Tradingday findTradingdayById(Integer id) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Tradingday instance = entityManager.find(Tradingday.class, id);
            if (null != instance) {
                for (Tradestrategy tradestrategy : instance.getTradestrategies()) {
                    tradestrategy.getStrategy().getIndicatorSeries().size();
                    tradestrategy.getTradeOrders().size();
                }
            }
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
     * Method findTradingdaysByDateRange.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    public Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Tradingdays tradingdays = new Tradingdays();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
            Root<Tradingday> from = query.from(Tradingday.class);
            query.select(from);
            query.orderBy(builder.desc(from.get("open")));
            List<Predicate> predicates = new ArrayList<Predicate>();

            if (null != startDate) {
                Predicate predicate = builder.greaterThanOrEqualTo(from.get("open").as(ZonedDateTime.class), startDate);
                predicates.add(predicate);
            }
            if (null != endDate) {
                Predicate predicate = builder.lessThanOrEqualTo(from.get("open").as(ZonedDateTime.class), endDate);
                predicates.add(predicate);
            }

            query.where(predicates.toArray(new Predicate[]{}));
            TypedQuery<Tradingday> typedQuery = entityManager.createQuery(query);
            List<Tradingday> items = typedQuery.getResultList();
            for (Tradingday tradingday : items) {
                tradingdays.add(tradingday);
                for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {
                    tradestrategy.getTradeOrders().size();
                    tradestrategy.getPortfolio().getPortfolioAccounts().size();
                    tradestrategy.getStrategy().getIndicatorSeries().size();
                }
            }
            entityManager.getTransaction().commit();
            return tradingdays;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findByOpen.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    public Tradingday findByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
            Root<Tradingday> from = query.from(Tradingday.class);
            query.select(from);
            if (null != openDate)
                query.where(builder.equal(from.get("open"), openDate));
            if (null != closeDate)
                query.where(builder.equal(from.get("close"), closeDate));
            List<Tradingday> items = entityManager.createQuery(query).getResultList();
            for (Tradingday tradingday : items) {
                for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {
                    tradestrategy.getTradeOrders().size();
                    tradestrategy.getStrategy().getIndicatorSeries().size();
                }
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
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    private Strategy findStrategyByName(String name) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Strategy> query = builder.createQuery(Strategy.class);
            Root<Strategy> from = query.from(Strategy.class);
            query.select(from);
            query.where(builder.equal(from.get("name"), name));
            List<Strategy> items = entityManager.createQuery(query).getResultList();
            if (items.size() > 0) {
                for (Strategy itme : items) {
                    itme.getIndicatorSeries().size();
                }
                return items.get(0);
            }
            return null;

        } catch (Exception re) {
            throw re;
        }
    }

    /**
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    private Tradingday findTradingdayByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
            Root<Tradingday> from = query.from(Tradingday.class);
            query.select(from);

            if (null != openDate)
                query.where(builder.equal(from.get("open"), openDate));
            if (null != closeDate)
                query.where(builder.equal(from.get("close"), closeDate));
            List<Tradingday> items = entityManager.createQuery(query).getResultList();

            if (items.size() > 0) {
                return items.get(0);
            }
            return null;

        } catch (Exception re) {
            throw re;
        }
    }

    /**
     * Method findTradestrategyByDate.
     *
     * @param open Date
     * @return List<Tradestrategy>
     */
    private List<Tradestrategy> findTradestrategyByIdTradingday(Integer idTradingday) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tradestrategy> query = builder.createQuery(Tradestrategy.class);
            Root<Tradestrategy> from = query.from(Tradestrategy.class);
            query.select(from);
            List<Predicate> predicates = new ArrayList<Predicate>();

            if (null != idTradingday) {
                Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
                Predicate predicate = builder.equal(tradingday.get("id"), idTradingday);
                predicates.add(predicate);
            }
            query.where(predicates.toArray(new Predicate[]{}));
            TypedQuery<Tradestrategy> typedQuery = entityManager.createQuery(query);
            List<Tradestrategy> items = typedQuery.getResultList();
            return items;

        } catch (Exception re) {
            throw re;
        }
    }

    /**
     * Method findContractByUniqueKey.
     *
     * @param SECType    String
     * @param symbol     String
     * @param exchange   String
     * @param currency   String
     * @param expiryDate Date
     * @return Contract
     */
    private Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                             ZonedDateTime expiryDate) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Contract> query = builder.createQuery(Contract.class);
            Root<Contract> from = query.from(Contract.class);
            query.select(from);
            List<Predicate> predicates = new ArrayList<Predicate>();

            if (null != SECType) {
                Predicate predicate = builder.equal(from.get("secType"), SECType);
                predicates.add(predicate);
            }
            if (null != symbol) {
                Predicate predicate = builder.equal(from.get("symbol"), symbol);
                predicates.add(predicate);
            }
            if (null != exchange) {
                Predicate predicate = builder.equal(from.get("exchange"), exchange);
                predicates.add(predicate);
            }
            if (null != currency) {
                Predicate predicate = builder.equal(from.get("currency"), currency);
                predicates.add(predicate);
            }
            if (null != expiryDate) {

                Integer yearExpiry = expiryDate.getYear();
                Expression<Integer> year = builder.function("year", Integer.class, from.get("expiry"));
                Predicate predicateYear = builder.equal(year, yearExpiry);
                predicates.add(predicateYear);

                Integer monthExpiry = expiryDate.getMonthValue();
                Expression<Integer> month = builder.function("month", Integer.class, from.get("expiry"));
                Predicate predicateMonth = builder.equal(month, new Integer(1 + monthExpiry.intValue()));
                predicates.add(predicateMonth);
            }
            query.where(predicates.toArray(new Predicate[]{}));
            TypedQuery<Contract> typedQuery = entityManager.createQuery(query);
            List<Contract> items = typedQuery.getResultList();
            if (items.size() > 0) {
                return items.get(0);
            }
            return null;

        } catch (Exception re) {
            throw re;
        }
    }
}