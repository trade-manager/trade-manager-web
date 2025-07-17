package org.trade.core.persistent.dao;


import java.util.List;

public interface RuleRepositoryCustom {

    Integer findByMaxRuleVersion(Strategy strategy);
}
