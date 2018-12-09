package org.trade.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Component
public class DatabaseLoader implements CommandLineRunner {


    public final static String ROLE_MANAGER = "ROLE_MANAGER";

    private final EmployeeRepository employees;
    private final ManagerRepository managers;
    private final DomainRepository domains;

    @Autowired
    public DatabaseLoader(EmployeeRepository employeeRepository,
                          ManagerRepository managerRepository, DomainRepository domainRepository) {

        this.employees = employeeRepository;
        this.managers = managerRepository;
        this.domains = domainRepository;
    }

    @Override
    public void run(String... strings) throws Exception {

        Domain global = this.domains.findByName("global");
        if (null == global) {
            global = this.domains.save(new Domain("global", "global"));
        }

        Manager admin = this.managers.findByName("admin");
        if (null == admin) {
            admin = this.managers.save(new Manager("admin", "admin",
                    global, ROLE_MANAGER));
        }
        Manager oliver = this.managers.findByName("oliver");
        if (null == oliver) {
            oliver = this.managers.save(new Manager("oliver", "admin",
                    global, ROLE_MANAGER));
        }


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "doesn't matter",
                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

        this.employees.save(new Employee("Frodo", "Baggins", "ring bearer", admin));
        this.employees.save(new Employee("Bilbo", "Baggins", "burglar", admin));
        this.employees.save(new Employee("Gandalf", "the Grey", "wizard", admin));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
                        AuthorityUtils.createAuthorityList("ROLE_MANAGER")));

        this.employees.save(new Employee("Samwise", "Gamgee", "gardener", oliver));
        this.employees.save(new Employee("Merry", "Brandybuck", "pony rider", oliver));
        this.employees.save(new Employee("Peregrin", "Took", "pipe smoker", oliver));

        SecurityContextHolder.clearContext();
    }
}
// end::code[]