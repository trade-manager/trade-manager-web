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
import org.trade.core.persistent.employee.EmployeeDTO;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.web.rest.dto.CreateEmployeeRequest;

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

    public EmployeeController(final EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @GetMapping
    public List<EmployeeDTO> getEmployees(@RequestParam(value = "text", required = false) String text) {

        List<Employee> employees = (text == null) ? employeeService.getEmployees() : employeeService.getEmployeesContainingText(text);
        return employees.stream().map(EmployeeDTO::from).collect(Collectors.toList());
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EmployeeDTO createEmployee(@Valid @RequestBody CreateEmployeeRequest createEmployeeRequest) {

        Employee employee = EmployeeController.from(createEmployeeRequest);
        return EmployeeDTO.from(employeeService.saveEmployee(employee));
    }

    @Operation(security = {@SecurityRequirement(name = BASIC_AUTH_SECURITY_SCHEME)})
    @DeleteMapping("/{id}")
    public EmployeeDTO deleteEmployee(@PathVariable Long id) {

        Employee employee = employeeService.validateAndGetEmployee(id);
        employeeService.deleteEmployee(employee);
        return EmployeeDTO.from(employee);
    }

    public static Employee from(CreateEmployeeRequest createEmployeeRequest) {

        return new Employee(createEmployeeRequest.id(), createEmployeeRequest.name(), createEmployeeRequest.firstName(), createEmployeeRequest.lastName(), createEmployeeRequest.description(), createEmployeeRequest.email());
    }
}
