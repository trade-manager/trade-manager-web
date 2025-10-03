package org.trade.core.persistent;

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
import org.trade.core.persistent.domain.Domain;
import org.trade.core.persistent.domain.DomainService;

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
public class DomainServiceIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(DomainServiceIT.class);

    @Autowired
    private DomainService domainService;

    private static final String childDomainName = "CHILD-" + TradestrategyBase.getRandomNumber(4);

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
    public void createDomain() {

        Domain gobalDomain = domainService.findDomainByName(Domain.GLOBAL);
        assertNotNull(gobalDomain);
        assertFalse(gobalDomain.hasParent());
        Domain childDomain = new Domain(childDomainName, childDomainName);
        childDomain.setParent(gobalDomain);
        childDomain = domainService.saveDomain(childDomain);
        assertNotNull(childDomain.getId());
        assertTrue(childDomain.hasParent());
        this.addRecord(childDomain);
    }
}
