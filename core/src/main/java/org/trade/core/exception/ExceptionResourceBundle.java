package org.trade.core.exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.PropertyResourceBundle;

/**
 * ExceptionResourceBundle handles storing messages for each Exception.
 *
 * @author Simon Allen
 */
public class ExceptionResourceBundle extends PropertyResourceBundle implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3004803155454107541L;

    private final static String DEFAULT = "default";

    /**
     * Takes an InputStream to the properties file where the Exception messages
     * are stored
     *
     * @param resourceStream the input stream to the resource
     * @throws IOException : thrown when the input stream doesn't find the resource
     */
    public ExceptionResourceBundle(InputStream resourceStream) throws IOException {
        super(resourceStream);
    }

    /**
     * Returns the message for the given <code>code</code>
     *
     * @param code exception code
     * @return the exception message
     */
    public String getMessage(ExceptionCode code) {

        return getString(code.getCode());
    }

    /**
     * Returns the default exception message for the package
     *
     * @return the exception message
     */
    public String getMessage() {

        return getString(DEFAULT);
    }
}