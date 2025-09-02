package org.trade.core.persistent.role;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface RoleService {

    /**
     * @param role Role
     * @return Role
     */
    Role saveRole(Role role);

    /**
     * @param role Role
     */
    void deleteRole(Role role);

    /**
     * @param name String
     * @return Role
     */
    Role findRoleByName(String name);
}
