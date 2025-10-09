package org.trade.core.persistent.candle;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;
import org.trade.core.persistent.contract.Contract;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface CandleRepository extends AspectRepository<Candle, Long> {

    /**
     * Method findByContractAndBarSizeOrderByStartPeriodAsc.
     *
     * @param contract Contract
     * @param barSize  Integer
     * @return List<Candle>
     */
    List<Candle> findByContractAndBarSizeOrderByStartPeriodAsc(Contract contract, Integer barSize);

}
