package org.trade.web.rest;

import org.json.JSONArray;
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
import org.springframework.test.web.servlet.MvcResult;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.TradingdayRecord;
import org.trade.core.persistent.user.User;
import org.trade.core.util.JSONMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ComponentScan(basePackages = {"org.trade.web.rest", "org.trade.web.service"})
@ContextConfiguration(classes = {ApplicationRepositoryConfig.class},
        initializers = ApplicationProfileInitializer.class)
@AutoConfigureMockMvc
class TradingdayControllerIT extends TradestrategyBase {

    private static final Logger _log = LoggerFactory.getLogger(TradingdayControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static Tradestrategy tradestrategy;
    private static ZonedDateTime open = null;
    private static ZonedDateTime close = null;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);
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
    public void setUpTest() throws Exception {

        Domain gobalDomain = tradeService.getDomainService().findByName(Domain.GLOBAL);
        assertNotNull(gobalDomain);
        Role role = tradeService.getRoleService().findByName(Role.ROLE_ADMIN);
        assertNotNull(role);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        User adminUser = tradeService.getUserService().findUserByName(adminUserName);
        assertNotNull(adminUser);
        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        open = tradestrategy.getTradingday().getOpen();
        assertNotNull(open);
        close = tradestrategy.getTradingday().getClose();
        assertNotNull(close);
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
    public void getTradingdayByOpenAndClose() throws Exception {

        _log.info("Info: open: {}, close: {}", open, close);

        MvcResult mvcResult = mockMvc.perform(get("/api/tradingday").param("text", open.toString(), close.toString())
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        JSONArray results = new JSONArray(responseBody);
        assertEquals(1, results.length());
        TradingdayRecord tradingdayRecord = JSONMapper.getRecord(results.getJSONObject(0).toString(), TradingdayRecord.class);
        _log.info("Info: open: {}", tradingdayRecord.open());
        assertEquals(open, tradingdayRecord.open());
        assertEquals(close, tradingdayRecord.close());
        assertNotNull(tradingdayRecord.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void createTradingday() throws Exception {

        ZonedDateTime openPlus2 = open.plusDays(2);
        ZonedDateTime closePlus2 = close.plusDays(2);
        Tradingday tradingday = new Tradingday(openPlus2, closePlus2);
        String jsonContent = JSONMapper.getJSONString(TradingdayRecord.from(tradingday));

        MvcResult mvcResult = mockMvc.perform(post("/api/tradingday")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(adminUserName, password))
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        TradingdayRecord tradingdayRecord = JSONMapper.getRecord(responseBody, TradingdayRecord.class);
        _log.info("Info: open: {}", tradingdayRecord.open());

        assertEquals(tradingday.getOpen(), tradingdayRecord.open());
        assertNotNull(tradingdayRecord.getId());
        tradingday = tradeService.getTradingdayService().findByOpenCloseDate(openPlus2, closePlus2);
        assertNotNull(tradingday);
        tradeService.getTradingdayService().delete(tradingday);
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void getTradingdays() throws Exception {

        mockMvc.perform(get("/api/tradingday")
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void deleteTradingday() throws Exception {

        Tradingday tradingday = tradeService.getTradingdayService().findByOpenCloseDate(open, close);
        MvcResult mvcResult = mockMvc.perform(delete("/api/tradingday/{id}", tradingday.getId()).with(csrf())
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        _log.info("Info: responseBody: {}", responseBody);
        TradingdayRecord tradingdayRecord = JSONMapper.getRecord(responseBody, TradingdayRecord.class);
        _log.info("Info: open: {}", tradingdayRecord.open());
        assertEquals(tradingday.getOpen(), tradingdayRecord.open());
    }
}
