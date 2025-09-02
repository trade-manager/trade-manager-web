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
import org.trade.core.persistent.TradeService;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class ContractIT {

    private final static Logger _log = LoggerFactory.getLogger(TradingdayIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ContractRepository contractRepository;

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

        if (null != contract) {

            tradeService.deleteAspect(contract);
            contract = null;
        }
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

        contract = tradeService.saveAspect(contract);
        _log.info("Contract added Id:{}", contract.getId());

        List<Contract> contracts = contractRepository.findContractByUniqueKey(contract.getSecType(),
                contract.getSymbol(), contract.getExchange(), contract.getCurrency(),
                expiry);
        assertFalse(contracts.isEmpty());
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
        contract = tradeService.saveAspect(contract);
        _log.info("Contract added Id:{}", contract.getId());

        // Expiry is monthly based
        expiry = expiry.plusMonths(2);
        _log.info("Expiry Date: {}", expiry);
        List<Contract> contracts = contractRepository.findContractByUniqueKey(contract.getSecType(),
                contract.getSymbol(), contract.getExchange(), contract.getCurrency(),
                expiry);
        assertTrue(contracts.isEmpty());
    }
}
