package org.trade.core.persistent.dao;

import java.time.ZonedDateTime;

public interface ContractRepositoryCustom {

    ContractLite findContractLiteById(Integer id);

    Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                     ZonedDateTime expiryDate);

}
