package org.trade.core.persistent.portfolio;

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
import org.trade.core.persistent.account.Account;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class PortfolioServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(PortfolioServiceIT.class);

    private static final String accountNumber = "TEST-" + TradestrategyBase.getRandomNumber(4);

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
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws ClassNotFoundException {

        this.deleteRecords();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void createAccount() {

        org.trade.core.persistent.portfolio.Portfolio portfolio = (org.trade.core.persistent.portfolio.Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        portfolio = tradeService.getPortfolioService().findByName(portfolio.getName());
        assertNotNull(portfolio);
        List<Account> accounts = new ArrayList<>(0);
        Account account = new Account("Test", accountNumber, Currency.USD, AccountType.INDIVIDUAL);
        accounts.add(account);
        portfolio.setAccounts(accounts);
        portfolio = tradeService.getPortfolioService().save(portfolio);
        assertNotNull(portfolio.getIndividualAccount());
        this.addRecord(portfolio.getAccounts().getFirst());
    }
}
