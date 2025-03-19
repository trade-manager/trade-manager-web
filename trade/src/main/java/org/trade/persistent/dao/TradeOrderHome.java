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

import org.trade.core.dao.EntityManagerHelper;
import org.trade.core.util.TradingCalendar;

import javax.ejb.Stateless;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.List;

/**
 *
 */
@Stateless
public class TradeOrderHome {

    public TradeOrderHome() {

    }

    /**
     * Method persist.
     *
     * @param transientInstance TradeOrder
     * @return TradeOrder
     */
    public synchronized TradeOrder persist(final TradeOrder transientInstance) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
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
                entityManager.getTransaction().commit();
                return transientInstance;
            } else {
                TradeOrder instance = entityManager.merge(transientInstance);
                entityManager.getTransaction().commit();
                transientInstance.setVersion(instance.getVersion());
                return instance;
            }
        } catch (Exception re) {
            EntityManagerHelper.logError("ERROR saving TradeOrder Msg: " + re.getCause().getMessage(), re);
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }

    /**
     * Method findById.
     *
     * @param id Integer
     * @return TradeOrder
     */
    public TradeOrder findById(Integer id) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            TradeOrder instance = entityManager.find(TradeOrder.class, id);
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
     * Method findTradeOrderByKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    public synchronized TradeOrder findTradeOrderByKey(Integer orderKey) {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<TradeOrder> query = builder.createQuery(TradeOrder.class);
            Root<TradeOrder> from = query.from(TradeOrder.class);
            query.select(from);
            query.where(builder.equal(from.get("orderKey"), orderKey));
            List<TradeOrder> items = entityManager.createQuery(query).getResultList();
            for (TradeOrder tradeOrder : items) {
                tradeOrder.getTradeOrderfills().size();
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
     * Method findTradeOrderByMaxKey.
     *
     * @return Integer
     */
    public Integer findTradeOrderByMaxKey() {

        try {
            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> query = builder.createQuery();
            Root<TradeOrder> from = query.from(TradeOrder.class);

            Expression<Integer> id = from.get("orderKey");
            Expression<Integer> minExpression = builder.max(id);
            CriteriaQuery<Object> select = query.select(minExpression);
            TypedQuery<Object> typedQuery = entityManager.createQuery(select);
            Object item = typedQuery.getSingleResult();
            entityManager.getTransaction().commit();
            if (null == item)
                item = new Integer(0);

            return (Integer) item;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }
}
