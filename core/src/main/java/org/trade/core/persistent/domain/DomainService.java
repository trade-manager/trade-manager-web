package org.trade.core.persistent.domain;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface DomainService {

    /**
     * Method finaAll.
     *
     * @return List<Domain>
     */
    List<Domain> finaAll();

    /**
     * Method save.
     *
     * @param domain Domain
     * @return Domain
     */
    Domain save(Domain domain);

    /**
     * Method delete.
     *
     * @param domain Domain
     */
    void delete(Domain domain);

    /**
     * Method findByName.
     *
     * @param name String
     * @return Domain
     */
    Domain findByName(String name);

    /**
     * Method validateAndGet.
     *
     * @param name String
     * @return Domain
     */
    Domain validateAndGet(String name);
}
