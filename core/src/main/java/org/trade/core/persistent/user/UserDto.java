package org.trade.core.persistent.user;

import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.role.Role;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record UserDto(Long id, String username, String name, String firstName, String lastName, String email,
                      Domain domain, Role role) {

    public static UserDto from(User user) {

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDomain(),
                user.getRole()
        );
    }
}