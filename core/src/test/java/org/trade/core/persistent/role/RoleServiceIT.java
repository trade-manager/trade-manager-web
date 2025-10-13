package org.trade.core.persistent.role;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.trade.core.util.JSONMapper;

import java.util.List;

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
public class RoleServiceIT extends TradestrategyBase {

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
    public void setUp() {
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
    public void createRole() {

        Role role = new Role(roleName, roleName);
        role = roleService.save(role);
        assertNotNull(role.getId());
        this.addRecord(role);
        role = roleService.findByName(roleName);
        assertNotNull(role.getId());
    }

    @Test
    public void findAllTopLevelRoleRecords() {

        List<RoleRecord> roles = roleService.findAllTopLevelRoleRecords();
        assertFalse(roles.isEmpty());
    }


    @Test
    public void findRoleRecordByNameAdmin() throws JsonProcessingException {

        Role managerRole = roleService.findByName(Role.ROLE_MANAGER);
        Role role = new Role(roleName, roleName);
        role.setContainedRole(managerRole);
        role = roleService.save(role);
        assertNotNull(role.getId());
        this.addRecord(role);

        RoleRecord roleRecord = roleService.findRoleRecordByName(Role.ROLE_ADMIN);
        assertNotNull(roleRecord);
        printRoleRecord(roleRecord);
        _log.info("RoleRecord:\n{}", JSONMapper.getJSONString(roleRecord));

        // Manager role
        assertEquals(1, roleRecord.containRoles().size());
        assertEquals(Role.ROLE_MANAGER, roleRecord.containRoles().getFirst().name());

        // USER and roleName
        assertEquals(2, roleRecord.containRoles().getFirst().containRoles().size());
    }

    @Test
    public void findRoleRecordByNameUser() throws JsonProcessingException {

        Role managerRole = roleService.findByName(Role.ROLE_MANAGER);
        Role role = new Role(roleName, roleName);
        role.setContainedRole(managerRole);
        role = roleService.save(role);
        assertNotNull(role.getId());
        this.addRecord(role);

        RoleRecord roleRecord = roleService.findRoleRecordByName(roleName);
        assertNotNull(roleRecord);
        printRoleRecord(roleRecord);
        _log.info("RoleRecord:\n{}", JSONMapper.getJSONString(roleRecord));

        // Manager role
        assertEquals(0, roleRecord.containRoles().size());
        assertEquals(Role.ROLE_MANAGER, roleRecord.containedRole().name());
    }

    @Test
    public void findRoleByNameRecord() {

        Role role = roleService.findByName(Role.ROLE_MANAGER);
        assertNotNull(role);
        RoleRecord roleRecord = RoleRecord.from(role, false);
        assertNotNull(roleRecord);
    }

    private void printRoleRecord(RoleRecord role) {

        _log.info("RoleRecord: {}", role.toString());

        if (role.containRoles() != null && !role.containRoles().isEmpty()) {

            for (RoleRecord containRole : role.containRoles()) {

                printRoleRecord(containRole);
            }
        }
    }
}
