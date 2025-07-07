package org.trade.core.persistent.dao;


public interface RuleRepositoryCustom {

    Integer findByMaxVersion(Strategy strategy);
}
