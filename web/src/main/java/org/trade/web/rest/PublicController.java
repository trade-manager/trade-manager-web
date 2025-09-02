package org.trade.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.user.UserService;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserService userService;
    private final EmployeeService employeeService;

    public PublicController(final UserService userService, final EmployeeService employeeService) {

        this.userService = userService;
        this.employeeService = employeeService;
    }

    @GetMapping("/numberOfUsers")
    public Integer getNumberOfUsers() {

        return userService.getUsers().size();
    }

    @GetMapping("/numberOfEmployees")
    public Integer getNumberOfEmployees() {

        return employeeService.getEmployees().size();
    }
}
