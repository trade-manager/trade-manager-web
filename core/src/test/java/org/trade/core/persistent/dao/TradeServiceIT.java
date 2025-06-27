package org.trade.core.persistent.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.persistent.TradeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradeServiceIT {

    @Autowired
    private TradeService tradeService;

    private static Tradestrategy tradestrategy;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);

    @BeforeEach
    public void setUp() throws Exception {

        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
        assertNotNull(tradestrategy);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    @Test
    public void fetchData() {

        /*Test data retrieval*/
        Optional<Contract> contract = tradeService.findContractBySymbol(symbol);
        assertNotNull(contract);
        Iterable<Contract> item = tradeService.findAllContracts();
        assertTrue(item.iterator().hasNext());
    }

    @Test
    public void findBySymbol() {

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime expiry = now.atZone(ZoneId.systemDefault());
        Contract contract = new Contract("STK", "Test3", "SMART", "USD", expiry, new BigDecimal(1));
        contract = tradeService.saveAspect(contract);

        Optional<Contract> contract1 = tradeService.findContractBySymbol(contract.getSymbol());
        assertThat(contract1.get()).extracting(Contract::getSymbol).isEqualTo(contract.getSymbol());
        tradeService.deleteAspect(contract);
    }
}
