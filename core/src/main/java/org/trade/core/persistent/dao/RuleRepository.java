package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface RuleRepository extends AspectRepository<Rule, Long>, RuleRepositoryCustom {

    List<Rule> findByStrategyAndContentTypeAndRuleVersion(Strategy strategy, String contentType, Integer ruleVersion);

    List<Rule> findByStrategy(Strategy strategy);

    List<Rule> findByStrategyAndActive(Strategy strategy, Boolean active);
}
