package org.trade.core.persistent.tradeorderfill;

import org.springframework.stereotype.Service;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradeOrderfillServiceImpl implements TradeOrderfillService {

    private final TradeOrderfillRepository tradeOrderfillRepository;

    public TradeOrderfillServiceImpl(final TradeOrderfillRepository tradeOrderfillRepository) {
        this.tradeOrderfillRepository = tradeOrderfillRepository;
    }

    public TradeOrderfill validateAndGet(Long id) {

        return this.tradeOrderfillRepository.findById(id).orElseThrow(() -> new TradeOrderfillNotFoundException(String.format("TradeOrderfill with id %s not found", id)));
    }

    public TradeOrderfill findByExecId(String execId) {

        return tradeOrderfillRepository.findByExecId(execId);
    }
}