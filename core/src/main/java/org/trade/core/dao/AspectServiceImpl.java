package org.trade.core.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class AspectServiceImpl implements AspectService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AspectRepository aspectRepository;

    public AspectServiceImpl(final AspectRepository aspectRepository) {

        this.aspectRepository = aspectRepository;
    }

    public Aspect findById(final Aspect aspect) throws ClassNotFoundException {

        Aspects aspects = this.findByClassNameAndFieldName(aspect.getClass().getName(), "Id", Objects.requireNonNull(aspect.getId()).toString());

        if (!aspects.getAspects().isEmpty()) {

            return aspects.getAspects().getFirst();
        }

        return null;
    }

    public void delete(Aspect instance) {

        if (null != instance) {

            this.aspectRepository.delete(instance);
        }
    }

    public void deleteAll(Iterable<? extends Aspect> entities) {

        this.aspectRepository.deleteAll(entities);
    }

    public <T extends Aspect> T save(Aspect instance) {

        return (T) this.aspectRepository.save(instance);
    }

    public <S extends Aspect> List<S> saveAll(final Iterable<S> entities) {

        return this.aspectRepository.saveAll(entities);
    }

    /**
     * @param className String
     * @return Aspects
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
     * @param fieldName String
     * @param value     String
     * @return Aspects
     */
    public Aspects findByClassNameAndFieldName(String className, String fieldName, String value) throws ClassNotFoundException {

        Aspects aspects = new Aspects();
        Class<?> c = Class.forName(className);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = builder.createQuery();
        Root<?> from = criteriaQuery.from(c);
        CriteriaQuery<Object> query = criteriaQuery.select(from);

        if (null != fieldName) {

            query.where(builder.equal(from.get(fieldName), value));
        }

        TypedQuery<Object> typedQuery = entityManager.createQuery(query);
        List<Object> items = typedQuery.getResultList();

        for (Object item : items) {

            aspects.add((Aspect) item);
        }

        return aspects;
    }

    /**
     *
     */
    public List<?> findCodesByClassName(String className) throws ClassNotFoundException {

        Class<?> c = Class.forName(className);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
        Root<?> from = criteriaQuery.from(c);
        CriteriaQuery<Object> select = criteriaQuery.select(from);
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        List<Object> items = typedQuery.getResultList();

        if (!items.isEmpty()) {

            return items;
        }

        return new ArrayList<>(0);
    }
}