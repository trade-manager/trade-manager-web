package org.trade.core.persistent.dao;


public interface RuleRepositoryCustom {

    Integer findByMaxRuleVersion(Strategy strategy);
}
