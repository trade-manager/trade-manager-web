package org.trade.core.persistent.domain;

import org.trade.core.persistent.role.Role;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface DomainService {

    /**
     * Method getDomains.
     *
     * @return List<Domain>
     */
    List<Domain> getDomains();

    /**
     * Method saveDomain.
     *
     * @param domain Domain
     * @return Domain
     */
    Domain saveDomain(Domain domain);

    /**
     * Method deleteDomain.
     *
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

    /**
     * Method validateAndGetDomain.
     *
     * @param name String
     * @return Domain
     */
    Domain validateAndGetDomain(String name);
}
