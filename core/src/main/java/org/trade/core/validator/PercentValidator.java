package org.trade.core.validator;

import org.trade.core.exception.ExceptionMessage;
import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageContextFactory;
import org.trade.core.message.MessageFactory;

import java.math.BigDecimal;


/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class PercentValidator implements IValidator {

    private IMessageFactory messageFactory;
    private final boolean isMandatory;
    private final boolean allowNegative;
    private final boolean allowZero;
    private final int maxNonDecimalLength;
    private final int maxDecimalLength;

    /**
     * Constructor for PercentValidator.
     *
     * @param messageFactory      IMessageFactory
     * @param allowNegative       boolean
     * @param allowZero           boolean
     * @param maxNonDecimalLength int
     * @param maxDecimalLength    int
     * @param isMandatory         boolean
     */
    public PercentValidator(IMessageFactory messageFactory, boolean allowNegative, boolean allowZero,
                            int maxNonDecimalLength, int maxDecimalLength, boolean isMandatory) {
        this.messageFactory = messageFactory;
        this.allowNegative = allowNegative;
        this.allowZero = allowZero;
        this.maxNonDecimalLength = maxNonDecimalLength;
        this.maxDecimalLength = maxDecimalLength;
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

    // from IPercentValidator

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

        if ((null == value) && (null == invalidValue)) {
            // Enforce optional/mandatory
            if (isMandatory) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.MANDATORY_VALUE_NOT_PROVIDED.create()));
            }
        } else if (null == invalidValue) // Able to parse input into a
        // BigDecimal
        {
            String stringValue = value.toString();

            int indexOfDot = stringValue.indexOf(".");
            String decimalString = "";
            String nonDecimalString = stringValue;
            if (-1 != indexOfDot) {
                decimalString = stringValue.substring(indexOfDot + 1);
                nonDecimalString = stringValue.substring(0, indexOfDot);
            }

            if (!nonDecimalString.isEmpty() && nonDecimalString.charAt(0) == '-') {
                nonDecimalString = nonDecimalString.substring(1);
            }

            long nonDecimalLength = nonDecimalString.length();

            // Note that the decimal length will be 1 for 00-09.
            long decimalLength = decimalString.length();

            // Enforce length of portion to right of decimal point
            if (decimalLength > maxDecimalLength) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_RIGHT_OF_DECIMAL_TOO_LONG
                                .create(MessageContextFactory.MAX_LENGTH.create("" + maxDecimalLength))));
            }

            // Enforce length of portion to left of decimal point
            if (valid && nonDecimalLength > maxNonDecimalLength) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_LEFT_OF_DECIMAL_TOO_LONG
                                .create(MessageContextFactory.MAX_LENGTH.create("" + maxNonDecimalLength))));
            }

            // Disallow zero for certain formats
            if (valid && !allowZero && (0 == ((BigDecimal) value).compareTo(new BigDecimal(0)))) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_ZERO_NOT_ALLOWED.create()));
            }

            // Disallow negative numbers
            if (valid && !allowNegative && (((BigDecimal) value).compareTo(new BigDecimal(0)) < 0)) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_NEGATIVE_NOT_ALLOWED.create()));
            }
        } else
        // Percent was not able to parse invalidValue into a BigDecimal
        {
            IValidator validator;

            if (allowNegative) {
                validator = new StringValidator(getMessageFactory(), 1, maxNonDecimalLength + maxDecimalLength + 2,
                        StringValidator.DIGITS, "-.", isMandatory);
            } else {
                validator = new StringValidator(getMessageFactory(), 1, maxNonDecimalLength + maxDecimalLength + 1,
                        StringValidator.DIGITS, ".", isMandatory);
            }

            if (invalidValue.equals(".")) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_DOT_WITH_NO_NUMBERS.create()));
            }

            if (valid && invalidValue.indexOf(".") != invalidValue.lastIndexOf(".")) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_MULTIPLE_DOTS.create()));
            }

            if (valid && allowNegative && (invalidValue.indexOf("-") != invalidValue.lastIndexOf("-"))) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_MULTIPLE_DASHES.create()));
            }

            if (valid && allowNegative && (invalidValue.contains("-")) && (invalidValue.indexOf("-") != 0)) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_DASH_NOT_FIRST_CHARACTER.create()));
            }

            if (valid) {

                valid = validator.isValid(invalidValue, invalidValue, expectedFormat, receiver);
            }
        }

        return valid;
    }
}
