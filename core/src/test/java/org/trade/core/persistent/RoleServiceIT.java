package org.trade.core.persistent;

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
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleDTO;
import org.trade.core.persistent.role.RoleRecord;
import org.trade.core.persistent.role.RoleService;
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
public class RoleServiceIT {

    private final static Logger _log = LoggerFactory.getLogger(RoleServiceIT.class);

    @Autowired
    private TradeService tradeService;

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

        TradestrategyBase.deleteRecords(tradeService);
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
        role = roleService.saveRole(role);
        assertNotNull(role.getId());
        TradestrategyBase.addRecord(role);
        role = roleService.findRoleByName(roleName);
        assertNotNull(role.getId());
    }

    @Test
    public void findAllTopLevelRoles() {

        List<RoleDTO> roles = roleService.findAllTopLevelRoleDTOs();
        assertFalse(roles.isEmpty());
    }

    @Test
    public void findRoleDTOByName() {

        Role managerRole = roleService.findRoleByName(Role.ROLE_MANAGER);
        Role role = new Role(roleName, roleName);
        role.setContainedRole(managerRole);
        role = roleService.saveRole(role);
        assertNotNull(role.getId());
        TradestrategyBase.addRecord(role);

        RoleDTO roleDTO = roleService.findRoleDTOByName(Role.ROLE_ADMIN);
        assertNotNull(roleDTO);
        printRoleRecord(roleDTO);

        // Manager role
        assertFalse(roleDTO.getContainRoleDTOs().isEmpty());
        assertEquals(Role.ROLE_MANAGER, roleDTO.getContainRoleDTOs().getFirst().getName());

        // User role
        assertFalse(roleDTO.getContainRoleDTOs().getFirst().getContainRoleDTOs().isEmpty());
        assertEquals(Role.ROLE_USER, roleDTO.getContainRoleDTOs().getFirst().getContainRoleDTOs().getFirst().getName());
    }

    @Test
    public void findRoleRecordByNameAdmin() throws JsonProcessingException {

        Role managerRole = roleService.findRoleByName(Role.ROLE_MANAGER);
        Role role = new Role(roleName, roleName);
        role.setContainedRole(managerRole);
        role = roleService.saveRole(role);
        assertNotNull(role.getId());
        TradestrategyBase.addRecord(role);

        RoleRecord roleRecord = roleService.findRoleRecordByName(Role.ROLE_ADMIN);
        assertNotNull(roleRecord);
        printRoleRecord(roleRecord);
        _log.info("RoleRecord:\n{}" , JSONMapper.getJSONString(roleRecord));

        // Manager role
        assertEquals(1, roleRecord.containRoles().size());
        assertEquals(Role.ROLE_MANAGER, roleRecord.containRoles().getFirst().name());

        // USER and roleName
        assertEquals(2, roleRecord.containRoles().getFirst().containRoles().size());
    }

    @Test
    public void findRoleRecordByNameUser() throws JsonProcessingException {

        Role managerRole = roleService.findRoleByName(Role.ROLE_MANAGER);
        Role role = new Role(roleName, roleName);
        role.setContainedRole(managerRole);
        role = roleService.saveRole(role);
        assertNotNull(role.getId());
        TradestrategyBase.addRecord(role);

        RoleRecord roleRecord = roleService.findRoleRecordByName(roleName);
        assertNotNull(roleRecord);
        printRoleRecord(roleRecord);
        _log.info("RoleRecord:\n{}" , JSONMapper.getJSONString(roleRecord));

        // Manager role
        assertEquals(0, roleRecord.containRoles().size());
        assertEquals(Role.ROLE_MANAGER, roleRecord.containedRole().name());
    }

    @Test
    public void findRoleByNameRecord() {

        Role role = roleService.findRoleByName(Role.ROLE_MANAGER);
        assertNotNull(role);
        RoleRecord roleRecord = RoleRecord.from(role);
        assertNotNull(roleRecord);
    }

    private void printRoleRecord(RoleDTO role) {

        _log.info("Name: {}", role.getName());

        if (role.getContainRoleDTOs() != null && !role.getContainRoleDTOs().isEmpty()) {

            for (RoleDTO containRole : role.getContainRoleDTOs()) {

                printRoleRecord(containRole);
            }
        }
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
