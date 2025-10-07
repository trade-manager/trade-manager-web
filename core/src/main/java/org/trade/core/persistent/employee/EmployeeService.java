package org.trade.core.persistent.employee;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface EmployeeService {

    /**
     * Method findAll.
     *
     * @return List<Employee>
     */
    List<Employee> findAll();

    /**
     * Method findContainingText.
     *
     * @param text String
     * @return List<Employee>
     */
    List<Employee> findContainingText(String text);

    /**
     * Method validateAndGet.
     *
     * @param id Long
     * @return Employee
     */
    Employee validateAndGet(Long id);

    /**
     * Method save.
     *
     * @param employee Employee
     * @return Employee
     */
    Employee save(Employee employee);

    /**
     * Method delete.
     *
     * @param employee Employee
     */
    void delete(Employee employee);

    /**
     * Method findByEmail.
     *
     * @param email String
     * @return Employee
     */
    Employee findByEmail(String email);

    /**
     * Method findByName.
     *
     * @param name String
     * @return Employee
     */
    Employee findByName(String name);
}
