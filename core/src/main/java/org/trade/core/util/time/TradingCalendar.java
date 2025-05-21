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
package org.trade.core.util.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.properties.ConfigProperties;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * @author Simon Allen
 */
public class TradingCalendar {

    /*
     * Set the default time zone i.e. user.timezone system property.
     */

    private final static Logger _log = LoggerFactory.getLogger(TradingCalendar.class);

    public static ZoneId MKT_TIMEZONE = null;
    public static ZoneId LOCAL_TIMEZONE = null;

    private static final HashMap<Integer, int[]> HOLIDAYS = new HashMap<>();
    private static int[] NONTRADINGDAYS = new int[]{};

    private static Integer openHour = 9;
    private static Integer openMinute = 30;
    private static Integer closeHour = 16;
    private static Integer closeMinute = 0;
    private static Integer closeDayOffset = 0;

    private static Integer currentYear = null;
    private static Integer currentMonth = null;
    private static Integer currentDay = null;

    /*
     * Initialize the calendar form the properties file. If values are not found
     * defaults will be used.
     */
    static {

        try {

            LOCAL_TIMEZONE = TimeZone.getDefault().toZoneId();

            String mktTZ = ConfigProperties.getPropAsString("trade.tws.timezone");
            MKT_TIMEZONE = ZoneId.of(mktTZ);
            TimeZone mktTimeZone = TimeZone.getTimeZone(mktTZ);
            TimeZone.setDefault(mktTimeZone);
            ZonedDateTime currentDateTime = ZonedDateTime.now(MKT_TIMEZONE);
            currentYear = currentDateTime.getYear();
            currentMonth = currentDateTime.getMonthValue();
            currentDay = currentDateTime.getDayOfMonth();
        } catch (Exception ex) {
            _log.warn("Property trade.tws.market.timezone not set in config.properties will use default");
        }

        try {
            if (null == openHour) {
                String open = ConfigProperties.getPropAsString("trade.market.open");
                String close = ConfigProperties.getPropAsString("trade.market.close");
                openHour = Integer.valueOf(open.substring(0, open.indexOf(":")));
                openMinute = Integer.valueOf(open.substring(open.indexOf(":") + 1));
                closeHour = Integer.valueOf(close.substring(0, close.indexOf(":")));
                closeMinute = Integer.valueOf(close.substring(close.indexOf(":") + 1));
                /*
                 * If the close time if before or equal to the open time assume
                 * its the next day.
                 */
                if (closeHour < openHour || (closeHour.equals(openHour) && closeMinute <= openMinute)) {
                    closeDayOffset++;
                }
            }
        } catch (IOException ex) {
            _log.warn(
                    "Property trade.market.open/trade.market.close not set in config.properties will use default 9:30am EST");
        }

        try {
            if (HOLIDAYS.isEmpty()) {
                int year = TradingCalendar.getDateTimeNowMarketTimeZone().getYear();
                String holidaysString = ConfigProperties.getPropAsString("trade.holidays." + year);
                parseHolidayIntegerCSVString(year, holidaysString);
            }

        } catch (IOException ex) {
            _log.warn("Property trade.holidays.{} not set in org/trade/core/util/config.properties", TradingCalendar.getDateTimeNowMarketTimeZone().getYear());
        }
        try {
            String nontradingdays = ConfigProperties.getPropAsString("trade.market.nontradingdays");
            StringTokenizer st = new StringTokenizer(nontradingdays, ",");
            if (st.countTokens() > 0) {
                NONTRADINGDAYS = new int[(st.countTokens())];
                int i = 0;
                while (st.hasMoreTokens()) {
                    NONTRADINGDAYS[(i)] = Integer.parseInt(st.nextToken());
                    i++;
                }
            }
        } catch (IOException ex) {
            _log.warn("Property trade.market.nontradingdays not set in config.properties");
        }
    }

