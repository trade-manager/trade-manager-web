package org.trade.core.persistent.account;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface AccountService {

    /**
     * Method findById.
     *
     * @return Account
     */
    Account findById(Long id);

    /**
     * Method findByAccountNumber.
     *
     * @return Account
     */
    Account findByAccountNumber(String accountNumber);

    /**
     * Method findAllOrderByAccountNumber.
     *
     * @return List<Account>
     */
    List<Account> findAllOrderByAccountNumber();

    /**
     * Method validateAndGet.
     *
     * @param accountNumber String
     * @return Account
     */
    Account validateAndGet(String accountNumber);

    /**
     * Method save.
     *
     * @param account Account
     * @return account
     */
    Account save(Account account);

    /**
     * Method delete.
     *
     * @param account Account
     */
    void delete(Account account);
}
