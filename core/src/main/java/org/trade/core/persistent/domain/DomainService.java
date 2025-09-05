package org.trade.core.persistent.domain;

import java.util.Optional;

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
    Optional<Domain> findDomainByName(String name);
}