    /**
     * Method addTradingDays.
     *
     * @param date   ZonedDateTime
     * @param noDays int
     * @return ZonedDateTime
     */
    public static ZonedDateTime addTradingDays(ZonedDateTime date, int noDays) {
        if ((date != null) && (noDays != 0)) {
            if (noDays > 0) {
                for (int i = 0; i < noDays; i++) {
                    date = date.plusDays(1);
                    if (!TradingCalendar.isTradingDay(date) || isHoliday(date)) {
                        noDays++;
                    }
                }
            } else {
                for (int i = 0; i > noDays; i--) {
                    date = date.minusDays(1);
                    if (!TradingCalendar.isTradingDay(date) || isHoliday(date)) {
                        noDays--;
                    }
                }

            }
        }
        return date;
    }

    /**
     * Method isTradingDay.
     *
     * @param date ZonedDateTime
     * @return boolean
     */
    public static boolean isTradingDay(ZonedDateTime date) {
        if (isHoliday(date)) {
            return false;
        }
        if (null != NONTRADINGDAYS) {
            for (int hol : NONTRADINGDAYS) {
                if (hol == date.getDayOfWeek().getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return the current date time in the market time zone.
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime getDateTimeNowMarketTimeZone() {
        ZonedDateTime defaultZonedDateTime = ZonedDateTime.now(TimeZone.getDefault().toZoneId());
        return defaultZonedDateTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
    }

    /**
     * Method adjustDateTimeToMarketTimeZone
     *
     * @param dateTime ZonedDateTime Return the date time in the market time zone.
     * @return ZonedDateTime
     */
    public static ZonedDateTime adjustDateTimeToMarketTimeZone(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());
    }

    /**
     * Get the date formated to the standard format string
     *
     * @param date   LocalDate
     * @param format String
     * @return String
     */
    public static String getFormattedDate(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Get the date formated to the standard format string
     *
     * @param date   LocalDateTime
     * @param format String
     * @return String
     */
    public static String getFormattedDate(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Get the date formated to the standard format string
     *
     * @param date   ZonedDateTime
     * @param format String
     * @return String
     */
    public static String getFormattedDate(ZonedDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Method getZonedDateTimeFromDateString
     *
     * @param date   String
     * @param format String
     * @param zoneId ZoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime getZonedDateTimeFromDateString(String date, String format, ZoneId zoneId) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
        return ZonedDateTime.of(localDate, LocalTime.of(0, 0), zoneId);
    }

    /**
     * Method getZonedDateTimeFromDateString
     *
     * @param dateTime String
     * @param format   String
     * @param zoneId   ZoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime getZonedDateTimeFromDateTimeString(String dateTime, String format, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
        return ZonedDateTime.of(localDateTime, zoneId);
    }

    /**
     * Method getZonedDateTimeFromDateString
     *
     * @param dateTime String
     * @param format   String
     * @return ZonedDateTime
     */
    public static ZonedDateTime getZonedDateTimeFromDateTimeString(String dateTime, String format) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
        return ZonedDateTime.of(localDateTime, TimeZone.getDefault().toZoneId());
    }

    /**
     * Method getLocalDateFromDateString
     *
     * @param date   String
     * @param format String
     * @return LocalDate
     */
    public static LocalDate getLocalDateFromDateString(String date, String format) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Method getLocalDateTimeFromDateTimeString
     *
     * @param dateTime String
     * @param format   String
     * @return ZonedDateTime
     */
    public static LocalDateTime getLocalDateTimeFromDateTimeString(String dateTime, String format) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Method getTradingDayStart.
     *
     * @param date ZonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime getTradingDayStart(ZonedDateTime date) {
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), openHour, openMinute, 0, 0,
                TimeZone.getDefault().toZoneId());
    }

    /**
     * Method getTradingDayEnd.
     *
     * @param date ZonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime getTradingDayEnd(ZonedDateTime date) {
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), closeHour, closeMinute, 0,
                0, TimeZone.getDefault().toZoneId());
    }

    /**
     * Method getDateAtTime.
     *
     * @param date   ZonedDateTime
     * @param atTime ZonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime getDateAtTime(ZonedDateTime date, ZonedDateTime atTime) {
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), atTime.getHour(),
                atTime.getMinute(), atTime.getSecond(), 0, TimeZone.getDefault().toZoneId());
    }

    /**
     * Method getDateAtTime.
     *
     * @param date     ZonedDateTime
     * @param atHour   int
     * @param atMinute int
     * @param atSecond int
     * @return ZonedDateTime
     */
    public static ZonedDateTime getDateAtTime(ZonedDateTime date, int atHour, int atMinute, int atSecond) {
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), atHour, atMinute, atSecond,
                0, TimeZone.getDefault().toZoneId());
    }

