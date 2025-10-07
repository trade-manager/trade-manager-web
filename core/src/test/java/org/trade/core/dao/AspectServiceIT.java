package org.trade.core.dao;

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
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradestrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class AspectServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(AspectServiceIT.class);

    private static Tradestrategy tradestrategy;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);

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
    public void findAspectById() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        _log.info("Find Aspects by className: {}", className);

        Aspect aspect = tradeService.getAspectService().findById(tradestrategy);
        assertNotNull(aspect);
    }

    @Test
    public void findByClassName() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        _log.info("Find Aspects by className: {}", className);

        Aspects aspects = tradeService.getAspectService().findByClassName(className);
        assertNotNull(aspects);
        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            _log.info("Aspect added Id: {}", aspect.getId());
        }
    }

    @Test
    public void findCodesByClassNameValid() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        _log.info("Find Aspects by className: {}", className);

        List<?> codes = tradeService.getAspectService().findCodesByClassName(className);
        assertNotNull(codes);
        assertFalse(codes.isEmpty());
        for (Object daoObject : codes) {

            _log.info("Found code name: {}", ((Strategy) daoObject).getName());
        }
    }

    @Test
    public void findCodesByClassNameEmpty() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.rule.Rule";
        _log.info("Find Aspects by className: {}", className);

        List<?> codes = tradeService.getAspectService().findCodesByClassName(className);
        assertNotNull(codes);
        assertTrue(codes.isEmpty());
    }

    @Test
    public void findByClassNameAndFieldName() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        String fieldName = "name";
        String indicatorName = "5MinGapBar";
        _log.info("Find Aspects by className: {}, fieldName: {}, value: {}", className, fieldName, indicatorName);

        Aspects instance = tradeService.getAspectService().findByClassNameAndFieldName(className, fieldName, indicatorName);
        assertNotNull(instance);

        for (Aspect aspect : instance.getAspects()) {

            _log.info("Aspect added Id = {}", aspect.getId());
        }
    }
}
