
package org.trade.core.validator;

import org.trade.core.exception.ExceptionMessage;
import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageContextFactory;
import org.trade.core.message.MessageFactory;

import java.math.BigDecimal;

/**
 *
 */
public class PercentValidator implements IValidator {
    private IMessageFactory m_messageFactory;

    private final boolean m_isMandatory;

    private final boolean m_allowNegative;

    private final boolean m_allowZero;

    private final int m_maxNonDecimalLength;

    private final int m_maxDecimalLength;

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
        m_messageFactory = messageFactory;
        m_allowNegative = allowNegative;
        m_allowZero = allowZero;
        m_maxNonDecimalLength = maxNonDecimalLength;
        m_maxDecimalLength = maxDecimalLength;
        m_isMandatory = isMandatory;
    }

    /**
     * Method getMessageFactory.
     *
     * @return IMessageFactory
     */
    protected IMessageFactory getMessageFactory() {
        if (null == m_messageFactory) {
            m_messageFactory = MessageFactory.SYSTEM_ERROR;
        }

        return m_messageFactory;
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
            if (m_isMandatory) {
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
            if (decimalLength > m_maxDecimalLength) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_RIGHT_OF_DECIMAL_TOO_LONG
                                .create(MessageContextFactory.MAX_LENGTH.create("" + m_maxDecimalLength))));
            }

            // Enforce length of portion to left of decimal point
            if (valid && nonDecimalLength > m_maxNonDecimalLength) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_LEFT_OF_DECIMAL_TOO_LONG
                                .create(MessageContextFactory.MAX_LENGTH.create("" + m_maxNonDecimalLength))));
            }

            // Disallow zero for certain formats
            if (valid && !m_allowZero && (0 == ((BigDecimal) value).compareTo(new BigDecimal(0)))) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_ZERO_NOT_ALLOWED.create()));
            }

            // Disallow negative numbers
            if (valid && !m_allowNegative && (((BigDecimal) value).compareTo(new BigDecimal(0)) < 0)) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_NEGATIVE_NOT_ALLOWED.create()));
            }
        } else
        // Percent was not able to parse invalidValue into a BigDecimal
        {
            IValidator validator;

            if (m_allowNegative) {
                validator = new StringValidator(getMessageFactory(), 1, m_maxNonDecimalLength + m_maxDecimalLength + 2,
                        StringValidator.DIGITS, "-.", m_isMandatory);
            } else {
                validator = new StringValidator(getMessageFactory(), 1, m_maxNonDecimalLength + m_maxDecimalLength + 1,
                        StringValidator.DIGITS, ".", m_isMandatory);
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

            if (valid && m_allowNegative && (invalidValue.indexOf("-") != invalidValue.lastIndexOf("-"))) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.PERCENT_MULTIPLE_DASHES.create()));
            }

            if (valid && m_allowNegative && (invalidValue.contains("-")) && (invalidValue.indexOf("-") != 0)) {
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
