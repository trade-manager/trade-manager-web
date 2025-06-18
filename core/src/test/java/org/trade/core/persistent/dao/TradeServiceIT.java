package org.trade.core.persistent.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.trade.core.dao.Aspect;
import org.trade.core.persistent.TradeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TradeServiceIT {

    @Autowired
    private TradeService tradeService;

    List<Aspect> entities = new ArrayList<>(0);

    @BeforeEach
    public void setUp() {

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime expiry = now.atZone(ZoneId.systemDefault());
        Contract contract1 = new Contract("STK", "Test1", "SMART", "USD", expiry, new BigDecimal(1));
        Contract contract2 = new Contract("STK", "Test2", "SMART", "USD", expiry, new BigDecimal(1));

        //save user, verify has ID value after save
        assertNull(contract1.getId());
        assertNull(contract2.getId());//null before save
        contract1 = tradeService.saveAspect(contract1);
        contract2 = tradeService.saveAspect(contract2);
        assertNotNull(contract1.getId());
        assertNotNull(contract2.getId());
    }

    @AfterEach
    public void tearDown() {

        tradeService.deleteAllAspects(entities);
    }

    @Test
    public void testFetchData() {

        /*Test data retrieval*/
        Optional<Contract> contract = tradeService.findContractBySymbol("Test2");
        assertNotNull(contract);
        Iterable<Contract> item = tradeService.findAllContracts();
        assertTrue(item.iterator().hasNext());
    }

    @Test
    public void testFindBySymbol() {

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime expiry = now.atZone(ZoneId.systemDefault());
        Contract contract = new Contract("STK", "Test3", "SMART", "USD", expiry, new BigDecimal(1));
        contract = tradeService.saveAspect(contract);

        Optional<Contract> contract1 = tradeService.findContractBySymbol(contract.getSymbol());
        assertThat(contract1.get()).extracting(Contract::getSymbol).isEqualTo(contract.getSymbol());
    }
}
