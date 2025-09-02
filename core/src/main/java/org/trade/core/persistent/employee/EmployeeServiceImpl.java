package org.trade.core.persistent.employee;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(final EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getEmployees() {

        return employeeRepository.findAllByOrderByName();
    }

    public List<Employee> getEmployeesContainingText(String text) {

        return employeeRepository.findByNameContainingIgnoreCaseOrderByName(text);
    }

    public Employee validateAndGetEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format("Book with id %s not found", id)));
    }

    public Employee saveEmployee(Employee employee) {

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Employee employee) {

        employeeRepository.delete(employee);
    }

    public Optional<Employee> findEmployeeByEmail(String email) {

        return employeeRepository.findByEmail(email);
    }

    public Employee findEmployeeByName(String name) {

        return employeeRepository.findByName(name);
    }


}