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
package org.trade.persistent.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.dao.AspectHome;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.ContractHome;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
public class ContractTest {

    private final static Logger _log = LoggerFactory.getLogger(ContractTest.class);


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
    public void testFindDeleteAddContract() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        AspectHome aspectHome = new AspectHome();
        ContractHome contractHome = new ContractHome();
        Contract transientInstance = new Contract(SECType.STOCK, "QQQ", Exchange.SMART, Currency.USD, null, null);

        transientInstance = aspectHome.persist(transientInstance);
        _log.info("Contract added Id:{}", transientInstance.getId());

        Contract contract = contractHome.findByUniqueKey(transientInstance.getSecType(),
                transientInstance.getSymbol(), transientInstance.getExchange(), transientInstance.getCurrency(),
                null);
        assertNotNull(contract);

        aspectHome.remove(contract);
        _log.info("Contract deleted Id:{}", transientInstance.getId());
    }

    @Test
    public void testFindDeleteAddFuture() throws Exception {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        AspectHome aspectHome = new AspectHome();
        ContractHome contractHome = new ContractHome();

        ZonedDateTime expiry = TradingCalendar.getDateAtTime(TradingCalendar.getDateTimeNowMarketTimeZone(), 19, 0,
                0);
        expiry = expiry.plusMonths(1);

        _log.info("Expiry Date: {}", expiry);
        Contract transientInstance = new Contract(SECType.FUTURE, "ES", Exchange.SMART, Currency.USD, expiry,
                new BigDecimal(50));
        transientInstance = aspectHome.persist(transientInstance);
        _log.info("Contract added Id:{}", transientInstance.getId());

        expiry = expiry.plusDays(1);
        _log.info("Expiry Date: {}", expiry);
        Contract contract = contractHome.findByUniqueKey(transientInstance.getSecType(),
                transientInstance.getSymbol(), transientInstance.getExchange(), transientInstance.getCurrency(),
                expiry);
        assertNotNull(contract);

        aspectHome.remove(contract);
        _log.info("Contract deleted Id:{}", transientInstance.getId());
        _log.info("Contract added Id:{}", transientInstance.getId());
    }
}
