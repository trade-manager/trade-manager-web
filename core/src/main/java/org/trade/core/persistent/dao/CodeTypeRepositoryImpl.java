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


@Repository
public class CodeTypeRepositoryImpl implements CodeTypeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findByNameAndType.
     *
     * @param codeName String
     * @param codeType String
     * @return List<CodeType>
     */
    public List<CodeType> findByNameAndType(String codeName, String codeType) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CodeType> query = builder.createQuery(CodeType.class);
        Root<CodeType> from = query.from(CodeType.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != codeName) {

            Predicate predicate = builder.equal(from.get("name"), codeName);
            predicates.add(predicate);
        }

        if (null != codeType) {

            Predicate predicate = builder.equal(from.get("type"), codeType);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<CodeType> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    /**
     * Method findByAttributeName.
     *
     * @param codeTypeName      String
     * @param codeAttributeName String
     * @return List<CodeValue>
     */
    public List<CodeValue> findByAttributeName(String codeTypeName, String codeAttributeName) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CodeValue> query = builder.createQuery(CodeValue.class);
        Root<CodeValue> from = query.from(CodeValue.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != codeAttributeName) {

            Join<CodeValue, CodeAttribute> codeAttribute = from.join("codeAttribute");
            Predicate predicate = builder.equal(codeAttribute.get("name"), codeAttributeName);
            predicates.add(predicate);
            Join<CodeAttribute, CodeType> codeType = codeAttribute.join("codeType");
            Predicate predicate1 = builder.equal(codeType.get("name"), codeTypeName);
            predicates.add(predicate1);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<CodeValue> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}