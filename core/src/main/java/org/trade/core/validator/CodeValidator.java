
package org.trade.core.validator;

import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageContextFactory;

import java.util.Collection;

/**
 *
 */
public class CodeValidator implements IValidator {
    private final IMessageFactory m_messageFactory;

    private final Collection<?> m_acceptableValues;

    private final boolean m_isMandatory;

    /**
     * Constructor for CodeValidator.
     *
     * @param messageFactory   IMessageFactory
     * @param acceptableValues Collection<?>
     * @param isMandatory      boolean
     */
    public CodeValidator(IMessageFactory messageFactory, Collection<?> acceptableValues, boolean isMandatory) {
        m_messageFactory = messageFactory;
        m_acceptableValues = acceptableValues;
        m_isMandatory = isMandatory;
    }

    /**
     * Method getMessageFactory.
     *
     * @return IMessageFactory
     */
    protected final IMessageFactory getMessageFactory() {
        return m_messageFactory;
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
        boolean valid = true;

        if (null == value) {
            value = "";
        }

        if (((String) value).isEmpty()) // Optional/mandatory check
        {
            if (m_isMandatory) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.MANDATORY_VALUE_NOT_PROVIDED.create()));
            }
        } else
        // 0 < length so check valid values
        {
            if (!m_acceptableValues.contains(value)) {
                valid = false;
                receiver.addExceptionMessage(getMessageFactory().create(
                        MessageContextFactory.CODE_NOT_VALID.create(MessageContextFactory.INVALID_CODE.create(value))));
            }
        }

        return valid;
    }
}