    /**
     * Method getDurationInSeconds.
     *
     * @param startOfPeriod ZonedDateTime
     * @param endOfPeriod   ZonedDateTime
     * @return ZonedDateTime
     */
    public static long getDurationInSeconds(ZonedDateTime startOfPeriod, ZonedDateTime endOfPeriod) {
        Duration duration = Duration.between(startOfPeriod, endOfPeriod);
        return duration.getSeconds();
    }

    /**
     * Method getDurationInDays.
     *
     * @param startOfPeriod ZonedDateTime
     * @param endOfPeriod   ZonedDateTime
     * @return ZonedDateTime
     */
    public static long getDurationInDays(ZonedDateTime startOfPeriod, ZonedDateTime endOfPeriod) {
        Duration duration = Duration.between(startOfPeriod, endOfPeriod);
        return (duration.getSeconds() / (24 * 60 * 60));
    }

    /**
     * Method getCurrentDayAtTime.
     *
     * @param time ZonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime getCurrentDayAtTime(ZonedDateTime time) {
        return ZonedDateTime.of(currentYear, currentMonth, currentDay, time.getHour(), time.getMinute(),
                time.getSecond(), 0, TimeZone.getDefault().toZoneId());
    }

    /**
     * Method getYearStart.
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime getYearStart() {
        return ZonedDateTime.of(TradingCalendar.getDateTimeNowMarketTimeZone().toLocalDate()
                .with(TemporalAdjusters.firstDayOfYear()).atStartOfDay(), TimeZone.getDefault().toZoneId());
    }

    /**
     * Method isMarketHours.
     *
     * @param date ZonedDateTime
     * @return boolean
     */
    public static boolean isMarketHours(ZonedDateTime date) {
        return !isAfterHours(date) && !isPreMarket(date);
    }

    /**
     * Method isMarketHours.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @param date      ZonedDateTime
     * @return boolean
     */
    public static boolean isMarketHours(ZonedDateTime openDate, ZonedDateTime closeDate, ZonedDateTime date) {

        if (isTradingDay(date)) {
            int diffDays = (int) (TradingCalendar.getDurationInDays(openDate, closeDate));
            return TradingCalendar.between(date, TradingCalendar.getDateAtTime(date, openDate),
                    TradingCalendar.addTradingDays(TradingCalendar.getDateAtTime(date, closeDate), diffDays));
        }
        return false;
    }

    /**
     * Method isPreMarket.
     *
     * @param date ZonedDateTime
     * @return boolean
     */
    public static boolean isPreMarket(ZonedDateTime date) {
        return getTradingDayStart(date).isAfter(date);
    }

    /**
     * Method isAfterHours.
     *
     * @param date ZonedDateTime
     * @return boolean
     */
    public static boolean isAfterHours(ZonedDateTime date) {
        return getTradingDayEnd(date).isBefore(date) || (getTradingDayEnd(date).compareTo(date) == 0);
    }

    /**
     * Method sameDay.
     *
     * @param date1 ZonedDateTime
     * @param date2 ZonedDateTime
     * @return boolean
     */
    public static boolean sameDay(ZonedDateTime date1, ZonedDateTime date2) {
        return date1.getYear() == date2.getYear() && date1.getMonthValue() == date2.getMonthValue()
                && date1.getDayOfMonth() == date2.getDayOfMonth();
    }

