package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

import java.util.Optional;

public interface ContractRepository extends AspectRepository<Contract, Integer>, ContractRepositoryCustom {

    Optional<Contract> findBySymbol(String symbol);
}
