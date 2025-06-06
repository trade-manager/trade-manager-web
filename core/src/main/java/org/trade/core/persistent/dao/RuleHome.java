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
package org.trade.core.persistent.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.trade.core.dao.EntityManagerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Stateless
public class RuleHome {

    public RuleHome() {

    }

    /**
     * Method findById.
     *
     * @param id Integer
     * @return Rule
     */
    public Rule findById(Integer id) {

        try {

            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
            Rule instance = entityManager.find(Rule.class, id);
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
     * Method findByMaxVersion.
     *
     * @param strategy Strategy
     * @return Integer
     */
    public Integer findByMaxVersion(Strategy strategy) {

        try {

            EntityManager entityManager = EntityManagerHelper.getEntityManager();
            entityManager.getTransaction().begin();
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
            entityManager.getTransaction().commit();

            if (null == item) {
                item = 0;
            }


            return (Integer) item;

        } catch (Exception re) {
            EntityManagerHelper.rollback();
            throw re;
        } finally {
            EntityManagerHelper.close();
        }
    }
}
