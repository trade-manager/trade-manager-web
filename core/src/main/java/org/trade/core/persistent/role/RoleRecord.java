package org.trade.core.persistent.role;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record RoleRecord(Long id, String name, RoleRecord containedRole) {

    public static RoleRecord from(Role role) {

        return new RoleRecord(
                role.getId(),
                role.getName(),
                (null == role.getContainedRole() ? null : RoleRecord.from(role.getContainedRole()))
        );
    }
}