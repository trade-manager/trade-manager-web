package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.Optional;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface ContractRepository extends AspectRepository<Contract, Long>, ContractRepositoryCustom {

    Optional<Contract> findBySymbol(String symbol);
}
