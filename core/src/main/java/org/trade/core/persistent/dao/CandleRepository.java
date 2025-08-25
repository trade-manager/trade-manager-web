package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CandleRepository extends AspectRepository<Candle, Long>, CandleRepositoryCustom {

    List<Candle> findByContractAndBarSizeOrderByStartPeriodAsc(Contract contract, Integer barSize);

}
