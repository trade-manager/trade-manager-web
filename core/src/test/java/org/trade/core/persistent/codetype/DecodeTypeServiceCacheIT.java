package org.trade.core.persistent.codetype;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class DecodeTypeServiceCacheIT extends TradestrategyBase {

    private static final Logger _log = LoggerFactory.getLogger(DecodeTypeServiceCacheIT.class);

    @Autowired
    CacheManager cacheManager;

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
    public void findCodeTypeType() {

        List<DecodeType> decodeTypes = tradeService.getCodeTypeService().findDecodeTypeByType("BarSize");
        assertFalse(decodeTypes.isEmpty());
        //codeTypes = cacheManager.getCache("codeTypes").get("BarSize", List.class);
        // Extracting a list of User objects from a cache named "users"

        decodeTypes = extractTypedList("BarSize", DecodeType.class);
        assertFalse(decodeTypes.isEmpty());
        for (DecodeType decodeType : decodeTypes) {

            for (CodeValue codeValue : decodeType.getCodeValues()) {

                _log.info(String.format("CodeValue type: %s, attribute: %s, value: %s", decodeType.getType(), codeValue.getCodeAttribute().getName(), codeValue.getCodeValue()));
            }
        }
    }

    private <T> List<T> extractTypedList(String cacheKey, Class<T> targetType) {

        // 1. Retrieve Spring's wrapper cache
        CaffeineCache springCache = (CaffeineCache) cacheManager.getCache(targetType.getSimpleName());

        if (springCache == null) {
            return Collections.emptyList();
        }

        // 2. Extract the native Caffeine cache instance
        Cache<Object, Object> nativeCache = springCache.getNativeCache();
        List<T> values = (List<T>) nativeCache.asMap().get(cacheKey);

        // 3. Stream, filter, and cast the values to a typed list
        return values.stream()
                .filter(targetType::isInstance)
                .map(targetType::cast)
                .collect(Collectors.toList());
    }
}
