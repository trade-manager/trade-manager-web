package org.trade.core.persistent.role;

import java.util.List;

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

    /**
     * @return List<RoleDTO>
     */
    List<RoleDTO> findAllTopLevelRoleDTOs();

    /**
     * @param name String
     * @return RoleDTO
     */
    RoleDTO findRoleDTOByName(String name);
}
