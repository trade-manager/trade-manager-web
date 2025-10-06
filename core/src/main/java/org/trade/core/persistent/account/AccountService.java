package org.trade.core.persistent.account;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface AccountService {

    /**
     * Method findAccountById.
     *
     * @return Account
     */
    Account findAccountById(Long id);

    /**
     * Method findAccountByAccountNumber.
     *
     * @return Account
     */
    Account findAccountByAccountNumber(String accountNumber);

    /**
     * Method findAllAccountsOrderByAccountNumber.
     *
     * @return List<Account>
     */
    List<Account> findAllAccountsOrderByAccountNumber();

    /**
     * Method validateAndGetAccount.
     *
     * @param accountNumber String
     * @return Account
     */
    Account validateAndGetAccount(String accountNumber);

    /**
     * Method saveAccount.
     *
     * @param account Account
     * @return account
     */
    Account saveAccount(Account account);

    /**
     * Method deleteAccount.
     *
     * @param account Account
     */
    void deleteAccount(Account account);
}
