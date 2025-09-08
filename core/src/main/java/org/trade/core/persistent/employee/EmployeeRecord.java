package org.trade.core.persistent.employee;

import org.trade.core.persistent.user.UserRecord;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record EmployeeRecord(Long id, String name, String firstName, String lastName, String email, UserRecord user) {

    public static EmployeeRecord from(Employee employee) {

        return new EmployeeRecord(
                employee.getId(),
                employee.getName(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                UserRecord.from(employee.getUser())
        );
    }
}