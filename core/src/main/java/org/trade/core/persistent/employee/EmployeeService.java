package org.trade.core.persistent.employee;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface EmployeeService {

    /**
     * @return List<Employee>
     */
    List<Employee> getEmployees();

    /**
     * @param text String
     * @return List<Employee>
     */
    List<Employee> getEmployeesContainingText(String text);

    /**
     * @param id Long
     * @return Employee
     */
    Employee validateAndGetEmployee(Long id);

    /**
     * @param employee Employee
     * @return Employee
     */
    Employee saveEmployee(Employee employee);

    /**
     * @param employee Employee
     */
    void deleteEmployee(Employee employee);

    /**
     * Method findEmployeeByEmail.
     *
     * @param email String
     * @return Optional<Employee>
     */
    Optional<Employee> findEmployeeByEmail(String email);

    /**
     * Method findEmployeeByName.
     *
     * @param name String
     * @return Employee
     */
    Employee findEmployeeByName(String name);

}
