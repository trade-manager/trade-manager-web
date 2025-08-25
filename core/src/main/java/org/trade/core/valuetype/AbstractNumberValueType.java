package org.trade.core.valuetype;

import org.trade.core.exception.ExceptionContext;
import org.trade.core.message.IMessageFactory;
import org.trade.core.validator.IExceptionMessageListener;
import org.trade.core.validator.IValidator;
import org.trade.core.validator.StringValidator;

import java.io.Serial;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public abstract class AbstractNumberValueType extends StringWrapper {

    @Serial
    private static final long serialVersionUID = 2808106869428773106L;

    public AbstractNumberValueType() {
    }

    /**
     * Constructor for AbstractNumberValueType.
     *
     * @param value String
     */
    public AbstractNumberValueType(String value) {
        super(value);
    }

    /**
     * Constructor for AbstractNumberValueType.
     *
     * @param value long
     */
    public AbstractNumberValueType(long value) {
        super(notNull(value));
    }

    /**
     * Constructor for AbstractNumberValueType.
     *
     * @param value Long
     */
    public AbstractNumberValueType(Long value) {
        super(notNull(value));
    }

    /**
     * Method getNumber.
     *
     * @return long
     */
    public long getNumber() {
        long value = 0;

        if (!isEmpty()) {
            value = Long.parseLong(getInternalValue());
        }

        return (value);
    }

    /**
     * Method getLong.
     *
     * @return Long
     */
    public Long getLong() {
        return (getNumber());
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        if (!isEmpty()) {
            return (getInternalValue());
        } else {
            return ("");
        }
    }

    /**
     * Returns validator of this value type.
     *
     * @param messageFactory IMessageFactory
     * @return validator * @see <code>IStringValidator</code>
     */
    public IValidator getDefaultMandatoryValidator(IMessageFactory messageFactory) {
        return getDefaultValidator(messageFactory, true);
    }

    /**
     * Returns validator of this value type.
     *
     * @param messageFactory IMessageFactory
     * @return validator * @see <code>IStringValidator</code>
     */
    public IValidator getDefaultOptionalValidator(IMessageFactory messageFactory) {
        return getDefaultValidator(messageFactory, false);
    }

    /**
     * Returns the maximum length. This method should be overwritten by
     * subclasses.
     *
     * @return int
     */
    protected abstract int getMaximumLength();

    /**
     * Method getMaximumValue.
     *
     * @return Long
     */
    protected abstract Long getMaximumValue();

    /**
     * Method getMinimumValue.
     *
     * @return Long
     */
    protected abstract Long getMinimumValue();

    /**
     * Returns validator of this value type.
     *
     * @param messageFactory IMessageFactory
     * @param isMandatory    boolean
     * @return validator * @see <code>IValidator</code>
     */
    protected IValidator getDefaultValidator(IMessageFactory messageFactory, boolean isMandatory) {
        return new StringValidator(messageFactory, getMaximumLength(), StringValidator.DIGITS, "", isMandatory) {
            public boolean isValid(Object value, String invalidValue, String expectedFormat,
                                   IExceptionMessageListener receiver) {
                boolean valid = super.isValid(value, invalidValue, expectedFormat, receiver);

                do {
                    if (valid && !AbstractNumberValueType.this.isEmpty()) {
                        Long max = AbstractNumberValueType.this.getMaximumValue();
                        Long min = AbstractNumberValueType.this.getMinimumValue();
                        long longValue;

                        try {
                            longValue = Long.parseLong((String) value);
                        } catch (Throwable t) {
                            receiver.addExceptionMessage(getMessageFactory().create(new ExceptionContext("edit_check",
                                    "Value [" + value + "] is not in correct number format.")));
                            break;
                        }

                        if ((max != null) && (longValue > max)) {
                            receiver.addExceptionMessage(getMessageFactory().create(
                                    new ExceptionContext("edit_check", "Value can not be greater than " + max)));

                        }
                        if ((min != null) && (longValue < min)) {
                            receiver.addExceptionMessage(getMessageFactory()
                                    .create(new ExceptionContext("edit_check", "Value can not be less than " + min)));
                        }
                    }
                } while (false);

                return (valid);
            }

        };
    }

    /**
     * Method notNull.
     *
     * @param value Long
     * @return String
     */
    private static String notNull(Long value) {
        if (value == null) {
            return ("");
        } else {
            return (value.toString());
        }
    }

    /**
     * Method notNull.
     *
     * @param value long
     * @return String
     */
    private static String notNull(long value) {
        if (value == 0) {
            return ("");
        } else {
            return (Long.toString(value));
        }
    }
}