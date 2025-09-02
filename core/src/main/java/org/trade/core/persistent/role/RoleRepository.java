package org.trade.core.persistent.role;

import org.springframework.stereotype.Repository;
import org.trade.core.dao.AspectRepository;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface RoleRepository extends AspectRepository<Role, Long> {

    Role findByName(String name);

    /**
     * Method findRoleByName.
     *
     * @param name String
     * @return Role
     */
    Role findRoleByName(String name);
}

