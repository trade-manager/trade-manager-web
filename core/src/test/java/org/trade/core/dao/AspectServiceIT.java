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
package org.trade.core.dao;


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
import org.trade.core.persistent.TradeService;
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
public class AspectServiceIT {

    private final static Logger _log = LoggerFactory.getLogger(AspectServiceIT.class);

    @Autowired
    private TradeService tradeService;

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

        Aspect aspect = tradeService.findAspectById(tradestrategy);
        assertNotNull(aspect);
    }

    @Test
    public void findByClassName() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        _log.info("Find Aspects by className: {}", className);

        Aspects aspects = tradeService.findByClassName(className);
        assertNotNull(aspects);
        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            _log.info("Aspect added Id: {}", aspect.getId());
        }
    }

    @Test
    public void findCodesByClassNameValid() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        _log.info("Find Aspects by className: {}", className);

        List<?> codes = tradeService.findCodesByClassName(className);
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
        String className = "org.trade.core.persistent.dao.Rule";
        _log.info("Find Aspects by className: {}", className);

        List<?> codes = tradeService.findCodesByClassName(className);
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

        Aspects instance = tradeService.findByClassNameAndFieldName(className, fieldName, indicatorName);
        assertNotNull(instance);

        for (Aspect aspect : instance.getAspect()) {

            _log.info("Aspect added Id = {}", aspect.getId());
        }
    }
}
