package org.trade.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Domain;
import org.trade.core.persistent.dao.Employee;
import org.trade.core.persistent.dao.Role;
import org.trade.core.persistent.dao.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.trade.core.persistent.dao.User.ROLE_MANAGER;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Component
public class DatabaseLoader implements CommandLineRunner {

    private final static Logger _log = LoggerFactory.getLogger(DatabaseLoader.class);

    private TradeService tradeService;

    @Autowired
    public DatabaseLoader(TradeService tradeService) {

        this.tradeService = tradeService;
    }

    public void run(String... strings) {

        Domain global = this.tradeService.findDomainByName("global");
        User admin = this.tradeService.findUserByName("admin");

        if (admin.validatePassword("admin")) {

            _log.info("Info DatabaseLoader::run {} password is valid.", admin.getName());
        }

        User oliver = this.tradeService.findUserByName("oliver");

        if (null == oliver) {

            List<Role> roles = new ArrayList<>();
            roles.add(this.tradeService.findRoleByName(ROLE_MANAGER));
            oliver = this.tradeService.saveUser(new User("oliver", "admin",
                    global, roles));
        } else {

            if (oliver.validatePassword("admin")) {

                _log.info("Info DatabaseLoader::run {} password is valid.", oliver.getName());
            }
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        createEmployee("Frodo", "Baggins", "ring bearer", global.getName(), admin);
        createEmployee("Bilbo", "Baggins", "burglar", global.getName(), admin);
        createEmployee("Gandalf", "Grey", "wizard", global.getName(), admin);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        createEmployee("Samwise", "Gamgee", "gardener", global.getName(), oliver);
        createEmployee("Merry", "Brandybuck", "pony rider", global.getName(), oliver);
        createEmployee("Peregrin", "Took", "pipe smoker", global.getName(), oliver);

        SecurityContextHolder.clearContext();
    }

    private void createEmployee(String firstName, String lastName, String descrition, String domain, User user){

        String email = firstName + "." + lastName +"@" + domain + ".com";
        Optional<Employee> employee = this.tradeService.findEmployeeByEmail(email);

        if (!employee.isPresent()) {

            this.tradeService.saveAspect(new Employee(firstName, lastName, descrition, email, user));
        }
    }
}
