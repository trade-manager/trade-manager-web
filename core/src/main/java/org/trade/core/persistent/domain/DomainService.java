package org.trade.core.persistent.domain;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface DomainService {

    /**
     * @param domain Domain
     * @return Domain
     */
    Domain saveDomain(Domain domain);

    /**
     * @param domain Domain
     */
    void deleteDomain(Domain domain);

    /**
     * Method findDomainByName.
     *
     * @param name String
     * @return Domain
     */
    Domain findDomainByName(String name);
}
