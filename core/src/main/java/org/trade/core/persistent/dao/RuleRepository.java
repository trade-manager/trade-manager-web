package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.List;


public interface RuleRepository extends AspectRepository<Rule, Long>, RuleRepositoryCustom {

    List<Rule> findByStrategyAndVersion(Strategy strategy, Integer version);
}
