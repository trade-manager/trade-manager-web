package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.List;


public interface RuleRepository extends AspectRepository<Rule, Long>, RuleRepositoryCustom {

    List<Rule> findByStrategyAndContentTypeAndRuleVersion(Strategy strategy, String contentType, Integer ruleVersion);

    List<Rule> findByStrategy(Strategy strategy);

    List<Rule> findByStrategyAndActive(Strategy strategy, Boolean active);
}
