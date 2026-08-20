package org.trade.core.persistent.strategy.series;

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
import org.trade.core.persistent.strategy.series.indicator.candle.CandlePeriod;
import org.trade.core.util.MatrixFunctions;
import org.trade.core.util.Pair;
import org.trade.core.util.time.TradingCalendar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Some tests for the {@link TradingCalendar} class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class MatrixFunctionsIT {

    private static final Logger _log = LoggerFactory.getLogger(MatrixFunctionsIT.class);

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
    public void angle() {

        List<Pair> pairs = new ArrayList<>();
        int polyOrder = 2;
        double vwap = 30.94;
        int longShort = 1;

        CandlePeriod period = new CandlePeriod(
                TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone()), 300);
        long startPeriod = TradingCalendar.geMillisFromZonedDateTime(period.getStart());
        long endPeriod;
        pairs.add(new Pair(0, vwap));

        for (int i = 0; i < 3; i++) {

            vwap = vwap + (0.1 * longShort) + (((double) i * longShort / 10));
            period = (CandlePeriod) period.next();
            endPeriod = TradingCalendar.geMillisFromZonedDateTime(period.getStart());
            pairs.add(new Pair(((double) (endPeriod - startPeriod) / (1000 * 60 * 60)), vwap));
        }

        pairs.sort(Pair.X_VALUE_ASC);

        for (Pair pair : pairs) {

            _log.info("x: {} y: {}", pair.x, pair.y);
        }
        Pair[] pairsArray = pairs.toArray(new Pair[]{});
        double[] terms = MatrixFunctions.solve(pairsArray, polyOrder);
        double correlationCoeff = MatrixFunctions.getCorrelationCoefficient(pairsArray, terms);
        double standardError = MatrixFunctions.getStandardError(pairsArray, terms);
        String output = MatrixFunctions.toPrint(polyOrder, correlationCoeff, standardError, terms,
                pairsArray.length);
        _log.info("Pivot Calc: {}", output);

        for (Pair pair : pairs) {

            double y = MatrixFunctions.fx(pair.x, terms);
            pair.y = y;
            _log.info("x: {} y: {}", pair.x, pair.y);
        }

        Pair startXY = pairs.getFirst();
        Pair endXY = pairs.getLast();
        double atan = Math.atan((endXY.y - startXY.y) / ((endXY.x - startXY.x)));
        double angle = (atan * 180) / Math.PI;
        _log.info("angle: {}", angle);
        assertEquals(new BigDecimal("67.38").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(angle).setScale(2, RoundingMode.HALF_UP));
    }
}
