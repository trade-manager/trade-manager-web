package org.trade.core.broker.client;

import org.trade.core.persistent.strategy.strategyrule.IStrategyChangeListener;
import org.trade.core.persistent.strategy.strategyrule.StrategyRuleException;
import org.trade.core.persistent.tradestrategy.Tradestrategy;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public abstract class Broker extends SwingWorker<Void, Void> implements IStrategyChangeListener {

    protected AtomicInteger ruleComplete = new AtomicInteger(0);
    protected AtomicInteger strategiesRunning = new AtomicInteger(0);
    protected final Object lockBackTestWorker = new Object();

    public Broker() {

    }

    /**
     * Method strategyComplete.
     *
     * @param strategyClassName String
     * @param tradestrategy     Tradestrategy
     */
    public synchronized void strategyComplete(String strategyClassName, Tradestrategy tradestrategy) {

        synchronized (lockBackTestWorker) {

            strategiesRunning.getAndDecrement();
            lockBackTestWorker.notifyAll();
        }
    }

    /**
     * Method strategyStarted.
     *
     * @param strategyClassName String
     * @param tradestrategy     Tradestrategy
     */
    public synchronized void strategyStarted(String strategyClassName, Tradestrategy tradestrategy) {

        synchronized (lockBackTestWorker) {
            strategiesRunning.getAndIncrement();
            lockBackTestWorker.notifyAll();
        }
    }

    /**
     * Method ruleComplete.
     *
     * @param tradestrategy Tradestrategy
     * @see IStrategyChangeListener#ruleComplete(Tradestrategy)
     */
    public synchronized void ruleComplete(Tradestrategy tradestrategy) {

        synchronized (lockBackTestWorker) {
            ruleComplete.getAndIncrement();
            lockBackTestWorker.notifyAll();
        }
    }

    /**
     * Method strategyError.
     *
     * @param strategyError StrategyRuleException
     */
    public void strategyError(StrategyRuleException strategyError) {

        this.cancel(true);
    }
}
