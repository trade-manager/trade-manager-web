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
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class UserServiceIT {

    private final static Logger _log = LoggerFactory.getLogger(UserServiceIT.class);

    @Autowired
    private DomainService domainService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

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
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws ClassNotFoundException {


        try {

            User user = userService.findUserByName(userName);
            userService.deleteUser(user);
        } catch (Exception ex) {
            // Do nothing
        }

    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void createUser() {


        Optional<Domain> gobalDomain = domainService.findDomainByName(Domain.GLOBAL);
        assertTrue(gobalDomain.isPresent());
        Optional<Role> role = roleService.findRoleByName(Role.ROLE_ADMIN);
        assertTrue(role.isPresent());
        List<Role> roles = new ArrayList<>();
        roles.add(role.get());
        User user = new User(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", userName, gobalDomain.get(), roles);
        user = userService.saveUser(user);
        assertNotNull(user.getId());
    }
}
