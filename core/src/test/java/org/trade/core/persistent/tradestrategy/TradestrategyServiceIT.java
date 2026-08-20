package org.trade.core.persistent.tradestrategy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.persistent.contract.Contract;
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
public class TradestrategyServiceIT extends TradestrategyBase {

    private static final Logger _log = LoggerFactory.getLogger(TradestrategyServiceIT.class);

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

        Integer version = tradeService.getTradestrategyService().findVersionById(tradestrategy.getId());
        assertNotNull(version);
        _log.info("findVersionById id:{} version: {}", tradestrategy.getId(), version);
    }

    @Test
    public void findPositionOrdersById() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("findPositionOrdersById id:{}", tradestrategy.getId());

        TradestrategyOrders positionOrders = tradeService.getTradestrategyService()
                .findPositionOrdersById(tradestrategy.getId());
        assertNotNull(positionOrders);
        _log.info("findPositionOrdersById PositionOrders id: {}", positionOrders.getId());
        positionOrders.setStatus(TradestrategyStatus.CANCELLED);

        positionOrders = tradeService.getAspectService().save(positionOrders);
        assertNotNull(positionOrders);
        positionOrders = tradeService.getTradestrategyService().findPositionOrdersById(tradestrategy.getId());
        _log.info("findPositionOrdersById PositionOrders id: {} Status: {}", positionOrders.getId(), positionOrders.getStatus());
        assertEquals(TradestrategyStatus.CANCELLED, positionOrders.getStatus());
    }

    @Test
    public void addTradestrategy() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("addTradestrategy id:{}", tradestrategy.getId());
        tradestrategy = tradeService.getTradestrategyService().findById(tradestrategy.getId());
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

            tradeService.getAspectService().delete(tradingday);

            for (String symbol : contracts) {

                Optional<Contract> contract = tradeService.getContractService().findBySymbol(symbol);
                assertTrue(contract.isPresent());
                tradeService.getAspectService().delete(contract.get());
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

            tradeService.getAspectService().delete(tradingday);

            for (String symbol : contracts) {

                Optional<Contract> contract = tradeService.getContractService().findBySymbol(symbol);
                assertTrue(contract.isPresent());
                tradeService.getAspectService().delete(contract.get());
            }
        }
    }

    @Test
    public void findByDateRangeDistinctContract() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("findByDateRangeDistinctContract id:{}", tradestrategy.getId());
        List<Tradestrategy> results = tradeService.getTradestrategyService().findByDateRangeDistinctContract(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());

        for (Tradestrategy value : results) {

            _log.info("BarSize: {} ChartDays: {} Strategy: {}", value.getBarSize(), value.getChartDays(), value.getContract().getSymbol());
        }
        assertNotNull(results);
    }

    @Test
    public void findByDateRangeDistinctBarsizeAndChartDaysAndStrategy() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
        _log.info("findByDateRangeDistinctBarsizeAndChartDaysAndStrategy id:{}", tradestrategy.getId());
        List<Tradestrategy> results = tradeService.getTradestrategyService().findByDateRangeDistinctBarSizeAndChartDaysAndStrategy(
                tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getOpen());

        for (Tradestrategy value : results) {

            _log.info("Contract: {}", value.getStrategy().getName());
        }
        assertNotNull(results);
    }
}
