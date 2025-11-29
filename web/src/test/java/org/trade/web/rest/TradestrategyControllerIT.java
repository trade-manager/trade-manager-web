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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.role.Role;
import org.trade.core.persistent.strategy.series.indicator.IndicatorSeriesRecord;
import org.trade.core.persistent.tradestrategy.Tradestrategy;
import org.trade.core.persistent.tradestrategy.TradestrategyRecord;
import org.trade.core.persistent.user.User;
import org.trade.core.util.JSONMapper;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

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
class TradestrategyControllerIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradestrategyControllerIT.class);

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
    public void getTradestrategyByOpenAndClose() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/tradestrategy").param("text", open.toString(), close.toString())
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        JSONArray results = new JSONArray(responseBody);
        assertEquals(1, results.length());
        TradestrategyRecord tradestrategyRecord = JSONMapper.getRecord(results.getJSONObject(0).toString(), TradestrategyRecord.class);
        _log.info("Info: open: {}", tradestrategyRecord.contract().getSymbol());
        assertEquals(symbol, tradestrategyRecord.contract().getSymbol());
        assertNotNull(tradestrategyRecord.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void createTradestrategy() throws Exception {

        String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
        Contract contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
        Tradestrategy tradestrategy = new Tradestrategy(contract, this.tradestrategy.getTradingday(), this.tradestrategy.getStrategy(), this.tradestrategy.getPortfolio(),
                this.tradestrategy.getRiskAmount(), this.tradestrategy.getSide(), this.tradestrategy.getTier(), this.tradestrategy.getTrade(), this.tradestrategy.getChartDays(), this.tradestrategy.getBarSize());
        tradestrategy.getStrategy().setIndicatorSeries(new ArrayList<>());
        String jsonContent = JSONMapper.getJSONString(TradestrategyRecord.from(tradestrategy));
        _log.info("Info: jsonContent: {}", jsonContent);

        MvcResult mvcResult = mockMvc.perform(post("/api/tradestrategy")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(adminUserName, password))
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        TradestrategyRecord tradestrategyRecord = JSONMapper.getRecord(responseBody, TradestrategyRecord.class);
        _log.info("Info: symbol: {}", tradestrategyRecord.getContract().getSymbol());

        assertEquals(symbol, tradestrategyRecord.getContract().getSymbol());
        assertNotNull(tradestrategyRecord.getId());
        tradestrategy = JSONMapper.convertRecordToEntity(tradestrategyRecord, Tradestrategy.class);
        tradestrategy = tradeService.getTradestrategyService().findByUniqueKeys(open, tradestrategy.getStrategy().getName(), tradestrategy.getContract(), tradestrategy.getPortfolio().getName());
        assertNotNull(tradestrategy);
        tradeService.getTradestrategyService().delete(tradestrategy);
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void getTradestrategies() throws Exception {

        mockMvc.perform(get("/api/tradestrategy")
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {Role.ROLE_ADMIN})
    public void deleteTradestrategy() throws Exception {


        MvcResult mvcResult = mockMvc.perform(delete("/api/tradestrategy/{id}", this.tradestrategy.getId()).with(csrf())
                        .accept(MediaType.APPLICATION_JSON).with(httpBasic(adminUserName, password)))
                .andExpect(status().isOk()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertNotNull(responseBody);
        _log.info("Info: responseBody: {}", responseBody);
        TradestrategyRecord tradestrategyRecord = JSONMapper.getRecord(responseBody, TradestrategyRecord.class);
        _log.info("Info: symbol: {}", tradestrategyRecord.getContract().getSymbol());
        assertEquals(this.tradestrategy.getId(), tradestrategyRecord.id());
    }
}
