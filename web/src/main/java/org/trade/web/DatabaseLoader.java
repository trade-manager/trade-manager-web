package org.trade.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final EmployeeRepository employees;
    private final UserRepository users;
    private final DomainRepository domains;

    public final static String ROLE_MANAGER = "ROLE_MANAGER";

    @Autowired
    public DatabaseLoader(EmployeeRepository employeeRepository,
                          UserRepository userRepository, DomainRepository domainRepository) {

        this.domains = domainRepository;
        this.employees = employeeRepository;
        this.users = userRepository;
    }


    public void run(String... strings) {

        Domain global = this.domains.findByName("global");
        if (null == global) {
            global = this.domains.save(new Domain("global", "global"));
        }

        User admin = this.users.findByName("admin");
        if (null == admin) {
            admin = this.users.save(new User("admin", "admin",
                    global, ROLE_MANAGER));
        }
        User oliver = this.users.findByName("oliver");
        if (null == oliver) {
            oliver = this.users.save(new User("oliver", "admin",
                    global, ROLE_MANAGER));
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        this.employees.save(new Employee("Frodo", "Baggins", "ring bearer", admin));
        this.employees.save(new Employee("Bilbo", "Baggins", "burglar", admin));
        this.employees.save(new Employee("Gandalf", "the Grey", "wizard", admin));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        this.employees.save(new Employee("Samwise", "Gamgee", "gardener", oliver));
        this.employees.save(new Employee("Merry", "Brandybuck", "pony rider", oliver));
        this.employees.save(new Employee("Peregrin", "Took", "pipe smoker", oliver));

        SecurityContextHolder.clearContext();

    }
}
