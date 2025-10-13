package org.trade.core.persistent.role;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record RoleRecord(Long id,
                         ZonedDateTime createdDate,
                         ZonedDateTime updatedDate,
                         Integer version,
                         Long domainId,
                         String name, String description,
                         RoleRecord containedRole,
                         List<RoleRecord> containRoles) {

    /**
     * Method fromWithChild note roles are LAZY loaded.
     *
     * @param role         Role
     * @param withChildren Boolean
     * @return RoleRecord
     */
    public static RoleRecord from(final Role role, Boolean withChildren) {

        List<RoleRecord> containRecordRoles = new ArrayList<>();

        if (withChildren && null != role.getContainRoles() && !role.getContainRoles().isEmpty()) {

            for (Role containRole : role.getContainRoles()) {

                containRecordRoles.add(RoleRecord.from(containRole, true));
            }
        }

        return new RoleRecord(
                role.getId(),
                role.getCreatedDate(),
                role.getUpdatedDate(),
                role.getVersion(),
                role.getDomainId(),
                role.getName(),
                role.getDescription(),
                (null != role.getContainedRole() ? RoleRecord.from(role.getContainedRole(), false) : null),
                List.copyOf(containRecordRoles)
        );
    }

    /**
     * @param role
     * @return
     */
    public static RoleRecord from(final Role role) {

        return RoleRecord.from(role, false);
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

    /**
     * @return name
     */
    public String getName() {

        return this.name;
    }

    /**
     * @return description String
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * @return containedRole RoleRecord
     */
    public RoleRecord getContainedRole() {

        return this.containedRole;
    }

    /**
     * Method getContainRoles.
     *
     * @return List<RoleRecord>
     */
    public List<RoleRecord> getContainRoles() {
        return this.containRoles;
    }
}