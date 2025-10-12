package org.trade.core.persistent.tradeposition;

import org.springframework.stereotype.Service;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradePositionServiceImpl implements TradePositionService {

    private final TradePositionRepository tradePositionRepository;

    public TradePositionServiceImpl(final TradePositionRepository tradePositionRepository) {

        this.tradePositionRepository = tradePositionRepository;
    }

    public TradePosition findById(final Long id) {

        return this.tradePositionRepository.findById(id).orElse(null);
    }

    public TradePosition validateAndGet(Long id) {

        return this.tradePositionRepository.findById(id).orElseThrow(() -> new TradePositionNotFoundException(String.format("TradePosition with id %s not found", id)));
    }
}