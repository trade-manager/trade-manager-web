package org.trade.core.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Money;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some tests for the {@link TradingCalendar} class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class CoreUtilsIT {

    private final static Logger _log = LoggerFactory.getLogger(CoreUtilsIT.class);

    private static final int SCALE = 5;
    private AtomicInteger timerRunning = null;
    private final Object lockCoreUtilsTest = new Object();

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
    public void isBetween() {

        assertTrue(CoreUtils.isBetween(BigDecimal.valueOf(12.20), BigDecimal.valueOf(12.24), BigDecimal.valueOf(12.23)));

        assertTrue(CoreUtils.isBetween(12, 18, 15));

        assertFalse(CoreUtils.isBetween(12, 18, 6));

        assertTrue(CoreUtils.isBetween(12.20d, 12.24d, 12.23d));

        assertTrue(CoreUtils.isBetween(12.20d, 12.26d, 12.26d));

        assertTrue(CoreUtils.isBetween(12.20d, 12.26d, 12.20d));

        assertTrue(CoreUtils.isBetween(12.24d, 12.20d, 12.23d));

        assertTrue(CoreUtils.isBetween(12.26d, 12.20d, 12.26d));

        assertTrue(CoreUtils.isBetween(12.26d, 12.20d, 12.20d));

        assertTrue(CoreUtils.isBetween(12.20d, 12.20d, 12.20d));

        assertFalse(CoreUtils.isBetween(12, 14, 11));

        assertFalse(CoreUtils.isBetween(12, 14, 15));
    }

    @Test
    public void nullSafe() {

        int returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.25), BigDecimal.valueOf(1.24));
        assertEquals(1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(1, 0);
        assertEquals(1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.23), null);
        assertEquals(1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(-1.23), BigDecimal.valueOf(-1.24));
        assertEquals(1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(null, null);
        assertEquals(0, returnVal);

        returnVal = CoreUtils.nullSafeComparator(0, 0);
        assertEquals(0, returnVal);

        returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.23), BigDecimal.valueOf(1.23));
        assertEquals(0, returnVal);

        returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(-1.23), BigDecimal.valueOf(-1.23));
        assertEquals(0, returnVal);

        returnVal = CoreUtils.nullSafeComparator(null, BigDecimal.valueOf(1.23));
        assertEquals(-1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.23), BigDecimal.valueOf(1.24));
        assertEquals(-1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(null, 1);
        assertEquals(-1, returnVal);

        returnVal = CoreUtils.nullSafeComparator(null, 0);
        assertEquals(-1, returnVal);


        Money avgFilledPrice = new Money(186.75);
        Money lastPrice = new Money(186.78);
        Money auxPrice = new Money(186.68);

        Money stopTriggerAmount = new Money(0.03);
        Money stopMoveAmount = new Money(0.02);

        Money initialStopTriggerAmount = new Money(0.02);
        Money initialStopMoveAmount = new Money(0.01);

        int buySellMultiplier = 1;

        if (CoreUtils.nullSafeComparator(auxPrice, avgFilledPrice) == -1 * buySellMultiplier) {
            if ((CoreUtils.nullSafeComparator(lastPrice.getBigDecimalValue(),
                    avgFilledPrice.getBigDecimalValue()
                            .add(initialStopTriggerAmount.getBigDecimalValue()
                                    .multiply(new BigDecimal(buySellMultiplier)))) == buySellMultiplier)
                    || (CoreUtils.nullSafeComparator(lastPrice.getBigDecimalValue(),
                    avgFilledPrice.getBigDecimalValue().add(initialStopTriggerAmount.getBigDecimalValue()
                            .multiply(new BigDecimal(buySellMultiplier)))) == 0)) {

                auxPrice = new Money(avgFilledPrice.getBigDecimalValue().add(
                        initialStopMoveAmount.getBigDecimalValue().multiply(new BigDecimal(buySellMultiplier))));

            }
        }
        assertEquals(
                new Money(avgFilledPrice.getBigDecimalValue().add(
                        initialStopMoveAmount.getBigDecimalValue().multiply(new BigDecimal(buySellMultiplier)))),
                auxPrice);

        lastPrice = new Money(lastPrice.getBigDecimalValue()
                .add(stopTriggerAmount.getBigDecimalValue().multiply(new BigDecimal(buySellMultiplier))));

        if (CoreUtils.nullSafeComparator(auxPrice, avgFilledPrice) == buySellMultiplier) {
            if ((CoreUtils.nullSafeComparator(lastPrice.getBigDecimalValue(),
                    auxPrice.getBigDecimalValue()
                            .add(stopTriggerAmount.getBigDecimalValue()
                                    .multiply(new BigDecimal(buySellMultiplier)))) == buySellMultiplier)
                    || (CoreUtils.nullSafeComparator(lastPrice.getBigDecimalValue(),
                    auxPrice.getBigDecimalValue().add(stopTriggerAmount.getBigDecimalValue()
                            .multiply(new BigDecimal(buySellMultiplier)))) == 0)) {
                auxPrice = new Money(lastPrice.getBigDecimalValue()
                        .subtract(stopMoveAmount.getBigDecimalValue().multiply(new BigDecimal(buySellMultiplier))));
            }
        }
        assertEquals(
                new Money(lastPrice.getBigDecimalValue()
                        .subtract(stopMoveAmount.getBigDecimalValue().multiply(new BigDecimal(buySellMultiplier)))),
                auxPrice);

    }

    @Test
    public void bigDecimalRounding() {

        BigDecimal avgFillPrice = new BigDecimal("35.34567897").setScale(SCALE, RoundingMode.HALF_EVEN);
        assertEquals(new BigDecimal("35.34568"), avgFillPrice);

        avgFillPrice = new BigDecimal("35.34567344").setScale(SCALE, RoundingMode.HALF_EVEN);
        assertEquals(new BigDecimal("35.34567"), avgFillPrice);

        assertEquals(0, BigDecimal.ZERO.compareTo(BigDecimal.valueOf(0.00)));

        assertEquals(-1, BigDecimal.ZERO.compareTo(BigDecimal.valueOf(0.01)));

        assertEquals(1, BigDecimal.ZERO.compareTo(BigDecimal.valueOf(-0.01)));
    }

    @Test
    public void tenMinTimer() throws Exception {

        Timer timer = new Timer(250, _ -> {
            synchronized (lockCoreUtilsTest) {
                timerRunning.addAndGet(250);
                lockCoreUtilsTest.notifyAll();
            }
        });

        timerRunning = new AtomicInteger(0);
        int sleeptime = 5;
        timer.start();
        synchronized (lockCoreUtilsTest) {
            while (timerRunning.get() < (1000 * sleeptime)) {
                String message = "Please wait " + (sleeptime - (timerRunning.get() / 1000)) + " seconds.";
                _log.info(message);
                lockCoreUtilsTest.wait();
            }
        }
        timer.stop();
    }

    @Test
    public void intRounding() {

        int barSize = 900;
        int[] barSizes = {3600, 1800, 900, 300, 120, 60, 30};
        for (int element : barSizes) {
            if (element <= barSize) {
                if ((Math.floor(barSize / (double) element) == (barSize / (double) element))) {
                    _log.info("BarSize integer devisable : {}", element);
                }
            }
        }
    }

    @Disabled
    @Test
    public void fixDemoData() {

        try {

            String filePath = "src/test/resources/demo-data-temp.sql";
            String tempFilePath = "src/test/resources/demo-data-temp1.sql";

            // 01/22/2024 and 2024-01-22
            String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // Example: YYYY-MM-DD
            String dateFormat = "yyyy-MM-dd";
            //String datePattern = "\\d{2}/\\d{2}/\\d{4}";
            //String dateFormat ="MM/dd/yyyy";

            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

            File inputFile = new File(filePath);
            File tempFile = new File(tempFilePath);

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            Pattern pattern = Pattern.compile(datePattern);
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {

                Matcher matcher = pattern.matcher(currentLine);
                StringBuilder sb = new StringBuilder();

                while (matcher.find()) {

                    String matchedValue = matcher.group();
                    Date date = formatter.parse(matchedValue);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.YEAR, 8);

                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {  //or sunday
                        calendar.add(Calendar.DAY_OF_WEEK, 2);
                    } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        calendar.add(Calendar.DAY_OF_WEEK, 2);
                    }

                    SimpleDateFormat formatterNew = new SimpleDateFormat(dateFormat);
                    String formattedDate = formatterNew.format(calendar.getTime());
                    matcher.appendReplacement(sb, formattedDate);
                }
                matcher.appendTail(sb);
                writer.write(sb + System.lineSeparator());
            }
            writer.close();
            reader.close();
/*
            if (inputFile.delete()) {

                if (!tempFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename temp file!");
                }
            } else {
                throw new IOException("Could not delete original file!");
            }
*/
            System.out.println("Dates replaced successfully!");

        } catch (Exception ex) {

            System.err.println("Error processing file: " + ex.getMessage());
        }
    }
}
