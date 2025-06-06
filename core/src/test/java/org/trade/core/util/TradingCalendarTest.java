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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.util.time.TradingCalendar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Some tests for the {@link TradingCalendar} class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradingCalendarTest {

    private final static Logger _log = LoggerFactory.getLogger(TradingCalendarTest.class);


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
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testAddBusinessDays() {

        ZonedDateTime date = TradingCalendar.getDateTimeNowMarketTimeZone();
        assertTrue(TradingCalendar.isTradingDay(TradingCalendar.addTradingDays(date, -4)));
        assertTrue(TradingCalendar.isTradingDay(TradingCalendar.addTradingDays(date, -5)));
    }

    @Test
    public void testGetDateAtTime() {

        ZonedDateTime date = TradingCalendar.getDateTimeNowMarketTimeZone().minusDays(1);
        _log.debug("date: {}", date);

        ZonedDateTime todayStartDate = TradingCalendar
                .getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone());
        _log.debug("todayStartDate: {}", todayStartDate);
        ZonedDateTime prevStartDate = TradingCalendar.getDateAtTime(date, todayStartDate);
        _log.debug("prevStartDate: {}", prevStartDate);

        assertEquals(todayStartDate.getHour(), prevStartDate.getHour());
        assertEquals(todayStartDate.getMinute(), prevStartDate.getMinute());
    }

    @Test
    public void testGetPrevTradingDay() {

        ZonedDateTime date = TradingCalendar.getDateTimeNowMarketTimeZone();
        date = TradingCalendar.getPrevTradingDay(date);
        _log.debug("date: {}", date);
        assertTrue(TradingCalendar.isTradingDay(date));
    }

    @Test
    public void testGetNextTradingDay() {

        ZonedDateTime date = TradingCalendar.getDateTimeNowMarketTimeZone();
        date = TradingCalendar.getNextTradingDay(date);
        _log.debug("date: {}", date);
        assertTrue(TradingCalendar.isTradingDay(date));
    }

    @Test
    public void testCetCurrentTradingDay() {

        ZonedDateTime date = TradingCalendar.getDateTimeNowMarketTimeZone();
        date = TradingCalendar.getCurrentTradingDay();
        _log.debug("date: {}", date);
        assertTrue(TradingCalendar.isTradingDay(date));
    }

    @Test
    public void testPrintTimeZones() {

        String[] timeZones = TimeZone.getAvailableIDs();

        for (String timeZone : timeZones) {
            _log.info("TZ: {}", timeZone);
        }
    }

    @Test
    public void testIsMarketHours() {

        // TradingCalendar.getFormattedDate("20150401 09:30",
        // "yyyyMMdd HH:mm");
        // Date openDate = TradingCalendar.getBusinessDayStart(new Date());
        // Date closeDate = TradingCalendar.getBusinessDayEnd(new Date());
        // Date date = TradingCalendar.addBusinessDays(new Date(), -1);

        ZonedDateTime openDate = TradingCalendar.getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone());
        ZonedDateTime closeDate = TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone());
        ZonedDateTime date = TradingCalendar.addTradingDays(TradingCalendar.getDateTimeNowMarketTimeZone(), -1);
        date = TradingCalendar.getDateAtTime(date, 9, 30, 0);
        _log.debug("Business day openDate: {} Business day closeDate: {} Date: {}", openDate, closeDate, date);

        assertTrue(TradingCalendar.isMarketHours(openDate, closeDate, date));

        date = TradingCalendar.getDateAtTime(date, 16, 0, 0);
        _log.debug("Business day openDate: {} Business day closeDate: {} Date: {}", openDate, closeDate, date);

        assertFalse(TradingCalendar.isMarketHours(openDate, closeDate, date));

        date = TradingCalendar.getDateAtTime(date, 15, 0, 0);
        _log.debug("Business day openDate: {} Business day closeDate: {} Date: {}", openDate, closeDate, date);

        assertTrue(TradingCalendar.isMarketHours(openDate, closeDate, date));

        date = TradingCalendar.getDateAtTime(date, 17, 0, 0);
        _log.debug("Business day openDate: {} Business day closeDate: {} Date: {}", openDate, closeDate, date);

        assertFalse(TradingCalendar.isMarketHours(openDate, closeDate, date));
    }

    @Test
    public void testIsTradingday() {

        ZonedDateTime date = TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone());
        assertTrue(TradingCalendar.isTradingDay(date));

        while (date.getDayOfWeek().compareTo(DayOfWeek.SUNDAY) != 0) {
            date = date.plusDays(1);
            _log.debug("dayOfWeek: {}", date.getDayOfWeek());
        }
        assertFalse(TradingCalendar.isTradingDay(date));
    }

    @Test
    public void testIsGreaterThan365() {

        int chartDays = 365;
        ZonedDateTime endDate = TradingCalendar.getDateTimeNowMarketTimeZone();
        if (TradingCalendar.getDurationInDays(endDate.minusDays(chartDays),
                TradingCalendar.getDateTimeNowMarketTimeZone()) > TradingCalendar.getDaysInYear(endDate)) {
            chartDays = (int) (TradingCalendar.getDaysInYear(endDate)
                    - TradingCalendar.getDurationInDays(TradingCalendar.getDateTimeNowMarketTimeZone(), endDate));
        }
        _log.debug("chartDays: {}", chartDays);
        assertEquals(365, chartDays);
    }

    @Test
    public void testNextRequestId() {

        AtomicInteger reqId;
        ZonedDateTime date = TradingCalendar.getDateTimeNowMarketTimeZone();
        _log.debug("date: {}", TradingCalendar.geMillisFromZonedDateTime(date));
        reqId = new AtomicInteger((int) (TradingCalendar.geMillisFromZonedDateTime(date) / 1000d));
        _log.debug("reqId: {}", reqId);
        _log.debug("reqId: {}", reqId.incrementAndGet());
        assertNotNull(reqId);
    }

    @Test
    public void testCreateChartPanel() {

        ZonedDateTime endDate = TradingCalendar.getDateTimeNowMarketTimeZone();
        ZonedDateTime close = TradingCalendar.getDateAtTime(TradingCalendar.getDateTimeNowMarketTimeZone(), 16, 0,
                0);
        ZonedDateTime open = TradingCalendar.getDateAtTime(TradingCalendar.getDateTimeNowMarketTimeZone(), 9, 30,
                0);

        endDate = TradingCalendar
                .getDateAtTime(TradingCalendar.getPrevTradingDay(TradingCalendar.addTradingDays(close, 0)), close);
        assertTrue(TradingCalendar.isTradingDay(endDate));

        ZonedDateTime startDate = endDate.minusDays((2 - 1));
        startDate = TradingCalendar.getPrevTradingDay(startDate);
        assertTrue(TradingCalendar.isTradingDay(startDate));

        startDate = TradingCalendar.getDateAtTime(startDate, open);
        assertTrue(TradingCalendar.isTradingDay(startDate));

        _log.debug("startDate: {}", startDate);
    }

    @Test
    public void testZonedDateTimeConverter() {

        ZonedDateTime mktDateTime = TradingCalendar.getDateTimeNowMarketTimeZone();
        _log.debug("mktDateTime: {}", mktDateTime);

        Instant instant = mktDateTime.toInstant();
        _log.debug("instant: {}", instant);

        java.util.Date javaDate = java.util.Date.from(instant);
        _log.debug("javaDate: {}", javaDate);

        javaDate = TradingCalendar.convertTimeZone(javaDate, TimeZone.getDefault(),
                TimeZone.getTimeZone(TradingCalendar.MKT_TIMEZONE));
        _log.debug("javaDate convertTimeZone: {}", javaDate);

        javaDate = TradingCalendar.convertTimeZone(javaDate, TimeZone.getTimeZone(TradingCalendar.MKT_TIMEZONE),
                TimeZone.getDefault());
        _log.debug("javaDate: {}", javaDate);

        Instant javaDateInstant = Instant.ofEpochMilli(javaDate.getTime());
        _log.debug("javaDateInstant: {}", javaDateInstant);
        ZonedDateTime convZonedDatetime = ZonedDateTime.ofInstant(javaDateInstant, TradingCalendar.MKT_TIMEZONE);
        _log.debug("convZonedDatetime: {}", convZonedDatetime);
    }

    @Test
    public void testZonedDateTimeSnippits() {

        LocalDateTime specificTime = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), 9, 30, 0);
        _log.debug("specificTime: {}", specificTime);

        ZoneId zone = ZoneId.systemDefault();
        _log.debug("zoneID: {}", zone);

        LocalDateTime endOfPeriod = specificTime.plusSeconds(299);
        _log.debug("endOfPeriod: {}", endOfPeriod);

        Duration duration = Duration.between(specificTime, endOfPeriod);
        long seconds = duration.getSeconds();
        _log.debug("Duration: {}", seconds);

        Period period = Period.between(endOfPeriod.toLocalDate(), specificTime.toLocalDate());
        _log.debug("period: {}", period.get(ChronoUnit.DAYS));

        ZonedDateTime zonedDateTime1 = endOfPeriod.atZone(ZoneId.of("America/Los_Angeles"));
        _log.debug("zonedDateTime at America/Los_Angeles: {}", zonedDateTime1.plusHours((zonedDateTime1.getOffset().getTotalSeconds() / 3600)));

        ZoneOffset offset = zonedDateTime1.getOffset();
        _log.debug("ZoneOffset at America/Los_Angeles: {}", offset.getTotalSeconds() / 3600);

        long millis = zonedDateTime1.toInstant().toEpochMilli();
        _log.debug("millis: {}", millis);
        _log.debug("seconds: {} milli: {}", millis / 1000, (int) (millis % 1000));
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond((millis / 1000), ((int) (millis % 1000)), offset);
        _log.debug("dateTime: {}", dateTime);

        java.util.Date inDate = new java.util.Date();
        _log.debug("inDate: {}", inDate);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(inDate.toInstant(), ZoneId.systemDefault());
        _log.debug("localDateTime: {}", localDateTime);

        java.util.Date outDate = java.util.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        _log.debug("outDate: {}", outDate);
        ZonedDateTime formattedDate = TradingCalendar.getZonedDateTimeFromDateString("03/31/2015", "MM/dd/yyyy",
                TradingCalendar.MKT_TIMEZONE);

        _log.debug("formattedDate: {}", formattedDate);

        String stringDate = "20150331";
        LocalDate date = TradingCalendar.getLocalDateFromDateString(stringDate, "yyyyMMdd");
        LocalDateTime localDateTime1 = date.atStartOfDay();
        _log.debug("localDateTime: {}", localDateTime1);

        // FORMAT: 20060505 08:00:00 {time zone}
        stringDate = "20150331 16:30:01";
        LocalDateTime goodTillDate = TradingCalendar.getLocalDateTimeFromDateTimeString(stringDate,
                "yyyyMMdd HH:mm:ss");
        _log.debug("goodTillDate: {}", goodTillDate);
        /*
         * TimeZone twsTimeZone = TimeZone.getTimeZone(ConfigProperties
         * .getPropAsString("trade.tws.timezone")); SimpleDateFormat sdf =
         * new SimpleDateFormat("yyyyMMdd HH:mm:ss");
         * sdf.setTimeZone(twsTimeZone); Date date =
         * sdf.parse(execution.m_time);
         */

        ZonedDateTime localZonedDateTime = ZonedDateTime.of(goodTillDate, TimeZone.getDefault().toZoneId());
        _log.debug("localZonedDateTime: {}", localZonedDateTime);

        ZonedDateTime mktZonedDateTime = localZonedDateTime.withZoneSameInstant(TradingCalendar.MKT_TIMEZONE);
        _log.debug("mktZonedDateTime: {}", mktZonedDateTime);

        stringDate = "2015-3-31";
        LocalDate candleDate = TradingCalendar.getLocalDateFromDateString(stringDate, "y-M-d");
        _log.debug("candleDate: {}", candleDate);

        // SimpleDateFormat _sdfLocal = new SimpleDateFormat(
        // "yyyyMMdd HH:mm:ss");
        // SimpleDateFormat _sdfExpiry = new SimpleDateFormat(
        // "yyyyMMdd");

        // expiry = 201506
        // _sdfExpiry.setTimeZone(TimeZone.getTimeZone("GMT"));
        // ibContract.m_expiry = _sdfExpiry.format(contract.getExpiry())
        // .substring(0, 6);
        //
        // ibOrder.m_goodTillDate =
        // _sdfLocal.format(order.getGoodTillTime());

        // TimeZone twsTimeZone = TimeZone.getTimeZone(ConfigProperties
        // .getPropAsString("trade.tws.timezone"));
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // sdf.setTimeZone(twsTimeZone);
        // executionFilter.m_time = sdf.format(mktOpen);

        // TradingCalendar.getFormattedDate(getOpen().toLocalDate(),
        // "MM/dd/yyyy");

        // expiryDate = TradingCalendar.getFormattedDateFromString(
        // token, "yyyyMM");

        // expiryDate = TradingCalendar.getFormattedDateFromString(
        // token, "yyyyMMdd");

        // String DATEFORMAT = "MM/dd/yyyy";
        // TradingCalendar.getFormattedDate(
        // endDate.toLocalDate(), DATEFORMAT),
    }

    @Test
    public void testGetLocalDateFromDateString() {

        LocalDate date = LocalDate.of(2015, 3, 31);
        LocalDate formattedDate = TradingCalendar.getLocalDateFromDateString("03/31/2015", "MM/dd/yyyy");

        _log.debug("formattedDate: {}", formattedDate);
        assertEquals(date, formattedDate);

        formattedDate = TradingCalendar.getLocalDateFromDateString("2015-3-31", "y-M-d");

        _log.debug("formattedDate: {}", formattedDate);
        assertEquals(date, formattedDate);

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2015, 3, 31, 0, 0, 0, 0, TradingCalendar.MKT_TIMEZONE);

        ZonedDateTime zonedDateTimeFormatted = TradingCalendar.getZonedDateTimeFromDateString("20150331",
                "yyyyMMdd", TradingCalendar.MKT_TIMEZONE);

        _log.debug("zonedDateTimeFormatted: {} zonedDateTime: {}", zonedDateTimeFormatted, zonedDateTime);
        assertEquals(zonedDateTimeFormatted, zonedDateTime);
    }

    @Test
    public void testGetLocalDateTimeFromDateTimeString() {

        LocalDateTime date = LocalDateTime.of(2015, 3, 31, 9, 59, 59);
        LocalDateTime formattedDate = TradingCalendar.getLocalDateTimeFromDateTimeString("20150331 09:59:59",
                "yyyyMMdd HH:mm:ss");

        _log.debug("formattedDate: {}", formattedDate);
        _log.debug("date: {}", date);
        assertEquals(date, formattedDate);
    }

    @Test
    public void testGetFormattedDate() {

        ZonedDateTime date = ZonedDateTime.of(2015, 3, 31, 9, 59, 59, 0, TimeZone.getDefault().toZoneId());

        _log.debug("date: {}", date);

        String DATE_FORMAT = "yyyyMMdd HH:mm:ss";

        String dateFormated = TradingCalendar.getFormattedDate(date, DATE_FORMAT);
        _log.debug("dateFormated ZonedDateTime: {}", dateFormated);

        dateFormated = TradingCalendar.getFormattedDate(date.toLocalDateTime(), DATE_FORMAT);
        _log.debug("dateFormated LocalDateTime: {}", dateFormated);

        DATE_FORMAT = "yyyyMMdd";
        dateFormated = TradingCalendar.getFormattedDate(date.toLocalDate(), "yyyyMMdd");
        _log.debug("dateFormated LocalDate: {}", dateFormated);

        ZonedDateTime dateBegin = ZonedDateTime.of(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC),
                ZoneOffset.UTC.normalized());
        _log.debug("dateBegin: {}", dateBegin);

        String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HHmmss'Z'";
        dateFormated = TradingCalendar.getFormattedDate(date, DATE_TIME_FORMAT);
        _log.debug("dateFormated: {}", dateFormated);

        assertEquals(date, TradingCalendar.getZonedDateTimeFromDateTimeString(dateFormated, DATE_TIME_FORMAT));
    }

    @Test
    public void getDateTimeNowMarketTimeZone() {

        LocalDateTime date = LocalDateTime.of(2015, 3, 31, 9, 59, 59);
        LocalDateTime formattedDate = TradingCalendar.getLocalDateTimeFromDateTimeString("20150331 09:59:59",
                "yyyyMMdd HH:mm:ss");

        _log.debug("formattedDate: {}", formattedDate);
        _log.debug("date: {}", date);
        assertEquals(date, formattedDate);

        ZonedDateTime nowMkt = TradingCalendar.getDateTimeNowMarketTimeZone();
        _log.debug("nowMkt: {}", nowMkt);

        ZonedDateTime nowPcZone = ZonedDateTime.now();
        _log.debug("nowPcZone: {}", nowPcZone);
        ZonedDateTime now = TradingCalendar.adjustDateTimeToMarketTimeZone(nowPcZone);
        _log.debug("now: {}", now);
        assertEquals(now.getZone(), nowMkt.getZone());
    }

    @Test
    public void testDurationDateTimeFromString() {

        ZonedDateTime startOfPeriod = ZonedDateTime.now();
        _log.debug("endOfPeriod: {}", startOfPeriod);

        ZonedDateTime endOfPeriod = startOfPeriod.plusSeconds(299);
        _log.debug("endOfPeriod: {}", endOfPeriod);

        long seconds = TradingCalendar.getDurationInSeconds(startOfPeriod, endOfPeriod);
        _log.debug("Duration: {}", seconds);
        assertEquals(299, seconds);

        endOfPeriod = endOfPeriod.plusDays(3);
        long days = TradingCalendar.getDurationInDays(startOfPeriod, endOfPeriod);
        _log.debug("Duration: {}", days);
        assertEquals(3, days);
    }

    @Test
    public void testCalendar() {

        // Date to Instant
        Instant timestamp = new java.util.Date().toInstant();
        // Now we can convert Instant to ZonedDateTime or other similar
        // classes
        LocalDateTime date = LocalDateTime.ofInstant(timestamp, ZoneId.of(ZoneId.SHORT_IDS.get("PST")));
        _log.debug("date: {}", date);

        // Get the date in JVM timeZone
        ZonedDateTime date1 = ZonedDateTime.now(TimeZone.getDefault().toZoneId());
        _log.debug("date1: {}", date1);

        // Calendar to Instant in GMT
        Instant time = Calendar.getInstance().toInstant();

        _log.debug("time: {}", time);

        // TimeZone to ZoneId
        ZoneId defaultZone = TimeZone.getDefault().toZoneId();
        _log.debug("defaultZone: {}", defaultZone);

        _log.debug("time at defaultZone: {}", time.atZone(defaultZone));

        _log.debug("date at defaultZone: {}", date.atZone(defaultZone));

        // ZonedDateTime from specific Calendar
        ZonedDateTime defaultZonedDateTime = ZonedDateTime.now(defaultZone);
        _log.debug("defaultZonedDateTime: {}", defaultZonedDateTime);

        ZonedDateTime zonedDateTime = new GregorianCalendar().toZonedDateTime();
        _log.debug("zonedDateTime: {}", zonedDateTime);

        // Date API to Legacy classes
        java.util.Date dt = java.util.Date.from(Instant.now());
        _log.debug("date: {}", dt);

        TimeZone timeZone = TimeZone.getTimeZone(defaultZone);
        _log.debug("timeZone: {}", timeZone);

        GregorianCalendar gc = GregorianCalendar.from(zonedDateTime);
        _log.debug("gc: {}", gc);
        _log.debug("gc date: {}", gc.getTime());

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        DateTimeFormatter formatter = builder.appendLiteral("Day is:").appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(", month is:").appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral(", and year:")
                .appendPattern("u").appendLiteral(" with the time:").appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral(":").appendText(ChronoField.MINUTE_OF_HOUR, TextStyle.NARROW_STANDALONE)
                .toFormatter();
        ZonedDateTime dateTime = ZonedDateTime.now();
        String str = dateTime.format(formatter);
        _log.debug("Formatted String: {}", str);
    }
}
