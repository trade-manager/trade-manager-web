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
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.TradeService;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@SpringBootTest
public class PortfolioIT {

    private final static Logger _log = LoggerFactory.getLogger(PortfolioIT.class);

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PortfolioRepository portfolioRepository;

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

        Aspects accounts = tradeService.findByClassName(Account.class.getName());
        for (Aspect aspect : accounts.getAspect()) {
            tradeService.deleteAspect(aspect);
        }
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testCreateAccount() throws Exception {

        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        portfolio = portfolioRepository.findByName(portfolio.getName());
        Account account = new Account("Test", "T123456", Currency.USD, AccountType.INDIVIDUAL);
        PortfolioAccount portfolioAccount = new PortfolioAccount(portfolio, account);
        portfolio.getPortfolioAccounts().add(portfolioAccount);
        portfolio = portfolioRepository.save(portfolio);
        assertNotNull(portfolio.getIndividualAccount());
    }
}
