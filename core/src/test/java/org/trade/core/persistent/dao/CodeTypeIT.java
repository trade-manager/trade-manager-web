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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class CodeTypeIT {

    private final static Logger _log = LoggerFactory.getLogger(CodeTypeIT.class);

    @Autowired
    CodeTypeRepository codeTypeRepository;

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
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void findCodeValueByName() {

        CodeType codeType = codeTypeRepository.findByName("MovingAverage");
        assertNotNull(codeType);
        _log.info("CodeType id: {}", codeType.getId());
        List<CodeValue> codeValues = codeTypeRepository.findByAttributeName(codeType.getName(), "Length");
        assertFalse(codeValues.isEmpty());
        _log.info("CodeValue id: {}", codeValues.getFirst().getId());
    }
}
