package org.trade.core.persistent.portfolio;

import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.account.AccountRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record PortfolioRecord(Long id, String name,
                              String alias,
                              String allocationMethod,
                              String description,
                              Boolean isDefault,
                              List<AccountRecord> accounts) {

    /**
     * Method from note roles are LAZY loaded.
     *
     * @param portfolio Portfolio
     * @return PortfolioRecord
     */
    public static PortfolioRecord from(Portfolio portfolio) {

        List<AccountRecord> accountRecords = new ArrayList<>();

        if (null != portfolio.getAccounts() && !portfolio.getAccounts().isEmpty()) {

            for (Account account : portfolio.getAccounts()) {

                accountRecords.add(AccountRecord.from(account));
            }
        }

        return new PortfolioRecord(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getAlias(),
                portfolio.getAllocationMethod(),
                portfolio.getDescription(),
                portfolio.getIsDefault(),
                List.copyOf(accountRecords)
        );
    }
}
