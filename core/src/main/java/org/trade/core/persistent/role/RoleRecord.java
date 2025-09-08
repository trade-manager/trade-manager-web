package org.trade.core.persistent.role;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record RoleRecord(Long id, String name) {

    public static RoleRecord from(Role role) {

        return new RoleRecord(
                role.getId(),
                role.getName()
        );
    }
}