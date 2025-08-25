
package org.trade.core.persistent.dao.strategy;

import org.trade.core.persistent.dao.Tradestrategy;

import java.util.EventListener;

/**
 * The interface that must be supported by classes that wish to receive
 * notification of changes to a dataset.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IStrategyChangeListener extends EventListener {

    /**
     * Receives notification of an strategRule change event.
     *
     * @param strategyClassName String
     * @param tradestrategy     Tradestrategy
     */

    void strategyComplete(String strategyClassName, Tradestrategy tradestrategy);

    /**
     * Method strategyStarted.
     *
     * @param strategyClassName String
     * @param tradestrategy     Tradestrategy
     */
    void strategyStarted(String strategyClassName, Tradestrategy tradestrategy);

    /**
     * Method ruleComplete.
     *
     * @param tradestrategy Tradestrategy
     */
    void ruleComplete(Tradestrategy tradestrategy);

    /**
     * Method strategyError.
     *
     * @param strategyError StrategyRuleException
     */
    void strategyError(StrategyRuleException strategyError);

}
