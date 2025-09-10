package org.trade.core.persistent.role;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface RoleService {

    /**
     * Method getRoles.
     *
     * @return List<Role>
     */
    List<Role> getRoles();

    /**
     * Method saveRole.
     *
     * @param role Role
     * @return Role
     */
    Role saveRole(Role role);

    /**
     * Method deleteRole.
     *
     * @param role Role
     */
    void deleteRole(Role role);

    /**
     * Method findRoleByName.
     *
     * @param name String
     * @return Role
     */
    Role findRoleByName(String name);

    /**
     * Method validateAndGetDomain.
     *
     * @param name String
     * @return Role
     */
    Role validateAndGetDomain(String name);


    /**
     * Method findAllTopLevelRoleRecords.
     *
     * @return List<RoleRecord>
     */
    List<RoleRecord> findAllTopLevelRoleRecords();

    /**
     * Method findRoleRecordByName.
     *
     * @param name String
     * @return RoleRecord
     */
    RoleRecord findRoleRecordByName(String name);
}
