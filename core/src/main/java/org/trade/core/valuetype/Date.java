package org.trade.core.valuetype;

import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageFactory;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.validator.DateValidator;
import org.trade.core.validator.IExceptionMessageListener;
import org.trade.core.validator.IValidator;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Date extends ValueType implements Comparator<Date>, Comparable<Date> {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5122615819171831028L;

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HHmmss'Z'";
    public final static String DATE_FORMAT = "yyyyMMdd";
    public final static int LEN_STRING_IN_DATE_TIME_FORMAT = 18;
    public final static int LEN_STRING_IN_DATE_FORMAT = 8;
    public static final Date NULLIPDATE = new Date(
            ZonedDateTime.of(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), ZoneOffset.UTC.normalized()));

    static {
        // Register the appropriate converters
        JavaTypeTranslator.registerDynamicTypeConverter(new ObjectToDate());
        JavaTypeTranslator.registerDynamicTypeConverter(new DateToObject());
    }

    private ZonedDateTime date = null;
    private String invalidDate = null;
    private String format = null;
    protected static Boolean ascending = true;

    /**
     * Default Constructor
     */
    public Date() {
        this.date = null;
    }

    /**
     * Constructor
     *
     * @param date ZonedDateTime
     */
    public Date(ZonedDateTime date) {
        this.date = date;
    }

    /**
     * Constructor
     *
     * @param date java.uti.Date
     */
    public Date(java.util.Date date) {
        this.date = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Parse the stringified date using the DATE_TIME_FORMAT
     *
     * @param date String
     */
    public Date(String date) {
        this(date, null);
    }

    /**
     * Parse the string field date using the incoming date form
     *
     * @param date       String
     * @param dateFormat String
     */
    public Date(String date, String dateFormat) {

        if ((date == null) || (date.isEmpty())) {

            return;
        }

        if (dateFormat == null) {

            format = DATE_TIME_FORMAT;

            if (date.length() == LEN_STRING_IN_DATE_FORMAT) {

                format = DATE_FORMAT;
            }
        } else {
            format = dateFormat;
        }

        if ((format.equals(DATE_TIME_FORMAT) && !rightLengthForDateTime(date))
                || (format.equals(DATE_FORMAT) && (date.length() != LEN_STRING_IN_DATE_FORMAT))) {

            invalidDate = date;
        } else {

            this.date = TradingCalendar.getZonedDateTimeFromDateTimeString(date.trim(), format);
            invalidDate = null;
        }
    }

    /**
     * @return The Date this Date is representing
     */
    public ZonedDateTime getZonedDateTime() {

        return this.date;
    }

    /**
     * @return The Date this Date is representing
     */
    public java.util.Date getDate() {

        Instant instant;

        if (null != getZonedDateTime()) {

            instant = getZonedDateTime().toInstant();
            return java.util.Date.from(instant);
        }

        return null;
    }

    /**
     * Method equals.
     *
     * @param objectToCompare Object
     * @return boolean
     * @see Comparator#equals(Object)
     */
    public boolean equals(Object objectToCompare) {

        if (this == objectToCompare) {

            return true;
        }

        if (objectToCompare == null) {

            return false;
        }

        boolean rVal = false;

        // Do not compare on nulls
        if (this.date != null) {

            ZonedDateTime cmpTo = null;

            if (objectToCompare instanceof Date) {

                cmpTo = ((Date) objectToCompare).date;
            } else if (objectToCompare instanceof java.util.Date) {

                cmpTo = (ZonedDateTime) objectToCompare;
            }

            // Do not compare on nulls
            if (cmpTo != null) {

                if (this.date.equals(cmpTo)) {

                    rVal = true;
                }
            }
        }

        return (rVal);
    }

    /**
     * Method compareTo.
     *
     * @param other Date
     * @return int
     */
    public int compareTo(final Date other) {

        return CoreUtils.nullSafeComparator(this.getZonedDateTime(), other.getZonedDateTime());
    }

    /**
     * Method compare.
     *
     * @param o1 Date
     * @param o2 Date
     * @return int
     */
    public int compare(Date o1, Date o2) {

        return CoreUtils.nullSafeComparator(o1.getZonedDateTime(), o2.getZonedDateTime());
    }

    /**
     * @return String
     */
    public String toString() {

        if (null != this.getZonedDateTime()) {

            return TradingCalendar.getFormattedDate(this.getZonedDateTime(), DATE_TIME_FORMAT);
        }

        return null;
    }

    /**
     * Method rightLengthForDateTime.
     *
     * @param dateAndTime String
     * @return boolean
     */
    public static boolean rightLengthForDateTime(String dateAndTime) {

        int length = dateAndTime.length();

        // do not count quotes
        int maxLength = DATE_TIME_FORMAT.length() - 4; // "yyyy-MM-dd'T'HHmmss'Z'";

        // month and day can be one digit : 2000-1-1T235959Z
        int minLength = maxLength - 2; // "yyyy-M-d'T'HHmmss'Z'";
        return (length <= maxLength) && (length >= minLength);
    }

    /**
     * Method isEmpty.
     *
     * @return boolean
     */
    public boolean isEmpty() {

        return null == this.date;
    }

    /**
     * Compares dates ignoring time.
     *
     * @param otherDate org.trade.core.valuetype.Date
     * @return the value 0 if the argument is a Date equal to this Date; a value
     * less than 0 if the argument is a Date after this Date; and a
     * value greater than 0 if the argument is a Date before this Date.
     */
    public int compareDates(Date otherDate) {

        return compareDates(otherDate.getZonedDateTime());
    }

    /**
     * Compares dates ignoring time.
     *
     * @param otherDate java.util.Date
     * @return the value 0 if the argument is a Date equal to this Date; a value
     * less than 0 if the argument is a Date after this Date; and a
     * value greater than 0 if the argument is a Date before this Date.
     */
    public int compareDates(ZonedDateTime otherDate) {

        return this.getZonedDateTime().compareTo(otherDate);
    }

    /**
     * @param value Object
     */
    public void setValue(Object value) throws ValueTypeException {

        if (value instanceof Date) {

            setDate(((Date) value).date);
        } else {

            try {

                setValue(JavaTypeTranslator.convert(Date.class, value));
            } catch (Exception ex) {

                throw new ValueTypeException(ex);
            }
        }
    }

    /**
     * Method isValid.
     *
     * @return boolean
     */
    public boolean isValid() {

        return isValid(getDefaultOptionalValidator(MessageFactory.SYSTEM_ERROR), null);
    }

    /**
     * Method isValid.
     *
     * @param validator IValidator
     * @param receiver  IExceptionMessageListener
     * @return boolean
     */
    public boolean isValid(IValidator validator, IExceptionMessageListener receiver) {

        return validator.isValid(this.date, invalidDate, format, receiver);
    }

    /**
     * Method getDefaultOptionalValidator.
     *
     * @param messageFactory IMessageFactory
     * @return IValidator
     */
    public IValidator getDefaultOptionalValidator(IMessageFactory messageFactory) {

        return getDefaultValidator(messageFactory, true);
    }

    /**
     * Method getDefaultMandatoryValidator.
     *
     * @param messageFactory IMessageFactory
     * @return IValidator
     */
    public IValidator getDefaultMandatoryValidator(IMessageFactory messageFactory) {

        return getDefaultValidator(messageFactory, true);
    }

    /**
     * Method getDefaultValidator.
     *
     * @param messageFactory IMessageFactory
     * @param isMandatory    boolean
     * @return IValidator
     */
    public IValidator getDefaultValidator(IMessageFactory messageFactory, boolean isMandatory) {

        return new DateValidator(messageFactory, isMandatory);
    }

    /**
     * Method getError. as long as the date was created by the conversion util,
     * it will be valid or the error will already be created
     *
     * @return String
     */
    public String getError() {

        if (this.date == null) {

            return "Date not set";
        }
        return null;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        return (super.clone());
    }

    /**
     * Method setDate.
     *
     * @param date java.time.ZonedDateTime
     */
    private void setDate(ZonedDateTime date) {

        this.date = date;
    }
}
