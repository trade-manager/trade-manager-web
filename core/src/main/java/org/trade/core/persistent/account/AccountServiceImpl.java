package org.trade.core.persistent.account;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(final AccountRepository accountRepository) {

        this.accountRepository = accountRepository;
    }

    public Account findAccountById(Long id) {

        return this.accountRepository.findById(id).orElse(null);
    }

    public List<Account> findAllAccountsOrderByAccountNumber() {

        return this.accountRepository.findAll();
    }

    public Account validateAndGetAccount(String accountNumber) {

        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AccountNotFoundException(String.format("Account with accountNumber %s not found", accountNumber)));
    }

    public Account findAccountByAccountNumber(String accountNumber) {

        return this.accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    public Account saveAccount(Account account) {

        return accountRepository.save(account);
    }

    public void deleteAccount(Account account) {

        if (null == account) {

            return;
        }

        accountRepository.delete(account);
    }
}