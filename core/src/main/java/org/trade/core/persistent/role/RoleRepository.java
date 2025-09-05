package org.trade.core.persistent.role;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface RoleRepository extends AspectRepository<Role, Long> {

    Optional<Role> findByName(String name);

    /**
     * Method findByContainedRoleIsNull.
     *
     * @return List<Role>
     */
    List<Role> findByContainedRoleIsNull();
}

