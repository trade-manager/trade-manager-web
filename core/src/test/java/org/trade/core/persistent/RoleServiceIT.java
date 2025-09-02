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
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class RoleServiceIT {

    private final static Logger _log = LoggerFactory.getLogger(RoleServiceIT.class);

    @Autowired
    private RoleService roleService;

    private static final String roleName = "TEST-" + TradestrategyBase.getRandomNumber(4);

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

        Role role = roleService.findRoleByName(roleName);
        roleService.deleteRole(role);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void createRole() {

        Role role = roleService.findRoleByName(roleName);
        assertNull(role);
        role = new Role(roleName, roleName);
        role = roleService.saveRole(role);
        assertNotNull(role.getId());
    }
}
