package org.trade.core.persistent.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Integer>, ContractRepositoryCustom {

    Optional<Contract> findBySymbol(String symbol);
}