    /**
     * Method getCurrentTradingDay.
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime getCurrentTradingDay() {
        ZonedDateTime currTradingDay = TradingCalendar
                .getTradingDayStart(TradingCalendar.getDateTimeNowMarketTimeZone());
        while (!TradingCalendar.isTradingDay(currTradingDay)) {
            currTradingDay = currTradingDay.minusDays(1);
        }
        return currTradingDay;
    }

    /**
     * Method getPrevTradingDay.
     *
     * @param tradingDay ZonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime getPrevTradingDay(ZonedDateTime tradingDay) {
        ZonedDateTime prevTradingDay = TradingCalendar.getTradingDayStart(tradingDay);
        do {
            prevTradingDay = prevTradingDay.minusDays(1);
        } while (!TradingCalendar.isTradingDay(prevTradingDay));
        return prevTradingDay;
    }

    /**
     * Method getNextTradingDay.
     *
     * @param input ZonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime getNextTradingDay(ZonedDateTime input) {
        ZonedDateTime nextTradingday = TradingCalendar.getTradingDayStart(input);

        do {
            nextTradingday = nextTradingday.plusDays(1);
        } while (!TradingCalendar.isTradingDay(nextTradingday));
        return nextTradingday;
    }

    /**
     * Method isHoliday.
     *
     * @param date ZonedDateTime
     * @return boolean
     */
    public static boolean isHoliday(ZonedDateTime date) {

        int year = date.getYear();
        int[] hols = HOLIDAYS.get(year);
        if (null != hols) {
            for (int hol : hols) {
                if (hol == date.getDayOfYear()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method between.
     *
     * @param date      ZonedDateTime
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return boolean
     */
    public static boolean between(ZonedDateTime date, ZonedDateTime openDate, ZonedDateTime closeDate) {
        return (date.isAfter(openDate) || date.equals(openDate)) && (date.isBefore(closeDate));
    }

    /**
     * Method getZonedDateTimeFromMilli.
     *
     * @param millis long
     * @return ZonedDateTime
     */
    public static ZonedDateTime getZonedDateTimeFromMilli(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
    }

    /**
     * Method geMillisFromZonedDateTime.
     *
     * @param date ZonedDateTime
     * @return long
     */
    public static long geMillisFromZonedDateTime(ZonedDateTime date) {
        return date.toInstant().toEpochMilli();
    }

    /**
     * getDaysInYear Return the Days in the year
     *
     * @param date Date
     * @return int
     */
    public static int getDaysInYear(ZonedDateTime date) {
        if (date.toLocalDate().isLeapYear())
            return 366;
        return 365;
    }

    /**
     * convertTimeZone Move the timeZone from System to Market this is only
     * needed because JPA 2.1 does not support ZonedDateTime i.e. EST 9:30 gets
     * converted to PC dateTime when stored.
     *
     * @param date   java.util.Date
     * @param fromTZ TimeZone
     * @param toTZ   TimeZone
     * @return java.util.Date
     */

    public static java.util.Date convertTimeZone(java.util.Date date, TimeZone fromTZ, TimeZone toTZ) {
        long fromTZDst = 0;
        if (fromTZ.inDaylightTime(date)) {
            fromTZDst = fromTZ.getDSTSavings();
        }

        long fromTZOffset = fromTZ.getRawOffset() + fromTZDst;

        long toTZDst = 0;
        if (toTZ.inDaylightTime(date)) {
            toTZDst = toTZ.getDSTSavings();
        }
        long toTZOffset = toTZ.getRawOffset() + toTZDst;

        return new java.util.Date(date.getTime() + (toTZOffset - fromTZOffset));
    }

    /**
     * Method parseHolidayIntegerCSVString.
     *
     * @param csvString String
     */
    private static void parseHolidayIntegerCSVString(Integer year, String csvString) {
        StringTokenizer st = new StringTokenizer(csvString, ",");
        if (st.countTokens() > 0) {
            int[] dates = new int[(st.countTokens())];
            int i = 0;
            while (st.hasMoreTokens()) {
                dates[(i)] = Integer.parseInt(st.nextToken());
                i++;
            }
            TradingCalendar.HOLIDAYS.put(year, dates);
        }
    }
}
