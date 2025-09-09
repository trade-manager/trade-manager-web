package org.trade.core.persistent.role;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;
import org.trade.core.persistent.domain.Domain;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface RoleRepository extends AspectRepository<Role, Long> {

    /**
     * Method findAllByOrderByName.
     *
     * @return List<Role>
     */
    List<Role> findAllByOrderByName();

    /**
     * Method findByName.
     *
     * @param name String
     * @return Optional<Role>
     */
    Optional<Role> findByName(String name);

    /**
     * Method findByContainedRoleIsNull.
     *
     * @return List<Role>
     */
    List<Role> findByContainedRoleIsNull();
}

