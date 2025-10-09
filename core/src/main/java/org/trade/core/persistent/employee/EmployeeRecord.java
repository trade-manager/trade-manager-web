package org.trade.core.persistent.employee;

import org.trade.core.persistent.user.UserRecord;

import java.time.ZonedDateTime;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record EmployeeRecord(Long id,
                             ZonedDateTime createdDate,
                             ZonedDateTime updatedDate,
                             Integer version,
                             Long domainId,
                             String name,
                             String firstName,
                             String lastName,
                             String description,
                             String email,
                             UserRecord user) {

    public static EmployeeRecord from(Employee employee) {

        return new EmployeeRecord(
                employee.getId(),
                employee.getCreatedDate(),
                employee.getUpdatedDate(),
                employee.getVersion(),
                employee.getDomainId(),
                employee.getName(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDescription(),
                employee.getEmail(),
                (null == employee.getUser() ? null : UserRecord.from(employee.getUser()))
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

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getEmail() {
        return this.email;
    }

    public UserRecord getUser() {
        return this.user;
    }

}