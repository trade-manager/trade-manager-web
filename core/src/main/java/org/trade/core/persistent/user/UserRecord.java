package org.trade.core.persistent.user;

import org.trade.core.persistent.domain.DomainRecord;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleRecord;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record UserRecord(Long id,
                         ZonedDateTime createdDate,
                         ZonedDateTime updatedDate,
                         Integer version,
                         Long domainId,
                         String username,
                         String firstName,
                         String lastName,
                         String name,
                         String email,
                         String password,
                         DomainRecord domain,
                         List<RoleRecord> roles) {

    public static UserRecord from(User user) {

        List<RoleRecord> roles = new ArrayList<>();

        if (null != user.getRoles() && !user.getRoles().isEmpty()) {

            for (Role role : user.getRoles()) {

                roles.add(RoleRecord.from(role, false));
            }
        }

        return new UserRecord(

                user.getId(),
                user.getCreatedDate(),
                user.getUpdatedDate(),
                user.getVersion(),
                user.getDomainId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                (null == user.getDomain() ? null : DomainRecord.from(user.getDomain())),
                List.copyOf(roles)
        );
    }

    public Long getId() {
        return id;
    }

    /**
     * Method getCreatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    /**
     * Method getUpdatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Method getDomainId
     *
     * @return Long
     */
    public Long getDomainId() {

        return domainId;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }
}