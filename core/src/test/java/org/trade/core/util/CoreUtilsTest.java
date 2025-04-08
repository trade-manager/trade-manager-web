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
package org.trade.core.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Some tests for the {@link TradingCalendar} class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class CoreUtilsTest {

    private final static Logger _log = LoggerFactory.getLogger(CoreUtilsTest.class);

    @Rule
    public TestName name = new TestName();

    private static final int SCALE = 5;

    /**
     * Method setUpBeforeClass.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * Method setUp.
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Method tearDown.
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testIsBetween() {
        try {

            assertTrue("1", CoreUtils.isBetween(BigDecimal.valueOf(12.20), BigDecimal.valueOf(12.24), BigDecimal.valueOf(12.23)));

            assertTrue("2", CoreUtils.isBetween(12, 18, 15));

            assertFalse("3", CoreUtils.isBetween(12, 18, 6));

            assertTrue("4", CoreUtils.isBetween(12.20d, 12.24d, 12.23d));

            assertTrue("5", CoreUtils.isBetween(12.20d, 12.26d, 12.26d));

            assertTrue("6", CoreUtils.isBetween(12.20d, 12.26d, 12.20d));

            assertTrue("7", CoreUtils.isBetween(12.24d, 12.20d, 12.23d));

            assertTrue("8", CoreUtils.isBetween(12.26d, 12.20d, 12.26d));

            assertTrue("9", CoreUtils.isBetween(12.26d, 12.20d, 12.20d));

            assertTrue("10", CoreUtils.isBetween(12.20d, 12.20d, 12.20d));

            assertFalse("11", CoreUtils.isBetween(12, 14, 11));

            assertFalse("12", CoreUtils.isBetween(12, 14, 15));

        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testNullSafe() {
        try {

            int returnVal = CoreUtils.nullSafeComparator(null, BigDecimal.valueOf(1.23));
            assertEquals("1", -1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.23), null);
            assertEquals("2", 1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(-1.23), BigDecimal.valueOf(-1.24));
            assertEquals("3", 1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(null, null);
            assertEquals("4", 0, returnVal);

            returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.23), BigDecimal.valueOf(1.24));
            assertEquals("5", -1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(BigDecimal.valueOf(1.25), BigDecimal.valueOf(1.24));
            assertEquals("6", 1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(null, 1);
            assertEquals("7", -1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(null, 0);
            assertEquals("8", -1, returnVal);

            returnVal = CoreUtils.nullSafeComparator(0, 0);
            assertEquals("9", 0, returnVal);

            returnVal = CoreUtils.nullSafeComparator(1, 0);
            assertEquals("10", 1, returnVal);

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
            assertEquals("11",
                    new Money(lastPrice.getBigDecimalValue()
                            .subtract(stopMoveAmount.getBigDecimalValue().multiply(new BigDecimal(buySellMultiplier)))),
                    auxPrice);

        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testBigDecimalRounding() {

        BigDecimal avgFillPrice = new BigDecimal("35.34567897").setScale(SCALE, RoundingMode.HALF_EVEN);
        assertEquals("1", new BigDecimal("35.34568"), avgFillPrice);

        avgFillPrice = new BigDecimal("35.34567344").setScale(SCALE, RoundingMode.HALF_EVEN);
        assertEquals("2", new BigDecimal("35.34567"), avgFillPrice);

        assertEquals("3", 0, BigDecimal.ZERO.compareTo(BigDecimal.valueOf(0.00)));

        assertEquals("4", -1, BigDecimal.ZERO.compareTo(BigDecimal.valueOf(0.01)));

        assertEquals("5", 1, BigDecimal.ZERO.compareTo(BigDecimal.valueOf(-0.01)));
    }

    private AtomicInteger timerRunning = null;
    private final Object lockCoreUtilsTest = new Object();

    @Test
    public void test10MinTimer() {

        try {

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
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testIntRounding() {

        try {
            int barSize = 900;
            int[] barSizes = {3600, 1800, 900, 300, 120, 60, 30};
            for (int element : barSizes) {
                if (element <= barSize) {
                    if ((Math.floor(barSize / (double) element) == (barSize / (double) element))) {
                        _log.info("BarSize integer devisable : {}", element);
                    }
                }
            }
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Ignore
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
