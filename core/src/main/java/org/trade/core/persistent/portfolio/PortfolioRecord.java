package org.trade.core.persistent.portfolio;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record PortfolioRecord(Long id, String name,
                              String alias,
                              String allocationMethod,
                              String description,
                              Boolean isDefault) {

    /**
     * Method from note roles are LAZY loaded.
     *
     * @param portfolio Portfolio
     * @return PortfolioRecord
     */
    public static PortfolioRecord from(Portfolio portfolio) {

        return new PortfolioRecord(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getAlias(),
                portfolio.getAllocationMethod(),
                portfolio.getDescription(),
                portfolio.getIsDefault()
        );
    }
}
