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
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.trade.core.dao.AspectRepository;
import org.trade.core.persistent.TradeService;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContractIT {

    private final static Logger _log = LoggerFactory.getLogger(ContractIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ContractRepository contractRepository;

    private static ZonedDateTime expiry;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

        expiry = LocalDateTime.now().atZone(ZoneId.systemDefault());
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
    public void testFindDeleteAddContract() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        Contract transientInstance = new Contract(SECType.STOCK, "QQQ", Exchange.SMART, Currency.USD, expiry, new BigDecimal(1));

        transientInstance = (Contract) tradeService.save(transientInstance);
        _log.info("Contract added Id:{}", transientInstance.getId());

        Contract contract = contractRepository.findContractByUniqueKey(transientInstance.getSecType(),
                transientInstance.getSymbol(), transientInstance.getExchange(), transientInstance.getCurrency(),
                expiry);
        assertNotNull(contract);

        tradeService.delete(contract);
        _log.info("Contract deleted Id:{}", transientInstance.getId());
    }

    @Test
    public void testFindDeleteAddFuture() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        ZonedDateTime expiry = TradingCalendar.getDateAtTime(TradingCalendar.getDateTimeNowMarketTimeZone(), 19, 0,
                0);
        expiry = expiry.plusMonths(1);

        _log.info("Expiry Date: {}", expiry);
        Contract transientInstance = new Contract(SECType.FUTURE, "ES", Exchange.SMART, Currency.USD, expiry,
                new BigDecimal(50));
        transientInstance = (Contract) tradeService.save(transientInstance);
        _log.info("Contract added Id:{}", transientInstance.getId());

        expiry = expiry.plusDays(1);
        _log.info("Expiry Date: {}", expiry);
        Contract contract = contractRepository.findContractByUniqueKey(transientInstance.getSecType(),
                transientInstance.getSymbol(), transientInstance.getExchange(), transientInstance.getCurrency(),
                expiry);
        assertNotNull(contract);

        tradeService.delete(contract);
        _log.info("Contract deleted Id:{}", transientInstance.getId());
        _log.info("Contract added Id:{}", transientInstance.getId());
    }
}
