package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RuleRepository extends JpaRepository<Rule, Integer>, RuleRepositoryCustom {

}
