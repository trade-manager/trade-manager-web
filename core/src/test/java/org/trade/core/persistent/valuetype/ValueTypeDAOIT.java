package org.trade.core.persistent.valuetype;

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
import org.trade.core.persistent.codetype.Entrylimit;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.DAODecode;
import org.trade.core.valuetype.DAOEntryLimit;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.DAOStrategyManager;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.Money;

import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class ValueTypeDAOIT {

    private final static Logger _log = LoggerFactory.getLogger(ValueTypeDAOIT.class);

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
    public void dOAStrategies() throws Exception {

        DAOStrategy strategies = new DAOStrategy();
        List<Decode> decodes = strategies.getCodesDecodes();
        ListIterator<Decode> eDecodes = decodes.listIterator();
        assertFalse(decodes.isEmpty());

        while (eDecodes.hasNext()) {

            Decode decode = eDecodes.next();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));

        }

        DAOStrategyManager strategyManagers = new DAOStrategyManager();
        decodes = strategyManagers.getCodesDecodes();
        assertFalse(decodes.isEmpty());
        eDecodes = decodes.listIterator();

        while (eDecodes.hasNext()) {

            Decode decode = eDecodes.next();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));

        }
    }

    @Test
    public void dOAEntryLimit() throws Exception {

        DAOEntryLimit entryLimits = new DAOEntryLimit();
        List<Decode> decodes = entryLimits.getCodesDecodes();
        assertFalse(decodes.isEmpty());
        ListIterator<Decode> eDecodes = decodes.listIterator();

        while (eDecodes.hasNext()) {

            Decode decode = eDecodes.next();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));
        }
        Money price = new Money(20.22);
        Entrylimit entrylimit = entryLimits.getValue(price);
        _log.info("Price:{} Percent:{} LimitAmount:{}", price, entrylimit.getPercentOfPrice(), entrylimit.getLimitAmount());
    }

    @Test
    public void chartDays() throws Exception {

        ChartDays DAOValues = new ChartDays();
        List<Decode> decodes = DAOValues.getCodesDecodes();
        assertFalse(decodes.isEmpty());
        ListIterator<Decode> eDecodes = decodes.listIterator();

        while (eDecodes.hasNext()) {

            Decode decode = eDecodes.next();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));
        }
    }
}
