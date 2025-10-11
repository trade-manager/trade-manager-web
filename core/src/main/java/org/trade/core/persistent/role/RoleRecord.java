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
     * Method from note roles are LAZY loaded., hence we do not get the children.
     *
     * @param role Role
     * @return RoleRecord
     */
    public static RoleRecord from(final Role role) {

        return new RoleRecord(
                role.getId(),
                role.getCreatedDate(),
                role.getUpdatedDate(),
                role.getVersion(),
                role.getDomainId(),
                role.getName(),
                role.getDescription(),
                (null == role.getContainedRole() ? null : RoleRecord.from(role.getContainedRole())),
                null
        );
    }

    /**
     * Method fromWithChild note roles are LAZY loaded.
     *
     * @param role Role
     * @return RoleRecord
     */
    public static RoleRecord fromWithChild(final Role role) {

        List<RoleRecord> containRecordRoles = new ArrayList<>();

        if (null != role.getContainRoles() && !role.getContainRoles().isEmpty()) {

            for (Role containRole : role.getContainRoles()) {

                containRecordRoles.add(RoleRecord.fromWithChild(containRole));
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
                (null != role.getContainedRole() ? RoleRecord.from(role.getContainedRole()) : null),
                List.copyOf(containRecordRoles)
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