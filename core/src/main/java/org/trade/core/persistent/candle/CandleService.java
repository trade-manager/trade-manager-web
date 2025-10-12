package org.trade.core.persistent.candle;


import org.trade.core.persistent.contract.Contract;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CandleService {


    /**
     * Method findById.
     *
     * @return Candle
     */
    Candle findById(Long id);

    /**
     * Method findById.
     *
     * @return Candle
     */
    Candle validateAndGet(Long id);

    /**
     * Method findByContractDateRangeBarSize.
     *
     * @param contract    Contract
     * @param startPeriod ZonedDateTime
     * @param endPeriod   ZonedDateTime
     * @param barSize     Integer
     * @return List<Candle>
     */
    List<Candle> findByContractDateRangeBarSize(Contract contract, ZonedDateTime startPeriod,
                                                ZonedDateTime endPeriod, Integer barSize);

    /**
     * Method findCount.
     *
     * @param contract Contract
     * @return Long
     */
    Long findCount(Contract contract);

    /**
     * Method findByContractAndBarSize.
     *
     * @param contract Contract
     * @param barSize  Integer
     * @return List<Candle>
     */
    List<Candle> findByContractAndBarSize(Contract contract, Integer barSize);

}
