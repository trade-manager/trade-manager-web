package org.trade.core.persistent.tradeorder;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradeOrderService {


    /**
     * Method findById.
     *
     * @param id Long
     * @return TradeOrder
     */
    TradeOrder findById(final Long id);

    /**
     * Method validateAndGet.
     *
     * @param id Long
     * @return TradeOrder
     */
    TradeOrder validateAndGet(Long id);

    /**
     * Method findByMaxOrderKey.
     *
     * @return Integer
     */
    Integer findByMaxOrderKey();

    /**
     * Method findByOrderKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    TradeOrder findByOrderKey(Integer orderKey);
}
