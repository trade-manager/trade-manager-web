package org.trade.core.persistent.role;

import org.trade.core.util.JSONMapper;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record RoleRecord(RoleDTO role) {

    public static RoleRecord from(Role role) {

        return new RoleRecord(
                JSONMapper.convertDTOToEntity(role, RoleDTO.class)
        );
    }
}