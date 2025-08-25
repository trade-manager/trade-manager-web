package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface AccountRepository extends AspectRepository<Account, Long>, AccountRepositoryCustom {

    Account findByAccountNumber(String accountNumber);
}
