package org.trade.core.message;

import org.trade.core.exception.NestingException;

import java.io.Serial;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class MessageTranslatorException extends NestingException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8703801242387795447L;

    /**
     * Constructor for MessageTranslatorException.
     *
     * @param t Throwable
     */
    public MessageTranslatorException(Throwable t) {
        super(t);
    }

    /**
     * Constructor for MessageTranslatorException.
     *
     * @param t       Throwable
     * @param message String
     */
    public MessageTranslatorException(Throwable t, String message) {
        super(t, message);
    }

    /**
     * Constructor for MessageTranslatorException.
     *
     * @param message String
     */
    public MessageTranslatorException(String message) {
        super(message);
    }
}
