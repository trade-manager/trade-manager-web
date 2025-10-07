package org.trade.core.persistent.rule;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;
import org.trade.core.persistent.strategy.Strategy;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface RuleRepository extends AspectRepository<Rule, Long> {

    List<Rule> findByStrategyAndContentTypeAndRuleVersion(Strategy strategy, String contentType, Integer ruleVersion);

    List<Rule> findByStrategy(Strategy strategy);

    List<Rule> findByStrategyAndActive(Strategy strategy, Boolean active);
}
