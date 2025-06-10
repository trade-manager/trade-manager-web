package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContractRepositoryImpl implements ContractRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findByUniqueKey.
     *
     * @param SECType    String
     * @param symbol     String
     * @param exchange   String
     * @param currency   String
     * @param expiryDate ZonedDateTime
     * @return Contract
     */
    public Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                            ZonedDateTime expiryDate) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contract> query = builder.createQuery(Contract.class);
        Root<Contract> from = query.from(Contract.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(from.get("secType"), SECType));
        predicates.add(builder.equal(from.get("symbol"), symbol));

        if (null != exchange) {

            predicates.add(builder.equal(from.get("exchange"), exchange));
        }

        if (null != currency) {

            predicates.add(builder.equal(from.get("currency"), currency));
        }

        if (null != expiryDate) {

            Integer yearExpiry = expiryDate.getYear();
            Expression<Integer> year = builder.function("year", Integer.class, from.get("expiry"));
            Predicate predicateYear = builder.equal(year, yearExpiry);
            predicates.add(predicateYear);

            Integer monthExpiry = expiryDate.getMonthValue();
            Expression<Integer> month = builder.function("month", Integer.class, from.get("expiry"));
            Predicate predicateMonth = builder.equal(month, monthExpiry);
            predicates.add(predicateMonth);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Contract> typedQuery = entityManager.createQuery(query);
        List<Contract> items = typedQuery.getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }

        return null;
    }

    /**
     * Method findByContractId.
     *
     * @param id Integer
     * @return ContractId
     */
    public ContractLite findContractLiteById(Integer id) {

        return entityManager.find(ContractLite.class, id);
    }


    public Optional<Contract> findById(Integer id) {

        Contract instance = entityManager.find(Contract.class, id);
        return Optional.ofNullable(instance);
    }
}