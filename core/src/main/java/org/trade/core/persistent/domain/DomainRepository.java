package org.trade.core.persistent.domain;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface DomainRepository extends AspectRepository<Domain, Long> {

    /**
     * Method findByName.
     *
     * @param name String
     * @return Optional<Domain>
     */
    Optional<Domain> findByName(String name);
}

