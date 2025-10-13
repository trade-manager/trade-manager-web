package org.trade.core.persistent.portfolio;

import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.account.AccountRecord;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradestrategy.TradestrategyRecord;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record PortfolioRecord(Long id,
                              ZonedDateTime createdDate,
                              ZonedDateTime updatedDate,
                              Integer version,
                              Long domainId,
                              String name,
                              String alias,
                              String allocationMethod,
                              String description,
                              Boolean isDefault,
                              List<TradestrategyRecord> tradestrategies,
                              List<AccountRecord> accounts) {

    /**
     * Method from note tradestrategyRecords are LAZY loaded.
     *
     * @param portfolio           Portfolio
     * @param withTradestrategies Boolean
     * @return PortfolioRecord
     */
    public static PortfolioRecord from(Portfolio portfolio, Boolean withTradestrategies) {

        List<AccountRecord> accountRecords = new ArrayList<>();

        if (null != portfolio.getAccounts() && !portfolio.getAccounts().isEmpty()) {

            for (Account account : portfolio.getAccounts()) {

                accountRecords.add(AccountRecord.from(account));
            }
        }

        List<TradestrategyRecord> tradestrategyRecords = new ArrayList<>();

        if (withTradestrategies && null != portfolio.getTradestrategies() && !portfolio.getTradestrategies().isEmpty()) {

            for (Tradestrategy tradestrategy : portfolio.getTradestrategies()) {

                tradestrategyRecords.add(TradestrategyRecord.from(tradestrategy));
            }
        }

        return new PortfolioRecord(
                portfolio.getId(),
                portfolio.getCreatedDate(),
                portfolio.getUpdatedDate(),
                portfolio.getVersion(),
                portfolio.getDomainId(),
                portfolio.getName(),
                portfolio.getAlias(),
                portfolio.getAllocationMethod(),
                portfolio.getDescription(),
                portfolio.getIsDefault(),
                List.copyOf(tradestrategyRecords),
                List.copyOf(accountRecords)
        );
    }

    public Long getId() {
        return id;
    }

    /**
     * Method getCreatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    /**
     * Method getUpdatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Method getDomainId
     *
     * @return Long
     */
    public Long getDomainId() {

        return domainId;
    }

    /**
     * Method getName.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method getAlias.
     *
     * @return String
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Method getAllocationMethod.
     *
     * @return String
     */
    public String getAllocationMethod() {
        return this.allocationMethod;
    }

    /**
     * Method getDescription.
     *
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Method getIsDefault.
     *
     * @return Boolean
     */
    public Boolean getIsDefault() {
        return this.isDefault;
    }

    /**
     * Method getTradestrategies.
     *
     * @return List<TradestrategyRecord>
     */
    public List<TradestrategyRecord> getTradestrategies() {
        return this.tradestrategies;
    }

    /**
     * Method getAccounts.
     *
     * @return List<AccountRecord>
     */
    public List<AccountRecord> getAccounts() {
        return this.accounts;
    }

}
