package org.trade.core.message;

import org.trade.core.exception.ExceptionCode;
import org.trade.core.exception.ExceptionContext;
import org.trade.core.exception.ExceptionMessage;

/**
 * Exception messages represent the user-friendly messages returned by the
 * system. This class should include every message that can possibly be returned
 * by the system.
 *
 * @author Simon Allen
 */
public class MessageFactory implements IMessageFactory {

    // This is a special case and represents an internal error. It will
    // be used when other messages cannot be located.
    public final static IMessageFactory SYSTEM_ERROR = new MessageFactory(createDefaultMessage());

    // Start modifications here, please follow the formatting.
    public final static IMessageFactory ERROR_UNABLE_TO_PROCESS_REQUEST = new MessageFactory(
            "ERROR_UNABLE_TO_PROCESS_REQUEST");

    public final static IMessageFactory MISSING_XML_ELEMENT = new MessageFactory("MISSING_XML_ELEMENT");
    public final static IMessageFactory XML_STRUCTURE_DOES_NOT_MATCH_REQUEST_TYPE = new MessageFactory(
            "XML_STRUCTURE_DOES_NOT_MATCH_REQUEST_TYPE");

    // Used by all handlers and the security authorization component
    public final static IMessageFactory ERROR_UNABLE_TO_PERFORM_SECURITY_AUTHORIZATION = new MessageFactory(
            "ERROR_UNABLE_TO_PERFORM_SECURITY_AUTHORIZATION");
    public final static IMessageFactory ERROR_UNAUTHORIZED_REQUEST = new MessageFactory("ERROR_UNAUTHORIZED_REQUEST");

    // Used by the XmlAdapter class.
    public final static IMessageFactory XML_NOT_WELL_FORMED = new MessageFactory("XML_NOT_WELL_FORMED");
    public final static IMessageFactory XML_REQUEST_TYPE_NOT_RECOGNIZED = new MessageFactory(
            "XML_REQUEST_TYPE_NOT_RECOGNIZED");

    private ExceptionMessage exceptionMessage;

    // TODO: This should be private, but it is needed for IntRequest to support
    // old valuetype mechanism

    /**
     * Constructor for MessageFactory.
     *
     * @param exceptionMessage ExceptionMessage
     */
    public MessageFactory(ExceptionMessage exceptionMessage) {

        this.exceptionMessage = exceptionMessage;
    }

    /**
     * Constructor for MessageFactory.
     *
     * @param indexIntoMessageFile String
     */
    private MessageFactory(String indexIntoMessageFile) {

        try {

            exceptionMessage = MessageTranslator.retrieveExceptionMessage(indexIntoMessageFile);
        } catch (Exception ex) {
            // Log the fact we could not load the message and default to the generic system error.
            exceptionMessage = createDefaultMessage();
        }
    }

    /**
     * Method create.
     *
     * @return ExceptionMessage
     * @see IMessageFactory#create()
     */
    public ExceptionMessage create() {

        return new ExceptionMessage(exceptionMessage);
    }

    /**
     * @param fieldSequence This should be used when checking repeating groups because it
     *                      will cause a group number to be appended to each field
     *                      reference.
     * @return ExceptionMessage
     * @see IMessageFactory#create(int)
     */
    public ExceptionMessage create(int fieldSequence) {

        return new ExceptionMessage(exceptionMessage.getExceptionCode().createSequencedCode(fieldSequence),
                exceptionMessage);
    }

    /**
     * Convenience method to add context to the exception message.
     *
     * @param exceptionContext ExceptionContext
     * @return ExceptionMessage
     * @see IMessageFactory#create(ExceptionContext)
     */
    public ExceptionMessage create(ExceptionContext exceptionContext) {

        ExceptionMessage returnValue = new ExceptionMessage(exceptionMessage);
        returnValue.addExceptionContext(exceptionContext);
        return returnValue;
    }

    /**
     * Convenience method to add context to the exception message.
     *
     * @param exceptionContext1 ExceptionContext
     * @param exceptionContext2 ExceptionContext
     * @return ExceptionMessage
     * @see IMessageFactory#create(ExceptionContext,
     * ExceptionContext)
     */
    public ExceptionMessage create(ExceptionContext exceptionContext1, ExceptionContext exceptionContext2) {

        ExceptionMessage returnValue = new ExceptionMessage(exceptionMessage);
        returnValue.addExceptionContext(exceptionContext1);
        returnValue.addExceptionContext(exceptionContext2);

        return returnValue;
    }

    /**
     * Method createDefaultMessage.
     *
     * @return ExceptionMessage
     */
    private static ExceptionMessage createDefaultMessage() {

        return new ExceptionMessage(new ExceptionCode("SYS0001"), "Unable to process request due to a system error");
    }
}
