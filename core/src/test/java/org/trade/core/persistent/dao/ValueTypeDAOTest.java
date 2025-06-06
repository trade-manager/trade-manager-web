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
package org.trade.core.persistent.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.DAODecode;
import org.trade.core.valuetype.DAOEntryLimit;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.DAOStrategyManager;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.Money;

import java.util.Enumeration;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Some tests for the  DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ValueTypeDAOTest {

    private final static Logger _log = LoggerFactory.getLogger(ValueTypeDAOTest.class);

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
    public void testDOAStrategies() throws Exception {

        DAOStrategy strategies = new DAOStrategy();
        Vector<Decode> decodes = strategies.getCodesDecodes();
        Enumeration<Decode> eDecodes = decodes.elements();
        assertFalse(decodes.isEmpty());
        while (eDecodes.hasMoreElements()) {
            Decode decode = eDecodes.nextElement();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));

        }

        DAOStrategyManager strategyManagers = new DAOStrategyManager();
        decodes = strategyManagers.getCodesDecodes();
        assertFalse(decodes.isEmpty());
        eDecodes = decodes.elements();
        while (eDecodes.hasMoreElements()) {
            Decode decode = eDecodes.nextElement();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));

        }
    }

    @Test
    public void testDOAEntryLimit() throws Exception {

        DAOEntryLimit entryLimits = new DAOEntryLimit();
        Vector<Decode> decodes = entryLimits.getCodesDecodes();
        assertFalse(decodes.isEmpty());
        Enumeration<Decode> eDecodes = decodes.elements();
        while (eDecodes.hasMoreElements()) {
            Decode decode = eDecodes.nextElement();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));

        }
        Money price = new Money(20.22);
        Entrylimit entrylimit = entryLimits.getValue(price);
        _log.info("Price:{} Percent:{} LimitAmount:{}", price, entrylimit.getPercentOfPrice(), entrylimit.getLimitAmount());
    }

    @Test
    public void testChartDays() throws Exception {

        ChartDays DAOValues = new ChartDays();
        Vector<Decode> decodes = DAOValues.getCodesDecodes();
        assertFalse(decodes.isEmpty());
        Enumeration<Decode> eDecodes = decodes.elements();
        while (eDecodes.hasMoreElements()) {
            Decode decode = eDecodes.nextElement();
            _log.info("TYPE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._TYPE));
            _log.info("CODE:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._CODE));
            _log.info("DISPLAY_NAME:{}", decode.getValue(DAODecode.CODE_DECODE_IDENTIFIER + DAODecode._DISPLAY_NAME));
        }
    }
}
