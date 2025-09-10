package org.trade.core.persistent.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record RoleRecord(Long id, String name, RoleRecord containedRecord) {

    public static List<RoleRecord> containRoles = null;

    public static RoleRecord from(Role role) {

        return new RoleRecord(
                role.getId(),
                role.getName(),
                (null == role.getContainedRole() ? null : RoleRecord.from(role.getContainedRole()))
        );
    }

    public List<RoleRecord> getContainRoleRecords() {
        return this.containRoles;
    }

    public void setContainRoleRecords(List<RoleRecord> containRoles) {

        this.containRoles = Collections.unmodifiableList(new ArrayList<>(containRoles));
    }
}