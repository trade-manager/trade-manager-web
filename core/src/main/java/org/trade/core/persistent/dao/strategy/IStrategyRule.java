package org.trade.core.persistent.dao.strategy;

import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;


/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IStrategyRule {

    String PACKAGE = "org.trade.strategy.";

    /**
     * Method initialize the strategy.
     */
    void initialize();

    /**
     * Method runStrategy.
     *
     * @param candleSeries CandleSeries
     * @param newBar       boolean
     */
    void runStrategy(CandleSeries candleSeries, boolean newBar) throws StrategyRuleException;

    /**
     * Method error.
     *
     * @param id        int
     * @param errorCode int
     * @param errorMsg  String
     */
    void error(int id, int errorCode, String errorMsg);

    void execute();

    void cancel();

    /**
     * Method isCancelled.
     *
     * @return boolean
     */
    boolean isCancelled();

    /**
     * Method isDone.
     *
     * @return boolean
     */
    boolean isDone();

    /**
     * Method isRunning.
     *
     * @return boolean
     */
    boolean isRunning();

    /**
     * Method isWaiting.
     *
     * @return boolean
     */
    boolean isWaiting();

    /**
     * Method addMessageListener.
     *
     * @param listener IStrategyChangeListener
     */
    void addMessageListener(IStrategyChangeListener listener);

    /**
     * Method removeMessageListener.
     *
     * @param listener IStrategyChangeListener
     */
    void removeMessageListener(IStrategyChangeListener listener);

    /**
     * Method tradeOrderFilled.
     *
     * @param tradeOrder TradeOrder
     */
    void tradeOrderFilled(TradeOrder tradeOrder);
}
