package org.trade.core.persistent.account;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface AccountRepository extends AspectRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

}
