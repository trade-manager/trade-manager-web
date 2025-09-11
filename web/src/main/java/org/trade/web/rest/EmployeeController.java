package org.trade.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.trade.core.persistent.employee.Employee;
import org.trade.core.persistent.employee.EmployeeRecord;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.trade.web.config.SwaggerConfig.BASIC_AUTH_SECURITY_SCHEME;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    public EmployeeController(final EmployeeService employeeService, UserService userService) {

        this.employeeService = employeeService;
        this.userService = userService;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping
    public List<EmployeeRecord> getEmployees(@RequestParam(value = "text", required = false) String text) {

        List<Employee> employees = (text == null) ? employeeService.getEmployees() : employeeService.getEmployeesContainingText(text);
        return employees.stream().map(EmployeeRecord::from).collect(Collectors.toList());
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EmployeeRecord createEmployee(@Valid @RequestBody EmployeeRecord employeeRecord) {

        Employee employee = null;

        if (null != employeeRecord.user()) {

            User user = userService.validateAndGetUserById(employeeRecord.user().id());

            if (null != user) {

                employee = EmployeeController.from(employeeRecord, user);
            }
        }

        if (null == employee) {

            employee = EmployeeController.from(employeeRecord, null);
        }

        employee = employeeService.saveEmployee(employee);
        return EmployeeRecord.from(employee);
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @DeleteMapping("/{id}")
    public EmployeeRecord deleteEmployee(@PathVariable Long id) {

        Employee employee = employeeService.validateAndGetEmployee(id);
        employeeService.deleteEmployee(employee);
        return EmployeeRecord.from(employee);
    }

    public static Employee from(EmployeeRecord employeeRecord, User user) {

        return new Employee(employeeRecord.name(), employeeRecord.firstName(), employeeRecord.lastName(), employeeRecord.name(), employeeRecord.email(), user);
    }
}
