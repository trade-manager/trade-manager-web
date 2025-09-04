package org.trade.core.persistent.employee;

import org.trade.core.util.JSONMapper;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record EmployeeRecord(EmployeeDTO employee) {

    public static EmployeeRecord from(Employee employee) {

        return new EmployeeRecord(
                JSONMapper.convertDTOToEntity(employee, EmployeeDTO.class)
        );
    }
}