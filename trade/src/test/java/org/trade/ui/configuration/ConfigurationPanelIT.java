package org.trade.ui.configuration;

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
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.codetype.CodeAttribute;
import org.trade.core.persistent.codetype.CodeType;
import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.valuetype.CalculationType;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.indicator.IndicatorSeries;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
class ConfigurationPanelIT {

    private final static Logger _log = LoggerFactory.getLogger(ConfigurationPanelIT.class);

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
    public void createIntegerClass() {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "Length", "The length of the MA", "10",
                "java.lang.Integer", null);
        CodeValue codeValue = new CodeValue(codeAttribute, "20");

        List<Object> params = new ArrayList<>(0);
        params.add(codeValue.getCodeValue());
        Integer value = null;

        try {

            value = (Integer) ClassFactory.getCreateClass(codeValue.getCodeAttribute().getClassName(), params,
                    this);
        } catch (Exception ex) {

            fail("Failed to create value msg: " + ex.getMessage());
        }
        _log.info("Value is: {}", value);
        assertEquals(20, value, 0);
    }

    @Test
    public void createBooleanClass() {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "Length", "The length of the MA", "true",
                "java.lang.Boolean", null);
        CodeValue codeValue = new CodeValue(codeAttribute, "true");

        List<Object> params = new ArrayList<>(0);
        params.add(codeValue.getCodeValue());
        Boolean value = null;

        try {

            value = (Boolean) ClassFactory.getCreateClass(codeValue.getCodeAttribute().getClassName(), params,
                    this);
        } catch (Exception ex) {

            fail("Failed to create value msg: " + ex.getMessage());
        }
        _log.info("Value is: {}", value);
        assertEquals(true, value);
    }

    @Test
    public void createStringClass() {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "Length", "The length of the MA", "Test",
                "java.lang.String", null);
        CodeValue codeValue = new CodeValue(codeAttribute, "Simple");

        List<Object> params = new ArrayList<>(0);
        params.add(codeValue.getCodeValue());
        String value = null;

        try {

            value = (String) ClassFactory.getCreateClass(codeValue.getCodeAttribute().getClassName(), params,
                    this);
        } catch (Exception ex) {

            fail("Failed to create value msg: " + ex.getMessage());
        }
        assertEquals("Simple", value);
        _log.info("Value is: {}", value);
    }

    @Test
    public void createDecodeClass() {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "SMAType", "The length of the MA", "LINEAR",
                "org.trade.core.valuetype.CalculationType", null);
        CodeValue codeValue = new CodeValue(codeAttribute, CalculationType.LINEAR);

        List<Object> params = new ArrayList<>(0);
        CalculationType value = null;
        // param.add(codeValue.getCodeValue());
        try {
            value = (CalculationType) ClassFactory
                    .getCreateClass(codeValue.getCodeAttribute().getClassName(), params, this);
        } catch (Exception ex) {

            fail("Failed to create value msg: " + ex.getMessage());
        }
        value.setValue(CalculationType.LINEAR);
        assertEquals(CalculationType.LINEAR, value.getCode());
        _log.info("Value is: {}", value);
    }

    @Test
    public void createIndicatorSeriesClass() {

        final String packageName = "org.trade.indicator.";
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        List<Object> params = new ArrayList<>(0);
        params.add(strategy);
        params.add("20-SMA");
        params.add(IndicatorSeries.MovingAverageSeries);
        params.add("20 Simple Moving Average");
        params.add(false);
        params.add(0);
        params.add(false);
        String className = packageName + IndicatorSeries.MovingAverageSeries;
        IndicatorSeries value = null;

        try {

            value = (IndicatorSeries) ClassFactory.getCreateClass(className, params, this);
        } catch (Exception ex) {

            fail("Failed to create value msg: " + ex.getMessage());
        }

        assertEquals(className, value.getClass().getName());
        _log.info("Value is: {}", value);
    }
}
