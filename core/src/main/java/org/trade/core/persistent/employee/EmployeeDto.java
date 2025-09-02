package org.trade.core.persistent.employee;

import org.trade.core.persistent.user.User;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record EmployeeDto(Long id, String name, String firstName, String lastName, String description, String email,
                          User user) {

    public static EmployeeDto from(Employee employee) {

        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDescription(),
                employee.getEmail(),
                employee.getUser()
        );
    }
}