package org.trade.core.persistent.contract;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface ContractRepository extends AspectRepository<Contract, Long> {

    /**
     * Method findBySymbol.
     *
     * @param symbol String
     * @return Optional<Contract>
     */
    Optional<Contract> findBySymbol(String symbol);
}
