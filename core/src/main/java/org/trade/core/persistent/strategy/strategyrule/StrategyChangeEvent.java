package org.trade.core.persistent.strategy.strategyrule;

import java.io.Serial;

/**
 * A change event that encapsulates information about a change to a
 * strategyRule.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StrategyChangeEvent extends java.util.EventObject {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6068031665553697870L;
    /**
     * The strategyRule that generated the change event.
     */
    private final IStrategyRule strategyRule;

    /**
     * Constructs a new event. The source is either the strategyRule or the
     * class. The strategyRule can be <code>null</code> (in this case the source
     * will be the class).
     *
     * @param source       the source of the event.
     * @param strategyRule IStrategyRule
     */
    public StrategyChangeEvent(Object source, IStrategyRule strategyRule) {
        super(source);
        this.strategyRule = strategyRule;
    }

    /**
     * Returns the strategyRule that generated the event. Note that the
     * strategyRule may be <code>null</code> since adding a <code>null</code>
     * strategyRule to a plot will generated a change event.
     *
     * @return The strategyRule (possibly <code>null</code>).
     */
    public IStrategyRule getStrategyRule() {
        return this.strategyRule;
    }

}
