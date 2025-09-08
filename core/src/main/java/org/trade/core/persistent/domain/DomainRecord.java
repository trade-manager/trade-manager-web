package org.trade.core.persistent.domain;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record DomainRecord(Long id, String name) {

    public static DomainRecord from(Domain domain) {

        return new DomainRecord(
                domain.getId(),
                domain.getName()
        );
    }
}