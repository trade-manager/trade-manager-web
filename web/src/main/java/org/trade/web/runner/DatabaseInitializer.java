package org.trade.web.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.employee.Employee;
import org.trade.core.persistent.employee.EmployeeService;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.trade.core.persistent.role.Role.ROLE_ADMIN;
import static org.trade.core.persistent.role.Role.ROLE_MANAGER;
import static org.trade.core.persistent.role.Role.ROLE_USER;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final static Logger _log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final UserService userService;
    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final DomainService domainService;
    private final PasswordEncoder passwordEncoder;

    private static final String EMPLOYEES_STR =
            """
                    9781603090773;Fred Luddy;Fred;Luddy;Fred Luddy user;fred.luddy@global.com
                    9781603090698;John Doe;John;Doe;John Doe user;john.doe@global.com
                    """;

    private static final List<User> USERS = Arrays.asList(

            new User("admin", "admin", "Admin", "admin@" + Domain.GLOBAL + ".com", ROLE_ADMIN),
            new User("user", "user", "User", "user@" + Domain.GLOBAL + " .com", ROLE_USER)
    );

    public DatabaseInitializer(final UserService userService, final EmployeeService employeeService, final RoleService roleService, final DomainService domainService, final PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.domainService = domainService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Optional<Domain> global = this.domainService.findDomainByName("global");
        User admin = this.userService.findUserByName("admin");

        if (null != admin) {

            if (!this.passwordEncoder.matches("admin", admin.getPassword())) {

                admin.setPassword(this.passwordEncoder.encode("admin"));
                admin = this.userService.saveUser(admin);
                _log.info("Info DatabaseLoader::run {} password: {}", admin.getName(), admin.getPassword());
            }
        }

        User oliver = this.userService.findUserByName("oliver");

        if (null == oliver) {

            List<Role> roles = new ArrayList<>();
            roles.add(this.roleService.findRoleByName(ROLE_USER).get());
            String name = "oliver";
            String email = name + "." + name + "@" + global.get().getName() + ".com";
            oliver = this.userService.saveUser(new User(name, name, name, name, email, passwordEncoder.encode("user"), global.get(), roles));
        } else {

            if (!this.passwordEncoder.matches("user", oliver.getPassword())) {

                oliver.setPassword(this.passwordEncoder.encode("user"));
                oliver = this.userService.saveUser(oliver);
                _log.info("Info DatabaseLoader::run {} password: {}", oliver.getName(), oliver.getPassword());
            }
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        createEmployee("Frodo", "Baggins", "ring bearer", global.get().getName(), admin);
        createEmployee("Bilbo", "Baggins", "burglar", global.get().getName(), admin);
        createEmployee("Gandalf", "Grey", "wizard", global.get().getName(), admin);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        createEmployee("Samwise", "Gamgee", "gardener", global.get().getName(), oliver);
        createEmployee("Merry", "Brandybuck", "pony rider", global.get().getName(), oliver);
        createEmployee("Peregrin", "Took", "pipe smoker", global.get().getName(), oliver);

        SecurityContextHolder.clearContext();

        if (!userService.getUsers().isEmpty()) {

            return;
        }

        USERS.forEach(user -> {

            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            this.userService.saveUser(user);
        });

        getEmployees().forEach(employeeService::saveEmployee);
        _log.info("Database initialized");
    }

    private List<Employee> getEmployees() {

        return Arrays.stream(EMPLOYEES_STR.split("\n"))
                .map(employeeInfoStr -> employeeInfoStr.split(";"))
                .map(employeeInfoArr -> new Employee(Long.parseLong(employeeInfoArr[0]), employeeInfoArr[1], employeeInfoArr[2], employeeInfoArr[3], employeeInfoArr[4], employeeInfoArr[5]))
                .collect(Collectors.toList());
    }

    private void createEmployee(String firstName, String lastName, String description, String domain, User user) {

        String email = firstName + "." + lastName + "@" + domain + ".com";
        Optional<Employee> employee = this.employeeService.findEmployeeByEmail(email);

        if (!employee.isPresent()) {

            this.employeeService.saveEmployee(new Employee(firstName + " " + lastName, firstName, lastName, description, email, user));
        }
    }
}
