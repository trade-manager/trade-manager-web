package org.trade.core.persistent.contract;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class ContractServiceImpl implements ContractService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ContractRepository contractRepository;

    public ContractServiceImpl(final ContractRepository contractRepository) {

        this.contractRepository = contractRepository;
    }

    /**
     * Method findByUniqueKey.
     *
     * @param SECType    String
     * @param symbol     String
     * @param exchange   String
     * @param currency   String
     * @param expiryDate ZonedDateTime
     * @return List<Contract>
     */
    public Contract findByUniqueKey(String SECType, String symbol, String exchange, String currency,
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
        List<Contract> contracts = typedQuery.getResultList();
        return (contracts.isEmpty() ? null : contracts.getFirst());
    }

    /**
     * Method findByContractId.
     *
     * @param id Long
     * @return ContractId
     */
    public ContractLite findLiteById(Long id) {

        return entityManager.find(ContractLite.class, id);
    }

    public Optional<Contract> findBySymbol(String symbol) {

        return contractRepository.findBySymbol(symbol);
    }


    public Iterable<Contract> findAll() {

        return contractRepository.findAll();
    }

    public Contract findById(final Long id) {

        return this.contractRepository.findById(id).orElse(null);
    }
}