package org.trade.core.persistent.domain;

import org.springframework.stereotype.Repository;
import org.trade.core.aspect.AspectRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface DomainRepository extends AspectRepository<Domain, Long> {

    /**
     * Method findAllByOrderByName.
     *
     * @return List<Domain>
     */
    List<Domain> findAllByOrderByName();

    /**
     * Method findByName.
     *
     * @param name String
     * @return Optional<Domain>
     */
    Optional<Domain> findByName(String name);
}

