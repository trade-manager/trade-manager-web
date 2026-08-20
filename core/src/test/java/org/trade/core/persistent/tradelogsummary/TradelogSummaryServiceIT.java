package org.trade.core.persistent.tradelogsummary;

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
import org.trade.core.valuetype.DAOPortfolio;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradelogSummaryServiceIT extends TradestrategyBase {

    private static final Logger _log = LoggerFactory.getLogger(TradelogSummaryServiceIT.class);

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
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void tradelogSummary() throws IOException {

        org.trade.core.persistent.portfolio.Portfolio portfolio = (org.trade.core.persistent.portfolio.Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        List<TradelogSummary> tradelogSummary = tradeService.getTradelogSummaryService().findByTradelogSummary(portfolio,
                TradingCalendar.getYearStart(),
                TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone()), null,
                new BigDecimal(0));
        assertTrue(tradelogSummary.isEmpty());

        for (TradelogSummary item : tradelogSummary) {

            _log.info("tradelogSummary tradelogDetail: getPeriod:{}getBattingAverage:{}getSimpleSharpeRatio:{}getQuantity:{}getGrossProfitLoss:{}getQuantity:{}getNetProfitLoss:{}", item.getPeriod(), item.getBattingAverage(), item.getSimpleSharpeRatio(), item.getQuantity(), item.getGrossProfitLoss(), item.getQuantity(), item.getNetProfitLoss());
        }
    }
}
