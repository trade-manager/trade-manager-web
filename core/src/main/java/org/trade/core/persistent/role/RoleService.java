package org.trade.core.persistent.role;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface RoleService {

    /**
     * Method findAll.
     *
     * @return List<Role>
     */
    List<Role> findAll();

    /**
     * Method save.
     *
     * @param role Role
     * @return Role
     */
    Role save(Role role);

    /**
     * Method delete.
     *
     * @param role Role
     */
    void delete(Role role);

    /**
     * Method findByName.
     *
     * @param name String
     * @return Role
     */
    Role findByName(String name);

    /**
     * Method validateAndGet.
     *
     * @param name String
     * @return Role
     */
    Role validateAndGet(String name);


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
