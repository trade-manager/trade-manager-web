package org.trade.core.validator;

import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageContextFactory;

/**
 *
 */
public class NumericRangeValidator extends StringValidator {
    private final long m_minValue;

    private final long m_maxValue;

    /**
     * Constructor for NumericRangeValidator.
     *
     * @param messageFactory IMessageFactory
     * @param maxLength      int
     * @param minValue       long
     * @param maxValue       long
     * @param isMandatory    boolean
     */
    public NumericRangeValidator(IMessageFactory messageFactory, int maxLength, long minValue, long maxValue,
                                 boolean isMandatory) {
        super(messageFactory, maxLength, StringValidator.DIGITS, isMandatory);

        m_minValue = minValue;
        m_maxValue = maxValue;
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


        if (null == value) {
            value = "";
        }

        // This will check length, etc.
        boolean valid = super.isValid(value, invalidValue, expectedFormat, receiver);

        // Now we perform the email specific checks
        if (valid && (!((String) value).isEmpty())) {
            try {
                long i = Long.parseLong(((String) value));
                if (i < m_minValue) {
                    valid = false;
                    receiver.addExceptionMessage(getMessageFactory().create(MessageContextFactory.BELOW_MIN_VALUE
                            .create(MessageContextFactory.MIN_VALUE.create(m_minValue))));
                }

                if (i > m_maxValue) {
                    valid = false;
                    receiver.addExceptionMessage(getMessageFactory().create(MessageContextFactory.EXCEEDS_MAX_VALUE
                            .create(MessageContextFactory.MAX_VALUE.create(m_maxValue))));
                }
            } catch (Exception ex) {
                // Should not happen as I already have checked it for
                // being numeric
                throw new Error("Coding error - received an exception attempting to conver " + value
                        + " to a long.  This should never happen");
            }
        }

        return valid;
    }
}
