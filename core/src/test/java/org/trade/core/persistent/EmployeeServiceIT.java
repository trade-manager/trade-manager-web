package org.trade.core.persistent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.employee.Employee;
import org.trade.core.persistent.employee.EmployeeDTO;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserDTO;
import org.trade.core.persistent.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class EmployeeServiceIT {

    private final static Logger _log = LoggerFactory.getLogger(EmployeeServiceIT.class);

    @Autowired
    private DomainService domainService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    private Domain gobalDomain;
    private User adminUser;
    private static final String userName = "TEST-" + TradestrategyBase.getRandomNumber(4);

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        gobalDomain = domainService.findDomainByName(Domain.GLOBAL);
        assertNotNull(gobalDomain.getId());
        adminUser = userService.findUserByName("admin");
        assertNotNull(adminUser.getId());
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws ClassNotFoundException {

        Employee employee = employeeService.findEmployeeByName(userName);

        if (null != employee) {

            employeeService.deleteEmployee(employee);
        }

        User user = userService.findUserByName(userName);

        if (null != user) {

            userService.deleteUser(user);
        }
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void createEmployee() {

        Employee employee = new Employee(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", adminUser);
        employeeService.saveEmployee(employee);
        assertNotNull(employee.getId());
    }

    @Test
    public void findEmployeeAdminDTO() {

        Employee instance = new Employee(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", adminUser);
        employeeService.saveEmployee(instance);
        assertNotNull(instance.getId());

        List<Employee> employees = employeeService.getEmployeesContainingText(userName);
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();

        for (Employee employee : employees) {

            UserDTO user = UserDTO.from(employee.getUser(), employee.getUser().getRoleDTOs());
            EmployeeDTO employeeDTO = EmployeeDTO.from(employee, user);
            employeeDTOs.add(employeeDTO);
        }
        //List<EmployeeDTO> employeesDTO =  employees.stream().map(EmployeeDTO::from).collect(Collectors.toList());
        assertFalse(employeeDTOs.isEmpty());
    }

    @Test
    public void findEmployeeManagerDTO() {

        Employee instanceAdmin = new Employee(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", adminUser);
        employeeService.saveEmployee(instanceAdmin);
        assertNotNull(instanceAdmin.getId());

        Role role = roleService.findRoleByName(Role.ROLE_MANAGER);
        assertNotNull(role.getId());
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        String name =  "TEST-" + TradestrategyBase.getRandomNumber(4);
        User user = new User(name, name, name, name, name + "@" + Domain.GLOBAL + ".com", name, gobalDomain, roles);
        user = userService.saveUser(user);
        assertNotNull(user.getId());

        Employee instance = new Employee(name, name, name, name, name + "@" + Domain.GLOBAL + ".com", user);
        employeeService.saveEmployee(instance);
        assertNotNull(instance.getId());

        List<Employee> employees = employeeService.getEmployeesContainingText(name);
        List<EmployeeDTO> employeeDTOs = new ArrayList<>();

        for (Employee employee : employees) {

            UserDTO userDTO = UserDTO.from(employee.getUser(), employee.getUser().getRoleDTOs());
            EmployeeDTO employeeDTO = EmployeeDTO.from(employee, userDTO);
            employeeDTOs.add(employeeDTO);
        }

        employeeService.deleteEmployee(instance);
        //List<EmployeeDTO> employeesDTO =  employees.stream().map(EmployeeDTO::from).collect(Collectors.toList());
        assertFalse(employeeDTOs.isEmpty());
    }
}
