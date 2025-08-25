package org.trade.core.persistent.dao;

import java.time.ZonedDateTime;
import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface ContractRepositoryCustom {

    List<Contract> findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                           ZonedDateTime expiryDate);

    ContractLite findContractLiteById(Long id);
}
