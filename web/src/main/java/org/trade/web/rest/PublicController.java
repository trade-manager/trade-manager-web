package org.trade.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trade.core.persistent.domain.DomainRecord;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.role.RoleRecord;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    private final DomainService domainService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmployeeService employeeService;

    public PublicController(final DomainService domainService, final RoleService roleService, final UserService userService, final EmployeeService employeeService) {

        this.domainService = domainService;
        this.roleService = roleService;
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

    @GetMapping("/domains")
    public List<DomainRecord> getDomains() {

        return domainService.getDomains().stream().map(DomainRecord::from).collect(Collectors.toList());
    }

    @GetMapping("/roles")
    public List<RoleRecord> getRoles() {

        return roleService.getRoles().stream().map(RoleRecord::from).collect(Collectors.toList());
    }
}
