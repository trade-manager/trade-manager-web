package org.trade.core.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class AspectRepositoryImpl implements AspectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public Aspects findByClassName(String className) throws ClassNotFoundException {

        Aspects aspects = new Aspects();
        Class<?> c = Class.forName(className);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
        Root<?> from = criteriaQuery.from(c);
        CriteriaQuery<Object> select = criteriaQuery.select(from);
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        List<Object> items = typedQuery.getResultList();
        for (Object item : items) {
            aspects.add((Aspect) item);
        }

        return aspects;
    }

    /**
     * Method findByClassNameFieldName.
     *
     * @param className String
     * @param fieldname String
     * @param value     String
     * @return Aspects
     */
    public Aspects findByClassNameFieldName(String className, String fieldname, String value) throws ClassNotFoundException {


        Aspects aspects = new Aspects();
        Class<?> c = Class.forName(className);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = builder.createQuery();
        Root<?> from = criteriaQuery.from(c);
        CriteriaQuery<Object> query = criteriaQuery.select(from);
        if (null != fieldname) {
            query.where(builder.equal(from.get(fieldname), value));
        }
        TypedQuery<Object> typedQuery = entityManager.createQuery(query);
        List<Object> items = typedQuery.getResultList();
        for (Object item : items) {
            aspects.add((Aspect) item);
        }

        return aspects;
    }
}