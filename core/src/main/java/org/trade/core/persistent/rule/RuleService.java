package org.trade.core.persistent.rule;

import org.trade.core.persistent.strategy.Strategy;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface RuleService {

    /**
     * Method findById.
     *
     * @return Rule
     */
    Rule findById(Long id);

    /**
     * Method findAll.
     *
     * @return List<Rule>
     */
    List<Rule> findAll();

    /**
     * Method findByMaxRuleVersion.
     *
     * @param strategy    Strategy
     * @param contentType String
     * @return Integer
     */
    Integer findByMaxRuleVersion(Strategy strategy, String contentType);

    /**
     * Method findByStrategyAndContentTypeAndRuleVersion.
     *
     * @param strategy    Strategy
     * @param contentType String
     * @param ruleVersion Integer
     * @return List<Rule>
     */
    List<Rule> findByStrategyAndContentTypeAndRuleVersion(Strategy strategy, String contentType, Integer ruleVersion);

    /**
     * Method findByStrategy.
     *
     * @param strategy Strategy
     * @return List<Rule>
     */
    List<Rule> findByStrategy(Strategy strategy);

    /**
     * Method findByStrategyAndActive.
     *
     * @param strategy Strategy
     * @param active   Boolean
     * @return List<Rule>
     */
    List<Rule> findByStrategyAndActive(Strategy strategy, Boolean active);

    /**
     * Method findByMaxVersion.
     *
     * @param strategy    Strategy
     * @param contentType String
     * @return Rule
     */
    Rule findByMaxVersion(final Strategy strategy, String contentType);
}
