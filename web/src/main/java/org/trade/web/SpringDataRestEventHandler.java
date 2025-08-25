package org.trade.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.trade.core.persistent.dao.Employee;
import org.trade.core.persistent.dao.Role;
import org.trade.core.persistent.dao.User;
import org.trade.core.persistent.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.trade.core.persistent.dao.User.ROLE_MANAGER;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Component
@RepositoryEventHandler(Employee.class)
public class SpringDataRestEventHandler {

    private final UserRepository userRepository;

    @Autowired
    public SpringDataRestEventHandler(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @HandleBeforeCreate
    @HandleBeforeSave
    public void applyUserInformationUsingSecurityContext(Employee employee) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByName(name);

        if (user == null) {

            List<Role> roles = new ArrayList<>();
            roles.add(new Role(ROLE_MANAGER, ROLE_MANAGER));
            User newUser = new User();
            newUser.setName(name);
            newUser.setRoles(roles);
            user = this.userRepository.save(newUser);
        }

        employee.setUser(user);
    }
}

