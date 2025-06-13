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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectRepository;
import org.trade.core.dao.Aspects;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
//@DataJpaTest
//@EnableAutoConfiguration
//@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class AspectServiceIT {

    private final static Logger _log = LoggerFactory.getLogger(AspectServiceIT.class);

    @Autowired
    private AspectRepository aspectRepository;

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
    public void testFindAspectByClassName() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        String className = "org.trade.core.persistent.dao.Strategy";
        _log.info("Find Aspects by className: {}", className);

        Aspects transientInstance = aspectRepository.findByClassName(className);
        assertNotNull(transientInstance);
        for (Aspect aspect : transientInstance.getAspect()) {
            _log.info("Aspect added Id = {}", aspect.getId());
        }
    }
}
