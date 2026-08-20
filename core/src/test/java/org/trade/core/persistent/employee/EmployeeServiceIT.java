package org.trade.core.persistent.employee;

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
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class EmployeeServiceIT extends TradestrategyBase {

    private static final Logger _log = LoggerFactory.getLogger(EmployeeServiceIT.class);

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
    public void setUp() {

        gobalDomain = domainService.findByName(Domain.GLOBAL);
        assertNotNull(gobalDomain.getId());
        adminUser = userService.findUserByName("admin");
        assertNotNull(adminUser.getId());
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() {

        this.deleteRecords();
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
        employeeService.save(employee);
        assertNotNull(employee.getId());
        this.addRecord(employee);
    }

    @Test
    public void findEmployeeAdminRecord() {

        Employee instance = new Employee(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", adminUser);
        employeeService.save(instance);
        assertNotNull(instance.getId());
        this.addRecord(instance);
        Employee instanceNew = employeeService.validateAndGet(instance.getId());
        assertEquals(instance.getId(), instanceNew.getId());
        List<Employee> employees = employeeService.findContainingText(userName);
        List<EmployeeRecord> employeeRecords = employees.stream().map(EmployeeRecord::from).collect(Collectors.toList());
        assertFalse(employeeRecords.isEmpty());
    }

    @Test
    public void findEmployeeManagerRecord() {

        Employee instanceAdmin = new Employee(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", adminUser);
        employeeService.save(instanceAdmin);
        assertNotNull(instanceAdmin.getId());
        this.addRecord(instanceAdmin);

        Role role = roleService.findByName(Role.ROLE_MANAGER);
        assertNotNull(role);
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        String name = "TEST-" + TradestrategyBase.getRandomNumber(4);
        User user = new User(name, name, name, name, name + "@" + Domain.GLOBAL + ".com", name, gobalDomain, roles);
        user = userService.save(user);
        assertNotNull(user.getId());
        this.addRecord(user);

        Employee instance = new Employee(name, name, name, name, name + "@" + Domain.GLOBAL + ".com", user);
        employeeService.save(instance);
        assertNotNull(instance.getId());

        List<Employee> employees = employeeService.findContainingText(name);

        this.addRecord(instance);
        List<EmployeeRecord> employeeRecords = employees.stream().map(EmployeeRecord::from).collect(Collectors.toList());
        assertFalse(employeeRecords.isEmpty());
    }
}
