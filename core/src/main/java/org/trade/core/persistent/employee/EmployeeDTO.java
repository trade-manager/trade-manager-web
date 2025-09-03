package org.trade.core.persistent.employee;

import org.trade.core.persistent.user.User;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record EmployeeDTO(Long id, String name, String firstName, String lastName, String description, String email,
                          User user) {

    public static EmployeeDTO from(Employee employee) {

        return new EmployeeDTO(
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