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
import org.trade.core.persistent.TradeService;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.MarketBar;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradingdayIT {

    private final static Logger _log = LoggerFactory.getLogger(TradingdayIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradingdayRepository tradingdayRepository;

    private static Tradingday tradingday;

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
    public void addTradingday() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        _log.debug("Adding Tradingday");

        ZonedDateTime open = TradingCalendar.getTradingDayStart(
                TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        tradingday = tradingdayRepository.findByOpenCloseDateOrderByOpenAsc(open,
                TradingCalendar.getTradingDayEnd(open));
        if (null == tradingday) {
            tradingday = Tradingday.newInstance(open);
        }
        tradeService.saveTradingday(tradingday);
        _log.info("Tradingday added Id = {}", tradingday.getId());
        assertNotNull(tradingday.getId());
        TradestrategyBase.addRecord(tradingday);
    }

    @Test
    public void updateTradingday() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        _log.debug("Updating Tradingday");

        ZonedDateTime open = TradingCalendar.getTradingDayStart(
                TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));
        tradingday = tradingdayRepository.findByOpenCloseDateOrderByOpenAsc(open,
                TradingCalendar.getTradingDayEnd(open));
        if (null == tradingday) {
            tradingday = Tradingday.newInstance(open);
        }
        tradingday.setMarketBar(MarketBar.newInstance("+WRB").getCode());
        tradeService.saveTradingday(tradingday);
        _log.info("Tradingday Update Id = {}", tradingday.getId());
        assertNotNull(tradingday.getId());
        TradestrategyBase.addRecord(tradingday);
    }
}
