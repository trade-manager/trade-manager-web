package org.trade.core.broker.request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Currency;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TWSFinancialAccountRequestIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TWSFinancialAccountRequestIT.class);

    private static final List<Account> accounts = new ArrayList<>();

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
    public void setUp() {

    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
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

        assertTrue(aspects.getAspects().isEmpty());
    }

    @Test
    public void aliasRequest() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliases.xml"));

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Account item = (Account) aspect;

            if (null == tradeService.findAccountByAccountNumber(item.getAccountNumber())) {

                item.setCurrency(Currency.USD);
                item.setAccountType(AccountType.INDIVIDUAL);
                item = tradeService.saveAspect(item);
                assertNotNull(item.getId());
                this.addRecord(item);
                accounts.add(item);
            }
        }
    }

    @Test
    public void aliasRequest1() throws Exception {

        final TWSAccountAliasRequest request = new TWSAccountAliasRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/aliases1.xml"));

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Account item = (Account) aspect;

            if (null == tradeService.findAccountByAccountNumber(item.getAccountNumber())) {

                item.setCurrency(Currency.USD);
                item.setAccountType(AccountType.INDIVIDUAL);
                item = tradeService.saveAspect(item);
                assertNotNull(item.getId());
                this.addRecord(item);
                accounts.add(item);
            }
        }
    }

    @Test
    public void allocationEmptyRequest() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocationEmpty.xml"));

        assertTrue(aspects.getAspects().isEmpty());
    }

    @Test
    public void allocationRequest() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocation.xml"));

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Portfolio item = (Portfolio) aspect;

            accounts.addAll(item.getAccounts());

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());

            for (Account account : item.getAccounts()) {

                this.addRecord(account);
            }
            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void allocationRequest1() throws Exception {

        final TWSAllocationRequest request = new TWSAllocationRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/allocation1.xml"));

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Portfolio item = (Portfolio) aspect;

            accounts.addAll(item.getAccounts());

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());

            for (Account account : item.getAccounts()) {

                this.addRecord(account);
            }
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

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Portfolio item = (Portfolio) aspect;

            accounts.addAll(item.getAccounts());

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());

            for (Account account : item.getAccounts()) {

                this.addRecord(account);
            }

            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void groupEmptyRequest() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groupsEmpty.xml"));

        assertTrue(aspects.getAspects().isEmpty());
    }

    @Test
    public void groupRequest() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groups.xml"));

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Portfolio item = (Portfolio) aspect;

            for (Account account : item.getAccounts()) {

                if (null == account.getName()) {
                    account.setName(account.getAccountNumber());
                }
                accounts.add(account);
            }

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());

            for (Account account : item.getAccounts()) {

                this.addRecord(account);
            }
            tradeService.deleteAspect(item);
        }
    }

    @Test
    public void groupRequest1() throws Exception {

        final TWSGroupRequest request = new TWSGroupRequest();
        final Aspects aspects = (Aspects) request.fromXML(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("broker/request/groups1.xml"));

        assertFalse(aspects.getAspects().isEmpty());

        for (Aspect aspect : aspects.getAspects()) {

            Portfolio item = (Portfolio) aspect;

            for (Account account : item.getAccounts()) {

                if (null == account.getName()) {
                    account.setName(account.getAccountNumber());
                }
                accounts.add(account);
            }

            item = tradeService.savePortfolio(item);
            assertNotNull(item.getId());

            for (Account account : item.getAccounts()) {

                this.addRecord(account);
            }

            tradeService.deleteAspect(item);
        }
    }
}
