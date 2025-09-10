package org.trade.core.persistent.user;

import org.trade.core.persistent.domain.DomainRecord;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record UserRecord(Long id, String username, String name, String email, String password, DomainRecord domain,
                         List<RoleRecord> roles) {

    public static UserRecord from(User user) {

        List<RoleRecord> roles = new ArrayList<>();

        for (Role role : user.getRoles()) {

            roles.add(RoleRecord.from(role));
        }

        return new UserRecord(

                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                DomainRecord.from(user.getDomain()),
                List.copyOf(roles)
        );
    }
}