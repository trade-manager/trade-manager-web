package org.trade.core.persistent.employee;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface EmployeeService {

    /**
     * Method getEmployees.
     *
     * @return List<Employee>
     */
    List<Employee> getEmployees();

    /**
     * Method getEmployeesContainingText.
     *
     * @param text String
     * @return List<Employee>
     */
    List<Employee> getEmployeesContainingText(String text);

    /**
     * Method validateAndGetEmployee.
     *
     * @param id Long
     * @return Employee
     */
    Employee validateAndGetEmployee(Long id);

    /**
     * Method saveEmployee.
     *
     * @param employee Employee
     * @return Employee
     */
    Employee saveEmployee(Employee employee);

    /**
     * Method deleteEmployee.
     *
     * @param employee Employee
     */
    void deleteEmployee(Employee employee);

    /**
     * Method findEmployeeByEmail.
     *
     * @param email String
     * @return Employee
     */
    Employee findEmployeeByEmail(String email);

    /**
     * Method findEmployeeByName.
     *
     * @param name String
     * @return Employee
     */
    Employee findEmployeeByName(String name);
}
