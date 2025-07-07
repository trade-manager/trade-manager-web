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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Currency;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TWSFinancialAccountRequestIT {

    private final static Logger _log = LoggerFactory.getLogger(TWSFinancialAccountRequestIT.class);

    @Autowired
    private TradeService tradeService;

    private static final List<Account> accounts = new ArrayList<>();

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

        for (Account account : accounts) {

            account = tradeService.findAccountByAccountNumber(account.getAccountNumber());

            if (null != account) {

                tradeService.deleteAspect(account);
            }
        }
        accounts.clear();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void aliasEmptyRequest() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();

        // String xml =
        // "<?xml version=\"1.0\"
        // encoding=\"UTF-8\"?><ListOfAccountAliases/>";
        // ByteArrayInputStream inputSource = new ByteArrayInputStream(
        // xml.getBytes("utf-8"));
        // final Aspects aspects = (Aspects) request.fromXML(inputSource);
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliasesEmpty.xml"));

        assertTrue(aspects.getAspect().isEmpty());
    }

    @Test
    public void aliasRequest() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliases.xml"));

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Account item = (Account) aspect;

            if (null == tradeService.findAccountByAccountNumber(item.getAccountNumber())) {

                item.setCurrency(Currency.USD);
                item.setAccountType(AccountType.INDIVIDUAL);
                item = tradeService.saveAspect(item);
                assertNotNull(item.getId());
                accounts.add(item);
            }
        }
    }

    @Test
    public void aliasRequest1() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliases1.xml"));

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Account item = (Account) aspect;

            if (null == tradeService.findAccountByAccountNumber(item.getAccountNumber())) {

                item.setCurrency(Currency.USD);
                item.setAccountType(AccountType.INDIVIDUAL);
                item = tradeService.saveAspect(item);
                assertNotNull(item.getId());
                accounts.add(item);
            }
        }
    }

    @Test
    public void allocationEmptyRequest() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocationEmpty.xml"));

        assertTrue(aspects.getAspect().isEmpty());
    }

    @Test
    public void allocationRequest() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocation.xml"));

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Portfolio item = (Portfolio) aspect;

            accounts.addAll(item.getAccounts());

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());
            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void allocationRequest1() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocation1.xml"));

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Portfolio item = (Portfolio) aspect;

            accounts.addAll(item.getAccounts());

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());
            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void allocationRequestNew() {

        Aspects aspects = new Aspects();
        Portfolio portfolio = new Portfolio("pf_eq_daily", "pf_eq_daily");
        Account account1 = new Account("DU12345", "DU12345", Currency.USD, AccountType.INDIVIDUAL);
        portfolio.getAccounts().add(account1);
        aspects.add(portfolio);

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Portfolio item = (Portfolio) aspect;

            accounts.addAll(item.getAccounts());

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());
            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void groupEmptyRequest() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groupsEmpty.xml"));

        assertTrue(aspects.getAspect().isEmpty());
    }

    @Test
    public void groupRequest() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groups.xml"));

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Portfolio item = (Portfolio) aspect;

            for (Account account : item.getAccounts()) {

                if (null == account.getName()) {
                    account.setName(account.getAccountNumber());
                }
                accounts.add(account);
            }

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());
            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void groupRequest1() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groups1.xml"));

        assertFalse(aspects.getAspect().isEmpty());

        for (Aspect aspect : aspects.getAspect()) {

            Portfolio item = (Portfolio) aspect;

            for (Account account : item.getAccounts()) {

                if (null == account.getName()) {
                    account.setName(account.getAccountNumber());
                }
                accounts.add(account);
            }

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());
            tradeService.deleteAspect(item);
        }
    }
}
