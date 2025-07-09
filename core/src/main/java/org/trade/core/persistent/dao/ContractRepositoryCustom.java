package org.trade.core.persistent.dao;

import java.time.ZonedDateTime;
import java.util.List;

public interface ContractRepositoryCustom {

    ContractLite findContractLiteById(Long id);

    List<Contract> findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                           ZonedDateTime expiryDate);
}
