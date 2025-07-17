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
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.persistent.TradeService;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class ContractIT {

    private final static Logger _log = LoggerFactory.getLogger(TradingdayIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ContractRepository contractRepository;

    private static ZonedDateTime expiry;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {

        expiry = LocalDateTime.now().atZone(ZoneId.systemDefault());
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
    public void findDeleteAddContract() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        Contract instance = new Contract(SECType.STOCK, "QQQ", Exchange.SMART, Currency.USD, expiry, new BigDecimal(1));

        instance = tradeService.saveAspect(instance);
        _log.info("Contract added Id:{}", instance.getId());

        List<Contract> contracts = contractRepository.findContractByUniqueKey(instance.getSecType(),
                instance.getSymbol(), instance.getExchange(), instance.getCurrency(),
                expiry);
        assertFalse(contracts.isEmpty());

        tradeService.deleteAspect(contracts.getFirst());
        _log.info("Contract deleted Id:{}", instance.getId());
    }

    @Test
    public void findDeleteAddFuture() {

        // Create new instance of Strategy and set
        // values in it by reading them from form object
        ZonedDateTime expiry = TradingCalendar.getDateAtTime(TradingCalendar.getDateTimeNowMarketTimeZone(), 19, 0,
                0);
        expiry = expiry.plusMonths(1);

        _log.info("Expiry Date: {}", expiry);
        Contract instance = new Contract(SECType.FUTURE, "ES", Exchange.SMART, Currency.USD, expiry,
                new BigDecimal(50));
        instance = tradeService.saveAspect(instance);
        _log.info("Contract added Id:{}", instance.getId());

        expiry = expiry.plusDays(1);
        _log.info("Expiry Date: {}", expiry);
        List<Contract> contracts = contractRepository.findContractByUniqueKey(instance.getSecType(),
                instance.getSymbol(), instance.getExchange(), instance.getCurrency(),
                expiry);
        assertFalse(contracts.isEmpty());
        _log.info("Contract added Id:{}", instance.getId());

        tradeService.deleteAspect(contracts.getFirst());
        _log.info("Contract deleted Id:{}", instance.getId());
    }
}
