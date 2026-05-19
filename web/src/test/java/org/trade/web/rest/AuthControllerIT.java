package org.trade.web.rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.user.User;
import org.trade.core.persistent.user.UserRecord;
import org.trade.core.util.JSONMapper;
import org.trade.web.service.CustomUserDetailsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ComponentScan(basePackages = {"org.trade.web.rest", "org.trade.web.service"})
@ContextConfiguration(classes = {ApplicationRepositoryConfig.class},
        initializers = ApplicationProfileInitializer.class)
@AutoConfigureMockMvc
class AuthControllerIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(AuthControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeService tradeService;

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

        Domain gobalDomain = tradeService.getDomainService().findByName(Domain.GLOBAL);
        assertNotNull(gobalDomain);
        Role role = tradeService.getRoleService().findByName(Role.ROLE_ADMIN);
        assertNotNull(role);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        User user = tradeService.getUserService().findUserByName(userName);
        assertNull(user);
        user = new User(userName, userName, userName, userName, userName + "@" + Domain.GLOBAL + ".com", this.passwordEncoder.encode(password), gobalDomain, roles);
        user = tradeService.getUserService().save(user);
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
    public void authorize() throws Exception {

        User mockUser = tradeService.getUserService().findByUsername(userName);
        mockUser.setPassword(password);
        String jsonContent = JSONMapper.getJSONString(mockUser);

        mockMvc.perform(post("/auth/authenticate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(userName, password))
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userName));
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void signup() throws Exception {

        final String userNameNew = "TEST-" + TradestrategyBase.getRandomNumber(4);
        Domain gobalDomain = tradeService.getDomainService().findByName(Domain.GLOBAL);
        assertNotNull(gobalDomain);
        Role role = tradeService.getRoleService().findByName(Role.ROLE_ADMIN);
        assertNotNull(role);
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        User user = new User(userNameNew, userNameNew, userNameNew, userNameNew, userNameNew + "@" + Domain.GLOBAL + ".com", password, gobalDomain, roles);
        String jsonContent = JSONMapper.getJSONString(UserRecord.from(user));

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(adminUserName, password))
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(userNameNew));

        user = tradeService.getUserService().findUserByName(userNameNew);
        assertNotNull(user.getId());
        this.addRecord(user);
    }
}
