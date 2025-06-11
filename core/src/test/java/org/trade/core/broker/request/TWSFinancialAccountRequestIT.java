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
package org.trade.core.broker.request;

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
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.PortfolioAccount;
import org.trade.core.properties.TradeAppLoadConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Currency;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TWSFinancialAccountRequestIT {

    private final static Logger _log = LoggerFactory.getLogger(TWSFinancialAccountRequestIT.class);

    @Autowired
    private TradeService tradeService;

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

        Aspects portfolioAccounts = tradeService.findAspectsByClassName(PortfolioAccount.class.getName());
        for (Aspect aspect : portfolioAccounts.getAspect()) {
            tradeService.deleteAspect(aspect);
        }

        Aspects accounts = tradeService.findAspectsByClassName(Account.class.getName());
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
    public void testAliasEmptyRequest() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();

        // String xml =
        // "<?xml version=\"1.0\"
        // encoding=\"UTF-8\"?><ListOfAccountAliases/>";
        // ByteArrayInputStream inputSource = new ByteArrayInputStream(
        // xml.getBytes("utf-8"));
        // final Aspects aspects = (Aspects) request.fromXML(inputSource);
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliasesEmpty.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            Account item = (Account) aspect;
            Account account = tradeService.findAccountByNumber(item.getAccountNumber());
            account.setAlias(item.getAlias());
            tradeService.persistAspect(account);
        }
    }

    @Test
    public void testAliasRequest() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliases.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            Account item = (Account) aspect;
            Account account = tradeService.findAccountByNumber(item.getAccountNumber());
            if (null == account) {
                account = new Account(item.getAccountNumber(), item.getAccountNumber(), Currency.USD,
                        AccountType.INDIVIDUAL);
            }
            account.setAlias(item.getAlias());
            account.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            tradeService.persistAspect(account);
        }
    }

    @Test
    public void testAliasRequest1() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliases1.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            Account item = (Account) aspect;
            Account account = tradeService.findAccountByNumber(item.getAccountNumber());
            if (null == account) {
                account = new Account(item.getAccountNumber(), item.getAccountNumber(), Currency.USD,
                        AccountType.INDIVIDUAL);
            }
            account.setAlias(item.getAlias());
            account.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
            tradeService.persistAspect(account);
        }
    }

    @Test
    public void testAllocationEmptyRequest() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocationEmpty.xml"));

        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }

    @Test
    public void testAllocationRequest() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocation.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }

    @Test
    public void testAllocationRequest1() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocation1.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }

    @Test
    public void testAllocationRequestNew() throws Exception {

        Aspects aspects = new Aspects();
        Portfolio portfolio = new Portfolio("pf_eq_daily", "pf_eq_daily");
        Account account = new Account("DU12345", "DU12345", Currency.USD, AccountType.INDIVIDUAL);
        PortfolioAccount portfolioAccount = new PortfolioAccount(portfolio, account);
        portfolio.getPortfolioAccounts().add(portfolioAccount);
        aspects.add(portfolio);

        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }

    @Test
    public void testGroupEmptyRequest() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groupsEmpty.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }

    @Test
    public void testGroupRequest() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groups.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }

    @Test
    public void testGroupRequest1() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groups1.xml"));
        for (Aspect aspect : aspects.getAspect()) {
            tradeService.savePortfolio((Portfolio) aspect);
        }
    }
}
