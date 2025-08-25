package org.trade.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Domain;
import org.trade.core.persistent.dao.DomainRepository;
import org.trade.core.persistent.dao.Employee;
import org.trade.core.persistent.dao.EmployeeRepository;
import org.trade.core.persistent.dao.Role;
import org.trade.core.persistent.dao.RoleRepository;
import org.trade.core.persistent.dao.User;
import org.trade.core.persistent.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.trade.core.persistent.dao.User.ROLE_MANAGER;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Component
public class DatabaseLoader implements CommandLineRunner {

    private TradeService tradeService;

    @Autowired
    public DatabaseLoader(TradeService tradeService) {

        this.tradeService = tradeService;
    }

    public void run(String... strings) {

        Domain global = this.tradeService.findDomainByName("global");
        User admin = this.tradeService.findUserByName("admin");
        User oliver = this.tradeService.findUserByName("oliver");

        if (null == oliver) {

            List<Role> roles = new ArrayList<>();
            roles.add(this.tradeService.findRoleByName(ROLE_MANAGER));
            oliver = this.tradeService.saveUser(new User("oliver", "admin",
                    global, roles));
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        this.tradeService.saveAspect(new Employee("Frodo", "Baggins", "ring bearer", admin));
        this.tradeService.saveAspect(new Employee("Bilbo", "Baggins", "burglar", admin));
        this.tradeService.saveAspect(new Employee("Gandalf", "the Grey", "wizard", admin));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("oliver", "doesn't matter",
                        AuthorityUtils.createAuthorityList(ROLE_MANAGER)));

        this.tradeService.saveAspect(new Employee("Samwise", "Gamgee", "gardener", oliver));
        this.tradeService.saveAspect(new Employee("Merry", "Brandybuck", "pony rider", oliver));
        this.tradeService.saveAspect(new Employee("Peregrin", "Took", "pipe smoker", oliver));

        SecurityContextHolder.clearContext();
    }
}
