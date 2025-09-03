package org.trade.core.persistent.user;

import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.role.RoleDTO;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record UserDTO(Long id, String username, String name, String firstName, String lastName, String email,
                      List<RoleDTO> role, Domain domain) {

    public static UserDTO from(User user, List<RoleDTO> roles) {

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roles,
                user.getDomain()
        );
    }
}