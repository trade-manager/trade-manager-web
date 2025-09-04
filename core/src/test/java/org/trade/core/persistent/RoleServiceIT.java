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
import org.trade.core.persistent.role.RoleDTO;
import org.trade.core.persistent.role.RoleRecord;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.util.JSONMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        if (null != role) {

            roleService.deleteRole(role);
        }
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

    @Test
    public void findAllTopLevelRoles() {

        List<RoleDTO> roles = roleService.findAllTopLevelRoleDTOs();
        assertFalse(roles.isEmpty());
    }

    @Test
    public void findRoleByName() {

        RoleDTO role = roleService.findRoleDTOByName(Role.ROLE_ADMIN);
        assertNotNull(role);

        // Manager role
        assertFalse(role.getContainRoleDTOs().isEmpty());
        assertEquals(Role.ROLE_MANAGER, role.getContainRoleDTOs().getFirst().getName());

        // User role
        assertFalse(role.getContainRoleDTOs().getFirst().getContainRoleDTOs().isEmpty());
        assertEquals(Role.ROLE_USER, role.getContainRoleDTOs().getFirst().getContainRoleDTOs().getFirst().getName());
    }

    @Test
    public void findRoleByNameRecord() {

        Role role = roleService.findRoleByName(Role.ROLE_MANAGER);
        assertNotNull(role);
        RoleRecord roleRecord = RoleRecord.from(role);
        assertNotNull(roleRecord);
    }
}
