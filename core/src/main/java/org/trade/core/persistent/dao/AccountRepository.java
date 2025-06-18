package org.trade.core.persistent.dao;

import org.trade.core.dao.AspectRepository;

public interface AccountRepository extends AspectRepository<Account, Integer>, AccountRepositoryCustom {

    Account findByAccountNumber(String accountNumber);
}
