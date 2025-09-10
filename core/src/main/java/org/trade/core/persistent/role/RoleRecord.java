package org.trade.core.persistent.role;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record RoleRecord(Long id, String name, RoleRecord containedRole, List<RoleRecord> containRoles) {

    public static RoleRecord from(final Role role) {

        return new RoleRecord(
                role.getId(),
                role.getName(),
                (null == role.getContainedRole() ? null : RoleRecord.from(role.getContainedRole())),
                null
        );
    }

    public static RoleRecord fromWithChild(final Role role) {

        List<RoleRecord> containRecordRoles = new ArrayList<>();

        if (null != role.getContainRoles() && !role.getContainRoles().isEmpty()) {

            for (Role containRole : role.getContainRoles()) {

                containRecordRoles.add(RoleRecord.fromWithChild(containRole));
            }
        }

        return new RoleRecord(
                role.getId(),
                role.getName(),
                (null != role.getContainedRole() ? RoleRecord.from(role) : null),
                List.copyOf(containRecordRoles)
        );
    }
}