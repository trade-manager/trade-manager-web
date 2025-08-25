package org.trade.core.validator;

import org.trade.core.exception.ExceptionContext;
import org.trade.core.exception.ExceptionMessage;
import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageContextFactory;
import org.trade.core.message.MessageFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DateValidator implements IValidator {

    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HHmmss'Z'";
    private IMessageFactory messageFactory;
    private final boolean isMandatory;

    /**
     * Constructor for DateValidator.
     *
     * @param messageFactory IMessageFactory
     * @param isMandatory    boolean
     */
    public DateValidator(IMessageFactory messageFactory, boolean isMandatory) {
        this.messageFactory = messageFactory;
        this.isMandatory = isMandatory;
    }

    /**
     * Method getMessageFactory.
     *
     * @return IMessageFactory
     */
    protected IMessageFactory getMessageFactory() {
        if (null == messageFactory) {
            messageFactory = MessageFactory.SYSTEM_ERROR;
        }

        return messageFactory;
    }

    /**
     * Method isValid.
     *
     * @param value          Object
     * @param invalidValue   String
     * @param expectedFormat String
     * @param receiver       IExceptionMessageListener
     * @return boolean
     * @see IValidator#isValid(Object, String, String,
     * IExceptionMessageListener)
     */
    public boolean isValid(Object value, String invalidValue, String expectedFormat,
                           IExceptionMessageListener receiver) {
        if (null == receiver) {
            receiver = new IExceptionMessageListener() {
                public void addExceptionMessage(ExceptionMessage e) {
                }
            };
        }

        boolean valid = true;

        if (null != invalidValue) {
            valid = false;

            if (expectedFormat == null) {
                expectedFormat = DATE_TIME_FORMAT;
            }
            receiver.addExceptionMessage(getMessageFactory().create(
                    new ExceptionContext("edit_check", "Value is not in the following format: " + expectedFormat)));
        } else if (null == value) {
            if (isMandatory) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.MANDATORY_VALUE_NOT_PROVIDED.create()));
            }
        } else {
            String errorMessage = validateDateValue((java.util.Date) value);

            if (errorMessage != null) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(new ExceptionContext("edit_check", errorMessage)));
            }
        }

        return valid;
    }

    /**
     * Method validateDateValue.
     *
     * @param date java.util.Date
     * @return String
     */
    private String validateDateValue(java.util.Date date) {
        int day, month, year;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);

        /*
         * None of this checks will ever fail. Leaving it here just to be
         * absolutely sure.
         */

        if ((day > 30) && ((month == 9) || (month == 4) || (month == 6) || (month == 11))) {
            return "day field of date is invalid - 31st of a day with 30 days";
        }

        if (month == 2) {
            int days = 28;

            if (new GregorianCalendar().isLeapYear(year)) {
                days = 29;
            }

            if (day > days) {
                return "day field of date is invalid - no day " + day + " in February of " + year;
            }
        }

        return null;
    }
}
