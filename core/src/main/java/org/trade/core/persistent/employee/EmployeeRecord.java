package org.trade.core.persistent.employee;

import org.trade.core.persistent.user.UserDTO;
import org.trade.core.persistent.user.UserRecord;
import org.trade.core.util.JSONMapper;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record EmployeeRecord(Long id, String name,  String firstName,   String lastName, String email) {

    public static EmployeeRecord from(Employee employee) {

        EmployeeDTO employeeDTO = JSONMapper.convertDTOToEntity(employee, EmployeeDTO.class);

        return new EmployeeRecord(
                employeeDTO.getId(),
                employeeDTO.getName(),
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                employeeDTO.getEmail()
        );
    }
}