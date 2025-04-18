/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.factory.ClassFactory;
import org.trade.core.valuetype.CalculationType;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.persistent.dao.CodeAttribute;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;
import org.trade.strategy.data.IndicatorSeries;
import org.trade.ui.TradeAppLoadConfig;

import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ConfigurationPanelTest {

    private final static Logger _log = LoggerFactory.getLogger(ConfigurationPanelTest.class);

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {
        TradeAppLoadConfig.loadAppProperties();
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testCreateIntegerClass() throws Exception {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "Length", "The length of the MA", "10",
                "java.lang.Integer", null);
        CodeValue codeValue = new CodeValue(codeAttribute, "20");

        Vector<Object> parm = new Vector<>();
        parm.add(codeValue.getCodeValue());

        Integer value = (Integer) ClassFactory.getCreateClass(codeValue.getCodeAttribute().getClassName(), parm,
                this);
        _log.info("Value is: {}", value);
        assertEquals(20, value, 0);
    }

    @Test
    public void testCreateBooleanClass() throws Exception {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "Length", "The length of the MA", "true",
                "java.lang.Boolean", null);
        CodeValue codeValue = new CodeValue(codeAttribute, "true");

        Vector<Object> parm = new Vector<>();
        parm.add(codeValue.getCodeValue());

        Boolean value = (Boolean) ClassFactory.getCreateClass(codeValue.getCodeAttribute().getClassName(), parm,
                this);
        _log.info("Value is: {}", value);
        assertEquals(true, value);
    }

    @Test
    public void testCreateStringClass() throws Exception {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "Length", "The length of the MA", "Test",
                "java.lang.String", null);
        CodeValue codeValue = new CodeValue(codeAttribute, "Simple");

        Vector<Object> parm = new Vector<>();
        parm.add(codeValue.getCodeValue());

        String value = (String) ClassFactory.getCreateClass(codeValue.getCodeAttribute().getClassName(), parm,
                this);
        assertEquals("Simple", value);
        _log.info("Value is: {}", value);
    }

    @Test
    public void testCreateDecodeClass() throws Exception {

        CodeType codeType = new CodeType("Moving Average", CodeType.IndicatorParameters, "Moving Average");
        CodeAttribute codeAttribute = new CodeAttribute(codeType, "SMAType", "The length of the MA", "LINEAR",
                "org.trade.dictionary.valuetype.CalculationType", null);
        CodeValue codeValue = new CodeValue(codeAttribute, CalculationType.LINEAR);

        Vector<Object> parm = new Vector<>();
        // parm.add(codeValue.getCodeValue());

        CalculationType value = (CalculationType) ClassFactory
                .getCreateClass(codeValue.getCodeAttribute().getClassName(), parm, this);
        value.setValue(CalculationType.LINEAR);
        assertEquals(CalculationType.LINEAR, value.getCode());
        _log.info("Value is: {}", value);
    }

    @Test
    public void testCreateIndicatorSeriesClass() throws Exception {

        final String packageName = "org.trade.strategy.data.";
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        Vector<Object> parm = new Vector<>();
        parm.add(strategy);
        parm.add("20-SMA");
        parm.add(IndicatorSeries.MovingAverageSeries);
        parm.add("20 Simple Moving Average");
        parm.add(false);
        parm.add(0);
        parm.add(false);
        String className = packageName + IndicatorSeries.MovingAverageSeries;

        IndicatorSeries value = (IndicatorSeries) ClassFactory.getCreateClass(className, parm, this);

        assertEquals(className, value.getClass().getName());
        _log.info("Value is: {}", value);
    }
}
