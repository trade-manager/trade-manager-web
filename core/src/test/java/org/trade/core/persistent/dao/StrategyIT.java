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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class StrategyIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(StrategyIT.class);

    private static final String name = "TEST-" + TradestrategyBase.getRandomNumber(4);

    @Autowired
    private StrategyRepository strategyRepository;

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

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void addStrategy() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        Strategy strategy = strategyRepository.findByName(name);
        assertNull(strategy);
        strategy = tradeService.getAspectService().save(new Strategy(name));
        _log.info("Strategy added Id = {}, name: {}", strategy.getId(), strategy.getName());
        assertNotNull(strategy.getId());
        this.addRecord(strategy);
    }
}
