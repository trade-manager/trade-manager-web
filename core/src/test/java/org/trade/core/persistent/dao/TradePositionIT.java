package org.trade.core.persistent.dao;

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
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Side;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradePositionIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradePositionIT.class);

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
    public void setUp() throws Exception {

        tradestrategy = this.createTestTradestrategy(symbol);
        assertNotNull(tradestrategy);
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
    public static void tearDownAfterClass() {
    }

    @Test
    public void addRemoveTradePosition() {

        TradePosition instance = new TradePosition(tradestrategy.getContractLite(),
                TradingCalendar.getDateTimeNowMarketTimeZone(), Side.BOT);

        instance = tradeService.getAspectService().save(instance);
        assertNotNull(instance.getId());
        _log.info("testAddTradePosition tradestrategyId: {}IdTradePosition: {}", tradestrategy.getId(), instance.getId());
        tradeService.getAspectService().delete(instance);
        _log.info("testDeleteTradePosition tradestrategyId: {}", tradestrategy.getId());
        this.addRecord(instance);
    }
}
