package org.trade.core.persistent.service;

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
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class ContractServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradingdayServiceIT.class);
    private static ZonedDateTime expiry;
    private static Contract contract;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {

        expiry = LocalDateTime.now().atZone(ZoneId.systemDefault());
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

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void findDeleteAddContract() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        contract = new Contract(SECType.STOCK, "QQQ", Exchange.SMART, Currency.USD, expiry, new BigDecimal(1));

        contract = tradeService.getAspectService().save(contract);
        _log.info("Contract added Id:{}", contract.getId());
        this.addRecord(contract);
        contract = tradeService.getContractService().findByUniqueKey(contract.getSecType(),
                contract.getSymbol(), contract.getExchange(), contract.getCurrency(),
                expiry);
        assertNotNull(contract);
    }

    @Test
    public void findDeleteAddFuture() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        ZonedDateTime expiry = TradingCalendar.getDateAtTime(TradingCalendar.getDateTimeNowMarketTimeZone(), 19, 0,
                0);
        expiry = expiry.plusMonths(1);

        _log.info("Expiry Date: {}", expiry);
        contract = new Contract(SECType.FUTURE, "ES", Exchange.SMART, Currency.USD, expiry,
                new BigDecimal(50));
        contract = tradeService.getAspectService().save(contract);
        _log.info("Contract added Id:{}", contract.getId());
        this.addRecord(contract);

        // Expiry is monthly based
        expiry = expiry.plusMonths(2);
        _log.info("Expiry Date: {}", expiry);
        contract = tradeService.getContractService().findByUniqueKey(contract.getSecType(),
                contract.getSymbol(), contract.getExchange(), contract.getCurrency(),
                expiry);
        assertNull(contract);
    }
}
