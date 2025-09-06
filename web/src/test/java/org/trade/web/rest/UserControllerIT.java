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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
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
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
@AutoConfigureMockMvc
class UserControllerIT {

    private final static Logger _log = LoggerFactory.getLogger(UserControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DomainService domainService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private static final String userName = "TEST-" + TradestrategyBase.getRandomNumber(4);

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
        user = new User(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", userName, gobalDomain, roles);
        user = userService.saveUser(user);
        assertNotNull(user.getId());
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDownTest() {

        User user = userService.findUserByName(userName);
        userService.deleteUser(user);
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void getUserByName() throws Exception {

        //  this.mockMvc.perform(get("/api/users")).andDo(print())
        //          .andExpect(view().name("users"));


        //  this.mockMvc.perform(get("/api/users/{userName}", userName)) // Perform GET request to /greet/{name}
        //          .andExpect(status().isOk()) // Expect HTTP status 200 OK
        //          .andExpect(content().contentType(HAL_JSON_VALUE)) // Expect JSON content type
        //          .andExpect(jsonPath("$.name").value("admin")); // Expect specific JSON content

        String username = "admin";
        String password = "admin";
        String auth = username + ":" + password;
        mockMvc.perform(get("/api/users/{userName}", userName)
                        .accept(MediaType.ALL).with(httpBasic(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("userName"));
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {

        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
