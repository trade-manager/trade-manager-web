package org.trade.core.persistent.tradeposition;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradePositionService {

    /**
     * Method findById.
     *
     * @param id Long
     * @return TradePosition
     */
    TradePosition findById(final Long id);

}
