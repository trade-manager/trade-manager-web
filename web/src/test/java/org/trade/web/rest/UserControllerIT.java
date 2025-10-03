package org.trade.web.rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.domain.DomainService;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.role.RoleService;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserService;
import org.trade.web.service.CustomUserDetailsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ComponentScan(basePackages = {"org.trade.web.rest", "org.trade.web.service"})
@ContextConfiguration(classes = {ApplicationRepositoryConfig.class},
        initializers = ApplicationProfileInitializer.class)
@AutoConfigureMockMvc
class UserControllerIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(UserControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DomainService domainService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final String userName = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static final String adminUserName = "admin";
    private static final String password = "admin";

    /**
     * Method setUp.
     */
    @BeforeAll
    public static void setUp() {

    }

    /**
     * Method tearDown.
     */
    @AfterAll
    public static void tearDown() {

    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUpTest() {

        Domain gobalDomain = domainService.findDomainByName(Domain.GLOBAL);
        assertNotNull(gobalDomain);
        Role role = roleService.findRoleByName(Role.ROLE_ADMIN);
        assertNotNull(role);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        User user = userService.findUserByName(userName);
        assertNull(user);
        user = new User(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", this.passwordEncoder.encode(password), gobalDomain, roles);
        user = userService.saveUser(user);
        assertNotNull(user.getId());
        this.addRecord(user);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDownTest() {

        this.deleteRecords();
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void getUserByName() throws Exception {
        ;
        mockMvc.perform(get("/api/users/{username}", userName).with(csrf())
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userName));
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void getCurrentUser() throws Exception {

        UserDetails mockUser = customUserDetailsService.loadUserByUsername(adminUserName);

        mockMvc.perform(get("/api/users/me").with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(adminUserName));

    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void getUsers() throws Exception {

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void deleteUser() throws Exception {

        mockMvc.perform(delete("/api/users/{username}", userName).with(csrf())
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userName));
    }
}
