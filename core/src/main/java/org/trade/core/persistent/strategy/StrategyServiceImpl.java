package org.trade.core.persistent.strategy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    private final StrategyRepository strategyRepository;

    public StrategyServiceImpl(final StrategyRepository strategyRepository) {

        this.strategyRepository = strategyRepository;
    }

    @Transactional
    public Strategy findById(final Long id) {

        Optional<Strategy> strategy = strategyRepository.findById(id);

        if (strategy.isPresent()) {

            strategy.get().getRules().size();
            return strategy.get();
        }

        return null;
    }

    /**
     * Method findByName.
     *
     * @param name String
     * @return Strategy
     */
    @Transactional
    public Strategy findByName(String name) {

        Strategy strategy = this.strategyRepository.findByName(name).orElse(null);

        if (null != strategy) {

            strategy.getRules().size();
            return strategy;
        }

        return null;

    }

    @Transactional
    public List<Strategy> findAll() {

        List<Strategy> strategies = strategyRepository.findAll();

        for (Strategy strategy : strategies) {

            strategy.getRules().size();
        }

        return strategies;
    }

    public Strategy validateAndGet(String name) {

        return strategyRepository.findByName(name).orElseThrow(() -> new StrategyNotFoundException(String.format("Strategy with name %s not found", name)));
    }


}