package org.trade.core.persistent.dao;

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
import org.trade.core.persistent.tradingday.Tradingday;
import org.trade.core.persistent.tradingday.Tradingdays;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.TradestrategyStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradestrategyIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradestrategyIT.class);

    @Autowired
    private TradestrategyRepository tradestrategyRepository;

    private static Tradestrategy tradestrategy;
    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);

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
    public void tearDown() throws Exception {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void findVersionById() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);

        Integer version = tradestrategyRepository.findVersionByTradestrategyId(tradestrategy.getId());
        assertNotNull(version);
        _log.info("findVersionById id:{} version: {}", tradestrategy.getId(), version);
    }

    @Test
    public void findPositionOrdersById() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("findPositionOrdersById id:{}", tradestrategy.getId());

        TradestrategyOrders positionOrders = tradestrategyRepository
                .findPositionOrdersByTradestrategyId(tradestrategy.getId());
        assertNotNull(positionOrders);
        _log.info("findPositionOrdersById PositionOrders id: {}", positionOrders.getId());
        positionOrders.setStatus(TradestrategyStatus.CANCELLED);

        positionOrders = tradeService.saveAspect(positionOrders);
        assertNotNull(positionOrders);
        positionOrders = tradestrategyRepository.findPositionOrdersByTradestrategyId(tradestrategy.getId());
        _log.info("findPositionOrdersById PositionOrders id: {} Status: {}", positionOrders.getId(), positionOrders.getStatus());
        assertEquals(TradestrategyStatus.CANCELLED, positionOrders.getStatus());
    }

    @Test
    public void addTradestrategy() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("addTradestrategy id:{}", tradestrategy.getId());
        tradestrategy = tradestrategyRepository.findById(tradestrategy.getId()).get();
        assertNotNull(tradestrategy);
        _log.info("addTradestrategy id: {}", tradestrategy.getId());
    }

    @Test
    public void updateTradeStrategy() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        ZonedDateTime open = TradingCalendar.getTradingDayStart(
                TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        Tradingdays tradingdays = tradeService.getTradingdayService().findTradingdaysByDateRangeOrderByOpenAsc(open, open);

        for (Tradingday tradingday : tradingdays.getTradingdays()) {

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                tradestrategy.setStatus(TradestrategyStatus.OPEN);
                tradestrategy.setDirty(true);
            }

            tradingday = tradeService.saveTradingday(tradingday);

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                _log.info("updateTradeStrategy id:{}  Status: {}", tradestrategy.getId(), tradestrategy.getStatus());
                assertEquals(TradestrategyStatus.OPEN, tradestrategy.getStatus(), "Error expected status to match found status: " + tradestrategy.getStatus());
            }
        }
    }

    @Test
    public void findAndSavefileMultipleDayTradestrategy() throws Exception {

        Tradingdays tradingdays = new Tradingdays();
        Tradingday instance = Tradingday
                .newInstance(TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        tradingdays.add(instance);

        String TEST_FILE = "../db/LoadFile10Stocks.csv";
        tradingdays.populateDataFromFile(TEST_FILE, instance);
        assertFalse(tradingdays.getTradingdays().isEmpty());
        List<String> contracts = new ArrayList<>();

        for (Tradingday tradingday : tradingdays.getTradingdays()) {

            tradingday = tradeService.saveTradingday(tradingday);
            assertEquals(10, tradingday.getTradestrategies().size());

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                _log.info("findAndSavefileMultipleDayTradestrategy id: {}", tradestrategy.getId());
                assertNotNull(tradestrategy.getId());
                contracts.add(tradestrategy.getContract().getSymbol());
            }

            tradeService.deleteAspect(tradingday);

            for (String symbol : contracts) {

                Optional<Contract> contract = tradeService.findContractBySymbol(symbol);
                assertTrue(contract.isPresent());
                tradeService.deleteAspect(contract.get());
            }
        }
    }

    @Test
    public void findAndSavefileOneDayTradestrategy() throws Exception {

        Tradingdays tradingdays = new Tradingdays();
        String TEST_FILE = "../db/LoadFile1Stock.csv";
        tradingdays.populateDataFromFile(TEST_FILE, null);
        assertFalse(tradingdays.getTradingdays().isEmpty());
        List<String> contracts = new ArrayList<>();

        for (Tradingday tradingday : tradingdays.getTradingdays()) {

            tradingday = tradeService.saveTradingday(tradingday);
            assertEquals(1, tradingday.getTradestrategies().size());

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                _log.info("findAndSavefileOneDayTradestrategy id:{}", tradestrategy.getId());
                assertNotNull(tradestrategy.getId());
                contracts.add(tradestrategy.getContract().getSymbol());
            }

            tradeService.deleteAspect(tradingday);

            for (String symbol : contracts) {

                Optional<Contract> contract = tradeService.findContractBySymbol(symbol);
                assertTrue(contract.isPresent());
                tradeService.deleteAspect(contract.get());
            }
        }
    }

    @Test
    public void findTradestrategyDistinctByDateRange() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("findTradestrategyDistinctByDateRange id:{}", tradestrategy.getId());
        List<Tradestrategy> results = tradestrategyRepository.findTradestrategyDistinctByDateRange(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());

        for (Tradestrategy value : results) {

            _log.info("BarSize: {} ChartDays: {} Strategy: {}", value.getBarSize(), value.getChartDays(), value.getStrategy().getName());
        }
        assertNotNull(results);
    }

    @Test
    public void findTradestrategyContractDistinctByDateRange() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("findTradestrategyContractDistinctByDateRange id:{}", tradestrategy.getId());
        List<Tradestrategy> results = tradestrategyRepository.findTradestrategyContractDistinctByDateRange(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());

        for (Tradestrategy value : results) {

            _log.info("Contract: {}", value.getContract().getSymbol());
        }
        assertNotNull(results);
    }
}
