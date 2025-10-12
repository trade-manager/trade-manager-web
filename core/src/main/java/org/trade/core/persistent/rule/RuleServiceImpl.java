package org.trade.core.persistent.rule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.trade.core.persistent.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class RuleServiceImpl implements RuleService {

    @PersistenceContext
    private EntityManager entityManager;

    private final RuleRepository ruleRepository;

    public RuleServiceImpl(final RuleRepository ruleRepository) {

        this.ruleRepository = ruleRepository;
    }

    public Rule findById(Long id) {

        return this.ruleRepository.findById(id).orElse(null);
    }

    public Rule validateAndGet(Long id) {

        return this.ruleRepository.findById(id).orElseThrow(() -> new RuleNotFoundException(String.format("Rule with id %s not found", name)));
    }

    public List<Rule> findAll() {

        return this.ruleRepository.findAll();
    }

    public List<Rule> findByStrategyAndContentTypeAndRuleVersion(Strategy strategy, String contentType, Integer ruleVersion) {

        return this.ruleRepository.findByStrategyAndContentTypeAndRuleVersion(strategy, contentType, ruleVersion);
    }

    public List<Rule> findByStrategy(Strategy strategy) {
        return this.ruleRepository.findByStrategy(strategy);
    }

    public List<Rule> findByStrategyAndActive(Strategy strategy, Boolean active) {

        return this.ruleRepository.findByStrategyAndActive(strategy, active);
    }

    /**
     * Method findByMaxVersion.
     *
     * @param strategy Strategy
     * @return Integer
     */
    public Integer findByMaxRuleVersion(Strategy strategy, String contentType) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Root<Rule> from = query.from(Rule.class);

        Expression<Integer> id = from.get("ruleVersion");
        Expression<Integer> minExpression = builder.max(id);
        CriteriaQuery<Object> select = query.select(minExpression);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(from.get("contentType"), contentType));

        if (null != strategy) {

            Join<Rule, Strategy> strategies = from.join("strategy");
            Predicate predicate = builder.equal(strategies.get("id"), strategy.getId());
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        Object item = typedQuery.getSingleResult();

        if (null == item) {

            return 0;
        }

        return (Integer) item;
    }

    public Rule findByMaxVersion(final Strategy strategy, String contentType) {

        Integer version = this.findByMaxRuleVersion(strategy, contentType);
        List<Rule> rules = this.findByStrategyAndContentTypeAndRuleVersion(strategy, contentType, version);

        if (!rules.isEmpty()) {

            return rules.getFirst();
        }

        return null;
    }
}