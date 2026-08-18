package org.trade.core.persistent.codetype;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class CodeTypeServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(CodeTypeServiceIT.class);

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
    public void findCodeTypeByCategory() {

        List<CodeType> codeTypes = tradeService.getCodeTypeService().findByCategory("CodeDecode");
        assertNotNull(!codeTypes.isEmpty());

        for (CodeType codeType : codeTypes) {

            _log.info("CodeType id: {}", codeType.getId());
            List<CodeValue> codeValues = tradeService.getCodeTypeService().findByAttributeName(codeType.getName(), "value");
            assertFalse(codeValues.isEmpty());
            _log.info("CodeValue id: {}", codeValues.getFirst().getId());
        }
    }

    @Test
    public void findCodeTypeByName() {

        CodeType codeType = tradeService.getCodeTypeService().findByName("MovingAverage");
        assertNotNull(codeType);
        _log.info("CodeType id: {}", codeType.getId());
        List<CodeValue> codeValues = tradeService.getCodeTypeService().findByAttributeName(codeType.getName(), "Length");
        assertFalse(codeValues.isEmpty());
        _log.info("CodeValue id: {}", codeValues.getFirst().getId());
    }

    @Test
    public void findCodeTypeByType() {

        List<CodeType> codeTypes = tradeService.getCodeTypeService().findByType("BarSize");
        assertNotNull(!codeTypes.isEmpty());

        for (CodeType codeType : codeTypes) {

            _log.info("CodeType id: {}", codeType.getId());
            List<CodeValue> codeValues = tradeService.getCodeTypeService().findByAttributeName(codeType.getName(), "value");
            assertFalse(codeValues.isEmpty());
            _log.info("CodeValue id: {}", codeValues.getFirst().getId());
        }
    }
}
