package org.trade.core.persistent.employee;

import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Employee> findAll() {

        return employeeRepository.findAllByOrderByName();
    }

    public List<Employee> findContainingText(String text) {

        return employeeRepository.findByNameContainingIgnoreCaseOrderByName(text);
    }

    public Employee validateAndGet(Long id) {

        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(String.format("Employee with id %s not found", id)));
    }

    public Employee save(Employee employee) {

        return employeeRepository.save(employee);
    }

    public void delete(Employee employee) {

        if (null == employee) {

            return;
        }

        employeeRepository.delete(employee);
    }

    public Employee findByEmail(String email) {

        return employeeRepository.findByEmail(email).orElse(null);
    }

    public Employee findByName(String name) {

        return employeeRepository.findByName(name).orElse(null);
    }
}